package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

public class BasicTBoxGraph implements TBoxGraph {

    private final GraphClosure<OWLClassExpression> conceptsClosure;

    private final Map<OWLClassExpression, Set<OWLClassExpression>> conceptsPredecessors;

    private Set<OWLObjectProperty> irreflexiveRoles;

    private final QLNormalizedOntology ontology;

    private final GraphClosure<OWLPropertyExpression<?, ?>> rolesClosure;

    private final Map<OWLPropertyExpression<?, ?>, Set<OWLPropertyExpression<?, ?>>> rolesPredecessor;

    private final Set<OWLEntity> unsatisfiableEntities;

    public BasicTBoxGraph(QLNormalizedOntology ontology) {
	this.ontology = ontology;
	conceptsPredecessors = new HashMap<OWLClassExpression, Set<OWLClassExpression>>();
	rolesPredecessor = new HashMap<OWLPropertyExpression<?, ?>, Set<OWLPropertyExpression<?, ?>>>();
	for (final OWLSubClassOfAxiom axiom : ontology.getConceptSubsumptions()) {
	    Set<OWLClassExpression> predecessors = conceptsPredecessors
		    .get(axiom.getSuperClass());
	    if (predecessors == null) {
		predecessors = new HashSet<OWLClassExpression>();
		conceptsPredecessors.put(axiom.getSuperClass(), predecessors);
	    }
	    predecessors.add(axiom.getSubClass());
	}
	for (final OWLSubPropertyAxiom<?> axiom : ontology
		.getRoleSubsumptions()) {
	    Set<OWLPropertyExpression<?, ?>> predecessors = rolesPredecessor
		    .get(axiom.getSuperProperty());
	    if (predecessors == null) {
		predecessors = new HashSet<OWLPropertyExpression<?, ?>>();
		rolesPredecessor.put(axiom.getSuperProperty(), predecessors);
	    }
	    predecessors.add(axiom.getSubProperty());
	}
	conceptsClosure = new BasicLazyGraphClosure<OWLClassExpression>(
		conceptsPredecessors);
	rolesClosure = new BasicLazyGraphClosure<OWLPropertyExpression<?, ?>>(
		rolesPredecessor);
	irreflexiveRoles = null;
	unsatisfiableEntities = null;
    }

    private OWLEntity atom(OWLClassExpression v) {
	if (DLUtils.isAtomic(v))
	    return (OWLClass) v;
	else if (DLUtils.isExistential(v)) {
	    final OWLObjectSomeValuesFrom existential = (OWLObjectSomeValuesFrom) v;
	    return existential.getProperty().getNamedProperty();
	} else
	    return null;
    }

    private OWLProperty<?, ?> atom(OWLPropertyExpression<?, ?> v) {
	if (!DLUtils.isInverse(v))
	    return (OWLProperty<?, ?>) v;
	else
	    return ((OWLObjectPropertyExpression) v).getNamedProperty();
    }

    private Set<OWLEntity> atoms(Set<OWLClassExpression> unsatisfiableConcepts,
	    Set<OWLPropertyExpression<?, ?>> unsatisfiableRoles) {
	final Set<OWLEntity> result = new HashSet<OWLEntity>();
	for (final OWLClassExpression c : unsatisfiableConcepts)
	    result.add(atom(c));
	for (final OWLPropertyExpression<?, ?> r : unsatisfiableRoles)
	    result.add(atom(r));
	return result;
    }

    private Set<OWLObjectProperty> conceptInverseIntersection(
	    Set<OWLClassExpression> s1, Set<OWLClassExpression> s2) {
	final Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
	for (final OWLClassExpression v : s1)
	    if (DLUtils.isExistential(v)) {
		final OWLObjectSomeValuesFrom exist = (OWLObjectSomeValuesFrom) v;
		final OWLObjectPropertyExpression prop = exist.getProperty();
		final OWLObjectPropertyExpression invProp = prop
			.getInverseProperty().getSimplified();
		final OWLDataFactory dataFactory = ontology
			.getOriginalOntology().getOWLOntologyManager()
			.getOWLDataFactory();
		final OWLObjectSomeValuesFrom invExist = dataFactory
			.getOWLObjectSomeValuesFrom(invProp,
				dataFactory.getOWLThing());
		if (s2.contains(invExist))
		    result.add(prop.getNamedProperty());
	    }
	return result;
    }

    public Set<OWLClassExpression> getAncestors(OWLClassExpression v) {
	return conceptsClosure.getAncestors(v);
    }

    public Set<OWLPropertyExpression<?, ?>> getAncestors(
	    OWLPropertyExpression<?, ?> v) {
	return rolesClosure.getAncestors(v);
    }

    @Override
    public Set<OWLObjectProperty> getIrreflexiveRoles() {
	if (irreflexiveRoles != null)
	    return irreflexiveRoles;
	irreflexiveRoles = new HashSet<OWLObjectProperty>();
	for (final OWLDisjointClassesAxiom axiom : ontology
		.getConceptDisjunctions())
	    for (final OWLDisjointClassesAxiom disjoint : axiom
		    .asPairwiseAxioms()) {
		final List<OWLClassExpression> concepts = disjoint
			.getClassExpressionsAsList();
		final OWLClassExpression c1 = concepts.get(0);
		final OWLClassExpression c2 = concepts.get(1);
		Set<OWLClassExpression> c1Ancs = getAncestors(c1);
		Set<OWLClassExpression> c2Ancs = getAncestors(c2);
		if (c1Ancs == null)
		    c1Ancs = new HashSet<OWLClassExpression>();
		if (c2Ancs == null)
		    c2Ancs = new HashSet<OWLClassExpression>();
		c1Ancs.add(c1);
		c2Ancs.add(c2);
		final Set<OWLObjectProperty> intersection = conceptInverseIntersection(
			c1Ancs, c2Ancs);
		irreflexiveRoles.addAll(intersection);
	    }
	for (final OWLNaryPropertyAxiom<?> disjPropsAxiom : ontology
		.getRoleDisjunctions()) {
	    final Object[] props = disjPropsAxiom.getProperties().toArray();
	    for (int i = 0; i < props.length; i++) {
		final OWLPropertyExpression<?, ?> q1 = (OWLPropertyExpression<?, ?>) props[i];
		for (int j = 0; j < props.length; j++)
		    if (i != j) {
			final OWLPropertyExpression<?, ?> q2 = (OWLPropertyExpression<?, ?>) props[j];
			Set<OWLPropertyExpression<?, ?>> q1Ancs = getAncestors(q1);
			Set<OWLPropertyExpression<?, ?>> q2Ancs = getAncestors(q2);
			if (q1Ancs == null)
			    q1Ancs = new HashSet<OWLPropertyExpression<?, ?>>();
			if (q2Ancs == null)
			    q2Ancs = new HashSet<OWLPropertyExpression<?, ?>>();
			q1Ancs.add(q1);
			q2Ancs.add(q2);
			final Set<OWLObjectProperty> intersection = roleInverseIntersection(
				q1Ancs, q2Ancs);
			irreflexiveRoles.addAll(intersection);
		    }
	    }
	}
	return irreflexiveRoles;
    }

    public Set<OWLClassExpression> getPredecessors(OWLClassExpression v) {
	return conceptsPredecessors.get(v);
    }

    public Set<OWLPropertyExpression<?, ?>> getPredecessors(
	    OWLObjectPropertyExpression v) {
	return rolesPredecessor.get(v);
    }

    // TODO refactor
    @Override
    public Set<OWLEntity> getUnsatisfiableEntities() {
	if (unsatisfiableEntities != null)
	    return unsatisfiableEntities;
	final Set<OWLClassExpression> unsatisfiableConcepts = new HashSet<OWLClassExpression>();
	for (final OWLDisjointClassesAxiom axiom : ontology
		.getConceptDisjunctions())
	    for (final OWLDisjointClassesAxiom disjoint : axiom
		    .asPairwiseAxioms()) {
		final List<OWLClassExpression> concepts = disjoint
			.getClassExpressionsAsList();
		final OWLClassExpression c1 = concepts.get(0);
		final OWLClassExpression c2 = concepts.get(1);
		Set<OWLClassExpression> c1Ancs = getAncestors(c1);
		Set<OWLClassExpression> c2Ancs = getAncestors(c2);
		if (c1Ancs == null)
		    c1Ancs = new HashSet<OWLClassExpression>();
		if (c2Ancs == null)
		    c2Ancs = new HashSet<OWLClassExpression>();
		c1Ancs.add(c1);
		c2Ancs.add(c2);
		final Set<OWLClassExpression> intersection = intersection(
			c1Ancs, c2Ancs);
		unsatisfiableConcepts.addAll(intersection);
	    }
	for (final OWLClassExpression c : ontology.getUnsatisfiableConcepts()) {
	    Set<OWLClassExpression> cAncs = getAncestors(c);
	    if (cAncs == null)
		cAncs = new HashSet<OWLClassExpression>();
	    cAncs.add(c);
	    unsatisfiableConcepts.addAll(cAncs);
	}
	final Set<OWLPropertyExpression<?, ?>> unsatisfiableRoles = new HashSet<OWLPropertyExpression<?, ?>>();
	for (final OWLNaryPropertyAxiom<?> disjPropsAxiom : ontology
		.getRoleDisjunctions()) {
	    final Object[] props = disjPropsAxiom.getProperties().toArray();
	    for (int i = 0; i < props.length; i++) {
		final OWLPropertyExpression<?, ?> q1 = (OWLPropertyExpression<?, ?>) props[i];
		for (int j = 0; j < props.length; j++)
		    if (i != j) {
			final OWLPropertyExpression<?, ?> q2 = (OWLPropertyExpression<?, ?>) props[j];
			Set<OWLPropertyExpression<?, ?>> q1Ancs = getAncestors(q1);
			Set<OWLPropertyExpression<?, ?>> q2Ancs = getAncestors(q2);
			if (q1Ancs == null)
			    q1Ancs = new HashSet<OWLPropertyExpression<?, ?>>();
			if (q2Ancs == null)
			    q2Ancs = new HashSet<OWLPropertyExpression<?, ?>>();
			q1Ancs.add(q1);
			q2Ancs.add(q2);
			final Set<OWLPropertyExpression<?, ?>> intersection = intersection(
				q1Ancs, q2Ancs);
			unsatisfiableRoles.addAll(intersection);
		    }
	    }
	}
	for (final OWLPropertyExpression<?, ?> q : ontology
		.getUnsatisfiableRoles()) {
	    Set<OWLPropertyExpression<?, ?>> qAncs = getAncestors(q);
	    if (qAncs == null)
		qAncs = new HashSet<OWLPropertyExpression<?, ?>>();
	    qAncs.add(q);
	    unsatisfiableRoles.addAll(qAncs);
	}
	// if (!DLUtils.isInverse(q))
	// unsatisfiableRoles.add(q);
	// else
	// unsatisfiableRoles.add(((OWLObjectPropertyExpression) q)
	// .getNamedProperty());
	return atoms(unsatisfiableConcepts, unsatisfiableRoles);
    }

    private <T> Set<T> intersection(Set<T> s1, Set<T> s2) {
	if (s1.size() >= s2.size()) {
	    s1.retainAll(s2);
	    return s1;
	} else {
	    s2.retainAll(s1);
	    return s2;
	}
    }

    private Set<OWLObjectProperty> roleInverseIntersection(
	    Set<OWLPropertyExpression<?, ?>> s1,
	    Set<OWLPropertyExpression<?, ?>> s2) {
	final Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
	for (final OWLPropertyExpression<?, ?> v : s1)
	    if (v instanceof OWLObjectPropertyExpression) {
		final OWLObjectPropertyExpression vo = (OWLObjectPropertyExpression) v;
		if (s2.contains(vo.getInverseProperty().getSimplified()))
		    result.add(vo.getNamedProperty());
	    }
	return result;
    }

}

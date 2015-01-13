package local.translate.ql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class BasicTBoxGraph implements TBoxGraph {

	private GraphClosure<OWLClassExpression> conceptsClosure;

	private OWLOntology ontology;

	private Map<OWLClassExpression, Set<OWLClassExpression>> conceptsPredecessors;

	private Map<OWLObjectPropertyExpression, Set<OWLObjectPropertyExpression>> rolesPredecessor;

	private GraphClosure<OWLObjectPropertyExpression> rolesClosure;

	private Set<OWLEntity> unsatisfiableEntities;

	public BasicTBoxGraph(OWLOntology ontology) {
		this.ontology = ontology;
		this.conceptsPredecessors = new HashMap<OWLClassExpression, Set<OWLClassExpression>>();
		this.rolesPredecessor = new HashMap<OWLObjectPropertyExpression, Set<OWLObjectPropertyExpression>>();
		for (OWLSubClassOfAxiom axiom : ontology
				.getAxioms(AxiomType.SUBCLASS_OF)) {
			Set<OWLClassExpression> predecessors = conceptsPredecessors
					.get(axiom.getSuperClass());
			if (predecessors == null) {
				predecessors = new HashSet<OWLClassExpression>();
				conceptsPredecessors.put(axiom.getSuperClass(), predecessors);
			}
			predecessors.add(axiom.getSubClass());
		}
		for (OWLSubObjectPropertyOfAxiom axiom : ontology
				.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
			Set<OWLObjectPropertyExpression> predecessors = rolesPredecessor
					.get(axiom.getSuperProperty());
			if (predecessors == null) {
				predecessors = new HashSet<OWLObjectPropertyExpression>();
				rolesPredecessor.put(axiom.getSuperProperty(), predecessors);
			}
			predecessors.add(axiom.getSubProperty());
		}
		this.conceptsClosure = new BasicLazyGraphClosure<OWLClassExpression>(
				conceptsPredecessors);
		this.rolesClosure = new BasicLazyGraphClosure<OWLObjectPropertyExpression>(
				rolesPredecessor);
		this.unsatisfiableEntities = null;
	}

	private OWLEntity atom(OWLClassExpression v) {
		if (DLUtils.isAtomic(v))
			return (OWLClass) v;
		else if (DLUtils.isExistential(v)) {
			OWLObjectSomeValuesFrom existential = (OWLObjectSomeValuesFrom) v;
			return existential.getProperty().getNamedProperty();
		} else
			return null;
	}

	private OWLObjectProperty atom(OWLObjectPropertyExpression v) {
		return v.getNamedProperty();
	}

	private Set<OWLEntity> atoms(Set<OWLClassExpression> unsatisfiableConcepts,
			Set<OWLObjectPropertyExpression> unsatisfiableRoles) {
		Set<OWLEntity> result = new HashSet<OWLEntity>();
		for (OWLClassExpression c : unsatisfiableConcepts)
			result.add(atom(c));
		for (OWLObjectPropertyExpression r : unsatisfiableRoles)
			result.add(atom(r));
		return result;
	}

	public Set<OWLClassExpression> getAncestors(OWLClassExpression v) {
		return conceptsClosure.getAncestors(v);
	}

	public Set<OWLObjectPropertyExpression> getAncestors(
			OWLObjectPropertyExpression v) {
		return rolesClosure.getAncestors(v);
	}

	@Override
	public List<OWLObjectProperty> getIrreflexiveRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLClassExpression> getPredecessors(OWLClassExpression v) {
		return conceptsPredecessors.get(v);
	}

	public Set<OWLObjectPropertyExpression> getPredecessors(
			OWLObjectPropertyExpression v) {
		return rolesPredecessor.get(v);
	}

	@Override
	public Set<OWLEntity> getUnsatisfiableEntities() {
		if (unsatisfiableEntities != null)
			return unsatisfiableEntities;
		Set<OWLClassExpression> unsatisfiableConcepts = new HashSet<OWLClassExpression>();
		for (OWLDisjointClassesAxiom axiom : ontology
				.getAxioms(AxiomType.DISJOINT_CLASSES))
			for (OWLDisjointClassesAxiom disjoint : axiom.asPairwiseAxioms()) {
				List<OWLClassExpression> concepts = disjoint
						.getClassExpressionsAsList();
				OWLClassExpression c1 = concepts.get(0);
				OWLClassExpression c2 = concepts.get(1);
				Set<OWLClassExpression> c1Ancs = getAncestors(c1);
				Set<OWLClassExpression> c2Ancs = getAncestors(c2);
				if (c1Ancs == null)
					c1Ancs = new HashSet<OWLClassExpression>();
				if (c2Ancs == null)
					c2Ancs = new HashSet<OWLClassExpression>();
				c1Ancs.add(c1);
				c2Ancs.add(c2);
				Set<OWLClassExpression> intersection = intersection(c1Ancs,
						c2Ancs);
				unsatisfiableConcepts.addAll(intersection);
			}
		Set<OWLObjectPropertyExpression> unsatisfiableRoles = new HashSet<OWLObjectPropertyExpression>();
		for (OWLDisjointObjectPropertiesAxiom disjPropsAxiom : ontology
				.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
			Object[] props = disjPropsAxiom.getProperties().toArray();
			for (int i = 0; i < props.length; i++) {
				OWLObjectPropertyExpression q1 = (OWLObjectPropertyExpression) props[i];
				for (int j = 0; j < props.length; j++) {
					if (i != j) {
						OWLObjectPropertyExpression q2 = (OWLObjectPropertyExpression) props[j];
						Set<OWLObjectPropertyExpression> q1Ancs = getAncestors(q1);
						Set<OWLObjectPropertyExpression> q2Ancs = getAncestors(q2);
						if (q1Ancs == null)
							q1Ancs = new HashSet<OWLObjectPropertyExpression>();
						if (q2Ancs == null)
							q2Ancs = new HashSet<OWLObjectPropertyExpression>();
						q1Ancs.add(q1);
						q2Ancs.add(q2);
						Set<OWLObjectPropertyExpression> intersection = intersection(
								q1Ancs, q2Ancs);
						unsatisfiableRoles.addAll(intersection);
					}
				}
			}
		}
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

}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

/**
 * @author nunocosta
 *
 */
public class ELReducedOntologyImpl implements ELReducedOntology {

    private static final AxiomType[] UNSUPP_TYPES = new AxiomType[] {
	    AxiomType.REFLEXIVE_OBJECT_PROPERTY,
	    AxiomType.OBJECT_PROPERTY_RANGE, AxiomType.DATA_PROPERTY_DOMAIN,
	AxiomType.DATA_PROPERTY_RANGE, AxiomType.SAME_INDIVIDUAL,
	    AxiomType.DIFFERENT_INDIVIDUALS,
	    AxiomType.FUNCTIONAL_DATA_PROPERTY, AxiomType.HAS_KEY };

    private final Set<OWLSubPropertyChainOfAxiom> chainSubsumptions;

    private final OWLOntology closure;

    private boolean hasDisjunction;

    private final OWLOntology ontology;

    private final Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions;

    public ELReducedOntologyImpl(OWLOntology ontology)
	    throws OWLOntologyCreationException, UnsupportedAxiomTypeException {
	for (final AxiomType type : UNSUPP_TYPES)
	    if (ontology.getAxiomCount(type) > 1)
		throw new UnsupportedAxiomTypeException(type);
	this.ontology = ontology;
	closure = reduce(ontology);
	roleSubsumptions = roleSubsumptions(ontology);
	chainSubsumptions = chainSubsumptions(ontology);
    }

    private OWLClassAssertionAxiom assertion(OWLClassExpression ce,
	    OWLIndividual i) {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLClassAssertionAxiom(ce, i);
    }

    private OWLClass bottom() {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLNothing();
    }

    private Set<OWLSubPropertyChainOfAxiom> chainSubsumptions(
	    OWLOntology ontology) {
	final Set<OWLSubPropertyChainOfAxiom> result = ontology
		.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF);
	for (final OWLTransitiveObjectPropertyAxiom axiom : ontology
		.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY))
	    result.add(norm(axiom));
	return result;
    }

    private void classify(OWLOntology ontology) {
	final OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	final OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	/** Classify the ontology. */
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	final List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
		3);
	generators.add(new InferredSubClassAxiomGenerator());
	generators.add(new InferredClassAssertionAxiomGenerator());
	final InferredOntologyGenerator inferredOntologyGenerator = new InferredOntologyGenerator(
		reasoner, generators);
	inferredOntologyGenerator.fillOntology(
		ontology.getOWLOntologyManager(), ontology);
    }

    private OWLObjectIntersectionOf conj(Set<OWLClassExpression> concepts) {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLObjectIntersectionOf(concepts);
    }

    @Override
    public Set<OWLSubPropertyChainOfAxiom> getChainSubsumptions() {
	return chainSubsumptions;
    }

    @Override
    public Set<OWLClassAssertionAxiom> getConceptAssertions() {
	return closure.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    @Override
    public Set<OWLSubClassOfAxiom> getConceptSubsumptions() {
	return closure.getAxioms(AxiomType.SUBCLASS_OF);
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataAssertion() {
	return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLSubDataPropertyOfAxiom> getDataSubsuptions() {
	return ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY);
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions() {
	return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLSubObjectPropertyOfAxiom> getRoleSubsumptions() {
	return roleSubsumptions;
    }

    @Override
    public boolean hasDisjunction() {
	return hasDisjunction;
    }

    private boolean hasExistential(OWLClassExpression ce) {
	if (ce instanceof OWLClass)
	    return false;
	else if (ce instanceof OWLObjectSomeValuesFrom)
	    return true;
	else if (ce instanceof OWLObjectHasValue)
	    return true;
	else if (ce instanceof OWLObjectIntersectionOf) {
	    for (final OWLClassExpression cei : ce.asConjunctSet())
		if (hasExistential(cei))
		    return true;
	    return false;
	} else
	    return false;
    }

    private boolean isExistential(OWLClassExpression ce) {
	return ce instanceof OWLObjectSomeValuesFrom;
    }

    private OWLClass newConcept() {
	final IRI iri = IRI.generateDocumentIRI();
	final OWLDataFactory df = ontology.getOWLOntologyManager()
		.getOWLDataFactory();
	return df.getOWLClass(iri);
    }

    private OWLSubClassOfAxiom norm(OWLObjectPropertyDomainAxiom axiom) {
	final OWLObjectPropertyExpression ope = axiom.getProperty();
	final OWLClassExpression ce = axiom.getDomain();
	return subsumption(some(ope, top()), ce);
    }

    private OWLSubPropertyChainOfAxiom norm(
	    OWLTransitiveObjectPropertyAxiom axiom) {
	final OWLObjectPropertyExpression ope = axiom.getProperty();
	final List<OWLObjectPropertyExpression> chain = new ArrayList<OWLObjectPropertyExpression>(
		2);
	chain.add(ope);
	chain.add(ope);
	return subsumption(chain, ope);
    }

    private OWLOntology normalized(OWLOntology ontology)
	    throws OWLOntologyCreationException {
	final OWLOntologyManager om = ontology.getOWLOntologyManager();
	final OWLOntology result = om.createOntology();
	final Set<OWLSubClassOfAxiom> conceptSubsumptions = ontology
		.getAxioms(AxiomType.SUBCLASS_OF);
	for (final OWLEquivalentClassesAxiom axiom : ontology
		.getAxioms(AxiomType.EQUIVALENT_CLASSES))
	    conceptSubsumptions.addAll(axiom.asOWLSubClassOfAxioms());
	for (final OWLDisjointClassesAxiom axiom : ontology
		.getAxioms(AxiomType.DISJOINT_CLASSES))
	    conceptSubsumptions.addAll(axiom.asOWLSubClassOfAxioms());
	for (final OWLObjectPropertyDomainAxiom axiom : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN))
	    om.addAxiom(result, norm(axiom));
	final Set<OWLClassAssertionAxiom> conceptAssertions = ontology
		.getAxioms(AxiomType.CLASS_ASSERTION);
	normConceptAssertions(conceptAssertions);
	normConceptSubsumptions(conceptSubsumptions);
	simplify(conceptSubsumptions);
	om.addAxioms(result, conceptAssertions);
	om.addAxioms(result, conceptSubsumptions);
	return result;
    }

    private boolean normComplexSides(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	for (final OWLSubClassOfAxiom axiom : axioms) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    final OWLClassExpression ce2 = axiom.getSuperClass();
	    if (!ce1.isAnonymous() && hasExistential(ce2)) {
		final OWLClass anew = newConcept();
		axioms.remove(axiom);
		axioms.add(subsumption(ce1, anew));
		axioms.add(subsumption(anew, ce2));
		changed = true;
	    }
	}
	return changed;
    }

    private boolean normConceptAssertions(Set<OWLClassAssertionAxiom> assertions) {
	boolean changed = false;
	for (final OWLClassAssertionAxiom assertion : assertions) {
	    final Set<OWLClassExpression> ceConj = assertion
		    .getClassExpression().asConjunctSet();
	    final OWLIndividual i = assertion.getIndividual();
	    if (ceConj.size() > 1) {
		assertions.remove(assertion);
		for (final OWLClassExpression ci : ceConj)
		    if (!ci.isTopEntity())
			assertions.add(assertion(ci, i));
		changed = true;
	    }
	}
	return changed;
    }

    private boolean normConceptSubsumptions(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	do {
	    final boolean n1 = normLeftConjunction(axioms);
	    final boolean n2 = normLeftExistential(axioms);
	    final boolean n3 = normLeftBottom(axioms);
	    changed = n1 || n2 || n3;
	} while (changed);
	do {
	    final boolean n4 = normComplexSides(axioms);
	    final boolean n5 = normRightConjunction(axioms);
	    changed = n4 || n5;
	} while (changed);
	return changed;
    }

    private boolean normLeftBottom(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	for (final OWLSubClassOfAxiom axiom : axioms)
	    if (axiom.getSubClass().isOWLNothing()) {
		axioms.remove(axiom);
		changed = true;
	    }
	return changed;
    }

    private boolean normLeftConjunction(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	for (final OWLSubClassOfAxiom axiom : axioms) {
	    final Set<OWLClassExpression> ce1Conj = axiom.getSubClass()
		    .asConjunctSet();
	    final OWLClassExpression ce2 = axiom.getSuperClass();
	    if (ce1Conj.size() > 1) {
		final Set<OWLClassExpression> normCe1Conj = new HashSet<OWLClassExpression>();
		for (final OWLClassExpression ci : ce1Conj)
		    if (isExistential(ci)) {
			final OWLClass anew = newConcept();
			axioms.add(subsumption(ci, anew));
			normCe1Conj.add(anew);
			changed = true;
		    } else
			normCe1Conj.add(ci);
		if (changed) {
		    axioms.remove(axiom);
		    axioms.add(subsumption(conj(normCe1Conj), ce2));
		}
	    }
	}
	return changed;
    }

    private boolean normLeftExistential(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	for (final OWLSubClassOfAxiom axiom : axioms) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    final OWLClassExpression ce2 = axiom.getSuperClass();
	    if (ce1 instanceof OWLObjectSomeValuesFrom) {
		final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce1;
		final OWLObjectPropertyExpression ope = some.getProperty();
		final Set<OWLClassExpression> fillerConj = some.getFiller()
			.asConjunctSet();
		if (fillerConj.size() > 1) {
		    final Set<OWLClassExpression> normFillerConj = new HashSet<OWLClassExpression>();
		    for (final OWLClassExpression ci : fillerConj)
			if (isExistential(ci)) {
			    final OWLClass anew = newConcept();
			    axioms.add(subsumption(ci, anew));
			    normFillerConj.add(anew);
			    changed = true;
			} else
			    normFillerConj.add(ci);
		    if (changed) {
			axioms.remove(axiom);
			axioms.add(subsumption(some(ope, conj(normFillerConj)),
				ce2));
		    }
		}
	    }
	}
	return changed;
    }

    private boolean normRightConjunction(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	for (final OWLSubClassOfAxiom axiom : axioms) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    final Set<OWLClassExpression> ce2Conj = axiom.getSuperClass()
		    .asConjunctSet();
	    if (ce2Conj.size() > 1) {
		axioms.remove(axiom);
		for (final OWLClassExpression ci : ce2Conj)
		    axioms.add(subsumption(ce1, ci));
		changed = true;
	    }
	}
	return changed;
    }

    private OWLOntology reduce(OWLOntology ontology)
	    throws OWLOntologyCreationException {
	final OWLOntologyManager om = ontology.getOWLOntologyManager();
	final OWLOntology result = normalized(ontology);
	classify(result);
	return result;
    }

    private Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions(
	    OWLOntology ontology) {
	final Set<OWLSubObjectPropertyOfAxiom> result = ontology
		.getAxioms(AxiomType.SUB_OBJECT_PROPERTY);
	for (final OWLEquivalentObjectPropertiesAxiom axiom : ontology
		.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES))
	    result.addAll(axiom.asSubObjectPropertyOfAxioms());
	return result;
    }

    private boolean simplify(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed = false;
	for (final OWLSubClassOfAxiom axiom : axioms) {
	    OWLClassExpression ce1 = axiom.getSubClass();
	    OWLClassExpression ce2 = axiom.getSuperClass();
	    if (ce2.isOWLThing()) {
		axioms.remove(ce2);
		changed = true;
	    } else {
		ce1 = simplifyLefTop(ce1);
		ce2 = simplifyRightBottom(ce2);
		if (ce1 != null && ce2 != null) {
		    axioms.remove(axiom);
		    axioms.add(subsumption(ce1, ce2));
		    changed = true;
		}
	    }
	}
	return changed;
    }

    private OWLClassExpression simplifyLefTop(OWLClassExpression ce) {
	final Set<OWLClassExpression> conjs = new HashSet<OWLClassExpression>(
		ce.asConjunctSet());
	boolean changed = false;
	for (final OWLClassExpression c : ce.asConjunctSet())
	    if (c.isOWLThing()) {
		conjs.remove(c);
		changed = true;
	    }
	if (changed)
	    return conj(conjs);
	else
	    return null;
    }

    private OWLClassExpression simplifyRightBottom(OWLClassExpression ce) {
	for (final OWLClassExpression ci : ce.asConjunctSet())
	    if (ci.isOWLNothing())
		return bottom();
	return null;
    }

    private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression op,
	    OWLClassExpression ce) {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLObjectSomeValuesFrom(op, ce);
    }

    private OWLSubPropertyChainOfAxiom subsumption(
	    List<OWLObjectPropertyExpression> chain,
	    OWLObjectPropertyExpression superProperty) {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLSubPropertyChainOfAxiom(chain, superProperty);
    }

    private OWLSubClassOfAxiom subsumption(OWLClassExpression ce1,
	    OWLClassExpression ce2) {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLSubClassOfAxiom(ce1, ce2);
    }

    private OWLClass top() {
	return ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLThing();
    }

}

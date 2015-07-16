/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * @author nunocosta
 *
 */
public class ELReducedOntologyImpl implements ELReducedOntology {

    private class ComplexSidesNormalizer implements
    Normalizer<OWLSubClassOfAxiom> {

	@Override
	public boolean addNormalization(OWLSubClassOfAxiom axiom,
		Set<OWLSubClassOfAxiom> newAxioms) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    final OWLClassExpression ce2 = axiom.getSuperClass();
	    if (!ce1.isAnonymous() && hasExistential(ce2)) {
		final OWLClass anew = newConcept();
		newAxioms.add(subsumption(ce1, anew));
		newAxioms.add(subsumption(anew, ce2));
		return true;
	    }
	    return false;
	}

    }

    private class ConceptAssertionsNormalizer implements
	    Normalizer<OWLClassAssertionAxiom> {

	@Override
	public boolean addNormalization(OWLClassAssertionAxiom assertion,
		Set<OWLClassAssertionAxiom> newAssertions) {
	    final Set<OWLClassExpression> ceConj = assertion
		    .getClassExpression().asConjunctSet();
	    final OWLIndividual i = assertion.getIndividual();
	    if (ceConj.size() > 1) {
		for (final OWLClassExpression ci : ceConj)
		    if (!ci.isTopEntity())
			newAssertions.add(assertion(ci, i));
		return true;
	    }
	    return false;
	}
    }

    private class LeftBottomNormalizer implements
	    Normalizer<OWLSubClassOfAxiom> {

	@Override
	public boolean addNormalization(OWLSubClassOfAxiom axiom,
		Set<OWLSubClassOfAxiom> newAxioms) {
	    return axiom.getSubClass().isOWLNothing();
	}

    }

    private class LeftConjunctionNormalizer implements
	    Normalizer<OWLSubClassOfAxiom> {

	@Override
	public boolean addNormalization(OWLSubClassOfAxiom axiom,
		Set<OWLSubClassOfAxiom> newAxioms) {
	    boolean changed = false;
	    final Set<OWLClassExpression> ce1Conj = axiom.getSubClass()
		    .asConjunctSet();
	    final OWLClassExpression ce2 = axiom.getSuperClass();
	    if (ce1Conj.size() > 1) {
		final Set<OWLClassExpression> normCe1Conj = new HashSet<OWLClassExpression>();
		for (final OWLClassExpression ci : ce1Conj)
		    if (isExistential(ci)) {
			final OWLClass anew = newConcept();
			newAxioms.add(subsumption(ci, anew));
			normCe1Conj.add(anew);
			changed = true;
		    } else
			normCe1Conj.add(ci);
		if (changed)
		    newAxioms.add(subsumption(conj(normCe1Conj), ce2));
	    }
	    return changed;
	}

    }

    private class LeftExistentialNormalizer implements
    Normalizer<OWLSubClassOfAxiom> {

	@Override
	public boolean addNormalization(OWLSubClassOfAxiom axiom,
		Set<OWLSubClassOfAxiom> newAxioms) {
	    boolean changed = false;
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
			    newAxioms.add(subsumption(ci, anew));
			    normFillerConj.add(anew);
			    changed = true;
			} else
			    normFillerConj.add(ci);
		    if (changed)
			newAxioms.add(subsumption(
				some(ope, conj(normFillerConj)), ce2));
		}
	    }
	    return changed;
	}
    }

    private interface Normalizer<T> {

	boolean addNormalization(T axiom, Set<T> newAxioms);

    }

    private class RightConjunctionNormalizer implements
	    Normalizer<OWLSubClassOfAxiom> {

	@Override
	public boolean addNormalization(OWLSubClassOfAxiom axiom,
		Set<OWLSubClassOfAxiom> newAxioms) {
	    boolean changed = false;
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    final Set<OWLClassExpression> ce2Conj = axiom.getSuperClass()
		    .asConjunctSet();
	    if (ce2Conj.size() > 1) {
		for (final OWLClassExpression ci : ce2Conj)
		    newAxioms.add(subsumption(ce1, ci));
		changed = true;
	    }
	    return changed;
	}
    }

    // private static final AxiomType<?>[] UNSUPP_TYPES = new AxiomType[] {
    // AxiomType.REFLEXIVE_OBJECT_PROPERTY,
    // AxiomType.OBJECT_PROPERTY_RANGE, AxiomType.DATA_PROPERTY_DOMAIN,
    // AxiomType.DATA_PROPERTY_RANGE, AxiomType.SAME_INDIVIDUAL,
    // AxiomType.DIFFERENT_INDIVIDUALS,
    // AxiomType.FUNCTIONAL_DATA_PROPERTY, AxiomType.HAS_KEY };

    private final Set<OWLSubPropertyChainOfAxiom> chainSubsumptions;

    private final OWLOntology closure;

    private final boolean hasDisjunction;

    private final OWLOntology ontology;

    private final Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions;

    public ELReducedOntologyImpl(OWLOntology ontology)
	    throws OWLOntologyCreationException, UnsupportedAxiomTypeException {
	// for (final AxiomType<?> type : UNSUPP_TYPES)
	// if (ontology.getAxiomCount(type) > 1)
	// throw new UnsupportedAxiomTypeException(type);
	this.ontology = ontology;
	final Set<OWLClassAssertionAxiom> conceptAssertions = ontology
		.getAxioms(AxiomType.CLASS_ASSERTION);
	final Set<OWLSubClassOfAxiom> conceptSubsumptions = conceptSubsumptions(ontology);
	roleSubsumptions = roleSubsumptions(ontology);
	closure = reducedOntology(conceptAssertions, conceptSubsumptions,
		roleSubsumptions);
	hasDisjunction = hasDisjunctions(closure);
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
	RuntimesLogger.start("ontology classification");
	Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
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
	inferredOntologyGenerator.fillOntology(ontology.getOWLOntologyManager()
		.getOWLDataFactory(), ontology);
	RuntimesLogger.stop("ontology classification", "loading");
    }

    private Set<OWLSubClassOfAxiom> conceptSubsumptions(OWLOntology ontology) {
	final Set<OWLSubClassOfAxiom> conceptSubsumptions = new HashSet<OWLSubClassOfAxiom>(
		ontology.getAxioms(AxiomType.SUBCLASS_OF));
	for (final OWLEquivalentClassesAxiom axiom : ontology
		.getAxioms(AxiomType.EQUIVALENT_CLASSES))
	    conceptSubsumptions.addAll(axiom.asOWLSubClassOfAxioms());
	for (final OWLDisjointClassesAxiom axiom : ontology
		.getAxioms(AxiomType.DISJOINT_CLASSES))
	    for (final OWLDisjointClassesAxiom ci : axiom.asPairwiseAxioms())
		conceptSubsumptions.add(subsumption(
			conj(ci.getClassExpressions()), bottom()));
	for (final OWLObjectPropertyDomainAxiom axiom : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN))
	    conceptSubsumptions.add(norm(axiom));
	return conceptSubsumptions;
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

    private boolean hasDisjunctions(OWLOntology closure) {
	for (final OWLSubClassOfAxiom axiom : closure
		.getAxioms(AxiomType.SUBCLASS_OF))
	    for (final OWLClassExpression ci : axiom.getSuperClass()
		    .asConjunctSet())
		if (ci.isOWLNothing())
		    return true;
	return false;
    }

    private boolean hasExistential(OWLClassExpression ce) {
	for (final OWLClassExpression cei : ce.asConjunctSet())
	    if (isExistential(cei))
		return true;
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

    private void norm(Set<OWLSubClassOfAxiom> axioms) {
	boolean changed1;
	boolean changed2 = false;
	boolean first = true;
	do {
	    changed1 = norm(axioms, new LeftConjunctionNormalizer());
	    if (first || changed1)
		changed2 = norm(axioms, new LeftExistentialNormalizer());
	    first = false;
	} while (changed1 || changed2);
	norm(axioms, new LeftBottomNormalizer());
	norm(axioms, new ComplexSidesNormalizer());
	norm(axioms, new RightConjunctionNormalizer());
    }

    private <T> boolean norm(Set<T> axioms, Normalizer<T> normalizer) {
	boolean changed = false;
	final Iterator<T> axiomsIt = axioms.iterator();
	final Set<T> newAxioms = new HashSet<T>();
	while (axiomsIt.hasNext()) {
	    final T axiom = axiomsIt.next();
	    final boolean hasChange = normalizer.addNormalization(axiom,
		    newAxioms);
	    if (hasChange) {
		axiomsIt.remove();
		changed = true;
	    }
	}
	if (changed)
	    axioms.addAll(newAxioms);
	return changed;
    }

    private OWLOntology normalized(
	    Set<OWLClassAssertionAxiom> conceptAssertions,
	    Set<OWLSubClassOfAxiom> conceptSubsumptions,
	    Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions)
		    throws OWLOntologyCreationException {
	final OWLOntologyManager om = ontology.getOWLOntologyManager();
	final OWLOntology result = om.createOntology();
	norm(conceptAssertions, new ConceptAssertionsNormalizer());
	norm(conceptSubsumptions);
	om.addAxioms(result, conceptAssertions);
	om.addAxioms(result, conceptSubsumptions);
	om.addAxioms(result, roleSubsumptions);
	return result;
    }

    private OWLOntology reducedOntology(
	    Set<OWLClassAssertionAxiom> conceptAssertions,
	    Set<OWLSubClassOfAxiom> conceptSubsumptions,
	    Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions)
	    throws OWLOntologyCreationException {
	final OWLOntology result = normalized(conceptAssertions,
		conceptSubsumptions, roleSubsumptions);
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

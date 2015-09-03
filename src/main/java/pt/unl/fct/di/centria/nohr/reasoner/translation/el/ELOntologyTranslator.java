package pt.unl.fct.di.centria.nohr.reasoner.translation.el;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link OntologyTranslator} for the {@link Profile#OWL2_EL EL} profile, according to {@link <a>A Correct EL Oracle for NoHR
 * (Technical Report)</a>}.
 *
 * @author Nuno Costa
 */
public class ELOntologyTranslator extends OntologyTranslatorImplementor {

	/** The {@link ELAxiomsTranslator} that obtain the double rules of this {@link OntologyTranslator}. */
	private final ELDoubleAxiomsTranslator doubleAxiomsTranslator;

	/** The {@link ELAxiomsTranslator} that obtain the original rules of this {@link OntologyTranslator}. */
	private final ELOriginalAxiomsTranslator originalAxiomsTranslator;

	/** The {@link ELOntologyReduction reduction} of the ontology that this translation refer. */
	private ELOntologyReduction reducedOntology;

	/**
	 * Constructs an {@link OntologyTranslator} of a given OWL 2 EL ontology.
	 *
	 * @param ontology
	 *            an OWL 2 EL ontology.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} contains some axioms of unsupported types.
	 */
	public ELOntologyTranslator(OWLOntology ontology, Vocabulary v, DeductiveDatabase dedutiveDatabase)
			throws UnsupportedAxiomsException {
		super(ontology, v, dedutiveDatabase);
		originalAxiomsTranslator = new ELOriginalAxiomsTranslator(v);
		doubleAxiomsTranslator = new ELDoubleAxiomsTranslator(v);
		RuntimesLogger.start("ontology reduction");
		reducedOntology = new ELOntologyReductionImpl(ontology, v);
		RuntimesLogger.stop("ontology reduction", "loading");
	}

	@Override
	public Profile getProfile() {
		return Profile.OWL2_EL;
	}

	@Override
	public boolean hasDisjunctions() {
		return reducedOntology.hasDisjunctions();
	}

	private void prepareUpdate() throws UnsupportedAxiomsException {
		reducedOntology = new ELOntologyReductionImpl(ontology, vocabulary);
	}

	/**
	 * Translate the ontology that this ontology refers with a given {@link ELAxiomsTranslator}. The resulting translation is added to {@code rules}.
	 *
	 * @param axiomTranslator
	 *            the {@link ELAxiomsTranslator} that will be used.
	 */
	private void translate(ELAxiomsTranslator axiomTranslator) {
		for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology.chainSubsumptions())
			translation.addAll(axiomTranslator.translation(axiom));
		for (final OWLClassAssertionAxiom assertion : reducedOntology.conceptAssertions())
			translation.addAll(axiomTranslator.translation(assertion));
		for (final OWLSubClassOfAxiom axiom : reducedOntology.conceptSubsumptions())
			translation.addAll(axiomTranslator.translation(axiom));
		for (final OWLDataPropertyAssertionAxiom assertion : reducedOntology.dataAssertion())
			translation.addAll(axiomTranslator.translation(assertion));
		for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.dataSubsuptions())
			translation.addAll(axiomTranslator.translation(axiom));
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.roleSubsumptions())
			translation.addAll(axiomTranslator.translation(axiom));
		for (final OWLObjectPropertyAssertionAxiom assertion : reducedOntology.roleAssertions())
			translation.addAll(axiomTranslator.translation(assertion));
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.roleSubsumptions())
			translation.addAll(axiomTranslator.translation(axiom));
	}

	@Override
	public void updateTranslation() throws UnsupportedAxiomsException {
		prepareUpdate();
		translation.clear();
		RuntimesLogger.start("ontology translation");
		translate(originalAxiomsTranslator);
		if (reducedOntology.hasDisjunctions())
			translate(doubleAxiomsTranslator);
		RuntimesLogger.stop("ontology translation", "loading");
	}
}

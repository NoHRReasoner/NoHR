package pt.unl.fct.di.centria.nohr.reasoner.translation.el;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link OntologyTranslator} for the {@link Profile#OWL2_EL EL} profile, according to {@link <a>A Correct EL Oracle for NoHR
 * (Technical Report)</a>}.
 *
 * @author Nuno Costa
 */
public class ELOntologyTranslation extends OntologyTranslatorImplementor {

	/** The {@link ELAxiomsTranslator} that obtain the double rules of this {@link OntologyTranslator}. */
	private final ELDoubleAxiomsTranslator doubleAxiomsTranslator;

	/** The {@link ELAxiomsTranslator} that obtain the original rules of this {@link OntologyTranslator}. */
	private final ELOriginalAxiomsTranslator originalAxiomsTranslator;

	/** The {@link ELOntologyReduction reduction} of the ontology that this translation refer. */
	private final ELOntologyReduction reducedOntology;

	/**
	 * Constructs an {@link OntologyTranslator} of a given OWL 2 EL ontology.
	 *
	 * @param ontology
	 *            an OWL 2 EL ontology.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} contains some axioms of unsupported types.
	 */
	public ELOntologyTranslation(OWLOntology ontology, DeductiveDatabaseManager dedutiveDatabase)
			throws UnsupportedAxiomsException {
		super(ontology, dedutiveDatabase);
		originalAxiomsTranslator = new ELOriginalAxiomsTranslator();
		doubleAxiomsTranslator = new ELDoubleAxiomsTranslator();
		RuntimesLogger.start("ontology reduction");
		reducedOntology = new ELOntologyReductionImpl(ontology);
		RuntimesLogger.stop("ontology reduction", "loading");
		translate();
	}

	@Override
	protected void execute() {
		RuntimesLogger.start("ontology translation");
		translate(originalAxiomsTranslator);
		if (reducedOntology.hasDisjunctions())
			translate(doubleAxiomsTranslator);
		RuntimesLogger.stop("ontology translation", "loading");
	}

	@Override
	public Profile getProfile() {
		return Profile.OWL2_EL;
	}

	@Override
	public boolean hasDisjunctions() {
		return reducedOntology.hasDisjunctions();
	}

	/**
	 * Translate the ontology that this ontology refers with a given {@link ELAxiomsTranslator}. The resulting translation is added to {@code rules}.
	 *
	 * @param axiomTranslator
	 *            the {@link ELAxiomsTranslator} that will be used.
	 */
	private void translate(ELAxiomsTranslator axiomTranslator) {
		for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology.getChainSubsumptions())
			addAll(axiomTranslator.translation(axiom));
		for (final OWLClassAssertionAxiom assertion : reducedOntology.getConceptAssertions())
			addAll(axiomTranslator.translation(assertion));
		for (final OWLSubClassOfAxiom axiom : reducedOntology.getConceptSubsumptions())
			addAll(axiomTranslator.translation(axiom));
		for (final OWLDataPropertyAssertionAxiom assertion : reducedOntology.getDataAssertion())
			addAll(axiomTranslator.translation(assertion));
		for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.getDataSubsuptions())
			addAll(axiomTranslator.translation(axiom));
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions())
			addAll(axiomTranslator.translation(axiom));
		for (final OWLObjectPropertyAssertionAxiom assertion : reducedOntology.getRoleAssertions())
			addAll(axiomTranslator.translation(assertion));
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions())
			addAll(axiomTranslator.translation(axiom));
	}
}

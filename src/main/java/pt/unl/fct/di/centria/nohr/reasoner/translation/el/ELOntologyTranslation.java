package pt.unl.fct.di.centria.nohr.reasoner.translation.el;

import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
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
	public ELOntologyTranslation(OWLOntology ontology, DedutiveDatabaseManager dedutiveDatabase)
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
	protected void computeNegativeHeadFunctors() {
		for (final OWLSubClassOfAxiom axiom : reducedOntology.getConceptSubsumptions())
			if (!axiom.getSuperClass().isOWLThing()) {
				final OWLClassExpression ce1 = axiom.getSubClass();
				assert ce1 instanceof OWLClass ? ce1.asOWLClass().getIRI().getFragment() != null : true : axiom;
				for (final Literal b : doubleAxiomsTranslator.tr(ce1, var()))
					negativeHeadFunctors.add(doubleAxiomsTranslator.negTr(b).getFunctor());
			}
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions()) {
			final OWLObjectProperty op1 = axiom.getSubProperty().asOWLObjectProperty();
			negativeHeadFunctors.add(doubleAxiomsTranslator.tr(op1, var(), var()).getFunctor());
		}
		for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.getDataSubsuptions()) {
			final OWLDataProperty op1 = axiom.getSubProperty().asOWLDataProperty();
			negativeHeadFunctors.add(doubleAxiomsTranslator.tr(op1, var(), var()).getFunctor());
		}
		for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology.getChainSubsumptions()) {
			final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
			for (final Literal r : doubleAxiomsTranslator.tr(chain, var(), var()))
				negativeHeadFunctors.add(doubleAxiomsTranslator.negTr(r).getFunctor());
		}
	}

	@Override
	protected void computeRules() {
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
			rules.addAll(axiomTranslator.translation(axiom));
		for (final OWLClassAssertionAxiom assertion : reducedOntology.getConceptAssertions())
			rules.addAll(axiomTranslator.translation(assertion));
		for (final OWLSubClassOfAxiom axiom : reducedOntology.getConceptSubsumptions())
			rules.addAll(axiomTranslator.translation(axiom));
		for (final OWLDataPropertyAssertionAxiom assertion : reducedOntology.getDataAssertion())
			rules.addAll(axiomTranslator.translation(assertion));
		for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.getDataSubsuptions())
			rules.addAll(axiomTranslator.translation(axiom));
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions())
			rules.addAll(axiomTranslator.translation(axiom));
		for (final OWLObjectPropertyAssertionAxiom assertion : reducedOntology.getRoleAssertions())
			rules.addAll(axiomTranslator.translation(assertion));
		for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions())
			rules.addAll(axiomTranslator.translation(axiom));
	}
}

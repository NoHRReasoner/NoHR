package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.io.IOException;
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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.Profiles;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class ELOntologyTranslation extends AbstractOntologyTranslation {

    private final ELDoubleAxiomsTranslator doubleAxiomsTranslator;

    private final ELOriginalAxiomsTranslator originalAxiomsTranslator;

    private final ELReducedOntology reducedOntology;

    public ELOntologyTranslation(OWLOntology ontology, OntologyLabeler ontologyLabel)
	    throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, CloneNotSupportedException,
	    UnsupportedOWLProfile, UnsupportedAxiomTypeException {
	super(ontology);
	originalAxiomsTranslator = new ELOriginalAxiomsTranslator(ontology);
	doubleAxiomsTranslator = new ELDoubleAxiomsTranslator(ontology);
	RuntimesLogger.start("ontology reduction");
	reducedOntology = new ELReducedOntologyImpl(ontology);
	RuntimesLogger.stop("ontology reduction", "loading");
	translate();
    }

    @Override
    protected void computeNegativeHeadsPredicates() {
	for (final OWLSubClassOfAxiom axiom : reducedOntology.getConceptSubsumptions()) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    assert ce1 instanceof OWLClass ? ce1.asOWLClass().getIRI().getFragment() != null : true : axiom;
	    for (final Literal b : doubleAxiomsTranslator.tr(ce1, var()))
		negativeHeadsPredicates.add(doubleAxiomsTranslator.negTr(b).getPredicate());
	}
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions()) {
	    final OWLObjectProperty op1 = axiom.getSubProperty().asOWLObjectProperty();
	    negativeHeadsPredicates.add(doubleAxiomsTranslator.tr(op1, var(), var()).getPredicate());
	}
	for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.getDataSubsuptions()) {
	    final OWLDataProperty op1 = axiom.getSubProperty().asOWLDataProperty();
	    negativeHeadsPredicates.add(doubleAxiomsTranslator.tr(op1, var(), var()).getPredicate());
	}
	for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology.getChainSubsumptions()) {
	    final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
	    for (final Literal r : doubleAxiomsTranslator.tr(chain, var(), var()))
		negativeHeadsPredicates.add(doubleAxiomsTranslator.negTr(r).getPredicate());
	}
    }

    @Override
    protected void computeRules() {
	RuntimesLogger.start("ontology translation");
	translate(originalAxiomsTranslator);
	if (reducedOntology.hasDisjunction())
	    translate(doubleAxiomsTranslator);
	RuntimesLogger.end("ontology translation", "loading");
    }

    @Override
    public Profiles getProfile() {
	return Profiles.OWL2_EL;
    }

    @Override
    public boolean hasDisjunctions() {
	return reducedOntology.hasDisjunction();
    }

    private void translate(AbstractELAxiomsTranslator axiomTranslator) {
	for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology.getChainSubsumptions())
	    rules.addAll(axiomTranslator.translate(axiom));
	for (final OWLClassAssertionAxiom assertion : reducedOntology.getConceptAssertions())
	    rules.addAll(axiomTranslator.translate(assertion));
	for (final OWLSubClassOfAxiom axiom : reducedOntology.getConceptSubsumptions())
	    rules.addAll(axiomTranslator.translate(axiom));
	for (final OWLDataPropertyAssertionAxiom assertion : reducedOntology.getDataAssertion())
	    rules.addAll(axiomTranslator.translate(assertion));
	for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.getDataSubsuptions())
	    rules.addAll(axiomTranslator.translate(axiom));
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions())
	    rules.addAll(axiomTranslator.translate(axiom));
	for (final OWLObjectPropertyAssertionAxiom assertion : reducedOntology.getRoleAssertions())
	    rules.addAll(axiomTranslator.translate(assertion));
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.getRoleSubsumptions())
	    rules.addAll(axiomTranslator.translate(axiom));
    }
}

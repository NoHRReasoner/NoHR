package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class ELOntologyTranslator extends AbstractOntologyTranslator {

    private final ELDoubleAxiomsTranslator doubleAxiomsTranslator;

    private final ELOriginalAxiomsTranslator originalAxiomsTranslator;

    private final ELReducedOntology reducedOntology;

    public ELOntologyTranslator(OWLOntologyManager ontologyManager,
	    OWLOntology ontology, OntologyLabeler ontologyLabel)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
		    IOException, CloneNotSupportedException, UnsupportedOWLProfile,
	    UnsupportedAxiomTypeException {
	super(ontologyManager, ontology);
	RuntimesLogger.start("ontology reduction");
	reducedOntology = new ELReducedOntologyImpl(ontology);
	RuntimesLogger.stop("ontology reduction", "loading");
	originalAxiomsTranslator = new ELOriginalAxiomsTranslator(ontology);
	doubleAxiomsTranslator = new ELDoubleAxiomsTranslator(ontology);
    }

    private void computeNegHeads() {
	for (final OWLSubClassOfAxiom axiom : reducedOntology
		.getConceptSubsumptions()) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    for (final Literal b : doubleAxiomsTranslator.tr(ce1, var()))
		negatedPredicates.add(doubleAxiomsTranslator.negTr(b)
			.getPredicate().getName());
	}
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology
		.getRoleSubsumptions()) {
	    final OWLObjectProperty op1 = axiom.getSubProperty()
		    .asOWLObjectProperty();
	    negatedPredicates.add(doubleAxiomsTranslator.tr(op1, var(), var())
		    .toString());
	}
	for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology
		.getDataSubsuptions()) {
	    final OWLDataProperty op1 = axiom.getSubProperty()
		    .asOWLDataProperty();
	    negatedPredicates.add(doubleAxiomsTranslator.tr(op1, var(), var())
		    .toString());
	}
	for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology
		.getChainSubsumptions()) {
	    final List<OWLObjectPropertyExpression> chain = axiom
		    .getPropertyChain();
	    for (final Literal r : doubleAxiomsTranslator.tr(chain, var(),
		    var()))
		negatedPredicates.add(doubleAxiomsTranslator.negTr(r)
			.getPredicate().getName());
	}
    }

    @Override
    public Set<Rule> getTranslation() {
	final Set<Rule> result = new HashSet<Rule>();
	if (reducedOntology.hasDisjunction())
	    computeNegHeads();
	result.addAll(translation(originalAxiomsTranslator));
	if (reducedOntology.hasDisjunction())
	    result.addAll(translation(doubleAxiomsTranslator));
	for (final Rule rule : result)
	    addTabledPredicates(rule);
	return result;
    }

    @Override
    public boolean hasDisjunctions() {
	return reducedOntology.hasDisjunction();
    }

    private Set<Rule> translation(AbstractELAxiomsTranslator axiomTranslator) {
	final Set<Rule> result = new HashSet<Rule>();
	for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology
		.getChainSubsumptions())
	    result.addAll(axiomTranslator.translate(axiom));
	for (final OWLClassAssertionAxiom assertion : reducedOntology
		.getConceptAssertions())
	    result.addAll(axiomTranslator.translate(assertion));
	for (final OWLSubClassOfAxiom axiom : reducedOntology
		.getConceptSubsumptions())
	    result.addAll(axiomTranslator.translate(axiom));
	for (final OWLDataPropertyAssertionAxiom assertion : reducedOntology
		.getDataAssertion())
	    result.addAll(axiomTranslator.translate(assertion));
	for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology
		.getDataSubsuptions())
	    result.addAll(axiomTranslator.translate(axiom));
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology
		.getRoleSubsumptions())
	    result.addAll(axiomTranslator.translate(axiom));
	for (final OWLObjectPropertyAssertionAxiom assertion : reducedOntology
		.getRoleAssertions())
	    result.addAll(axiomTranslator.translate(assertion));
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology
		.getRoleSubsumptions())
	    result.addAll(axiomTranslator.translate(axiom));
	return result;
    }
}

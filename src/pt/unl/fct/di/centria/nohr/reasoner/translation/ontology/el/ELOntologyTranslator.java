package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import other.Utils;
import static pt.unl.fct.di.centria.nohr.model.Model.var;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import utils.Tracer;

public class ELOntologyTranslator extends AbstractOntologyTranslator {

    protected final ELDoubleAxiomsTranslator doubleAxiomsTranslator;

    private final Set<String> negHeads;

    protected final ELOriginalAxiomsTranslator originalAxiomsTranslator;

    private final ELReducedOntology reducedOntology;

    private final Set<String> tabled;

    public ELOntologyTranslator(OWLOntologyManager ontologyManager,
	    OWLOntology ontology, OntologyLabeler ontologyLabel)
		    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    IOException, CloneNotSupportedException, UnsupportedOWLProfile,
		    UnsupportedAxiomTypeException {
	super(ontologyManager, ontology);
	tabled = new HashSet<String>();
	negHeads = new HashSet<String>();
	Tracer.start("ontology reduction");
	reducedOntology = new ELReducedOntologyImpl(ontology);
	Tracer.stop("ontology reduction", "loading");
	originalAxiomsTranslator = new ELOriginalAxiomsTranslator(ontology);
	doubleAxiomsTranslator = new ELDoubleAxiomsTranslator(ontology);
    }

    private void addAll(Set<Rule> set, Set<String> target) {
	for (final Rule rule : set) {
	    addPredicates(rule);
	    target.add(rule.toString());
	}
    }

    private void addPredicates(Rule rule) {
	final Predicate headPred = rule.getHead().getPredicate();
	tabled.add(headPred.getName());
	for (final Literal negLiteral : rule.getNegativeBody())
	    tabled.add(negLiteral.getPredicate().getName());
    }

    private void computeNegHeads() {
	for (final OWLSubClassOfAxiom axiom : reducedOntology
		.getConceptSubsumptions()) {
	    final OWLClassExpression ce1 = axiom.getSubClass();
	    for (final Literal b : doubleAxiomsTranslator.tr(ce1, var()))
		negHeads.add(doubleAxiomsTranslator.negTr(b).getPredicate()
			.getName());
	}
	for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology
		.getRoleSubsumptions()) {
	    final OWLObjectProperty op1 = axiom.getSubProperty()
		    .asOWLObjectProperty();
	    negHeads.add(doubleAxiomsTranslator.tr(op1, var(), var())
		    .toString());
	}
	for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology
		.getDataSubsuptions()) {
	    final OWLDataProperty op1 = axiom.getSubProperty()
		    .asOWLDataProperty();
	    negHeads.add(doubleAxiomsTranslator.tr(op1, var(), var())
		    .toString());
	}
	for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology
		.getChainSubsumptions()) {
	    final List<OWLObjectPropertyExpression> chain = axiom
		    .getPropertyChain();
	    for (final Literal r : doubleAxiomsTranslator.tr(chain, var(),
		    var()))
		negHeads.add(doubleAxiomsTranslator.negTr(r).getPredicate()
			.getName());
	}
    }

    @Override
    public Set<String> getNegatedPredicates() {
	return negHeads;
    }

    @Override
    public Set<String> getTabledPredicates() {
	return tabled;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator
     * #hasDisjunctions()
     */
    @Override
    public boolean hasDisjunctions() {
	return reducedOntology.hasDisjunction();
    }

    private Set<Rule> translate(AbstractELAxiomsTranslator axiomTranslator) {
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

    @Override
    public void translate(Set<String> translationContainer)
	    throws ParserException {
	if (reducedOntology.hasDisjunction())
	    computeNegHeads();
	addAll(translate(originalAxiomsTranslator), translationContainer);
	if (reducedOntology.hasDisjunction())
	    addAll(translate(doubleAxiomsTranslator), translationContainer);
    }
}

package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabel;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.xsb.Rule;

public class QLOntologyTranslator implements OntologyTranslator {

    private TBoxGraph graph;

    private OWLOntologyManager om;

    private INormalizedOntology normalizedOntology;

    private QLAxiomsTranslator ruleCreatorQL;

    private static final String X = "X";
    private static final String Y = "Y";

    public QLOntologyTranslator(INormalizedOntology normalizedOntology,
	    OWLDataFactory dataFactory, OWLOntologyManager ontologyManager,
	    OntologyLabel ol) {
	om = ontologyManager;
	this.normalizedOntology = normalizedOntology;
	graph = new BasicTBoxGraph(normalizedOntology, dataFactory);
	ruleCreatorQL = new QLAxiomsTranslator(ol, normalizedOntology,
		ontologyManager, normalizedOntology.hasDisjointStatement(),
		graph);
    }

    private void addAll(Set<Rule> source, Set<String> target) {
	for (Rule rule : source)
	    target.add(rule.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator
     * #getNegatedPredicates()
     */
    @Override
    public Set<String> getNegatedPredicates() {
	return ruleCreatorQL.getNegatedPredicates();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator
     * #getTabledPredicates()
     */
    @Override
    public Set<String> getTabledPredicates() {
	return ruleCreatorQL.getTabledPredicates();
    }

    @Override
    public boolean proceed(Set<String> translationContainer) {
	boolean hasDisjunctions = normalizedOntology.hasDisjointStatement();
	ruleCreatorQL.computeNegHeads();
	utils.Tracer.start("ontology translation");
	utils.Tracer.info("ruleCreatorQL.translate");
	addAll(translate(), translationContainer);
	utils.Tracer.stop("ontology translation", "loading");
	utils.Tracer.start("ontology classification");
	for (OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		addAll(ruleCreatorQL.i1((OWLClass) e), translationContainer);
	    else if (e instanceof OWLProperty)
		addAll(ruleCreatorQL.i2((OWLProperty) e), translationContainer);
	for (OWLObjectProperty p : graph.getIrreflexiveRoles())
	    addAll(ruleCreatorQL.ir(p), translationContainer);
	utils.Tracer.stop("ontology classification", "loading");
	return hasDisjunctions;
    }

    private Set<Rule> translate() {
	Set<Rule> tr = new HashSet<Rule>();
	for (OWLClassAssertionAxiom f : normalizedOntology
		.getConceptAssertions())
	    tr.addAll(ruleCreatorQL.translate(f));
	for (OWLObjectPropertyAssertionAxiom f : normalizedOntology
		.getRoleAssertions())
	    tr.addAll(ruleCreatorQL.translate(f));
	for (OWLDataPropertyAssertionAxiom f : normalizedOntology
		.getDataAssertions())
	    ruleCreatorQL.translate(f);
	for (OWLSubClassOfAxiom s : normalizedOntology.getConceptSubsumptions())
	    tr.addAll(ruleCreatorQL.translate(s));
	for (OWLDisjointClassesAxiom d : normalizedOntology
		.getConceptDisjunctions())
	    tr.addAll(ruleCreatorQL.translate(d));
	for (OWLSubPropertyAxiom<?> s : normalizedOntology
		.getRoleSubsumptions())
	    if (s instanceof OWLSubObjectPropertyOfAxiom)
		tr.addAll(ruleCreatorQL
			.translate((OWLSubObjectPropertyOfAxiom) s));
	    else if (s instanceof OWLSubDataPropertyOfAxiom)
		tr.addAll(ruleCreatorQL
			.translate((OWLSubDataPropertyOfAxiom) s));
	for (OWLNaryPropertyAxiom<?> d : normalizedOntology
		.getRoleDisjunctions())
	    tr.addAll(ruleCreatorQL.translate(d));
	for (OWLPropertyExpression p : normalizedOntology.getRoles())
	    if (p instanceof OWLObjectPropertyExpression)
		tr.addAll(ruleCreatorQL.e(((OWLObjectPropertyExpression) p)
			.getNamedProperty()));

	return tr;
    }

}

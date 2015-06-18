package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.io.IOException;
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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabel;
import pt.unl.fct.di.centria.nohr.xsb.Rule;
import utils.Tracer;

public class QLOntologyTranslator extends AbstractOntologyTranslator {

    private TBoxGraph graph;

    private INormalizedOntology normalizedOntology;

    private QLAxiomsTranslator ruleCreatorQL;

    public QLOntologyTranslator(OWLOntology ontology,
	    OWLDataFactory dataFactory, OWLOntologyManager ontologyManager,
	    OntologyLabel ol) throws OWLOntologyCreationException,
	    OWLOntologyStorageException, IOException,
	    CloneNotSupportedException, UnsupportedOWLProfile {
	super(ontologyManager, ontology);
	Tracer.start("normalize ontology");
	normalizedOntology = new Normalizer(ontology);
	Tracer.stop("normalize ontology", "loading");
	graph = new BasicTBoxGraph(normalizedOntology, dataFactory);
	ruleCreatorQL = new QLAxiomsTranslator(ol, normalizedOntology,
		ontologyManager, normalizedOntology.hasDisjointStatement(),
		graph);
	hasDisjunctions = normalizedOntology.hasDisjointStatement();
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

    @Override
    public void translate(Set<String> translationContainer) {
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
    }

}

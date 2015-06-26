package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import utils.Tracer;

public class QLOntologyTranslator extends AbstractOntologyTranslator {

    private final QLDoubleAxiomsTranslator doubleAxiomsTranslator;

    private final TBoxGraph graph;

    private Set<String> negatedPredicates;

    private final QLNormalizedOntology normalizedOntology;

    private final QLOriginalAxiomsTranslator originalAxiomsTranslator;

    private Set<String> tabled;

    public QLOntologyTranslator(OWLOntology ontology,
	    OWLDataFactory dataFactory, OWLOntologyManager ontologyManager,
	    OntologyLabeler ol) throws OWLOntologyCreationException,
	    OWLOntologyStorageException, IOException,
	    CloneNotSupportedException, UnsupportedOWLProfile {
	super(ontologyManager, ontology);
	Tracer.start("normalize ontology");
	normalizedOntology = new QLNormalizedOntologyImpl(ontology);
	Tracer.stop("normalize ontology", "loading");
	graph = new BasicTBoxGraph(normalizedOntology, dataFactory);
	originalAxiomsTranslator = new QLOriginalAxiomsTranslator(ontology);
	doubleAxiomsTranslator = new QLDoubleAxiomsTranslator(ontology);
	hasDisjunctions = normalizedOntology.hasDisjointStatement();
    }

    private void addAll(Rule rule, Set<String> target) {
	addPredicates(rule);
	target.add(rule.toString());
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

    public void computeNegHeads() {
	final Variable X = Model.var("X");
	final Variable Y = Model.var("Y");
	negatedPredicates = new HashSet<String>();
	for (final OWLClassExpression b : normalizedOntology.getSubConcepts())
	    negatedPredicates.add(originalAxiomsTranslator.negTr(b, X)
		    .getPredicate().getName());
	for (final OWLClassExpression b : normalizedOntology
		.getDisjointConcepts())
	    negatedPredicates.add(originalAxiomsTranslator.negTr(b, X)
		    .getPredicate().getName());
	for (final OWLPropertyExpression q : normalizedOntology.getSubRoles())
	    negatedPredicates.add(originalAxiomsTranslator.negTr(q, X, Y)
		    .getPredicate().getName());
	for (final OWLPropertyExpression q : normalizedOntology
		.getDisjointRoles())
	    negatedPredicates.add(originalAxiomsTranslator.negTr(q, X, Y)
		    .getPredicate().getName());
	for (final OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		negatedPredicates.add(originalAxiomsTranslator
			.negTr((OWLClass) e, X).getPredicate().getName());
	    else if (e instanceof OWLProperty)
		negatedPredicates.add(originalAxiomsTranslator
			.negTr((OWLProperty) e, X, Y).getPredicate().getName());
	for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
	    negatedPredicates.add(originalAxiomsTranslator.negTr(p, X, Y)
		    .getPredicate().getName());
    }

    @Override
    public Set<String> getNegatedPredicates() {
	return negatedPredicates;
    }

    @Override
    public Set<String> getTabledPredicates() {
	return tabled;
    }

    private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression ope) {
	final OWLDataFactory df = ontology.getOWLOntologyManager()
		.getOWLDataFactory();
	return df.getOWLObjectSomeValuesFrom(ope, df.getOWLThing());
    }

    private Set<Rule> translate(AbstractQLAxiomsTranslator axiomsTranslator) {
	final Set<Rule> result = new HashSet<Rule>();
	for (final OWLClassAssertionAxiom f : normalizedOntology
		.getConceptAssertions())
	    result.addAll(axiomsTranslator.translate(f));
	for (final OWLObjectPropertyAssertionAxiom f : normalizedOntology
		.getRoleAssertions())
	    result.addAll(axiomsTranslator.translate(f));
	for (final OWLDataPropertyAssertionAxiom f : normalizedOntology
		.getDataAssertions())
	    axiomsTranslator.translate(f);
	for (final OWLSubClassOfAxiom s : normalizedOntology
		.getConceptSubsumptions())
	    result.addAll(axiomsTranslator.translate(s));
	for (final OWLDisjointClassesAxiom d : normalizedOntology
		.getConceptDisjunctions())
	    result.addAll(axiomsTranslator.translate(d));
	for (final OWLSubPropertyAxiom<?> s : normalizedOntology
		.getRoleSubsumptions())
	    if (s instanceof OWLSubObjectPropertyOfAxiom) {
		result.addAll(axiomsTranslator.translate(s));
		final OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom) s;
		final OWLObjectPropertyExpression ope1 = axiom.getSubProperty();
		final OWLObjectPropertyExpression ope2 = axiom
			.getSuperProperty();
		final OWLObjectPropertyExpression invOpe1 = ope1
			.getInverseProperty();
		final OWLObjectPropertyExpression invOpe2 = ope2
			.getInverseProperty();
		if ((normalizedOntology.isSuper(some(ope1)) || normalizedOntology
			.isSuper(ope1))
			&& normalizedOntology.isSub(some(ope2))
			|| normalizedOntology.isSub(ope2))
		    result.add(axiomsTranslator.translateDomain(axiom));
		if ((normalizedOntology.isSuper(some(invOpe1)) || normalizedOntology
			.isSuper(invOpe1))
			&& (normalizedOntology.isSub(some(invOpe2)) || normalizedOntology
				.isSub(invOpe2)))
		    result.add(axiomsTranslator.translateRange(axiom));
	    } else if (s instanceof OWLSubDataPropertyOfAxiom)
		result.addAll(axiomsTranslator.translate(s));
	for (final OWLNaryPropertyAxiom d : normalizedOntology
		.getRoleDisjunctions())
	    result.addAll(axiomsTranslator.translate(d));
	for (final OWLPropertyExpression ope : normalizedOntology.getRoles())
	    if (ope instanceof OWLObjectPropertyExpression) {
		final OWLObjectProperty p = ((OWLObjectPropertyExpression) ope)
			.getNamedProperty();
		final OWLObjectPropertyExpression invP = p.getInverseProperty();
		if (normalizedOntology.isSub(some(p))
			|| normalizedOntology.isSub(p))
		    result.add(axiomsTranslator.translateDomain(p));
		if (normalizedOntology.isSub(some(invP))
			|| normalizedOntology.isSub(invP))
		    result.add(axiomsTranslator.translateRange(p));
	    }
	return result;
    }

    @Override
    public void translate(Set<String> translationContainer) {
	tabled = new HashSet<String>();
	final boolean hasDisjunctions = normalizedOntology
		.hasDisjointStatement();
	computeNegHeads();
	utils.Tracer.start("ontology translation");
	utils.Tracer.info("ruleCreatorQL.translate");
	addAll(translate(originalAxiomsTranslator), translationContainer);
	if (hasDisjunctions)
	    addAll(translate(doubleAxiomsTranslator), translationContainer);
	utils.Tracer.stop("ontology translation", "loading");
	utils.Tracer.start("ontology classification");
	for (final OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		addAll(doubleAxiomsTranslator
			.translateUnsatisfaible((OWLClass) e),
			translationContainer);
	    else if (e instanceof OWLProperty)
		addAll(doubleAxiomsTranslator
			.translateUnsatisfaible((OWLProperty) e),
			translationContainer);
	for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
	    addAll(doubleAxiomsTranslator.translateIrreflexive(p),
		    translationContainer);
	utils.Tracer.stop("ontology classification", "loading");
    }

}

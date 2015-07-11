package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
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

import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;

public class QLOntologyTranslator extends AbstractOntologyTranslator {

    private final QLDoubleAxiomsTranslator doubleAxiomsTranslator;

    private final TBoxGraph graph;

    private final boolean hasDisjunctions;

    private final QLNormalizedOntology normalizedOntology;

    private final QLOriginalAxiomsTranslator originalAxiomsTranslator;

    public QLOntologyTranslator(OWLOntology ontology,
	    OWLDataFactory dataFactory, OWLOntologyManager ontologyManager,
	    OntologyLabeler ol) throws OWLOntologyCreationException,
	    OWLOntologyStorageException, IOException,
	    CloneNotSupportedException, UnsupportedOWLProfile {
	super(ontologyManager, ontology);
	normalizedOntology = new QLNormalizedOntologyImpl(ontology);
	graph = new BasicTBoxGraph(normalizedOntology);
	originalAxiomsTranslator = new QLOriginalAxiomsTranslator(ontology);
	doubleAxiomsTranslator = new QLDoubleAxiomsTranslator(ontology);
	hasDisjunctions = normalizedOntology.hasDisjointStatement();
    }

    public void computeNegHeads() {
	final Variable X = Model.var("X");
	final Variable Y = Model.var("Y");
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
    public Set<Rule> getTranslation() {
	final Set<Rule> result = new HashSet<Rule>();
	final boolean hasDisjunctions = normalizedOntology
		.hasDisjointStatement();
	computeNegHeads();
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger
	.start("ontology translation");
	result.addAll(translate(originalAxiomsTranslator));
	if (hasDisjunctions)
	    result.addAll(translate(doubleAxiomsTranslator));
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger.stop(
		"ontology translation", "loading");
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger
	.start("ontology classification");
	for (final OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		result.add(doubleAxiomsTranslator
			.translateUnsatisfaible((OWLClass) e));
	    else if (e instanceof OWLProperty)
		result.add(doubleAxiomsTranslator
			.translateUnsatisfaible((OWLProperty) e));
	for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
	    result.add(doubleAxiomsTranslator.translateIrreflexive(p));
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger.stop(
		"ontology classification", "loading");
	for (final Rule rule : result)
	    addTabledPredicates(rule);
	return result;
    }

    @Override
    public boolean hasDisjunctions() {
	return hasDisjunctions;
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
			&& (normalizedOntology.isSub(some(ope2)) || normalizedOntology
				.isSub(ope2)))
		    result.add(axiomsTranslator.translateDomain(axiom));
		if ((normalizedOntology.isSuper(some(invOpe1)) || normalizedOntology
			.isSuper(invOpe1))
			&& (normalizedOntology.isSub(some(invOpe2)) || normalizedOntology
				.isSub(invOpe2)))
		    result.add(axiomsTranslator.translateRange(axiom));
	    } else if (s instanceof OWLSubDataPropertyOfAxiom)
		result.addAll(axiomsTranslator.translate(s));
	for (final OWLNaryPropertyAxiom<?> d : normalizedOntology
		.getRoleDisjunctions())
	    if (d instanceof OWLDisjointObjectPropertiesAxiom)
		result.addAll(axiomsTranslator
			.translate((OWLDisjointObjectPropertiesAxiom) d));
	    else if (d instanceof OWLDisjointDataPropertiesAxiom)
		result.addAll(axiomsTranslator
			.translate((OWLDisjointDataPropertiesAxiom) d));

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

}

package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.TabExpander;

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
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
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
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import pt.unl.fct.di.centria.nohr.xsb.XsbRule;
import utils.Tracer;

public class QLOntologyTranslator extends AbstractOntologyTranslator {

    private final DoubleAxiomsTranslator doubleAxiomsTranslator;

    private final TBoxGraph graph;

    private final INormalizedOntology normalizedOntology;

    private final OriginalAxiomsTranslator originalAxiomsTranslator;

    private final QLAxiomsTranslator ruleCreatorQL;

    private Set<String> tabled;

    public QLOntologyTranslator(OWLOntology ontology,
	    OWLDataFactory dataFactory, OWLOntologyManager ontologyManager,
	    OntologyLabeler ol) throws OWLOntologyCreationException,
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
	originalAxiomsTranslator = new OriginalAxiomsTranslator(ontology);
	doubleAxiomsTranslator = new DoubleAxiomsTranslator(ontology);
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
	return tabled;
    }

    /**
     * @param p
     * @return
     */
    private boolean hasDomOnLeft(OWLObjectProperty p) {
	// TODO Auto-generated method stub
	return false;
    }

    /**
     * @param p
     * @return
     */
    private boolean hasRanOnLeft(OWLObjectProperty p) {
	// TODO Auto-generated method stub
	return false;
    }

    private Set<XsbRule> translate() {
	final Set<XsbRule> tr = new HashSet<XsbRule>();
	for (final OWLClassAssertionAxiom f : normalizedOntology
		.getConceptAssertions())
	    tr.addAll(ruleCreatorQL.translate(f));
	for (final OWLObjectPropertyAssertionAxiom f : normalizedOntology
		.getRoleAssertions())
	    tr.addAll(ruleCreatorQL.translate(f));
	for (final OWLDataPropertyAssertionAxiom f : normalizedOntology
		.getDataAssertions())
	    ruleCreatorQL.translate(f);
	for (final OWLSubClassOfAxiom s : normalizedOntology
		.getConceptSubsumptions())
	    tr.addAll(ruleCreatorQL.translate(s));
	for (final OWLDisjointClassesAxiom d : normalizedOntology
		.getConceptDisjunctions())
	    tr.addAll(ruleCreatorQL.translate(d));
	for (final OWLSubPropertyAxiom<?> s : normalizedOntology
		.getRoleSubsumptions())
	    if (s instanceof OWLSubObjectPropertyOfAxiom)
		tr.addAll(ruleCreatorQL
			.translate((OWLSubObjectPropertyOfAxiom) s));
	    else if (s instanceof OWLSubDataPropertyOfAxiom)
		tr.addAll(ruleCreatorQL
			.translate((OWLSubDataPropertyOfAxiom) s));
	for (final OWLNaryPropertyAxiom<?> d : normalizedOntology
		.getRoleDisjunctions())
	    tr.addAll(ruleCreatorQL.translate(d));
	for (final OWLPropertyExpression p : normalizedOntology.getRoles())
	    if (p instanceof OWLObjectPropertyExpression)
		tr.addAll(ruleCreatorQL.e(((OWLObjectPropertyExpression) p)
			.getNamedProperty()));

	return tr;
    }

    private Set<Rule> translate(AbstractAxiomsTranslator axiomsTranslator) {
	final Set<Rule> result = new HashSet<Rule>();
	final Set<OWLObjectPropertyExpression> left = new HashSet<OWLObjectPropertyExpression>();
	final Set<OWLObjectPropertyExpression> right = new HashSet<OWLObjectPropertyExpression>();
	for (final OWLClassAssertionAxiom alpha : ontology
		.getAxioms(AxiomType.CLASS_ASSERTION))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLObjectPropertyAssertionAxiom alpha : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLDataPropertyAssertionAxiom alpha : ontology
		.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLSubClassOfAxiom alpha : ontology
		.getAxioms(AxiomType.SUBCLASS_OF)) {
	    result.addAll(axiomsTranslator.translate(alpha));
	    final OWLClassExpression ce1 = alpha.getSubClass();
	    final OWLClassExpression ce2 = alpha.getSuperClass();
	    if (ce1 instanceof OWLObjectSomeValuesFrom) {
		final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce1;
		left.add(some.getProperty());
	    }
	    if (ce2 instanceof OWLObjectSomeValuesFrom) {
		final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce2;
		right.add(some.getProperty());
	    }
	}
	for (final OWLSubObjectPropertyOfAxiom alpha : ontology
		.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
	    result.addAll(axiomsTranslator.translate(alpha));
	    left.add(alpha.getSubProperty());
	    left.add(alpha.getSubProperty().getInverseProperty());
	    right.add(alpha.getSuperProperty());
	    right.add(alpha.getSuperProperty());
	}
	for (final OWLSubDataPropertyOfAxiom alpha : ontology
		.getAxioms(AxiomType.SUB_DATA_PROPERTY))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLDisjointClassesAxiom alpha : ontology
		.getAxioms(AxiomType.DISJOINT_CLASSES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLDisjointObjectPropertiesAxiom alpha : ontology
		.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLDisjointDataPropertiesAxiom alpha : ontology
		.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLEquivalentClassesAxiom alpha : ontology
		.getAxioms(AxiomType.EQUIVALENT_CLASSES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLEquivalentObjectPropertiesAxiom alpha : ontology
		.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLEquivalentDataPropertiesAxiom alpha : ontology
		.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLInverseObjectPropertiesAxiom alpha : ontology
		.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLIrreflexiveObjectPropertyAxiom alpha : ontology
		.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
	    result.addAll(axiomsTranslator.translate(alpha));
	    final OWLObjectPropertyExpression ope = alpha.getProperty();
	    final OWLObjectPropertyExpression invOpe = ope.getInverseProperty();
	    left.add(ope);
	    left.add(invOpe);
	}
	for (final OWLObjectPropertyDomainAxiom alpha : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
	    result.addAll(axiomsTranslator.translate(alpha));
	    left.add(alpha.getProperty());
	}
	for (final OWLObjectPropertyRangeAxiom alpha : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
	    result.addAll(axiomsTranslator.translate(alpha));
	    left.add(alpha.getProperty().getInverseProperty());
	}
	for (final OWLReflexiveObjectPropertyAxiom alpha : ontology
		.getAxioms(AxiomType.REFLEXIVE_OBJECT_PROPERTY)) {
	    result.addAll(axiomsTranslator.translate(alpha));
	    final OWLObjectPropertyExpression ope = alpha.getProperty();
	    final OWLObjectPropertyExpression invOpe = ope.getInverseProperty();
	    left.add(ope);
	    left.add(invOpe);
	    right.add(ope);
	    right.add(invOpe);
	}
	for (final OWLSymmetricObjectPropertyAxiom alpha : ontology
		.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY))
	    result.addAll(axiomsTranslator.translate(alpha));
	for (final OWLSubObjectPropertyOfAxiom alpha : ontology
		.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
	    result.add(axiomsTranslator.translateDomain(alpha));
	    result.add(axiomsTranslator.translateRange(alpha));
	}
	for (final OWLSubObjectPropertyOfAxiom alpha : ontology
		.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
	    final OWLObjectPropertyExpression ope1 = alpha.getSubProperty();
	    final OWLObjectPropertyExpression ope2 = alpha.getSuperProperty();
	    if (right.contains(ope1) && left.contains(ope2))
		result.add(axiomsTranslator.translateDomain(alpha));
	    if (right.contains(ope1.getInverseProperty())
		    && left.contains(ope2.getInverseProperty()))
		result.add(axiomsTranslator.translateRange(alpha));
	}
	for (final OWLObjectPropertyExpression ope : left)
	    if (ope instanceof OWLObjectProperty)
		result.add(axiomsTranslator
			.translateDomain((OWLObjectProperty) ope));
	    else
		result.add(axiomsTranslator.translateRange(ope
			.getNamedProperty()));
	return result;
    }

    @Override
    public void translate(Set<String> translationContainer) {
	tabled = new HashSet<String>();
	final boolean hasDisjunctions = normalizedOntology
		.hasDisjointStatement();
	ruleCreatorQL.computeNegHeads();
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

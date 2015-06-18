package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import other.Utils;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabel;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import utils.Tracer;

public class ELOntologyTranslator extends AbstractOntologyTranslator {

    protected ELAxiomsTranslator ruleCreator;
    private OWLDataFactory owlDataFactory;

    public ELOntologyTranslator(OWLOntologyManager ontologyManager,
	    OWLOntology ontology, OntologyLabel ontologyLabel)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
		    IOException, CloneNotSupportedException, UnsupportedOWLProfile {
	super(ontologyManager, ontology);
	classify();
	if (isOntologyNeedToBeNormalized(ontology))
	    ontology = normalizeOntology(ontology, null);
	ruleCreator = new ELAxiomsTranslator(ontologyLabel, hasDisjunctions);
    }

    private void classify() throws OWLOntologyCreationException {
	Tracer.start("ontology classification");
	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	/** Classify the ontology. */
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
		3);
	generators.add(new InferredSubClassAxiomGenerator());
	generators.add(new InferredEquivalentClassAxiomGenerator());
	generators.add(new InferredClassAssertionAxiomGenerator());
	InferredOntologyGenerator inferredOntologyGenerator = new InferredOntologyGenerator(
		reasoner, generators);
	inferredOntologyGenerator.fillOntology(ontologyManager, ontology);
	Tracer.stop("ontology classification", "loading");
    }

    private Set<OWLAxiom> createNormalizedAxioms(OWLClassExpression superClass,
	    OWLClassExpression subClass) {
	Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
	if (hasOwlClassExpressionAnyExists(subClass)) {
	    IRI iri = IRI.generateDocumentIRI();
	    OWLClass tempClass = owlDataFactory.getOWLClass(iri);
	    axioms.add(owlDataFactory.getOWLSubClassOfAxiom(superClass,
		    tempClass));
	    axioms.add(owlDataFactory
		    .getOWLSubClassOfAxiom(tempClass, subClass));
	}
	return axioms;
    }

    /**
     * Going throw all ontologies and preprocess them.
     */
    protected boolean fillExistsOntologiesAndRules() {
	boolean isTopClass;
	hasDisjunctions = false;
	ClassExpressionType expressionType;

	for (OWLClass owlClass : ontology.getClassesInSignature()) {

	    isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();

	    // Going into loop throw all Disjoint classes
	    for (OWLClassExpression owlClassExpression : owlClass
		    .getDisjointClasses(ontology)) {
		hasDisjunctions = true;
		expressionType = owlClassExpression.getClassExpressionType();
		if (expressionType == ClassExpressionType.OWL_CLASS
			&& (owlClassExpression.isOWLThing() || owlClassExpression
				.isOWLNothing()))
		    break;
		if (expressionType == ClassExpressionType.OBJECT_SOME_VALUES_FROM
			&& isTopClass)
		    ruleCreator.writeRuleI3(owlClassExpression);
		else if (expressionType == ClassExpressionType.OBJECT_INTERSECTION_OF
			&& isTopClass)
		    ruleCreator.writeRuleI2(owlClassExpression);
		else if (expressionType == ClassExpressionType.OWL_CLASS
			&& isTopClass)
		    ruleCreator.writeRuleI1(owlClassExpression);
		else if (!isTopClass)
		    ruleCreator.writeNegEquivalentRules(owlClassExpression,
			    owlClass);
	    }
	}

	for (OWLClassAxiom owlClassAxiom : ontology.getGeneralClassAxioms())
	    if (owlClassAxiom.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
		hasDisjunctions = true;
		ruleCreator
		.writeGeneralClassAxiomsWithComplexAssertions(owlClassAxiom);
	    }
	return hasDisjunctions;

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
	return new HashSet<String>();
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
	return ruleCreator.getTabledPredicates();
    }

    private boolean hasOwlClassExpressionAnyExists(
	    OWLClassExpression owlClassExpression) {
	boolean hasAnyExists = false;
	ClassExpressionType expressionType = owlClassExpression
		.getClassExpressionType();

	switch (expressionType) {
	case OBJECT_SOME_VALUES_FROM:
	    hasAnyExists = true;
	    break;
	case OBJECT_INTERSECTION_OF:
	    List<OWLClassExpression> operands = ((OWLObjectIntersectionOf) owlClassExpression)
		    .getOperandsAsList();
	    for (OWLClassExpression operand : operands) {
		hasAnyExists = hasOwlClassExpressionAnyExists(operand);
		if (hasAnyExists == true)
		    break;
	    }
	    break;
	case OWL_CLASS:
	default:
	    hasAnyExists = false;
	    break;
	}
	return hasAnyExists;
    }

    public boolean isOntologyNeedToBeNormalized(OWLOntology ontology) {
	boolean isOntologyNeedToBeNormalized = false;
	for (OWLClassAxiom owlClassAxiom : ontology.getGeneralClassAxioms()) {
	    if (owlClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
		isOntologyNeedToBeNormalized = true;
		break;
	    }
	    if (owlClassAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
		isOntologyNeedToBeNormalized = true;
		break;
	    }
	}
	return isOntologyNeedToBeNormalized;
    }

    /**
     * Going into loop of all classes.
     */
    private void loopThrowAllClasses() {
	boolean isTopClass;
	OWLClassExpression rightPartOfRule;
	List<OWLClassExpression> equivalentClasses;
	OWLClassExpression equivalentClass;

	for (OWLClass owlClass : ontology.getClassesInSignature()) {
	    isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();
	    equivalentClasses = new ArrayList<OWLClassExpression>();

	    if (!isTopClass) {
		for (OWLIndividual individual : owlClass
			.getIndividuals(ontology))
		    ruleCreator.writeRuleA1(individual, owlClass);

		for (OWLClassExpression owlClassExpression : owlClass
			.getSubClasses(ontology))
		    ruleCreator.writeDoubledRules(owlClassExpression, owlClass);
	    }

	    for (OWLEquivalentClassesAxiom equivalentClassesAxiom : ontology
		    .getEquivalentClassesAxioms(owlClass)) {
		List<OWLClassExpression> list = equivalentClassesAxiom
			.getClassExpressionsAsList();
		for (int i = 0; i < list.size(); i++) {
		    equivalentClass = list.get(i);
		    if (!(equivalentClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS && (equivalentClass
			    .isOWLThing() || equivalentClass.isOWLNothing())))
			equivalentClasses.add(equivalentClass);
		}
	    }
	    if (equivalentClasses.size() > 0) {
		equivalentClasses = Utils.removeDuplicates(equivalentClasses);
		for (int i = 0; i < equivalentClasses.size(); i++) {
		    rightPartOfRule = equivalentClasses.get(i);
		    if (!isTopClass)
			if (rightPartOfRule.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
			    if (!owlClass.equals(rightPartOfRule))
				ruleCreator.writeRuleC1(rightPartOfRule,
					owlClass, false);
			} else
			    ruleCreator.writeEquivalentRule(owlClass,
				    rightPartOfRule);
		}
	    }
	}
	for (OWLClassAxiom owlClassAxiom : ontology.getGeneralClassAxioms()) {
	    if (owlClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF)
		ruleCreator.writeGeneralClassAxiomsSubClasses(owlClassAxiom);
	    if (owlClassAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES)
		ruleCreator.writeGeneralClassAxiomsEquivClasses(owlClassAxiom);
	}
    }

    /**
     * Loop throw all properties.
     */
    private void loopThrowAllProperties() {
	for (OWLObjectProperty objectProperty : ontology
		.getObjectPropertiesInSignature()) {

	    for (OWLAxiom axiom : objectProperty.getReferencingAxioms(ontology))
		if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION)
		    ruleCreator.writeRuleA2(axiom);
	    for (OWLObjectPropertyExpression objectPropertyExpression : objectProperty
		    .getSubProperties(ontology))
		ruleCreator.writeRuleR1(objectPropertyExpression,
			objectProperty);
	}
	for (OWLAxiom axiom : ontology
		.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF))
	    ruleCreator.writeRuleR2(axiom);

	/**
	 * Translate all data property assertions by looping through all
	 * individuals, find for each of them all data properties and for each
	 * such data property all property values; then translate each single
	 * obtained axiom
	 */
	for (OWLNamedIndividual individual : ontology
		.getIndividualsInSignature())
	    for (OWLDataProperty dataProperty : ontology
		    .getDataPropertiesInSignature())
		for (OWLLiteral literal : individual.getDataPropertyValues(
			dataProperty, ontology))
		    ruleCreator.translateDataPropertyAssertion(dataProperty,
			    individual, literal);
    }

    public OWLOntology normalizeOntology(OWLOntology ontology,
	    OWLOntologyManager owlOntologyManager)
	    throws OWLOntologyCreationException, OWLOntologyStorageException {
	if (owlOntologyManager == null)
	    owlOntologyManager = OWLManager.createOWLOntologyManager();
	Set<OWLOntology> _onts = new HashSet<OWLOntology>();
	_onts.add(ontology);
	OWLOntology owlOntology = owlOntologyManager.createOntology(
		IRI.generateDocumentIRI(), _onts);
	owlDataFactory = owlOntologyManager.getOWLDataFactory();
	Set<OWLAxiom> axiomsToBeAdded;
	OWLClassExpression superClassExpression;
	OWLClassExpression subClassExpression;
	List<OWLClassExpression> equivalentClassExpressions;
	for (OWLClassAxiom owlClassAxiom : owlOntology.getGeneralClassAxioms()) {
	    axiomsToBeAdded = new HashSet<OWLAxiom>();
	    if (owlClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF)
		axiomsToBeAdded.addAll(createNormalizedAxioms(
			((OWLSubClassOfAxiomImpl) owlClassAxiom).getSubClass(),
			((OWLSubClassOfAxiomImpl) owlClassAxiom)
				.getSuperClass()));
	    if (owlClassAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
		equivalentClassExpressions = ((OWLEquivalentClassesAxiomImpl) owlClassAxiom)
			.getClassExpressionsAsList();
		if (equivalentClassExpressions.size() >= 2) {
		    superClassExpression = equivalentClassExpressions.get(0);
		    subClassExpression = equivalentClassExpressions.get(1);
		    axiomsToBeAdded.addAll(createNormalizedAxioms(
			    superClassExpression, subClassExpression));
		    axiomsToBeAdded.addAll(createNormalizedAxioms(
			    subClassExpression, superClassExpression));
		}
	    }
	    if (axiomsToBeAdded.size() > 0) {
		for (OWLAxiom axiom : axiomsToBeAdded)
		    owlOntologyManager.addAxiom(owlOntology, axiom);
		owlOntologyManager.removeAxiom(owlOntology, owlClassAxiom);
	    }
	}
	return owlOntology;
    }

    /**
     * Main function.
     *
     * @throws org.semanticweb.owlapi.expression.ParserException
     *             the parser exception
     */
    @Override
    public void translate(Set<String> translationContainer)
	    throws ParserException {

	ruleCreator.setTranslationContainer(translationContainer);

	Tracer.start("ontology translation");
	Date date1 = new Date();
	// setProgressLabelText("Rule translation");
	boolean hasDisjunctions = fillExistsOntologiesAndRules();
	// Utils.getDiffTime(date1, "Preprocessing DisjointWith axioms: ");
	date1 = new Date();
	loopThrowAllClasses();
	// Utils.getDiffTime(date1, "Processing classes: ");
	date1 = new Date();
	loopThrowAllProperties();
	// Utils.getDiffTime(date1, "Processing properties: ");
	Tracer.stop("ontology translation", "loading");
    }
}

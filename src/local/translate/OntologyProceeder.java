package local.translate;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

import java.util.*;

public class OntologyProceeder {
    private CollectionsManager cm;
    protected List<OWLOntology> ontologies = new ArrayList<OWLOntology>();
    protected RuleCreator ruleCreator;
    private OWLDataFactory owlDataFactory;
    public OntologyProceeder(CollectionsManager _cm, RuleCreator _ruleCreator) {
        cm = _cm;
        ruleCreator = _ruleCreator;
    }

    public void setOntologiesToProceed(List<OWLOntology> _ontologies) {
        ontologies = _ontologies;
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


    public OWLOntology normalizeOntology(OWLOntology ontology, OWLOntologyManager owlOntologyManager) throws OWLOntologyCreationException, OWLOntologyStorageException {
        if (owlOntologyManager == null) {
            owlOntologyManager = OWLManager.createOWLOntologyManager();
        }
        Set<OWLOntology> _onts = new HashSet<OWLOntology>();
        _onts.add(ontology);
        OWLOntology owlOntology = owlOntologyManager.createOntology(IRI.generateDocumentIRI(), _onts);
        owlDataFactory = owlOntologyManager.getOWLDataFactory();
        Set<OWLAxiom> axiomsToBeAdded;
        OWLClassExpression superClassExpression;
        OWLClassExpression subClassExpression;
        List<OWLClassExpression> equivalentClassExpressions;
        for (OWLClassAxiom owlClassAxiom : owlOntology.getGeneralClassAxioms()) {
            axiomsToBeAdded = new HashSet<OWLAxiom>();
            if (owlClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
                axiomsToBeAdded.addAll(createNormalizedAxioms(((OWLSubClassOfAxiomImpl) owlClassAxiom).getSubClass(), ((OWLSubClassOfAxiomImpl) owlClassAxiom).getSuperClass()));
            }
            if (owlClassAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                equivalentClassExpressions = ((OWLEquivalentClassesAxiomImpl) owlClassAxiom).getClassExpressionsAsList();
                if (equivalentClassExpressions.size() >= 2) {
                    superClassExpression = equivalentClassExpressions.get(0);
                    subClassExpression = equivalentClassExpressions.get(1);
                    axiomsToBeAdded.addAll(createNormalizedAxioms(superClassExpression, subClassExpression));
                    axiomsToBeAdded.addAll(createNormalizedAxioms(subClassExpression, superClassExpression));
                }
            }
            if (axiomsToBeAdded.size() > 0) {
                for(OWLAxiom axiom : axiomsToBeAdded) {
                    owlOntologyManager.addAxiom(owlOntology, axiom);
                }
                owlOntologyManager.removeAxiom(owlOntology, owlClassAxiom);
            }
        }
        return owlOntology;
    }

    private Set<OWLAxiom> createNormalizedAxioms(OWLClassExpression superClass, OWLClassExpression subClass) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        if (hasOwlClassExpressionAnyExists(subClass)) {
            IRI iri = IRI.generateDocumentIRI();
            OWLClass tempClass = owlDataFactory.getOWLClass(iri);
            axioms.add(owlDataFactory.getOWLSubClassOfAxiom(superClass, tempClass));
            axioms.add(owlDataFactory.getOWLSubClassOfAxiom(tempClass, subClass));
        }
        return axioms;
    }

    private boolean hasOwlClassExpressionAnyExists(OWLClassExpression owlClassExpression) {
        boolean hasAnyExists = false;
        ClassExpressionType expressionType = owlClassExpression.getClassExpressionType();

        switch (expressionType) {
            case OBJECT_SOME_VALUES_FROM:
                hasAnyExists = true;
                break;
            case OBJECT_INTERSECTION_OF:
                List<OWLClassExpression> operands = ((OWLObjectIntersectionOf) owlClassExpression).getOperandsAsList();
                for (OWLClassExpression operand : operands) {
                    hasAnyExists = hasOwlClassExpressionAnyExists(operand);
                    if (hasAnyExists == true) {
                        break;
                    }
                }
                break;
            case OWL_CLASS:
            default:
                hasAnyExists = false;
                break;
        }
        return hasAnyExists;
    }

    /**
     * Main function.
     *
     * @throws org.semanticweb.owlapi.expression.ParserException the parser exception
     */
    public void proceed() throws ParserException {

        Date date1 = new Date();
        // setProgressLabelText("Rule translation");
        fillExistsOntologiesAndRules();
        Utils.getDiffTime(date1, "Preprocessing DisjointWith axioms: ");
        date1 = new Date();
        loopThrowAllClasses();
        Utils.getDiffTime(date1, "Processing classes: ");
        date1 = new Date();
        loopThrowAllProperties();
        Utils.getDiffTime(date1, "Processing properties: ");
    }
    /**
     * Going throw all ontologies and preprocess them.
     */
    protected void fillExistsOntologiesAndRules() {
        boolean isTopClass;
        ClassExpressionType expressionType;
        cm.setIsAnyDisjointStatement(false);

        for (OWLOntology ont : ontologies) {
            for (OWLClass owlClass : ont.getClassesInSignature()) {

                isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();

                // Going into loop throw all Disjoint classes
                for (OWLClassExpression owlClassExpression : owlClass.getDisjointClasses(ont)) {
                    cm.setIsAnyDisjointStatement(true);
                    expressionType = owlClassExpression.getClassExpressionType();
                    if ((expressionType == ClassExpressionType.OWL_CLASS)
                        && (owlClassExpression.isOWLThing() || owlClassExpression.isOWLNothing())) {
                        break;
                    }
                    if ((expressionType == ClassExpressionType.OBJECT_SOME_VALUES_FROM) && isTopClass) {
                        ruleCreator.writeRuleI3(owlClassExpression);
                    } else if ((expressionType == ClassExpressionType.OBJECT_INTERSECTION_OF) && isTopClass) {
                        ruleCreator.writeRuleI2(owlClassExpression);
                    } else {
                        if ((expressionType == ClassExpressionType.OWL_CLASS) && isTopClass) {
                            ruleCreator.writeRuleI1(owlClassExpression);
                        } else if (!isTopClass) {
                            ruleCreator.writeNegEquivalentRules(owlClassExpression, owlClass);
                        }
                    }
                }
            }

            for (OWLClassAxiom owlClassAxiom : ont.getGeneralClassAxioms()) {
                if (owlClassAxiom.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
                    cm.setIsAnyDisjointStatement(true);
                    ruleCreator.writeGeneralClassAxiomsWithComplexAssertions(owlClassAxiom);
                }
            }
        }

    }

    /**
     * Going into loop of all classes.
     */
    private void loopThrowAllClasses() {
        boolean isTopClass;
        OWLClassExpression rightPartOfRule;
        List<OWLClassExpression> equivalentClasses;
        OWLClassExpression equivalentClass;

        for (OWLOntology ont : ontologies) {
            for (OWLClass owlClass : ont.getClassesInSignature()) {
                isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();
                equivalentClasses = new ArrayList<OWLClassExpression>();

                if (!isTopClass) {
                    for (OWLIndividual individual : owlClass.getIndividuals(ont)) {
                        ruleCreator.writeRuleA1(individual, owlClass);
                    }

                    for (OWLClassExpression owlClassExpression : owlClass.getSubClasses(ont)) {
                        ruleCreator.writeDoubledRules(owlClassExpression, owlClass);
                    }
                }

                for (OWLEquivalentClassesAxiom equivalentClassesAxiom : ont.getEquivalentClassesAxioms(owlClass)) {
                    List<OWLClassExpression> list = equivalentClassesAxiom.getClassExpressionsAsList();
                    for (int i = 0; i < list.size(); i++) {
                        equivalentClass = list.get(i);
                        if (!((equivalentClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) && (equivalentClass.isOWLThing() || equivalentClass.isOWLNothing()))) {
                            equivalentClasses.add(equivalentClass);
                        }
                    }
                }
                if (equivalentClasses.size() > 0) {
                    equivalentClasses = Utils.removeDuplicates(equivalentClasses);
                    for (int i = 0; i < equivalentClasses.size(); i++) {
                        rightPartOfRule = equivalentClasses.get(i);
                        if (!isTopClass) {
                            if (rightPartOfRule.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                                if (!owlClass.equals(rightPartOfRule)) {
                                    ruleCreator.writeRuleC1(rightPartOfRule, owlClass, false);
                                }
                            } else {
                                ruleCreator.writeEquivalentRule(owlClass, rightPartOfRule);
                            }
                        }
                    }
                }
            }
            for (OWLClassAxiom owlClassAxiom : ont.getGeneralClassAxioms()) {
                if (owlClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
                    ruleCreator.writeGeneralClassAxiomsSubClasses(owlClassAxiom);
                }
                if (owlClassAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                    ruleCreator.writeGeneralClassAxiomsEquivClasses(owlClassAxiom);
                }
            }
        }
    }

    /**
     * Loop throw all properties.
     */
    private void loopThrowAllProperties() {
        for (OWLOntology ont : ontologies) {
            for (OWLObjectProperty objectProperty : ont.getObjectPropertiesInSignature()) {

                for (OWLAxiom axiom : objectProperty.getReferencingAxioms(ont)) {
                    if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
                        ruleCreator.writeRuleA2(axiom);
                    }
                }
                for (OWLObjectPropertyExpression objectPropertyExpression : objectProperty.getSubProperties(ont)) {
                    ruleCreator.writeRuleR1(objectPropertyExpression, objectProperty);
                }
            }
            for (OWLAxiom axiom : ont.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
                ruleCreator.writeRuleR2(axiom);
            }

            /**
             * Translate all data property assertions by looping through all individuals, find for each of them
             * all data properties and for each such data property all property values; then translate each single
             * obtained axiom
             */
            for (OWLNamedIndividual individual : ont.getIndividualsInSignature()) {

                for (OWLDataProperty dataProperty : ont.getDataPropertiesInSignature()) {

                    for (OWLLiteral literal : individual.getDataPropertyValues(dataProperty, ont)) {

                        ruleCreator.translateDataPropertyAssertion(dataProperty, individual, literal);
                    }
                }
            }
        }
    }
}

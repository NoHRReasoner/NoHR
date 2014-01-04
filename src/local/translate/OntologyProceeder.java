package local.translate;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OntologyProceeder {
    private CollectionsManager cm;
    private List<OWLOntology> ontologies = new ArrayList<OWLOntology>();
    private RuleCreator ruleCreator;
    public OntologyProceeder(CollectionsManager _cm, List<OWLOntology> _ontologies, RuleCreator _ruleCreator) {
        cm = _cm;
        ontologies = _ontologies;
        ruleCreator = _ruleCreator;
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
    private void fillExistsOntologiesAndRules() {
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
        }
    }
}

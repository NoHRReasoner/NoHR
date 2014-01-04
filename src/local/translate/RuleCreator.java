package local.translate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleCreator.
 */
public class RuleCreator {

    /** The cm. */
    private final CollectionsManager cm;

    /** The current rule. */
    private String currentRule;

    /** The ontology label. */
    private final OntologyLabel ontologyLabel;

    /**
     * Instantiates a new rule creator.
     * 
     * @param c
     *            the c
     * @param ol
     *            the ol
     */
    public RuleCreator(CollectionsManager c, OntologyLabel ol) {
	cm = c;
	cm.clearOntology();
	ontologyLabel = ol;
    }

    /**
     * Adds the predicate to set predicates appeared under nunderscore.
     * 
     * @param s
     *            the s
     */
    private void addPredicateToSetPredicatesAppearedUnderNunderscore(String s) {
	cm.addPrediactesAppearedUnderNunderscore(s);
    }

    /**
     * Gets the all owl classes from expression.
     * 
     * @param owlClassExpressions
     *            the owl class expressions
     * @return the all owl classes from expression
     */
    private HashSet<OWLClass> getAllOWLClassesFromExpression(
	    Set<OWLClassExpression> owlClassExpressions) {
	HashSet<OWLClass> classes = new HashSet<OWLClass>();
	for (OWLClassExpression owlClassExpression : owlClassExpressions) {
	    if (owlClassExpression.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
		classes.add((OWLClass) owlClassExpression);
	    } else if (owlClassExpression.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
		classes.addAll(getAllOWLClassesFromExpression(((OWLObjectIntersectionOfImpl) owlClassExpression)
			.getOperands()));
	    }
	}
	return classes;
    }

    /**
     * Checks if is predicate appeared in head under nunderscore.
     * 
     * @param predicate
     *            the predicate
     * @return true, if is predicate appeared in head under nunderscore
     */
    private boolean isPredicateAppearedInHeadUnderNunderscore(String predicate) {
	return cm.isPrediactesAppearedUnderNunderscore(predicate);
    }

    /**
     * Write doubled rules.
     * 
     * @param classExpression
     *            the class expression
     * @param owlClass
     *            the owl class
     */
    public void writeDoubledRules(OWLClassExpression classExpression,
	    OWLClassExpression owlClass) {
	currentRule = "%DoubledRule";
	EquivalentClass rules = ontologyLabel.getLabelEquivalentClasses(
		classExpression, 1, 1);
	String _owlClass = ontologyLabel.getLabel(owlClass, 1);
	String finalRule = rules.getFinalRule();
	if (finalRule.length() > 0) {
	    writeLineToFile("a" + _owlClass + "(X1)" + Utils.getEqForRule()
		    + finalRule);
	} else {
	    writeLineToFile("a" + _owlClass + "(X1).");
	}
	cm.addTabledPredicateOntology("a" + _owlClass + "/1");
	if (cm.isAnyDisjointStatement()) {// if(isExistOntology(_owlClass)){
	    String rule = "d" + _owlClass + "(X1)" + Utils.getEqForRule()
		    + rules.getDoubledRules();
	    cm.addTabledPredicateOntology("d" + _owlClass + "/1");
	    if (isPredicateAppearedInHeadUnderNunderscore("n" + _owlClass
		    + "/1")) {
		rule += ", " + Config.negation + " n" + _owlClass + "(X1)";
	    }
	    writeLineToFile(rule + ".");
	}
    }

    /**
     * Write equivalent rule.
     * 
     * @param owlClass
     *            the owl class
     * @param rightPartOfRule
     *            the right part of rule
     */
    public void writeEquivalentRule(OWLClass owlClass,
	    OWLClassExpression rightPartOfRule) {
	currentRule = "%EquivalentRule";
	EquivalentClass rightSideOfRule = ontologyLabel
		.getLabelEquivalentClasses(rightPartOfRule, 1, 1);
	String ruleHead = "a" + ontologyLabel.getLabel(owlClass, 1);
	String rule = ruleHead + "(X1) " + Config.eq + " "
		+ rightSideOfRule.getFinalRule();
	writeLineToFile(rule);
	cm.addTabledPredicateOntology(ruleHead + "/1");
    }

    /**
     * Write general class axioms.
     * 
     * @param superClass
     *            the super class
     * @param subClass
     *            the sub class
     */
    private void writeGeneralClassAxioms(OWLClassExpression superClass,
	    OWLClassExpression subClass) {
	if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
	    HashSet<OWLClass> classes = new HashSet<OWLClass>();
	    classes.addAll(getAllOWLClassesFromExpression(((OWLObjectIntersectionOfImpl) superClass)
		    .getOperands()));
	    for (OWLClass owlClass : classes) {
		writeEquivalentRule(owlClass, subClass);
	    }

	}

    }

    /**
     * Write general class axioms equiv classes.
     * 
     * @param owlClassAxiom
     *            the owl class axiom
     */
    public void writeGeneralClassAxiomsEquivClasses(OWLClassAxiom owlClassAxiom) {
	currentRule = "%GeneralClassAxiomsEquivClasses";
	List<OWLClassExpression> classExpressions = ((OWLEquivalentClassesAxiomImpl) owlClassAxiom)
		.getClassExpressionsAsList();
	if (classExpressions.size() >= 2) {
	    writeGeneralClassAxioms(classExpressions.get(0),
		    classExpressions.get(1));
	    writeGeneralClassAxioms(classExpressions.get(1),
		    classExpressions.get(0));
	}
    }

    /**
     * Write general class axioms sub classes.
     * 
     * @param owlClassAxiom
     *            the owl class axiom
     */
    public void writeGeneralClassAxiomsSubClasses(OWLClassAxiom owlClassAxiom) {
	currentRule = "%GeneralClassAxiomsSubClasses";
	OWLClassExpression superClass = ((OWLSubClassOfAxiomImpl) owlClassAxiom)
		.getSuperClass();
	OWLClassExpression subClass = ((OWLSubClassOfAxiomImpl) owlClassAxiom)
		.getSubClass();
	writeGeneralClassAxioms(superClass, subClass);

    }

    /**
     * Write general class axioms with complex assertions.
     * 
     * @param owlClassAxiom
     *            the owl class axiom
     */
    public void writeGeneralClassAxiomsWithComplexAssertions(
	    OWLClassAxiom owlClassAxiom) {
	currentRule = "%GeneralClassAxiomsWithComplexAssertions";

	EquivalentClass rules = new EquivalentClass(1);
	EquivalentClass subEquivalentClass = new EquivalentClass(1);
	for (OWLClassExpression owlClassExpression : ((OWLDisjointClassesAxiomImpl) owlClassAxiom)
		.getClassExpressions()) {
	    subEquivalentClass = ontologyLabel.getLabelEquivalentClasses(
		    owlClassExpression, 1,
		    subEquivalentClass.getVariableIterator());
	    for (EquivalentClass.EquivalentRules equivalentRules : subEquivalentClass
		    .getListOfRules()) {
		rules.addRule(equivalentRules.name,
			equivalentRules.localIterator,
			equivalentRules.iterator, equivalentRules.ontologyType);
	    }

	}
	for (String rule : rules.getNegRules()) {
	    writeLineToFile(rule);
	}
	for (String rule : rules.getNegRulesHeadForTabling()) {
	    cm.addTabledPredicateOntology(rule);
	    addPredicateToSetPredicatesAppearedUnderNunderscore(rule);
	}
    }

    /**
     * Write line to file.
     * 
     * @param string
     *            the string
     */
    private void writeLineToFile(String string) {
	string += Config.ruleCreationDebug ? currentRule : "";
	cm.addTranslatedOntology(string);
    }

    /**
     * Write neg equivalent rules.
     * 
     * @param classExpression
     *            the class expression
     * @param owlClass
     *            the owl class
     */
    public void writeNegEquivalentRules(OWLClassExpression classExpression,
	    OWLClassExpression owlClass) {
	currentRule = "%NegEquivalentRule";
	EquivalentClass rules = ontologyLabel.getLabelEquivalentClasses(
		classExpression, 1, 1);
	if (!(owlClass.isOWLThing() || owlClass.isOWLNothing())) {
	    rules.addRule(ontologyLabel.getLabel(owlClass, 1), 1, 1,
		    EquivalentClass.OntologyType.ONTOLOGY);
	}
	for (String rule : rules.getNegRules()) {
	    writeLineToFile(rule);
	}
	for (String rule : rules.getNegRulesHeadForTabling()) {
	    cm.addTabledPredicateOntology(rule);
	    addPredicateToSetPredicatesAppearedUnderNunderscore(rule);
	}

    }

    /**
     * (a1). for each C(a) ��� A: C(a) ��� and Cd(a) ��� notNC(a).
     * 
     * @param member
     *            the member
     * @param entity
     *            the entity
     */
    public void writeRuleA1(OWLIndividual member, OWLClass entity) {
	currentRule = "%A1";
	String a = ontologyLabel.getLabel(member, 1);
	String C = ontologyLabel.getLabel(entity, 1);
	writeLineToFile("a" + C + "(c" + a + ").");
	cm.addTabledPredicateOntology("a" + C + "/1");
	if (cm.isAnyDisjointStatement()) {
	    String rule = "d" + C + "(c" + a + ")";
	    cm.addTabledPredicateOntology("d" + C + "/1");
	    if (isPredicateAppearedInHeadUnderNunderscore("n" + C + "/1")) {
		rule += Utils.getEqForRule() + Config.negation + " n" + C
			+ "(c" + a + ")";
	    }
	    writeLineToFile(rule + ".");
	}
    }

    /**
     * (a2). for each R(a, b) ��� A: R(a, b) ��� and Rd(a, b) ��� not NR(a, b).
     * 
     * @param entity
     *            the entity
     */
    public void writeRuleA2(OWLAxiom entity) {
	currentRule = "%A2";
	String R = ontologyLabel.getLabel(entity, 1), a = ontologyLabel
		.getLabel(entity, 2), b = ontologyLabel.getLabel(entity, 3);
	writeLineToFile("a" + R + "(c" + a + ", c" + b + ").");
	cm.addTabledPredicateOntology("a" + R + "/2");
	if (cm.isAnyDisjointStatement()) {
	    String rule = "d" + R + "(c" + a + ", c" + b + ")";
	    cm.addTabledPredicateOntology("d" + R + "/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("n" + R + "/2")) {
		rule += Utils.getEqForRule() + Config.negation + " n" + R
			+ "(c" + a + ", c" + b + ")";
	    }
	    writeLineToFile(rule + ".");
	}
    }

    /**
     * (c1). foreach GCI C ��� D ��� T: D(x)���C(x) and Dd(x) ��� Cd(x), not
     * ND(x).
     * 
     * @param expression
     *            the expression
     * @param superclass
     *            the superclass
     * @param lastIndex
     *            the last index
     */
    public void writeRuleC1(OWLClassExpression expression, OWLClass superclass,
	    boolean lastIndex) {
	currentRule = "%C1";
	String D = ontologyLabel.getLabel(superclass, 1);
	String C = ontologyLabel.getLabel(expression, lastIndex ? -1 : 1);
	writeLineToFile("a" + D + "(X)" + Utils.getEqForRule() + "a" + C
		+ "(X).");
	cm.addTabledPredicateOntology("a" + D + "/1");
	if (cm.isAnyDisjointStatement()) {
	    String rule = "d" + D + "(X)" + Utils.getEqForRule() + "d" + C
		    + "(X)";
	    if (isPredicateAppearedInHeadUnderNunderscore("n" + D + "/1")) {
		rule += ", " + Config.negation + " n" + D + "(X)";
	    }
	    writeLineToFile(rule + ".");
	    cm.addTabledPredicateOntology("d" + D + "/1");
	}
    }

    /**
     * (i1). for each C ��������� T : NC(x) ���.
     * 
     * @param expression
     *            the expression
     */
    public void writeRuleI1(OWLClassExpression expression) {
	currentRule = "%I1";
	String C = ontologyLabel.getLabel(expression, 1);
	writeLineToFile("n" + C + "(X).");
	cm.addTabledPredicateOntology("n" + C + "/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("n" + C + "/1");
    }

    /**
     * (i2). for each C1 ��� C2 ��������� T : NC2(x) ��� C1(x) and NC1(x) ���
     * C2(x).
     * 
     * @param expression
     *            the expression
     */
    public void writeRuleI2(OWLClassExpression expression) {
	currentRule = "%I2";
	String C2 = ontologyLabel.getLabel(expression, 1);
	String C1 = ontologyLabel.getLabel(expression, 2);
	writeLineToFile("n" + C2 + "(X) :- a" + C1 + "(X).");
	cm.addTabledPredicateOntology("n" + C2 + "/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("n" + C2 + "/1");
	writeLineToFile("n" + C1 + "(X) :- a" + C2 + "(X).");
	cm.addTabledPredicateOntology("n" + C1 + "/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("n" + C1 + "/1");

    }

    /**
     * (i3). for each ���R.C ��������� T : NC(y) ��� R(x,y) and NR(x,y) ��� C(y)
     * .
     * 
     * @param expression
     *            the expression
     */
    public void writeRuleI3(OWLClassExpression expression) {
	currentRule = "%I3";
	String C = ontologyLabel.getLabel(expression, 2);
	String R = ontologyLabel.getLabel(expression, 1);
	writeLineToFile("n" + C + "(Y) :- a" + R + "(X,Y).");
	cm.addTabledPredicateOntology("n" + C + "/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("n" + C + "/1");
	writeLineToFile("n" + R + "(X,Y) :- a" + C + "(Y).");
	cm.addTabledPredicateOntology("n" + R + "/2");
    }

    /**
     * (r1). foreach RI R���S ��� T: S(x,y)���R(x,y) and Sd(x, y) ��� Rd(x, y),
     * not NS(x, y).
     * 
     * @param expression
     *            the expression
     * @param superclass
     *            the superclass
     */
    public void writeRuleR1(OWLObjectPropertyExpression expression,
	    OWLObjectProperty superclass) {
	currentRule = "%R1";
	if (expression == superclass) {
	    return;
	}
	String S = ontologyLabel.getLabel(superclass, 1);
	String R = ontologyLabel.getLabel(expression, 1);
	writeLineToFile("a" + S + "(X,Y)" + Utils.getEqForRule() + "a" + R
		+ "(X,Y).");
	cm.addTabledPredicateOntology("a" + S + "/2");
	if (cm.isAnyDisjointStatement()) {
	    String rule = "d" + S + "(X,Y)" + Utils.getEqForRule() + "d" + R
		    + "(X,Y)";
	    cm.addTabledPredicateOntology("d" + S + "/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("n" + S + "/2")) {
		rule += ", " + Config.negation + " n" + S + "(X,Y)";
	    }
	    writeLineToFile(rule + ".");
	}
    }

    /**
     * (r2). foreach R���S ��� T ��� T: T(x,z)���R(x,y),S(y,z) and Td(x,z) ���
     * Rd(x,y),Sd(y,z),notNT(x,z).
     * 
     * @param axiom
     *            the axiom
     */
    public void writeRuleR2(OWLAxiom axiom) {
	currentRule = "%R2";
	String S = ontologyLabel.getLabel(axiom, 2);
	String R = ontologyLabel.getLabel(axiom, 1);
	String T = ontologyLabel.getLabel(axiom, 3);
	writeLineToFile("a" + T + "(X,Z)" + Utils.getEqForRule() + "a" + R
		+ "(X,Y), " + "a" + S + "(Y,Z).");
	cm.addTabledPredicateOntology("a" + T + "/2");
	if (cm.isAnyDisjointStatement()) {// if(isExistRule(T)){
	    String rule = "d" + T + "(X,Z)" + Utils.getEqForRule() + "d" + R
		    + "(X,Y), " + "d" + S + "(Y,Z)";
	    cm.addTabledPredicateOntology("d" + T + "/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("n" + T + "/2")) {
		rule += ", " + Config.negation + " n" + T + "(X,Z)";
	    }
	    writeLineToFile(rule + ".");
	}
    }

}

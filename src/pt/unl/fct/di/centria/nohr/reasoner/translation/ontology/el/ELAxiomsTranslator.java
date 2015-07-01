package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import other.Config;
import other.Utils;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.EquivalentClass;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

/**
 * The Class RuleCreator. Translating ontology into the rule
 */
public class ELAxiomsTranslator {

    /** The current rule. */
    private String currentRule;
    private final boolean hasDisjunctions;

    private final Set<String> negatedPredicates;

    /** The ontology label. */
    protected final OntologyLabeler ontologyLabel;
    private final Set<String> tabledPredicates;

    private Set<String> translation;

    /**
     * Instantiates a new rule creator.
     *
     * @param c
     *            the collection manager
     * @param ol
     *            the ontology label
     */
    public ELAxiomsTranslator(OntologyLabeler ol, boolean hasDisjunctions) {
	ontologyLabel = ol;
	this.hasDisjunctions = hasDisjunctions;
	negatedPredicates = new HashSet<String>();
	tabledPredicates = new HashSet<String>();
    }

    /**
     * Adds the predicate to set predicates appeared under NOT.
     *
     * @param s
     *            the s
     */
    private void addPredicateToSetPredicatesAppearedUnderNunderscore(String s) {
	negatedPredicates.add(s);
    }

    protected void addRule(String rule) {
	translation.add(rule);
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
	final HashSet<OWLClass> classes = new HashSet<OWLClass>();
	for (final OWLClassExpression owlClassExpression : owlClassExpressions)
	    if (owlClassExpression.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
		classes.add((OWLClass) owlClassExpression);
	    else if (owlClassExpression.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF)
		classes.addAll(getAllOWLClassesFromExpression(((OWLObjectIntersectionOfImpl) owlClassExpression)
			.getOperands()));
	return classes;
    }

    public Set<String> getTabledPredicates() {
	return tabledPredicates;
    }

    /**
     * Checks if is predicate appeared in head under NOT.
     *
     * @param predicate
     *            the predicate
     * @return true, if is predicate appeared in head under NOT
     */
    private boolean isPredicateAppearedInHeadUnderNunderscore(String predicate) {
	return negatedPredicates.contains(predicate);
    }

    public void setTranslationContainer(Set<String> translationContainer) {
	translation = translationContainer;
    }

    /**
     *
     * @param dataProperty
     * @param individual
     * @param value
     *            Translates a given data property with its property name,
     *            individual, and value into a rule (or two rules).
     */
    public void translateDataPropertyAssertion(OWLDataProperty dataProperty,
	    OWLIndividual individual, OWLLiteral value) {
	final String Predicate = ontologyLabel.getLabel(dataProperty, 1);
	final String Individual = ontologyLabel.getLabel(individual, 1);
	final String Value = OntologyLabeler.escapeAtom(value.getLiteral());
	addRule("'a" + Predicate + "'('" + Individual + "','" + Value + "').");
	tabledPredicates.add("a" + Predicate + "/2");
	if (hasDisjunctions) {
	    String rule = "'d" + Predicate + "'('" + Individual + "','" + Value
		    + "')";
	    tabledPredicates.add("'d" + Predicate + "'/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + Predicate
		    + "'/2"))
		rule += Utils.getEqForRule() + Config.negation + " 'n"
			+ Predicate + "'('" + Individual + "','" + Value + "0)";
	    addRule(rule + ".");
	}
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
	final EquivalentClass rules = ontologyLabel.getLabelEquivalentClasses(
		classExpression, 1, 1);
	final String _owlClass = ontologyLabel.getLabel(owlClass, 1);
	final String finalRule = rules.getFinalRule();
	if (finalRule.length() > 0)
	    addRule("'a" + _owlClass + "'(X1)" + Utils.getEqForRule()
		    + finalRule);
	else
	    addRule("'a" + _owlClass + "'(X1).");
	tabledPredicates.add("'a" + _owlClass + "'/1");
	if (hasDisjunctions) {// if(isExistOntology(_owlClass)){
	    String rule = "'d" + _owlClass + "'(X1)" + Utils.getEqForRule()
		    + rules.getDoubledRules();
	    tabledPredicates.add("'d" + _owlClass + "'/1");
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + _owlClass
		    + "'/1"))
		rule += ", " + Config.negation + " 'n" + _owlClass + "'(X1)";
	    addRule(rule + ".");
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
	final EquivalentClass rightSideOfRule = ontologyLabel
		.getLabelEquivalentClasses(rightPartOfRule, 1, 1);
	final String owlClassName = ontologyLabel.getLabel(owlClass, 1);
	final String ruleHead = "'a" + owlClassName + "'";
	String rule = ruleHead + "(X1) " + Config.eq + " "
		+ rightSideOfRule.getFinalRule();
	addRule(rule);
	tabledPredicates.add(ruleHead + "/1");
	if (hasDisjunctions) {
	    rule = "'d" + owlClassName + "'(X1)" + Utils.getEqForRule()
		    + rightSideOfRule.getFinalDoubledRule();
	    tabledPredicates.add("'d" + owlClassName + "'/1");
	    addRule(rule);
	}
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
	    final HashSet<OWLClass> classes = new HashSet<OWLClass>();
	    classes.addAll(getAllOWLClassesFromExpression(((OWLObjectIntersectionOfImpl) superClass)
		    .getOperands()));
	    for (final OWLClass owlClass : classes)
		writeEquivalentRule(owlClass, subClass);

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
	final List<OWLClassExpression> classExpressions = ((OWLEquivalentClassesAxiomImpl) owlClassAxiom)
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
	final OWLClassExpression superClass = ((OWLSubClassOfAxiomImpl) owlClassAxiom)
		.getSuperClass();
	final OWLClassExpression subClass = ((OWLSubClassOfAxiomImpl) owlClassAxiom)
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

	final EquivalentClass rules = new EquivalentClass(1);
	EquivalentClass subEquivalentClass = new EquivalentClass(1);
	for (final OWLClassExpression owlClassExpression : ((OWLDisjointClassesAxiomImpl) owlClassAxiom)
		.getClassExpressions()) {
	    subEquivalentClass = ontologyLabel.getLabelEquivalentClasses(
		    owlClassExpression, 1,
		    subEquivalentClass.getVariableIterator());
	    for (final EquivalentClass.EquivalentRules equivalentRules : subEquivalentClass
		    .getListOfRules())
		rules.addRule(equivalentRules.name,
			equivalentRules.localIterator,
			equivalentRules.iterator, equivalentRules.ontologyType);

	}
	for (final String rule : rules.getNegRules())
	    addRule(rule);
	for (final String rule : rules.getNegRulesHeadForTabling()) {
	    tabledPredicates.add(rule);
	    addPredicateToSetPredicatesAppearedUnderNunderscore(rule);
	}
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
	final EquivalentClass rules = ontologyLabel.getLabelEquivalentClasses(
		classExpression, 1, 1);
	if (!(owlClass.isOWLThing() || owlClass.isOWLNothing()))
	    rules.addRule(ontologyLabel.getLabel(owlClass, 1), 1, 1,
		    EquivalentClass.OntologyType.ONTOLOGY);
	for (final String rule : rules.getNegRules())
	    addRule(rule);
	for (final String rule : rules.getNegRulesHeadForTabling()) {
	    tabledPredicates.add(rule);
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
	final String a = ontologyLabel.getLabel(member, 1);
	final String C = ontologyLabel.getLabel(entity, 1);
	addRule("'a" + C + "'(" + a + ").");
	tabledPredicates.add("'a" + C + "'/1");
	if (hasDisjunctions) {
	    String rule = "'d" + C + "'(" + a + ")";
	    tabledPredicates.add("'d" + C + "'/1");
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + C + "'/1"))
		rule += Utils.getEqForRule() + Config.negation + " 'n" + C
			+ "'(" + a + ")";
	    addRule(rule + ".");
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
	final String R = ontologyLabel.getLabel(entity, 1), a = ontologyLabel
		.getLabel(entity, 2), b = ontologyLabel.getLabel(entity, 3);
	addRule("'a" + R + "'('" + a + "','" + b + "').");
	tabledPredicates.add("'a" + R + "'/2");
	if (hasDisjunctions) {
	    String rule = "'d" + R + "'('" + a + "','" + b + "')";
	    tabledPredicates.add("'d" + R + "'/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + R + "'/2"))
		rule += Utils.getEqForRule() + Config.negation + " 'n" + R
			+ "'('" + a + "','" + b + "')";
	    addRule(rule + ".");
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
	final String D = ontologyLabel.getLabel(superclass, 1);
	final String C = ontologyLabel.getLabel(expression, lastIndex ? -1 : 1);
	addRule("'a" + D + "'(X)" + Utils.getEqForRule() + "'a" + C + "'(X).");
	tabledPredicates.add("'a" + D + "'/1");
	if (hasDisjunctions) {
	    String rule = "'d" + D + "'(X)" + Utils.getEqForRule() + "'d" + C
		    + "'(X)";
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + D + "'/1"))
		rule += ", " + Config.negation + " 'n" + D + "'(X)";
	    addRule(rule + ".");
	    tabledPredicates.add("'d" + D + "'/1");
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
	final String C = ontologyLabel.getLabel(expression, 1);
	addRule("'n" + C + "'(X).");
	tabledPredicates.add("'n" + C + "'/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("'n" + C + "'/1");
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
	final String C2 = ontologyLabel.getLabel(expression, 1);
	final String C1 = ontologyLabel.getLabel(expression, 2);
	addRule("'n" + C2 + "'(X) :- 'a" + C1 + "'(X).");
	tabledPredicates.add("'n" + C2 + "'/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("n" + C2 + "/1");
	addRule("'n" + C1 + "'(X) :- 'a" + C2 + "'(X).");
	tabledPredicates.add("'n" + C1 + "'/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("'n" + C1 + "'/1");

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
	final String C = ontologyLabel.getLabel(expression, 2);
	final String R = ontologyLabel.getLabel(expression, 1);
	addRule("'n" + C + "'(Y) :- 'a" + R + "'(X,Y).");
	tabledPredicates.add("'n" + C + "'/1");
	addPredicateToSetPredicatesAppearedUnderNunderscore("'n" + C + "'/1");
	addRule("'n" + R + "'(X,Y) :- 'a" + C + "'(Y).");
	tabledPredicates.add("'n" + R + "'/2");
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
	if (expression == superclass)
	    return;
	final String S = ontologyLabel.getLabel(superclass, 1);
	final String R = ontologyLabel.getLabel(expression, 1);
	addRule("'a" + S + "'(X,Y)" + Utils.getEqForRule() + "'a" + R
		+ "'(X,Y).");
	tabledPredicates.add("'a" + S + "'/2");
	if (hasDisjunctions) {
	    String rule = "'d" + S + "'(X,Y)" + Utils.getEqForRule() + "'d" + R
		    + "(X,Y)'";
	    tabledPredicates.add("'d" + S + "'/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + S + "'/2"))
		rule += ", " + Config.negation + " 'n" + S + "'(X,Y)";
	    addRule(rule + ".");
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
	final String S = ontologyLabel.getLabel(axiom, 2);
	final String R = ontologyLabel.getLabel(axiom, 1);
	final String T = ontologyLabel.getLabel(axiom, 3);
	addRule("'a" + T + "''(X,Z)" + Utils.getEqForRule() + "'a" + R
		+ "'(X,Y), " + "'a" + S + "'(Y,Z).");
	tabledPredicates.add("'a" + T + "'/2");
	if (hasDisjunctions) {// if(isExistRule(T)){
	    String rule = "'d" + T + "'(X,Z)" + Utils.getEqForRule() + "'d" + R
		    + "'(X,Y), " + "'d" + S + "'(Y,Z)";
	    tabledPredicates.add("'d" + T + "'/2");
	    if (isPredicateAppearedInHeadUnderNunderscore("'n" + T + "'/2"))
		rule += ", " + Config.negation + " 'n" + T + "'(X,Z)";
	    addRule(rule + ".");
	}
    }

}

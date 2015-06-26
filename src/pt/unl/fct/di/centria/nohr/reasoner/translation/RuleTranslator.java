package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.HashSet;
import java.util.Set;

import other.Config;
import other.Utils;

/**
 * The Class RuleTranslator.
 */
public class RuleTranslator {

    /** The current rule. */
    private String currentRule;

    /** The parsed rule. */
    private ParsedRule parsedRule;

    private Set<String> rules;

    private Set<String> tabledPredicates;

    /**
     * Instantiates a new rule translator.
     *
     * @param c
     *            the collection manager
     */
    public RuleTranslator() {
	tabledPredicates = new HashSet<String>();
    }

    /**
     * Adds the plain rule for tabled predicate.
     *
     * @param parsedRule
     *            the parsed rule
     */
    private void addPlainRuleForTabledPredicate(ParsedRule parsedRule) {
	writeLineToAppendedRules(parsedRule.getRule() + " " + Config.eq
		+ " fail.");
    }

    /**
     * Adds the sub rule for tabled predicate.
     *
     * @param parsedRule
     *            the parsed rule
     */
    private void addSubRuleForTabledPredicate(ParsedRule parsedRule) {
	writeLineToAppendedRules(parsedRule.getSubRule() + " " + Config.eq
		+ " fail.");
    }

    public Set<String> getTabledPredicates() {
	return tabledPredicates;
    }

    /**
     * Proceed rule.
     *
     * @param rule
     *            the rule
     * @throws Exception
     *             the exception
     */
    public Set<String> proceedRule(String rule, boolean hasDisjunctions,
	    Set<String> negatedPredicates) {
	rules = new HashSet<String>();
	if (rule.startsWith(Config.eq))
	    return rules;
	if (rule.endsWith("."))
	    rule = rule.substring(0, rule.length() - 1);

	final String[] arrayRule = rule.split(Config.eq);

	final ParsedRule leftSideRule = new ParsedRule(arrayRule[0]);
	String rightSideRule = null;
	if (arrayRule.length > 1 && arrayRule[1] != null)
	    rightSideRule = arrayRule[1].trim();
	tablePredicateFromRule(leftSideRule, hasDisjunctions);

	if (hasDisjunctions) {
	    writeArule(leftSideRule, rightSideRule, hasDisjunctions);
	    writeBrule(leftSideRule, rightSideRule, hasDisjunctions,
		    negatedPredicates);
	} else
	    writePlainRule(leftSideRule, rightSideRule, hasDisjunctions);
	return rules;
    }

    public void reset() {
	tabledPredicates = new HashSet<String>();
    }

    /**
     * Table predicate from rule.
     *
     * @param rule
     *            the rule
     */
    private void tablePredicateFromRule(ParsedRule rule, boolean hasDisjunctions) {
	tabledPredicates.add(rule.getTabledRule());
	if (hasDisjunctions)
	    tabledPredicates.add(rule.getTabledDoubledRule());
    }

    /**
     * Write arule.
     *
     * @param leftSide
     *            the left side
     * @param rightSide
     *            the right side
     */
    private void writeArule(ParsedRule leftSide, String rightSide,
	    boolean hasDisjunctions) {
	currentRule = "%writeArule";
	if (rightSide == null)
	    writeLineToAppendedRules(leftSide.getRule() + ".");
	else {
	    String result = leftSide.getRule() + Utils.getEqForRule();

	    for (final String subRule : Utils.getSubRulesFromRule(rightSide)) {
		parsedRule = new ParsedRule(subRule);
		if (parsedRule.isUnderTnot()) {
		    tablePredicateFromRule(parsedRule, hasDisjunctions);
		    addSubRuleForTabledPredicate(parsedRule);
		    result += parsedRule.getNegSubRule() + ", ";
		} else
		    result += parsedRule.getRule() + ", ";
	    }
	    result = result.substring(0, result.length() - 2);
	    writeLineToAppendedRules(result + ".");
	}
    }

    /**
     * Write brule.
     *
     * @param leftSide
     *            the left side
     * @param rightSide
     *            the right side
     */
    private void writeBrule(ParsedRule leftSide, String rightSide,
	    boolean hasDisjunctions, Set<String> negatedPredicates) {
	currentRule = "%writeBrule";
	if (rightSide == null) {
	    String rule = leftSide.getSubRule();
	    // if (negatedPredicates.contains(leftSide.getTabledNegRule()))
	    rule += Utils.getEqForRule() + leftSide.getNegRule();
	    writeLineToAppendedRules(rule + ".");
	} else {
	    String result = leftSide.getSubRule() + Utils.getEqForRule();

	    for (final String subRule : Utils.getSubRulesFromRule(rightSide)) {
		parsedRule = new ParsedRule(subRule);
		if (parsedRule.isUnderTnot()) {
		    tablePredicateFromRule(parsedRule, hasDisjunctions);
		    addPlainRuleForTabledPredicate(parsedRule);
		    result += parsedRule.getNegPlainRule() + ", ";
		} else
		    result += parsedRule.getSubRule() + ", ";
	    }
	    if (negatedPredicates.contains(leftSide.getTabledNegRule()))
		result += leftSide.getNegRule() + ", ";
	    result = result.substring(0, result.length() - 2);
	    writeLineToAppendedRules(result + ".");
	}
    }

    private void writeLineToAppendedRules(String string) {
	rules.add(string);
    }

    /**
     * Write plain rule.
     *
     * @param leftSide
     *            the left side
     * @param rightSide
     *            the right side
     */
    private void writePlainRule(ParsedRule leftSide, String rightSide,
	    boolean hasDisjunctions) {
	currentRule = "%writePlainRule";
	if (rightSide == null)
	    writeLineToAppendedRules(leftSide.getRule() + ".");
	else {
	    String result = leftSide.getRule() + Utils.getEqForRule();
	    for (final String subRule : Utils.getSubRulesFromRule(rightSide)) {
		parsedRule = new ParsedRule(subRule);
		if (parsedRule.isUnderTnot()) {
		    tablePredicateFromRule(parsedRule, hasDisjunctions);
		    addPlainRuleForTabledPredicate(parsedRule);
		    result += parsedRule.getNegPlainRule() + ", ";
		} else
		    result += parsedRule.getRule() + ", ";
	    }

	    result = result.substring(0, result.length() - 2);
	    writeLineToAppendedRules(result + ".");
	}
    }
}
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import other.Config;
import other.Utils;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.CollectionsManager;

/**
 * The Class RuleTranslator.
 */
public class RuleTranslator {

    /** The collections manager. */
    private final CollectionsManager cm;

    /** The parsed rule. */
    private ParsedRule parsedRule;

    /** The current rule. */
    private String currentRule;

    /**
     * Instantiates a new rule translator.
     *
     * @param c the collection manager
     */
    public RuleTranslator(CollectionsManager c) {
        cm = c;
        cm.clearRules();
    }

    /**
     * Adds the plain rule for tabled predicate.
     *
     * @param parsedRule the parsed rule
     */
    private void addPlainRuleForTabledPredicate(ParsedRule parsedRule) {
        writeLineToAppendedRules(parsedRule.getRule() + " " + Config.eq + " fail.");
    }

    /**
     * Adds the sub rule for tabled predicate.
     *
     * @param parsedRule the parsed rule
     */
    private void addSubRuleForTabledPredicate(ParsedRule parsedRule) {
        writeLineToAppendedRules(parsedRule.getSubRule() + " " + Config.eq + " fail.");
    }

    /**
     * Proceed rule.
     *
     * @param rule the rule
     * @throws Exception the exception
     */
    public void proceedRule(String rule) throws Exception {
        if (rule.startsWith(Config.eq)) {
            return;
        }
        if (rule.endsWith(".")) {
            rule = rule.substring(0, rule.length() - 1);
        }

        String[] arrayRule = rule.split(Config.eq);

        ParsedRule leftSideRule = new ParsedRule(arrayRule[0]);
        String rightSideRule = null;
        if ((arrayRule.length > 1) && (arrayRule[1] != null)) {
            rightSideRule = arrayRule[1].trim();
        }
        tablePredicateFromRule(leftSideRule);

        if (cm.isAnyDisjointStatement()) {
            writeArule(leftSideRule, rightSideRule);
            writeBrule(leftSideRule, rightSideRule);
        } else {
            writePlainRule(leftSideRule, rightSideRule);
        }

    }

    /**
     * Table predicate from rule.
     *
     * @param rule the rule
     */
    private void tablePredicateFromRule(ParsedRule rule) {
        cm.addTabledPredicateRule(rule.getTabledRule());
        if (cm.isAnyDisjointStatement()) {
            cm.addTabledPredicateRule(rule.getTabledDoubledRule());
        }
    }

    /**
     * Write arule.
     *
     * @param leftSide  the left side
     * @param rightSide the right side
     */
    private void writeArule(ParsedRule leftSide, String rightSide) {
        currentRule = "%writeArule";
        if (rightSide == null) {
            writeLineToAppendedRules(leftSide.getRule() + ".");
        } else {
            String result = leftSide.getRule() + Utils.getEqForRule();

            for (String subRule : Utils.getSubRulesFromRule(rightSide)) {
                parsedRule = new ParsedRule(subRule);
                if (parsedRule.isUnderTnot()) {
                    tablePredicateFromRule(parsedRule);
                    addSubRuleForTabledPredicate(parsedRule);
                    result += parsedRule.getNegSubRule() + ", ";
                } else {
                    result += parsedRule.getRule() + ", ";
                }
            }
            result = result.substring(0, result.length() - 2);
            writeLineToAppendedRules(result + ".");
        }
    }

    /**
     * Write brule.
     *
     * @param leftSide  the left side
     * @param rightSide the right side
     */
    private void writeBrule(ParsedRule leftSide, String rightSide) {
        currentRule = "%writeBrule";
        if (rightSide == null) {
            String rule = leftSide.getSubRule();
            if (cm.isPrediactesAppearedUnderNunderscore(leftSide.getTabledNegRule())) {
                rule += Utils.getEqForRule() + leftSide.getNegRule();
            }
            writeLineToAppendedRules(rule + ".");
        } else {
            String result = leftSide.getSubRule() + Utils.getEqForRule();

            for (String subRule : Utils.getSubRulesFromRule(rightSide)) {
                parsedRule = new ParsedRule(subRule);
                if (parsedRule.isUnderTnot()) {
                    tablePredicateFromRule(parsedRule);
                    addPlainRuleForTabledPredicate(parsedRule);
                    result += parsedRule.getNegPlainRule() + ", ";
                } else {
                    result += parsedRule.getSubRule() + ", ";
                }
            }
            if (cm.isPrediactesAppearedUnderNunderscore(leftSide.getTabledNegRule())) {
                result += leftSide.getNegRule() + ", ";
            }
            result = result.substring(0, result.length() - 2);
            writeLineToAppendedRules(result + ".");
        }
    }

    /**
     * Write line to appended rules.
     *
     * @param string the string
     */
    private void writeLineToAppendedRules(String string) {
        string += Config.ruleCreationDebug ? currentRule : "";
        cm.addTranslatedRule(string);
    }

    /**
     * Write plain rule.
     *
     * @param leftSide  the left side
     * @param rightSide the right side
     */
    private void writePlainRule(ParsedRule leftSide, String rightSide) {
        currentRule = "%writePlainRule";
        if (rightSide == null) {
            writeLineToAppendedRules(leftSide.getRule() + ".");
        } else {
            String result = leftSide.getRule() + Utils.getEqForRule();
            for (String subRule : Utils.getSubRulesFromRule(rightSide)) {
                parsedRule = new ParsedRule(subRule);
                if (parsedRule.isUnderTnot()) {
                    tablePredicateFromRule(parsedRule);
                    addPlainRuleForTabledPredicate(parsedRule);
                    result += parsedRule.getNegPlainRule() + ", ";
                } else {
                    result += parsedRule.getRule() + ", ";
                }
            }

            result = result.substring(0, result.length() - 2);
            writeLineToAppendedRules(result + ".");
        }
    }
}
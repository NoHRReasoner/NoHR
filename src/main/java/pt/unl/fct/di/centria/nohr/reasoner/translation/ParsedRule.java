package nohr.reasoner.translation;

import java.util.Map;

import org.apache.log4j.Logger;

import other.Config;
import other.Utils;

/**
 * The Class ParsedRule.
 */
public class ParsedRule {

    /** The rule. */
    private String rule;

    /** The len. */
    private int len;

    /** The predicate. */
    private String predicate;

    /** The variables. */
    private String variables;

    /** The is under tnot. */
    private boolean isUnderTnot = false;
  

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(ParsedRule.class); 

    /**
     * Instantiates a new parsed rule.
     *
     * @param r the r
     */
    public ParsedRule(String r) {
        log.setLevel(Config.logLevel);
        rule = r;
        parse();
    }

    /**
     * Gets the hashed rule for query.
     *
     * @return the hashed rule for query
     */
    public String getHashedRuleForQuery() {
        if (isUnderTnot()) {
            return getNegSubRule();
        } else {
            return getRule();
        }
    }

    /**
     * Gets the neg plain rule.
     *
     * @return the neg plain rule
     */
    public String getNegPlainRule() {
        return Config.negation + " " + getRule();
    }

    /**
     * Gets the negated rule.
     *
     * @return the negated rule
     */
    public String getNegRule() {
        return Config.negation + " n" + predicate + variables;
    }

    /**
     * Gets the negated sub rule.
     *
     * @return the neg sub rule
     */
    public String getNegSubRule() {
        return Config.negation + " " + getSubRule();
    }

    /**
     * Gets the plain hashed rule.
     *
     * @return the plain hashed rule
     */
    public String getPlainHashedRule() {
        if (isUnderTnot()) {
            return getNegPlainRule();
        } else {
            return getRule();
        }
    }

    /**
     * Gets the plain sub rule.
     *
     * @return the plain sub rule
     */
    public String getPlainSubRule() {
        if (isUnderTnot()) {
            return getNegSubRule();
        } else {
            return getSubRule();
        }
    }

    /**
     * Gets the rule.
     *
     * @return the rule
     */
    public String getRule() {
        return "a" + predicate + variables;
    }

    /**
     * Gets the sub rule.
     *
     * @return the sub rule
     */
    public String getSubRule() {
        return "d" + predicate + variables;
    }

    /**
     * Gets the tabled doubled rule.
     *
     * @return the tabled doubled rule
     */
    public String getTabledDoubledRule() {
        return "d" + predicate + "/" + len;
    }

    /**
     * Gets the tabled neg rule.
     *
     * @return the tabled neg rule
     */
    public String getTabledNegRule() {
        return "n" + predicate + "/" + len;
    }

    /**
     * Gets the tabled rule.
     *
     * @return the tabled rule
     */
    public String getTabledRule() {
        return "a" + predicate + "/" + len;
    }

    /**
     * Checks if is under tnot.
     *
     * @return true, if is under tnot
     */
    public boolean isUnderTnot() {
        return isUnderTnot;
    }

    /**
     * Parses the.
     */
    private void parse() {
        rule = rule.trim();
        if (rule.startsWith(Config.searchNegation)) {
            rule = rule.replaceFirst(Config.searchNegation, Config.negation);
        }
        if (rule.startsWith(Config.negation)) {
            rule = rule.replaceFirst(Config.negation + " ", "").trim();
            isUnderTnot = true;
        }
        len = 0;
        predicate = "";
        variables = "";
        String[] _;
        if (rule.startsWith("'")) {
            int index = rule.lastIndexOf("'");
            predicate = rule.substring(0, index) + "'";
            _ = rule.substring(index + 1, rule.length()).split("\\(");
        } else if (rule.startsWith("\"")) {
            int index = rule.lastIndexOf("\"");
            predicate = rule.substring(0, index) + "\"";
            _ = rule.substring(index + 1, rule.length()).split("\\(");
        } else {
            _ = rule.split("\\(");
            predicate = _[0];

        }
        if ((_.length > 1) && (_[1] != null)) {
            // log.info("vars:"+_);
            _ = _[1].split("\\)");
            _ = _[0].split(",");
            // log.info("vars under ,:"+_);
            variables = "(";
            for (String argument : _) {
                argument = argument.trim();
                if (Character.isLowerCase(argument.charAt(0))) {
                    // variables+="c"+Utils.getHash(argument)+", ";
                    variables += "c" + getHashedLabel(argument) + ", ";
                } else {
                    variables += argument + ", ";
                }
            }
            variables = variables.substring(0, variables.length() - 2);
            variables += ")";
            len = _.length;
        }
        // predicate = Utils.getHash(Utils.replaceQuotes(predicate));
        predicate = getHashedLabel(Utils.replaceQuotes(predicate));
    }
    
     public String getHashedLabel(String label) { 
    	 return Utils.getHash(label);
    }
}

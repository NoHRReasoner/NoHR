package local.translate;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class Utils.
 */
public class Utils {
    /*
     * private static Pattern p = Pattern.compile(
     * "(?x)          # enable comments                                      \n"
     * +
     * "((\"[^\"]*\")|# quoted data, and store in group #1                   \n"
     * +
     * "(\"[^\"]*\")) # quoted data, and store in group #1                   \n"
     * +
     * "|             # OR                                                   \n"
     * +
     * "([^,]+)       # one or more chars other than ',', and store it in #2 \n"
     * +
     * "|             # OR                                                   \n"
     * +
     * "\\s*,\\s*     # a ',' optionally surrounded by space-chars           \n"
     * );
     */
    /** The p. */
    private static Pattern p = Pattern.compile("(\\w+\\s)?(\\\"[^\\\"]+\\\"|'[^']+'|\\w+)(\\(\\w+\\d?\\s?(,\\s?\\w+\\d?)*\\))?");

    /**
     * _d allrule.
     *
     * @param rule the rule
     * @return the string
     */
    public static String _dAllrule(String rule) {
        // System.out.println("Original rule for doub.: "+rule);
        String[] _ = rule.split("\\)\\s*,");
        String result = "";
        if (_.length == 1) {
            result = Utils.getSubRule(rule);
        } else {
            for (String s : _) {
                result += Utils.getSubRule(s);
                if (s.contains("(") && !s.endsWith(")")) {
                    result += ")";
                }
                result += ", ";
            }
            result = result.substring(0, result.length() - 2);
        }
        // System.out.println("Final doub. rule: "+result);
        return result;
    }

    /**
     * Gets the eq for rule.
     *
     * @return the eq for rule
     */
    public static String getEqForRule() {
        return " " + Config.eq + " ";
    }

    /**
     * Gets the hash.
     *
     * @param s the s
     * @return the hash
     */
    public static String getHash(String s) {
        String result = DigestUtils.md5Hex(s);
        // for(int i=0;i<10;i++) result = result.replace(Integer.toString(i),
        // letters.get(i));
        return result;
    }


    /**
     * Gets the sub rule.
     *
     * @param rule the rule
     * @return the sub rule
     */
    public static String getSubRule(String rule) {
        rule = rule.trim();
        // System.out.println("rule of d.: "+rule+" and result is: "+"d"+rule.substring(1,rule.length()));
        if (rule.startsWith(Config.negation)) {
            rule = rule.replaceFirst(Config.negation + " ", "");
            return Config.negation + " a" + rule.substring(1, rule.length());
        }
        return "d" + rule.substring(1, rule.length());
        // ParsedRule parsedRule = new ParsedRule(rule);
        // return parsedRule.getPlainSubRule();
    }

    /**
     * Gets the sub rules from rule.
     *
     * @param rule the rule
     * @return the sub rules from rule
     */
    public static ArrayList<String> getSubRulesFromRule(String rule) {

        Matcher m = p.matcher(rule);
        ArrayList<String> rules = new ArrayList<String>();
        while (m.find()) {
            rules.add(m.group().trim());
        }
        return rules;
    }

    /**
     * Replace quotes.
     *
     * @param predicate the predicate
     * @return the string
     */
    public static String replaceQuotes(String predicate) {
        String result = "";
        if ((predicate.startsWith("'") && predicate.endsWith("'")) || (predicate.startsWith("\"") && predicate.endsWith("\""))) {
            result = predicate.substring(1, predicate.length() - 1);
        } else {
            result = predicate;
        }

        return result;
    }
}

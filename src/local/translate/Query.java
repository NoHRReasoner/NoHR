package local.translate;


/**
 * The Class Query.
 */
public class Query {

    /** The parsed rule. */
    private ParsedRule parsedRule;

    /** The cm. */
    private final CollectionsManager cm;

    /**
     * Instantiates a new query.
     *
     * @param collectionsManager the collections manager
     */
    public Query(CollectionsManager collectionsManager) {
        cm = collectionsManager;
    }

    /**
     * Prepare query.
     *
     * @param q                          the query
     * @param isAnyDisjointWithStatement the is any disjoint with statement
     * @return the string
     */
    public String prepareQuery(String q, boolean isAnyDisjointWithStatement) {
        String result = "";
        String tabledRule = "";
        boolean isAvailable;
        for (String s : Utils.getSubRulesFromRule(q)) {
            parsedRule = new ParsedRule(s);
            isAvailable = true;
            if (parsedRule.isUnderTnot()) {
                tabledRule = isAnyDisjointWithStatement ? parsedRule.getTabledDoubledRule() : parsedRule.getTabledRule();
                isAvailable = cm.isTabled(tabledRule);
            }
            if (isAvailable) {
                result += (isAnyDisjointWithStatement ? parsedRule.getHashedRuleForQuery() : parsedRule.getPlainHashedRule())+ ", ";
            }
        }
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

}
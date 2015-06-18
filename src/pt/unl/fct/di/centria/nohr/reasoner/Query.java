package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.Set;

import other.Utils;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ParsedRule;

/**
 * The Class Query.
 */
public class Query {

    /** The parsed rule. */
    private ParsedRule parsedRule;

    /** The cm. */

    private Set<String> tablePredicatesOntology;
    private Set<String> tablePredicatesRules;

    /**
     * Instantiates a new query.
     *
     * @param collectionsManager
     *            the collections manager
     */
    public Query(Set<String> tabledPredicates, Set<String> tabledRulePredicates) {
	tablePredicatesOntology = tabledPredicates;
	tablePredicatesRules = tabledRulePredicates;
    }

    public boolean isTabled(String title) {
	return tablePredicatesOntology.contains(title)
		|| tablePredicatesRules.contains(title);
    }

    /**
     * Prepare query.
     *
     * @param q
     *            the query
     * @param isAnyDisjointWithStatement
     *            the is any disjoint with statement
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
		tabledRule = isAnyDisjointWithStatement ? parsedRule
			.getTabledDoubledRule() : parsedRule.getTabledRule();
			isAvailable = isTabled(tabledRule);
	    }
	    if (isAvailable)
		result += (isAnyDisjointWithStatement ? parsedRule
			.getHashedRuleForQuery() : parsedRule
			.getPlainHashedRule())
			+ ", ";
	}
	if (result.endsWith(", "))
	    result = result.substring(0, result.length() - 2);
	return result;
    }

}
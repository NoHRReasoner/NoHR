/**
 *
 */
package pt.unl.fct.di.centria.nohr.rulebase;

import java.util.Collection;

import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * Represents a non monotonic rule-base. Is is a {@link Collection collection} of {@link Rules non-monotonic rules}.
 *
 * @author Nuno Costa
 */
public interface RuleBase extends Collection<Rule> {

	/**
	 * Adds a {@link RuleBaseListener}, which listens all changes to this {@link RuleBase rule-base}.
	 *
	 * @param listner
	 *            a {@link RuleBaseListener}.
	 */
	void addListner(RuleBaseListener listner);

	/**
	 * Removes a previously added {@link RuleBaseListener}.
	 *
	 * @param listner
	 *            a {@link RuleBaseListener}.
	 */
	void removeListener(RuleBaseListener listener);

	/**
	 * Update a given {@link RuleBase rule-base's} rule. If the given role isn't in this {@link RuleBase rule-base}, nothing is done.
	 *
	 * @param oldRule
	 *            the rule that will be replaced.
	 * @param newRule
	 *            the new rule.
	 * @return true iff the the rule could be updated, i.e. this {@link RuleBase rule-base} contained {@code oldRule}.
	 */
	boolean update(Rule oldRule, Rule newRule);

}
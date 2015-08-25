/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.Set;

/**
 * Represents a <i>nonmonotonic logic program</i>. Is is a {@link Set set} of {@link Rules non-monotonic rules}.
 *
 * @author Nuno Costa
 */
public interface Program extends Set<Rule> {

	/**
	 * Adds a {@link ProgramChangeListener}, which listens all changes to this {@link Program rule-base}.
	 *
	 * @param listner
	 *            a {@link ProgramChangeListener}.
	 */
	void addListener(ProgramChangeListener listner);

	/**
	 * Removes a previously added {@link ProgramChangeListener}.
	 *
	 * @param listner
	 *            a {@link ProgramChangeListener}.
	 */
	void removeListener(ProgramChangeListener listener);

	/**
	 * Update a given {@link Program rule-base's} rule. If the given role isn't in this {@link Program rule-base}, nothing is done.
	 *
	 * @param oldRule
	 *            the rule that will be replaced.
	 * @param newRule
	 *            the new rule.
	 * @return true iff the the rule could be updated, i.e. this {@link Program rule-base} contained {@code oldRule}.
	 */
	boolean update(Rule oldRule, Rule newRule);

}
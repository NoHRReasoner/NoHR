/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

/**
 * A {@link Program program} listener.
 *
 * @author Nuno Costa
 */
public interface ProgramChangeListener {

	/**
	 * Called when a given rule was added to the program.
	 *
	 * @param rule
	 *            the rule that was added.
	 */
	public void added(Rule rule);

	/**
	 * Called when the program was cleared.
	 */
	public void cleared();

	/**
	 * Called when a given rule was removed from the program.
	 *
	 * @param rule
	 *            the rule that was added.
	 */
	public void removed(Rule rule);

	/**
	 * Called when a given rule was updated
	 *
	 * @param oldRule
	 *            the old rule.
	 * @param newRule
	 *            the new rule.
	 */
	public void updated(Rule oldRule, Rule newRule);

}

package pt.unl.fct.di.novalincs.nohr.deductivedb;

import java.util.Collection;

import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * Represents a logic program (i.e. set of {@link Rules rules}) loaded in a certain {@link DeductiveDatabase}. All changes to a program are reflected
 * in the later queries to the {@link DeductiveDatabase} where the program is loaded.
 *
 * @author Nuno Costa
 */
public interface DatabaseProgram {

	/**
	 * Adds a given {@link Rule rule} to this {@link DatabaseProgram program}.
	 *
	 * @param rule
	 *            the rule to be added.
	 */
	void add(Rule rule);

	/**
	 * Adds all rules of given {@link Collection collection} of {@link Rule rules} to this {@link DatabaseProgram program}.
	 *
	 * @param rules
	 *            the collection of rules to be added.
	 */
	void addAll(Collection<Rule> rules);

	/**
	 * Removes all the {@link Rule rules} from this {@link DatabaseProgram program}.
	 */
	void clear();

	/**
	 * Returns the {@link DeductiveDatabase} where this {@link DatabaseProgram program} is loaded.
	 *
	 * @return
	 */
	DeductiveDatabase getDeductiveDatabase();

	/**
	 * Removes a given {@link Rule rule} from this {@link DatabaseProgram program}.
	 *
	 * @param rule
	 *            the rule to be removed.
	 */
	void remove(Rule rule);

	/**
	 * Removes all rules of given {@link Collection collection} of {@link Rule rules} from this {@link DatabaseProgram program}.
	 *
	 * @param rules
	 *            the collection of rules to be removed.
	 */
	void removeAll(Collection<Rule> rules);
}

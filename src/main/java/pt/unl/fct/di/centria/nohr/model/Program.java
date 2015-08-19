/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.Set;

/**
 * Represents a Prolog program. Is composed by a set of tabled predicates, failed predicates and rules.
 *
 * @author Nuno Costa
 */
public interface Program {

	public Program accept(ModelVisitor visitor);

	/**
	 * Gets the hash of this {@link Program program}, an integer that univocally identifies this program.
	 *
	 * @return the hash of this {@link Program program}
	 */
	public String getHash();

	/**
	 * Return the set of rules in this {@link Program program}.
	 *
	 * @return the set of rules in this {@link Program program}
	 */
	public Set<Rule> getRules();

	/**
	 * Get the set of table directives in this {@link Program program}.
	 *
	 * @return the set of table directives in this {@link Program program}.
	 */
	public Set<TableDirective> getTableDirectives();

}

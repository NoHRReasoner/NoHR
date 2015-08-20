/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.Set;

/**
 * Represents a logic program. Is composed by the a set of {@link Rule rules} and {@link TableDirective directives} specifying which predicates must
 * be tabled, and s univocally identified by a certain {@link Object}.
 *
 * @author Nuno Costa
 */
public interface Program {

	public Program accept(ModelVisitor visitor);

	/**
	 * Gets the ID of this {@link Program program}, an object that univocally identifies this {@link Program}.
	 *
	 * @return the ID of this {@link Program program}
	 */
	public Object getID();

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

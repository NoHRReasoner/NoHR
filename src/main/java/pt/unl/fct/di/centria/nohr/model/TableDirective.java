/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * Represents a table directive.
 *
 * @author Nuno Costa
 */
public interface TableDirective extends FormatVisitable {

	public TableDirective accept(ModelVisitor visitor);

	/**
	 * Returns the tabled predicate.
	 *
	 * @return the table predicate.
	 */
	public Predicate getPredicate();

}

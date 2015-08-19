/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * Implementation of {@link TableDirective}.
 *
 * @author Nuno Costa
 */
public class TableDirectiveImpl implements TableDirective {

	/** The table predicate. */
	private final Predicate predicate;

	/**
	 * Creates a table directive for a specified predicate.
	 *
	 * @param the
	 *            predicate.
	 */
	TableDirectiveImpl(Predicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public TableDirective accept(ModelVisitor visitor) {
		return new TableDirectiveImpl(predicate.accept(visitor));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TableDirectiveImpl other = (TableDirectiveImpl) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}

	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (predicate == null ? 0 : predicate.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "table(" + predicate.toString() + ")";
	}

}

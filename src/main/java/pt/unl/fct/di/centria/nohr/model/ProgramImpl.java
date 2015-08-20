/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of {@link Program}.
 *
 * @author Nuno Costa
 */
public class ProgramImpl implements Program {

	/** The object that univocally identifies this {@link Program program} */
	private final Object id;

	/* The set of table directives in this {@link Program program} */
	private final Set<TableDirective> tableDirectives;

	/** The set of rules in this {@link Program program}. */
	private final Set<Rule> rules;

	/**
	 * Constructs a {@link Program program} with given sets of tabled predicate, failed predicates, and rules.
	 *
	 * @id the {@link Object} that univocally identified the {@link Program program}.
	 * @param tableDirectives
	 *            the tabled predicates.
	 * @param rules
	 *            the rules.
	 */
	ProgramImpl(Object id, Set<TableDirective> tableDirectives, Set<Rule> rules) {
		Objects.requireNonNull(tableDirectives);
		Objects.requireNonNull(rules);
		this.id = id;
		this.tableDirectives = tableDirectives;
		this.rules = rules;
	}

	@Override
	public Program accept(ModelVisitor visitor) {
		final Set<TableDirective> tabledPredicates = new HashSet<>();
		final Set<Rule> rules = new HashSet<>();
		for (final TableDirective predicate : tableDirectives)
			tabledPredicates.add(predicate.accept(visitor));
		for (final Rule rule : this.rules)
			rules.add(rule.accept(visitor));
		return new ProgramImpl(id, tabledPredicates, rules);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ProgramImpl))
			return false;
		final ProgramImpl other = (ProgramImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (rules == null) {
			if (other.rules != null)
				return false;
		} else if (!rules.equals(other.rules))
			return false;
		if (tableDirectives == null) {
			if (other.tableDirectives != null)
				return false;
		} else if (!tableDirectives.equals(other.tableDirectives))
			return false;
		return true;
	}

	@Override
	public Object getID() {
		return id;
	}

	@Override
	public Set<Rule> getRules() {
		return rules;
	}

	@Override
	public Set<TableDirective> getTableDirectives() {
		return tableDirectives;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (rules == null ? 0 : rules.hashCode());
		result = prime * result + (tableDirectives == null ? 0 : tableDirectives.hashCode());
		return result;
	}

}

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

	/* The set of table directives in this {@link Program program} */
	private final Set<TableDirective> tableDirectives;

	/** The set of rules in this {@link Program program}. */
	private final Set<Rule> rules;

	/**
	 * Constructs a {@link Program program} with given sets of tabled predicate, failed predicates, and rules.
	 *
	 * @param tabledPredicates
	 *            the tabled predicates.
	 * @param failedPredicates
	 *            the failed predicates.
	 * @param rules
	 *            the rules.
	 */
	ProgramImpl(Set<TableDirective> tabledPredicates, Set<Rule> rules) {
		Objects.requireNonNull(tabledPredicates);
		Objects.requireNonNull(rules);
		tableDirectives = tabledPredicates;
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
		return new ProgramImpl(tabledPredicates, rules);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ProgramImpl other = (ProgramImpl) obj;
		if (tableDirectives == null) {
			if (other.tableDirectives != null)
				return false;
		} else if (!tableDirectives.equals(other.tableDirectives))
			return false;
		if (rules == null) {
			if (other.rules != null)
				return false;
		} else if (!rules.equals(other.rules))
			return false;
		return true;
	}

	@Override
	public String getHash() {
		// TODO implement
		return "";
	}

	@Override
	public Set<Rule> getRules() {
		return rules;
	}

	@Override
	public Set<TableDirective> getTableDirectives() {
		return tableDirectives;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (rules == null ? 0 : rules.hashCode());
		result = prime * result + (tableDirectives == null ? 0 : tableDirectives.hashCode());
		return result;
	}

}

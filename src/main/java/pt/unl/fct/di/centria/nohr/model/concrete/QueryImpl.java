package pt.unl.fct.di.centria.nohr.model.concrete;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.StringUtils;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypeVisitor;

/**
 * Implementation of {@link Query}.
 *
 * @author Nuno Costa
 */
public class QueryImpl implements Query {

	/** The query's literals */
	private final List<Literal> literals;

	/** The query's free variables */
	private final List<Variable> variables;

	private final Map<Variable, Integer> index;

	/**
	 * Construct a query with a specified list of literals and a specified list of free variables.
	 *
	 * @param literals
	 *            the query's literals.
	 * @param variables
	 *            the query's free variables. All the those variables must appear in {@code literals}.
	 * @throws IllegalArgumentException
	 *             if {@code variables} contains some variable that doesn't appear in {@code literals}.
	 */
	QueryImpl(List<Literal> literals, List<Variable> variables) {
		final Set<Variable> vars = new HashSet<>();
		for (final Literal literal : literals)
			vars.addAll(literal.getVariables());
		for (final Variable var : variables)
			if (!vars.contains(var))
				throw new IllegalArgumentException("variables " + var + "doesn't appear in" + literals);
		this.literals = literals;
		this.variables = variables;
		index = new HashMap<>();
		int i = 0;
		for (final Variable var : variables)
			index.put(var, i++);
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Query accept(ModelVisitor visitor) {
		final List<Literal> lits = new LinkedList<Literal>();
		final List<Variable> vars = new LinkedList<Variable>();
		for (final Literal literal : literals)
			lits.add(literal.accept(visitor));
		for (final Variable var : variables)
			vars.add(var.accept(visitor));
		return new QueryImpl(lits, vars);
	}

	@Override
	public Query apply(List<Term> termList) {
		if (termList.size() != variables.size())
			throw new IllegalArgumentException(
					"termList size must have the same size that the number of variables of the query");
		final Map<Variable, Term> map = new HashMap<Variable, Term>();
		final Iterator<Variable> varsIt = variables.iterator();
		final Iterator<Term> listIt = termList.iterator();
		while (varsIt.hasNext() && listIt.hasNext())
			map.put(varsIt.next(), listIt.next());
		final List<Literal> lits = new LinkedList<Literal>();
		for (final Literal literal : literals)
			lits.add(literal.apply(map));
		return new QueryImpl(lits, new LinkedList<Variable>());
	}

	private Query encode(PredicateType predicateType) {
		final ModelVisitor encoder = new PredicateTypeVisitor(predicateType);
		return accept(encoder);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QueryImpl))
			return false;
		final QueryImpl other = (QueryImpl) obj;
		if (literals == null) {
			if (other.literals != null)
				return false;
		} else if (!literals.equals(other.literals))
			return false;
		if (variables == null) {
			if (other.variables != null)
				return false;
		} else if (!variables.equals(other.variables))
			return false;
		return true;
	}

	@Override
	public Query getDouble() {
		return encode(PredicateType.DOUBLE);
	}

	@Override
	public Map<Variable, Integer> getIndex() {
		return index;
	}

	@Override
	public List<Literal> getLiterals() {
		return literals;
	}

	@Override
	public Query getOriginal() {
		return encode(PredicateType.ORIGINAL);
	}

	@Override
	public List<Variable> getVariables() {
		return variables;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (literals == null ? 0 : literals.hashCode());
		result = prime * result + (variables == null ? 0 : variables.hashCode());
		return result;
	}

	@Override
	public String toString() {
		final String vars = StringUtils.concat(",", variables);
		return "q(" + vars + "):-" + StringUtils.concat(",", literals);
	}
}

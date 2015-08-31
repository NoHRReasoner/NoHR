package pt.unl.fct.di.centria.nohr.model.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import pt.unl.fct.di.centria.nohr.StringUtils;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * Implementation of {@link Answer}.
 *
 * @author Nuno Costa
 */
public class AnswerImpl implements Answer {

	/**
	 * The query to which the answer applies.
	 */
	private final Query query;

	/**
	 * The truth value of the answer.
	 */
	private final TruthValue truthValue;

	/**
	 * The list of terms to which each query's variable is mapped.
	 */
	private final List<Term> values;

	/**
	 * Constructs an answer to a specified query, with the specified truth value and substitution.
	 *
	 * @param query
	 *            the query to which the answer applies.
	 * @param truthValue
	 *            the truth value of the answer.
	 * @param values
	 *            the list of terms to which each query's variable is mapped.
	 * @param query.getIndex()
	 *            the mapping between the query's variables and the position in {@code values} where the terms to which those variables are mapped
	 *            appear.
	 */
	AnswerImpl(Query query, TruthValue truthValue, List<Term> values) {
		Objects.requireNonNull(query);
		Objects.requireNonNull(truthValue);
		Objects.requireNonNull(values);
		this.query = query;
		this.truthValue = truthValue;
		this.values = values;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Answer accept(ModelVisitor visitor) {
		final List<Term> vals = new ArrayList<Term>();
		for (final Term val : values)
			vals.add(val.accept(visitor));
		return new AnswerImpl(query, truthValue, vals);
	}

	@Override
	public List<Literal> apply() {
		final Map<Variable, Term> substitution = new HashMap<Variable, Term>();
		for (final Entry<Variable, Integer> entry : query.getIndex().entrySet())
			substitution.put(entry.getKey(), values.get(entry.getValue()));
		final List<Literal> literals = new LinkedList<Literal>();
		for (final Literal literal : query.getLiterals())
			literals.add(literal.apply(substitution));
		return literals;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AnswerImpl))
			return false;
		final AnswerImpl other = (AnswerImpl) obj;
		if (truthValue != other.truthValue)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		if (query.getIndex() == null) {
			if (other.query.getIndex() != null)
				return false;
		} else if (!query.getIndex().equals(other.query.getIndex()))
			return false;
		return true;
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public TruthValue getValuation() {
		return truthValue;
	}

	@Override
	public Term getValue(Variable var) {
		return values.get(query.getIndex().get(var));
	}

	@Override
	public List<Term> getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (truthValue == null ? 0 : truthValue.hashCode());
		result = prime * result + (values == null ? 0 : values.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return StringUtils.concat(",", apply());
	}

}

package pt.unl.fct.di.centria.nohr.model.predicates;

import java.util.Objects;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

/**
 * Implementation of {@link Predicate}.
 *
 * @author Nuno Costa
 */
public class RulePredicateImpl implements RulePredicate {

	/** The arity of this predicate */
	protected final int arity;

	/** The symbol that represents this predicate */
	protected final String symbol;

	/**
	 * Constructs a predicate represented by a specified symbol with a specified arity.
	 *
	 * @param symbol
	 *            the symbol that represents this predicate. Must be an non-empty string.
	 * @param arity
	 *            the arity of this predicate. Must be a positive integer.
	 * @throws IllegalArgumentException
	 *             if {@code symbol} is an empty string or {@code arity} is negative.
	 */
	RulePredicateImpl(String symbol, int arity) {
		Objects.requireNonNull(symbol);
		Objects.requireNonNull(arity);
		if (symbol.length() <= 0)
			throw new IllegalArgumentException("symbol: can't be an empty string");
		if (arity < 0)
			throw new IllegalArgumentException("arity: must be positive");
		this.symbol = symbol;
		this.arity = arity;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Predicate accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RulePredicateImpl))
			return false;
		final RulePredicateImpl other = (RulePredicateImpl) obj;
		if (arity != other.arity)
			return false;
		if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public String getSignature() {
		return symbol + "/" + arity;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arity;
		result = prime * result + symbol.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return symbol;
	}

}

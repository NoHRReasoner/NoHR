package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Implementation of rule {@link Constant}.
 *
 * @author Nuno Costa
 */
public class RuleConstantImpl implements Constant {

	/**
	 * The symbol that represents this constant.
	 */
	private final String symbol;

	/**
	 * Constructs a rule constant with a specified symbol.
	 *
	 * @param symbol
	 *            the symbol.
	 */
	RuleConstantImpl(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Constant accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Constant asConstant() {
		return this;
	}

	@Override
	public Number asNumber() {
		throw new ClassCastException();
	}

	@Override
	public OWLIndividual asOWLIndividual() {
		throw new ClassCastException();
	}

	@Override
	public OWLLiteral asOWLLiteral() {
		throw new ClassCastException();
	}

	@Override
	public String asRuleConstant() {
		return symbol;
	}

	@Override
	public TruthValue asTruthValue() {
		if (symbol.equals("true"))
			return TruthValue.TRUE;
		else if (symbol.equals("undefined"))
			return TruthValue.UNDEFINED;
		else if (symbol.equals("false"))
			return TruthValue.FALSE;
		else
			throw new ClassCastException();
	}

	@Override
	public Variable asVariable() {
		throw new ClassCastException();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RuleConstantImpl))
			return false;
		final RuleConstantImpl other = (RuleConstantImpl) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return symbol.hashCode();
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public boolean isOWLIndividual() {
		return false;
	}

	@Override
	public boolean isOWLLiteral() {
		return false;
	}

	@Override
	public boolean isRuleConstant() {
		return true;
	}

	@Override
	public boolean isTruthValue() {
		return symbol.equals("true") || symbol.equals("undefined") || symbol.equals("false");
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public String toString() {
		return symbol;
	}

}

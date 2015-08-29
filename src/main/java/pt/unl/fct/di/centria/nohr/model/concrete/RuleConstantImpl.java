package pt.unl.fct.di.centria.nohr.model.concrete;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Visitor;

/**
 * Implementation of rule {@link Constant}.
 *
 * @author Nuno Costa
 */
public class RuleConstantImpl implements RuleConstant {

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
	public void accept(Visitor visitor) {
		visitor.visit(this);
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

	@Override
	public String getSymbol() {
		return symbol;
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
	public String toString() {
		return symbol;
	}

}

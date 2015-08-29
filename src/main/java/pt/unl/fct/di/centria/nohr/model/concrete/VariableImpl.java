package pt.unl.fct.di.centria.nohr.model.concrete;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * Implementation of {@link Variable}.
 *
 * @author Nuno Costa
 */
public class VariableImpl implements Variable {

	/**
	 * The symbol that represents this variable.
	 */
	private final String symbol;

	/**
	 * Constructs a symbol with a specified symbol
	 *
	 * @param symbol
	 */
	VariableImpl(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Variable accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public int compareTo(Variable o) {
		return symbol.compareTo(o.getSymbol());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariableImpl))
			return false;
		final VariableImpl other = (VariableImpl) obj;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (symbol == null ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "?" + symbol;
	}

}

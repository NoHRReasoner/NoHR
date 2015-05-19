package nohr.model;

import java.util.List;

public class ConstantImpl implements Constant {
	
	private String symbol;

	public ConstantImpl(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public Constant asConstant() {
		return this;
	}

	@Override
	public List<Term> asList() throws ModelException {
		throw new ModelException();
	}

	@Override
	public Number asNumber() throws ModelException {
		throw new ModelException();
	}

	@Override
	public String asString() {
		return symbol;
	}

	@Override
	public TruthValue asTruthValue() throws ModelException {
		if (symbol.equals("true"))
			return TruthValue.TRUE;
		else if (symbol.equals("undefined"))
			return TruthValue.UNDIFINED;
		else if (symbol.equals("false"))
			return TruthValue.FALSE;
		else 
			throw new ModelException();
	}

	@Override
	public Variable asVariable() throws ModelException {
		throw new ModelException();
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

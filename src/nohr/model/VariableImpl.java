package nohr.model;

import java.util.List;

public class VariableImpl implements Variable {
	
	private String symbol;

	public VariableImpl(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public Constant asConstant() throws ModelException {
		throw new ModelException();
	}

	@Override
	public List<Term> asList() throws ModelException {
		throw new ModelException();
	}

	@Override
	public Variable asVariable() throws ModelException {
		return this;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isVariable() {
		return true;
	}
	
	@Override
	public String toString() {
		return symbol;
	}

}

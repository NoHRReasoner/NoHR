package nohr.model;

import java.util.List;

public class NumericConstantImpl implements Constant {
	
	private Number number;

	public NumericConstantImpl(Number number) {
		this.number = number;
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
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public Number asNumber() {
		return number;
	}

	@Override
	public String asString() throws ModelException {
		throw new ModelException();
	}

	@Override
	public TruthValue asTruthValue() throws ModelException {
		throw new ModelException();
	}
	
	@Override
	public Variable asVariable() throws ModelException {
		throw new ModelException();
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public boolean isTruthValue() {
		return false;
	}	
	
	@Override
	public String toString() {
		return String.valueOf(number.doubleValue());
	}
}

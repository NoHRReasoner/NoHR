package nohr.model;

import java.util.Map;

public class PositiveLiteralImpl extends LiteralImpl implements PositiveLiteral {
	
	
	public PositiveLiteralImpl(Atom atom) {
		super(atom);
	}

	@Override
	public NegativeLiteral asNegativeLiteral() throws ModelException {
		throw new ModelException();
	}

	@Override
	public PositiveLiteral asPositiveLiteral() {
		return this;
	}	

	@Override
	public boolean isNegative() {
		return false;
	}

	@Override
	public boolean isPositive() {
		return true;
	}
	
	@Override
	public String toString() {
		return atom.toString();
	}

	@Override
	public Literal apply(Variable var, Term term) {
		return new PositiveLiteralImpl(atom.apply(var, term));
	}

	@Override
	public Literal apply(Map<Variable, Term> substitution) {
		return new PositiveLiteralImpl(atom.apply(substitution));
	}

}

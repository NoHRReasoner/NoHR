package nohr.model;

import java.util.Map;

public class NegativeLiteralImpl extends LiteralImpl implements NegativeLiteral {

	public NegativeLiteralImpl(Atom atom) {
		super(atom);
	}

	@Override
	public Literal apply(Variable var, Term term) {
		return new NegativeLiteralImpl(atom.apply(var, term));
	}

	@Override
	public Literal apply(Map<Variable, Term> substitution) {
		return new NegativeLiteralImpl(atom.apply(substitution));
	}

	@Override
	public NegativeLiteral asNegativeLiteral() throws ModelException {
		return this;
	}

	@Override
	public PositiveLiteral asPositiveLiteral() throws ModelException {
		throw new ModelException();
	}

	@Override
	public boolean isNegative() {
		return true;
	}

	@Override
	public boolean isPositive() {
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("tnot(%s)", atom);
	}

}

package nohr.model;

import java.util.Map;

public interface Literal {
	
	public NegativeLiteral asNegativeLiteral() throws ModelException;
	
	public PositiveLiteral asPositiveLiteral() throws ModelException;
	
	public Literal apply(Variable var, Term term);
	
	public Literal apply(Map<Variable, Term> substitution);
	
	@Override
	public boolean equals(Object obj);
	
	public Atom getAtom();
	
	public boolean isGrounded();
	
	public boolean isNegative();
	
	public boolean isPositive();
	
	@Override
	public String toString();

}

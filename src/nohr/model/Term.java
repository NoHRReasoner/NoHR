package nohr.model;

import java.util.List;

public interface Term {
	
	public Constant asConstant() throws ModelException;
	
	public List<Term> asList() throws ModelException;
	
	public Variable asVariable() throws ModelException;
	
	@Override
	public int hashCode();
	
	public boolean isConstant();
	
	public boolean isList();

	public boolean isVariable();	
	
	@Override
	public String toString();
	
}

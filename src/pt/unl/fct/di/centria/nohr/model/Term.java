package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public interface Term {
	
	public Constant asConstant();
	
	public List<Term> asList();
	
	public Variable asVariable();
	
	@Override
	public int hashCode();
	
	public boolean isConstant();
	
	public boolean isList();

	public boolean isVariable();	
	
	@Override
	public String toString();
	
	public Term acept(Visitor visitor);
	
}

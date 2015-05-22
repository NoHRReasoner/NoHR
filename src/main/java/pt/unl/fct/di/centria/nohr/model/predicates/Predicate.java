package nohr.model.predicates;

import nohr.model.Visitor;

public interface Predicate {
	
	@Override
	public boolean equals(Object obj);
	
	public int getArity();
	
	public String getName();
	
	public String getSymbol();
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
	
	public Predicate acept(Visitor visitor);

}

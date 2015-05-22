package pt.unl.fct.di.centria.nohr.model;

public interface Constant extends Term {

	public Number asNumber() throws ModelException;
	
	public String asString() throws ModelException;
	
	public TruthValue asTruthValue() throws ModelException;
	
	@Override
	public boolean equals(Object obj);
	
	@Override
	public int hashCode();
	
	public boolean isNumber();
	
	public boolean isTruthValue();
	
	@Override
	public String toString();
	
	public Constant acept(Visitor visitor);
	
}

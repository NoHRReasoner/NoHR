package nohr.model;

public interface Answer {
	
	@Override
	public boolean equals(Object obj);
	
	public Query getQuery();
	
	public TruthValue getValuation();

	public Term getValue(Variable var);
	
	@Override
	public String toString();
	
}

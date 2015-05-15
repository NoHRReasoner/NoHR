package nohr.model;

public interface Answer {
	
	public TruthValue getValuation();
	
	public Term getValue(Variable var);

}

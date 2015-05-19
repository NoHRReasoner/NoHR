package nohr.model;

public class PredicateImpl implements Predicate {
	
	String symbol;
	
	int arity;

	public PredicateImpl(String symbol, int arity) {
		this.symbol = symbol;
		this.arity = arity;
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public String getName() {
		return symbol + "/" + arity;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return symbol;
	}

}

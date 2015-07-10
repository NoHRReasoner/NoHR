package pt.unl.fct.di.centria.nohr.model.predicates;

import pt.unl.fct.di.centria.nohr.model.Visitor;

public class PredicateImpl implements Predicate {

    protected final int arity;

    protected final String symbol;

    public PredicateImpl(String symbol, int arity) {
	this.symbol = symbol;
	this.arity = arity;
    }

    @Override
    public Predicate acept(Visitor visitor) {
	return visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof PredicateImpl))
	    return false;
	final PredicateImpl other = (PredicateImpl) obj;
	if (arity != other.arity)
	    return false;
	if (symbol == null) {
	    if (other.symbol != null)
		return false;
	} else if (!symbol.equals(other.symbol))
	    return false;
	return true;
    }

    @Override
    public int getArity() {
	return arity;
    }

    @Override
    public String getName() {
	return "'" + symbol + "'/" + arity;
    }

    @Override
    public String getSymbol() {
	return symbol;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + arity;
	result = prime * result + (symbol == null ? 0 : symbol.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "'" + symbol + "'";
    }

}

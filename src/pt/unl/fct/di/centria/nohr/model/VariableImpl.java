package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public class VariableImpl implements Variable {

    private final String symbol;

    VariableImpl(String symbol) {
	this.symbol = symbol;
    }

    @Override
    public String acept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Variable acept(Visitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Constant asConstant() {
	throw new ClassCastException();
    }

    @Override
    public List<Term> asList() {
	throw new ClassCastException();
    }

    @Override
    public Variable asVariable() {
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Variable o) {
	return symbol.compareTo(o.getSymbol());
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
	if (!(obj instanceof VariableImpl))
	    return false;
	final VariableImpl other = (VariableImpl) obj;
	if (symbol == null) {
	    if (other.symbol != null)
		return false;
	} else if (!symbol.equals(other.symbol))
	    return false;
	return true;
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
	result = prime * result + (symbol == null ? 0 : symbol.hashCode());
	return result;
    }

    @Override
    public boolean isConstant() {
	return false;
    }

    @Override
    public boolean isList() {
	return false;
    }

    @Override
    public boolean isVariable() {
	return true;
    }

    @Override
    public String toString() {
	return "?" + symbol;
    }

}

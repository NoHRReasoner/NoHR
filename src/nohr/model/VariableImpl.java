package nohr.model;

import java.util.List;

public class VariableImpl implements Variable {

    private String symbol;

    public VariableImpl(String symbol) {
	this.symbol = symbol;
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

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Variable))
	    return false;
	Variable var = (Variable) obj;
	return symbol.equals(var.getSymbol());
    }

    @Override
    public String getSymbol() {
	return symbol;
    }

    @Override
    public int hashCode() {
	return symbol.hashCode();
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
	return symbol;
    }

}

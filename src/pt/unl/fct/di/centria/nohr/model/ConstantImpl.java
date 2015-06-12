package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public class ConstantImpl implements Constant {

    private String symbol;

    ConstantImpl(String symbol) {
	this.symbol = symbol;
    }

    @Override
    public Constant acept(Visitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Constant asConstant() {
	return this;
    }

    @Override
    public List<Term> asList() {
	throw new ClassCastException();
    }

    @Override
    public Number asNumber() {
	throw new ClassCastException();
    }

    @Override
    public String asString() {
	return symbol;
    }

    @Override
    public TruthValue asTruthValue() throws ModelException {
	if (symbol.equals("true"))
	    return TruthValue.TRUE;
	else if (symbol.equals("undefined"))
	    return TruthValue.UNDEFINED;
	else if (symbol.equals("false"))
	    return TruthValue.FALSE;
	else
	    throw new ModelException();
    }

    @Override
    public Variable asVariable() {
	throw new ClassCastException();
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
	if (!(obj instanceof ConstantImpl))
	    return false;
	ConstantImpl other = (ConstantImpl) obj;
	if (symbol == null) {
	    if (other.symbol != null)
		return false;
	} else if (!symbol.equals(other.symbol))
	    return false;
	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return symbol.hashCode();
    }

    @Override
    public boolean isConstant() {
	return true;
    }

    @Override
    public boolean isList() {
	return false;
    }

    @Override
    public boolean isNumber() {
	return false;
    }

    @Override
    public boolean isTruthValue() {
	return symbol.equals("true") || symbol.equals("undefined")
		|| symbol.equals("false");
    }

    @Override
    public boolean isVariable() {
	return false;
    }

    @Override
    public String toString() {
	return symbol;
    }

}

package nohr.model;

import java.util.List;

public class NumericConstantImpl implements Constant {

    private Number number;

    public NumericConstantImpl(Number number) {
	this.number = number;
    }

    @Override
    public Constant acept(Visitor visit) {
	return visit.visit(this);
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
    public Number asNumber() {
	return number;
    }

    @Override
    public String asString() throws ModelException {
	throw new ModelException();
    }

    @Override
    public TruthValue asTruthValue() throws ModelException {
	throw new ModelException();
    }

    @Override
    public Variable asVariable() {
	throw new ClassCastException();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	NumericConstantImpl other = (NumericConstantImpl) obj;
	if (number == null) {
	    if (other.number != null)
		return false;
	} else if (!number.equals(other.number))
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	return number.hashCode();
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
	return true;
    }

    @Override
    public boolean isTruthValue() {
	return false;
    }

    @Override
    public boolean isVariable() {
	return false;
    }

    @Override
    public String toString() {
	return String.valueOf(number.doubleValue());
    }
}

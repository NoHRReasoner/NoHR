package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

public class NumericConstantImpl implements Constant {

    private final Number number;

    NumericConstantImpl(Number number) {
	final double dval = number.doubleValue();
	if (number.shortValue() == dval)
	    number = number.shortValue();
	else if (number.intValue() == dval)
	    number = number.intValue();
	else if (number.longValue() == dval)
	    number = number.longValue();
	else if (number.floatValue() == dval)
	    number = number.floatValue();
	else
	    number = number.doubleValue();
	this.number = number;
    }

    @Override
    public String acept(FormatVisitor visitor) {
	return visitor.visit(this);
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

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asOWLIndividual()
     */
    @Override
    public OWLIndividual asOWLIndividual() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asOWLLiteral()
     */
    @Override
    public OWLLiteral asOWLLiteral() {
	throw new ClassCastException();
    }

    @Override
    public String asString() {
	throw new ClassCastException();
    }

    @Override
    public TruthValue asTruthValue() {
	throw new ClassCastException();
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
	final NumericConstantImpl other = (NumericConstantImpl) obj;
	if (number == null) {
	    if (other.number != null)
		return false;
	} else if (number.doubleValue() != other.number.doubleValue())
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

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isOWLIndividual()
     */
    @Override
    public boolean isOWLIndividual() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isOWLLiteral()
     */
    @Override
    public boolean isOWLLiteral() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isString()
     */
    @Override
    public boolean isString() {
	return false;
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
	return number.toString();
    }
}

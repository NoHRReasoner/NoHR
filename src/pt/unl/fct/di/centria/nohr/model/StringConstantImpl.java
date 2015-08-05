package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

public class StringConstantImpl implements Constant {

    private final String symbol;

    StringConstantImpl(String symbol) {
	this.symbol = symbol;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.FormatVisitable#acept(pt.unl.fct.di.
     * centria.nohr.model.FormatVisitor)
     */
    @Override
    public String acept(FormatVisitor visitor) {
	return visitor.visit(this);
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
	return symbol;
    }

    @Override
    public TruthValue asTruthValue() {
	if (symbol.equals("true"))
	    return TruthValue.TRUE;
	else if (symbol.equals("undefined"))
	    return TruthValue.UNDEFINED;
	else if (symbol.equals("false"))
	    return TruthValue.FALSE;
	else
	    throw new ClassCastException();
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
	if (!(obj instanceof StringConstantImpl))
	    return false;
	final StringConstantImpl other = (StringConstantImpl) obj;
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
	return true;
    }

    @Override
    public boolean isTruthValue() {
	return symbol.equals("true") || symbol.equals("undefined") || symbol.equals("false");
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

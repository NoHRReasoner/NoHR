package pt.unl.fct.di.centria.nohr.model;

import java.util.Map;

public class PositiveLiteralImpl extends LiteralImpl implements PositiveLiteral {

    PositiveLiteralImpl(Atom atom) {
	super(atom);
    }

    @Override
    public PositiveLiteral acept(Visitor visitor) {
	return new PositiveLiteralImpl(visitor.visit(atom));
    }

    @Override
    public Literal apply(Map<Variable, Term> substitution) {
	return new PositiveLiteralImpl(atom.apply(substitution));
    }

    /*
     * (non-Javadoc)
     *
     * @see nohr.model.Literal#apply(nohr.model.Substitution)
     */
    @Override
    public Literal apply(Substitution sub) {
	return new PositiveLiteralImpl(atom.apply(sub));
    }

    @Override
    public Literal apply(Variable var, Term term) {
	return new PositiveLiteralImpl(atom.apply(var, term));
    }

    @Override
    public NegativeLiteral asNegativeLiteral() throws ModelException {
	throw new ModelException();
    }

    @Override
    public PositiveLiteral asPositiveLiteral() {
	return this;
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
	if (!(obj instanceof PositiveLiteralImpl))
	    return false;
	PositiveLiteralImpl other = (PositiveLiteralImpl) obj;
	if (atom == null) {
	    if (other.atom != null)
		return false;
	} else if (!atom.equals(other.atom))
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
	final int prime = 31;
	int result = 1;
	result = prime * result + (atom == null ? 0 : atom.hashCode());
	return result;
    }

    @Override
    public boolean isNegative() {
	return false;
    }

    @Override
    public boolean isPositive() {
	return true;
    }

    @Override
    public String toString() {
	return atom.toString();
    }
}
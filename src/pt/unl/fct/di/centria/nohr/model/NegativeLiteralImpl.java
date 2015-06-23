package pt.unl.fct.di.centria.nohr.model;

import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public class NegativeLiteralImpl extends LiteralImpl implements NegativeLiteral {

    NegativeLiteralImpl(Atom atom) {
	super(atom);
    }

    @Override
    public NegativeLiteral acept(Visitor visitor) {
	return new NegativeLiteralImpl(visitor.visit(atom));
    }

    @Override
    public Literal apply(Map<Variable, Term> substitution) {
	return new NegativeLiteralImpl(atom.apply(substitution));
    }

    /*
     * (non-Javadoc)
     * 
     * @see nohr.model.Literal#apply(nohr.model.Substitution)
     */
    @Override
    public Literal apply(Substitution sub) {
	return new NegativeLiteralImpl(atom.apply(sub));
    }

    @Override
    public Literal apply(Variable var, Term term) {
	return new NegativeLiteralImpl(atom.apply(var, term));
    }

    @Override
    public NegativeLiteral asNegativeLiteral() {
	return this;
    }

    @Override
    public Atom asPositiveLiteral() {
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
	if (!(obj instanceof NegativeLiteralImpl))
	    return false;
	final NegativeLiteralImpl other = (NegativeLiteralImpl) obj;
	if (atom == null) {
	    if (other.atom != null)
		return false;
	} else if (!atom.equals(other.atom))
	    return false;
	return true;
    }

    @Override
    public Predicate getPredicate() {
	return atom.getPredicate();
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
	return true;
    }

    @Override
    public boolean isPositive() {
	return false;
    }

    @Override
    public String toString() {
	return String.format("tnot(%s)", atom);
    }

}

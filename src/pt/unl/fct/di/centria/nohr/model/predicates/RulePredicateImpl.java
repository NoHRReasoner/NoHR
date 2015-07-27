package pt.unl.fct.di.centria.nohr.model.predicates;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Visitor;

public class RulePredicateImpl implements Predicate {

    protected final int arity;

    protected final String symbol;

    RulePredicateImpl(String symbol, int arity) {
	Objects.requireNonNull(symbol);
	if (symbol.length() <= 0)
	    throw new IllegalArgumentException(
		    "symbol: can't be an empty string");
	if (arity < 0)
	    throw new IllegalArgumentException("arity: must be positive");
	this.symbol = symbol;
	this.arity = arity;
    }

    @Override
    public String acept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Predicate acept(Visitor visitor) {
	return visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asConcept()
     */
    @Override
    public OWLClass asConcept() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asRole()
     */
    @Override
    public OWLProperty<?, ?> asRole() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asRulePredicate()
     */
    @Override
    public String asRulePredicate() {
	return symbol;
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
	if (!(obj instanceof RulePredicateImpl))
	    return false;
	final RulePredicateImpl other = (RulePredicateImpl) obj;
	if (arity != other.arity)
	    return false;
	if (!symbol.equals(other.symbol))
	    return false;
	return true;
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
	result = prime * result + symbol.hashCode();
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isConcept()
     */
    @Override
    public boolean isConcept() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isRole()
     */
    @Override
    public boolean isRole() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isRulePredicate()
     */
    @Override
    public boolean isRulePredicate() {
	return true;
    }

    @Override
    public String toString() {
	return symbol;
    }

}

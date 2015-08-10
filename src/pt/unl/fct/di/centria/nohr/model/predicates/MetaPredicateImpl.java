/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

/**
 * @author nunocosta
 *
 */
public class MetaPredicateImpl implements MetaPredicate {

    protected final Predicate predicate;
    protected final PredicateType type;

    MetaPredicateImpl(Predicate predicate, PredicateType type) {
	Objects.requireNonNull(predicate);
	Objects.requireNonNull(type);
	this.predicate = predicate;
	this.type = type;
    }

    @Override
    public String accept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#acept(pt.unl.fct
     * .di.centria.nohr.model.Visitor)
     */
    @Override
    public Predicate acept(ModelVisitor visitor) {
	return visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asConcept()
     */
    @Override
    public OWLClass asConcept() {
	return predicate.asConcept();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asRole()
     */
    @Override
    public OWLProperty<?, ?> asRole() {
	return predicate.asRole();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asRulePredicate()
     */
    @Override
    public String asRulePredicate() {
	return predicate.asRulePredicate();
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
	if (!(obj instanceof MetaPredicateImpl))
	    return false;
	final MetaPredicateImpl other = (MetaPredicateImpl) obj;
	if (type != other.type)
	    return false;
	if (!predicate.equals(other.predicate))
	    return false;
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#getArity()
     */
    @Override
    public int getArity() {
	return predicate.getArity();
    }

    @Override
    public String getName() {
	return predicate.getSymbol() + "/" + predicate.getArity();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate#getPredicate()
     */
    @Override
    public Predicate getPredicate() {
	return predicate;
    }

    @Override
    public char getPrefix() {
	switch (type) {
	case ORIGINAL:
	    return 'a';
	case DOUBLE:
	    return 'd';
	case ORIGINAL_DOMAIN:
	    return 'e';
	case ORIGINAL_RANGE:
	    return 'f';
	case DOUBLE_DOMAIN:
	    return 'g';
	case DOUBLED_RANGE:
	    return 'h';
	case NEGATIVE:
	    return 'n';
	default:
	    return (char) 0;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#getSymbol()
     */
    @Override
    public String getSymbol() {
	return getPrefix() + predicate.getSymbol();
    }

    @Override
    public PredicateType getType() {
	return type;
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
	result = prime * result + type.hashCode();
	result = prime * result + predicate.hashCode();
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isConcept()
     */
    @Override
    public boolean isConcept() {
	return predicate.isConcept();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isRole()
     */
    @Override
    public boolean isRole() {
	return predicate.isRole();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isRulePredicate()
     */
    @Override
    public boolean isRulePredicate() {
	return isRulePredicate();
    }

    @Override
    public String toString() {
	return getSymbol();
    }

}

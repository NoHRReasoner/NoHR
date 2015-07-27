/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Visitor;

/**
 * @author nunocosta
 *
 */
public class RolePredicateImpl implements Predicate {

    private final OWLProperty<?, ?> role;

    /**
     *
     */
    RolePredicateImpl(OWLProperty<?, ?> role) {
	Objects.requireNonNull(role);
	Objects.requireNonNull(role.getIRI().getFragment(),
		"must have a valid IRI (with fragment)");
	this.role = role;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#acept(pt.unl.fct
     * .di.centria.nohr.model.FormatVisitor)
     */
    @Override
    public String acept(FormatVisitor visitor) {
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
	return role;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asRulePredicate()
     */
    @Override
    public String asRulePredicate() {
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
	if (!(obj instanceof RolePredicateImpl))
	    return false;
	final RolePredicateImpl other = (RolePredicateImpl) obj;
	if (!role.getIRI().equals(other.role.getIRI()))
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
	return 2;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#getName()
     */
    @Override
    public String getName() {
	return getSymbol() + "/" + getArity();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#getSymbol()
     */
    @Override
    public String getSymbol() {
	return role.getIRI().getFragment();
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
	result = prime * result + role.getIRI().hashCode();
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
	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isRulePredicate()
     */
    @Override
    public boolean isRulePredicate() {
	return false;
    }

}

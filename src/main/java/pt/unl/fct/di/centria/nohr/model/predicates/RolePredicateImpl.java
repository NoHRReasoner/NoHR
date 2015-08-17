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
 * Implementation of a {@link Predicate} representing a role.
 *
 * @author Nuno Costa
 */
public class RolePredicateImpl implements Predicate {

	/** The role represented by this predicate. */
	private final OWLProperty<?, ?> role;

	/**
	 * Constructs a predicate representing a specified role.
	 *
	 * @param role
	 *            the role represented by the predicate. Must have a IRI fragment.
	 * @throws IllegalArgumentException
	 *             if {@code role} hasn't a IRI fragment.
	 */
	RolePredicateImpl(OWLProperty<?, ?> role) {
		Objects.requireNonNull(role);
		if (role.getIRI().toURI().getFragment() == null)
			throw new IllegalArgumentException("role: must have an IRI fragment");
		this.role = role;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#acept(pt.unl.fct .di.centria.nohr.model.FormatVisitor)
	 */
	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#acept(pt.unl.fct .di.centria.nohr.model.Visitor)
	 */
	@Override
	public Predicate accept(ModelVisitor visitor) {
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
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asMetaPredicate()
	 */
	@Override
	public MetaPredicate asMetaPredicate() {
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
	public String getSignature() {
		return getSymbol() + "/" + getArity();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#getSymbol()
	 */
	@Override
	public String getSymbol() {
		return role.getIRI().toURI().getFragment();
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
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isMetaPredicate()
	 */
	@Override
	public boolean isMetaPredicate() {
		// TODO Auto-generated method stub
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
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#isRulePredicate()
	 */
	@Override
	public boolean isRulePredicate() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getSymbol();
	}

}

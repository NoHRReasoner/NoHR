/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.Visitor;

/**
 * Implementation of a {@link Predicate} representing a role.
 *
 * @author Nuno Costa
 */
public class RolePredicateImpl implements RolePredicate {

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

	@Override
	public Predicate accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
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
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#asRole()
	 */
	@Override
	public OWLProperty<?, ?> getRole() {
		return role;
	}

	@Override
	public String getSignature() {
		return getSymbol() + "/" + getArity();
	}

	@Override
	public String getSymbol() {
		return role.getIRI().toQuotedString();
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getSymbol();
	}

}

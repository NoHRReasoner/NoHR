/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.terminals;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Predicate;

/**
 * Implementation of a {@link Predicate} representing a role.
 *
 * @author Nuno Costa
 */
public class RolePredicateImpl implements HybridPredicate {

	/** The role represented by this predicate. */
	private final OWLProperty<?, ?> role;

	private String label;

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
	public OWLClass asConcept() {
		throw new ClassCastException();
	}

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

	@Override
	public boolean isConcept() {
		return false;
	}

	@Override
	public boolean isRole() {
		return true;
	}

	void setLabel(String label) {
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (label != null)
			return label;
		final String fragment = role.getIRI().toURI().getFragment();
		if (fragment != null)
			return fragment;
		else
			return role.getIRI().toString();
	}

}

/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Term;

/**
 * Ontology individual {@link Constant} implementation.
 *
 * @see Term
 * @author Nuno Costa
 */
public class IndividualConstantImpl implements HybridConstant {

	/** The ontology individual. */
	private final OWLIndividual individual;

	String label;

	/**
	 * Constructs an ontology individual constant with a specified individual.
	 *
	 * @param individual
	 *            the ontology individual
	 */
	IndividualConstantImpl(OWLIndividual individual) {
		Objects.requireNonNull(individual);
		this.individual = individual;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Constant accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public OWLIndividual asIndividual() {
		return individual;
	}

	@Override
	public OWLLiteral asLiteral() {
		throw new ClassCastException();
	}

	@Override
	public Number asNumber() {
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
		final IndividualConstantImpl other = (IndividualConstantImpl) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!toString().equals(other.toString()))
			return false;
		return true;
	}

	@Override
	public String getSymbol() {
		if (individual.isNamed())
			return individual.asOWLNamedIndividual().getIRI().toQuotedString();
		else
			return individual.toStringID();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (individual == null ? 0 : toString().hashCode());
		return result;
	}

	@Override
	public boolean isIndividual() {
		return true;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		if (label != null)
			return label;
		if (individual.isNamed()) {
			final String fragment = individual.asOWLNamedIndividual().getIRI().toURI().getFragment();
			if (fragment != null)
				return fragment;
		}
		return individual.toStringID();
	}

}

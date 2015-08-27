/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Ontology individual {@link Constant} implementation.
 *
 * @see Term
 * @author nunocosta
 */
public class IndividualConstantImpl implements Constant {

	/** The ontology individual. */
	private final OWLIndividual individual;

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
	public Constant asConstant() {
		throw new ClassCastException();
	}

	@Override
	public Number asNumber() {
		throw new ClassCastException();
	}

	@Override
	public OWLIndividual asOWLIndividual() {
		return individual;
	}

	@Override
	public OWLLiteral asOWLLiteral() {
		throw new ClassCastException();
	}

	@Override
	public String asRuleConstant() {
		return individual.asOWLNamedIndividual().getIRI().getFragment();
	}

	@Override
	public TruthValue asTruthValue() {
		throw new ClassCastException();
	}

	@Override
	public Variable asVariable() {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (individual == null ? 0 : toString().hashCode());
		return result;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public boolean isOWLIndividual() {
		return true;
	}

	@Override
	public boolean isOWLLiteral() {
		return false;
	}

	@Override
	public boolean isRuleConstant() {
		return true;
	}

	@Override
	public boolean isTruthValue() {
		return false;
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public String toString() {
		return individual.toStringID();
	}

}

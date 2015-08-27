/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Implementation of ontology literal {@link Constant}.
 *
 * @author Nuno Costa
 */
public class LiteralConstant implements Constant {

	/**
	 * The OWL literal.
	 */
	private final OWLLiteral literal;

	/**
	 * Constructs a constant with a specified OWL literal.
	 *
	 * @param literal
	 *            the OWL literal
	 */
	public LiteralConstant(OWLLiteral literal) {
		this.literal = literal;
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
		return this;
	}

	@Override
	public Number asNumber() {
		throw new ClassCastException();
	}

	@Override
	public OWLIndividual asOWLIndividual() {
		throw new ClassCastException();
	}

	@Override
	public OWLLiteral asOWLLiteral() {
		return literal;
	}

	@Override
	public String asRuleConstant() {
		return literal.getLiteral();
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
		final LiteralConstant other = (LiteralConstant) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!toString().equals(other.toString()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (literal == null ? 0 : toString().hashCode());
		return result;
	}

	@Override
	public boolean isConstant() {
		return true;
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
		return false;
	}

	@Override
	public boolean isOWLLiteral() {
		return true;
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
		return literal.getLiteral() + (literal.getLang().isEmpty() ? "" : "@" + literal.getLang());
	}

}

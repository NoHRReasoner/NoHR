/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Implementation of ontology literal {@link Constant}.
 *
 * @author Nuno Costa
 */
public class LiteralConstantImpl implements LiteralConstant {

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
	public LiteralConstantImpl(OWLLiteral literal) {
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LiteralConstantImpl other = (LiteralConstantImpl) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!toString().equals(other.toString()))
			return false;
		return true;
	}

	@Override
	public OWLLiteral getOWLLiteral() {
		return literal;
	}

	@Override
	public String getSymbol() {
		return literal.getLiteral();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (literal == null ? 0 : toString().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return literal.getLiteral() + (literal.getLang().isEmpty() ? "" : "@" + literal.getLang());
	}

}

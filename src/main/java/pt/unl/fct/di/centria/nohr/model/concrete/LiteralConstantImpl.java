/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.concrete;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

/**
 * Implementation of ontology literal {@link Constant}.
 *
 * @author Nuno Costa
 */
public class LiteralConstantImpl implements Constant {

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
	public OWLIndividual asIndividual() {
		throw new ClassCastException();
	}

	@Override
	public OWLLiteral asLiteral() {
		return literal;
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
		final LiteralConstantImpl other = (LiteralConstantImpl) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!toString().equals(other.toString()))
			return false;
		return true;
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
	public boolean isIndividual() {
		return false;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public String toString() {
		return getSymbol();
	}

}

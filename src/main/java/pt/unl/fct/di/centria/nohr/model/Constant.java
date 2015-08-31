package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Represents a constant. A constant can be a number, a rule constant, an ontology individual or an ontology literal.
 *
 * @see Term
 */
public interface Constant extends Term {

	@Override
	public Constant accept(ModelVisitor visitor);

	OWLIndividual asIndividual();

	OWLLiteral asLiteral();

	Number asNumber();

	boolean isIndividual();

	boolean isLiteral();

	boolean isNumber();

}

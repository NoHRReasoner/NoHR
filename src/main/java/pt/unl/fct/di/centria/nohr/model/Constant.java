package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.concrete.ModelVisitor;

/**
 * Represents a constant. A constant can be a number, a rule constant, an ontology individual or an ontology literal.
 *
 * @see Term
 */
public interface Constant extends Term {

	@Override
	public Constant accept(ModelVisitor visitor);

}

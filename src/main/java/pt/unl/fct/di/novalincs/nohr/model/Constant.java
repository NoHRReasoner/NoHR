package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * Represents a constant.
 *
 * @see Term
 */
public interface Constant extends Term {

	@Override
	public Constant accept(ModelVisitor visitor);

}

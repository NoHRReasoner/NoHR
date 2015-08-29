package pt.unl.fct.di.centria.nohr.model;

/**
 * Represents a constant. A constant can be a number, a rule constant, an ontology individual or an ontology literal.
 *
 * @see Term
 */
public interface Constant extends Term {

	@Override
	public Constant accept(ModelVisitor visitor);

	/**
	 * Returns the string representing of this constant if it is an rule constant.
	 *
	 * @return this constant as a string representing this constant.
	 * @throws ClassCastException
	 *             if this isn't a rule constant.
	 */
	public String getSymbol();

}

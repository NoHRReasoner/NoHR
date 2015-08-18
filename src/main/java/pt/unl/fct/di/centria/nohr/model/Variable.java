package pt.unl.fct.di.centria.nohr.model;

/**
 * Represents variable.
 *
 * @author Nuno Costa
 */

public interface Variable extends Term, Comparable<Variable> {

	@Override
	Variable accept(ModelVisitor visitor);

	/**
	 * Returns the symbol that represents this variable.
	 *
	 * @return the symbol that represents this variable.
	 */
	public String getSymbol();
}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

/**
 * @author Nuno Costa
 */
public interface Predicate extends Symbol {

	/**
	 * Returns the arity of this predicate.
	 *
	 * @return the arity of this predicate.
	 */
	public int getArity();

	/**
	 * Returns the signature of this predicate, i.e. the pair {@code symbol/arity}.
	 *
	 * @return the signature of this predicate.
	 */
	public String getSignature();

}

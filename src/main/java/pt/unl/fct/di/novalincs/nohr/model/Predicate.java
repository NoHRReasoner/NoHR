package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * Represents a predicate.
 *
 * @author Nuno Costa
 */
public interface Predicate extends Symbol {

	@Override
	public Predicate accept(ModelVisitor modelVisitor);

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

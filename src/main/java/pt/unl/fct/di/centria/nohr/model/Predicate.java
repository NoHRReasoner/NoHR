package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;

/**
 * Represents a predicate. Can be a rule predicate or represent a concept, a role or a meta-predicate (see {@link MetaPredicate}) in the translation
 * of an ontology.
 *
 * @author Nuno Costa
 */
public interface Predicate extends Symbolic {

	@Override
	Predicate accept(ModelVisitor visitor);

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

package pt.unl.fct.di.centria.nohr.reasoner.translation;

import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * Represents the translation of the ontology component of some hybrid KB.
 *
 * @author Nuno Costa
 */
public interface OntologyTranslation {

	/**
	 * Returns the profile of the translated ontology.
	 *
	 * @return the profile of the translated ontology.
	 */
	public Profile getProfile();

	/**
	 * Returns the {@link Program program} corresponding to this {@link OntologyTranslation translation}.
	 *
	 * @return the {@link Program program} corresponding to this {@link OntologyTranslation translation}.
	 */
	public Program getProgram();

	/**
	 * Retruns true iff the translated ontology has disjunctions.
	 *
	 * @return iff the translated ontology has disjunctions.
	 */
	public boolean hasDisjunctions();

	/**
	 * Returns true iff the negative meta-predicate of given predicate predicate is head of some rule in this {@link OntologyTranslation translation}.
	 *
	 * @param predicate
	 *            a (non-meta) predicate.
	 * @return true iff the negative meta-predicate of given predicate predicate is head of some rule in this {@link OntologyTranslation translation}.
	 * @throws IllegalArgumentException
	 *             if {@code predicate} is a meta-predicate.
	 */
	public boolean isNegativeHeadFunctor(Predicate predicate);

}
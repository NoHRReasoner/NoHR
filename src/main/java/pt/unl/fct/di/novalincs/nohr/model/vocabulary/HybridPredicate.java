/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * A {@link Predicate} of an Hybrid KB. Can be a rule predicate or represent a concept or role.
 *
 * @author Nuno Costa
 */
public interface HybridPredicate extends Predicate {

	@Override
	Predicate accept(ModelVisitor visitor);

	/**
	 * Returns the concept that this predicate represents, if it represent some concept.
	 *
	 * @throws ClassCastException
	 *             if this constant doesn't represent a concept.
	 * @return the concept that this constant represents.
	 */
	OWLClass asConcept();

	/**
	 * Returns the role that this predicate represents, if it represent some role.
	 *
	 * @throws ClassCastException
	 *             if this constant doesn't represent a role.
	 * @return the role that this predicate represents.
	 */
	OWLProperty<?, ?> asRole();

	/**
	 * Returns true iff this predicate represents a concept.
	 *
	 * @return true iff this predicate represents a concept.
	 */
	boolean isConcept();

	/**
	 * Returns true iff this predicate represents a role.
	 *
	 * @return true iff this predicate represets a role.
	 */
	boolean isRole();

}

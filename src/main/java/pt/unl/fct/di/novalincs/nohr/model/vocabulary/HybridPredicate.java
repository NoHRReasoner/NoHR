/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * A predicate that can be a rule predicate or represent a concept, a role or a meta-predicate (see {@link MetaPredicate}).
 *
 * @author Nuno Costa
 */
public interface HybridPredicate extends Predicate {

	@Override
	Predicate accept(ModelVisitor visitor);

	OWLClass asConcept();

	OWLProperty<?, ?> asRole();

	boolean isConcept();

	boolean isRole();

}

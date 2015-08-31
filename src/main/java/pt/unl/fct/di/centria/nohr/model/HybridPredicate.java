package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;

/**
 * Represents a predicate. Can be a rule predicate or represent a concept, a role or a meta-predicate (see {@link MetaPredicate}) in the translation
 * of an ontology.
 *
 * @author Nuno Costa
 */
public interface HybridPredicate extends Predicate {

	@Override
	HybridPredicate accept(ModelVisitor visitor);

	OWLClass asConcept();

	OWLProperty<?, ?> asRole();

	boolean isConcept();

	boolean isRole();

}

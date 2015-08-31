/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.terminals;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.Predicate;

/**
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

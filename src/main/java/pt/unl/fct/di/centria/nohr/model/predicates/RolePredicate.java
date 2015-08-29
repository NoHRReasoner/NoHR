/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLProperty;

/**
 * @author Nuno Costa
 */
public interface RolePredicate extends Predicate {

	OWLProperty<?, ?> getRole();

}
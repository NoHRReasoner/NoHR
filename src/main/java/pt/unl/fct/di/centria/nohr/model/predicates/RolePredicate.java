/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.Predicate;

/**
 * @author Nuno Costa
 */
public interface RolePredicate extends Predicate {

	OWLProperty<?, ?> getRole();

}
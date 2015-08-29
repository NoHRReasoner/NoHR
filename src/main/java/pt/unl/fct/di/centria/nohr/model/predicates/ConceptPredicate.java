/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLClass;

import pt.unl.fct.di.centria.nohr.model.Predicate;

/**
 * @author Nuno Costa
 */
public interface ConceptPredicate extends Predicate {

	OWLClass getConcept();

}
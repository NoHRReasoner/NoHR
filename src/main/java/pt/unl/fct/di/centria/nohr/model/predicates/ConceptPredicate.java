/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Nuno Costa
 */
public interface ConceptPredicate extends Predicate {

	OWLClass getConcept();

}
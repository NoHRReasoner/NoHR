/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class DoublePredicate extends MetaPredicateImpl {

    DoublePredicate(Predicate predicate) {
	super(predicate, PredicateType.DOUBLE);
    }

}

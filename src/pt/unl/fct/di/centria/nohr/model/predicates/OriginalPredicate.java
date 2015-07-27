/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class OriginalPredicate extends MetaPredicateImpl {

    OriginalPredicate(Predicate predicate) {
	super(predicate, PredicateType.ORIGINAL);
    }

}

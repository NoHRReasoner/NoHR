/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class NegativePredicate extends MetaPredicateImpl {

    NegativePredicate(Predicate predicate) {
	super(predicate, PredicateType.NEGATIVE);
    }

}

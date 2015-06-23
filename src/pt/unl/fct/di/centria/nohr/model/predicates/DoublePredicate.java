/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class DoublePredicate extends MetaPredicateImpl {

    public DoublePredicate(String symbol, int arity) {
	super(symbol, arity, PredicateType.DOUBLE, 'd');
    }

}

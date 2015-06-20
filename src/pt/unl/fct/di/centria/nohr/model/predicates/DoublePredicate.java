/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class DoublePredicate extends MetaPredicateImpl {

    {
	prefix = 'h';
    }

    public DoublePredicate(String symbol, int arity) {
	super(symbol, arity, PredicateType.DOUBLE);
    }

}

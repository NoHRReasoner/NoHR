/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class DoubleRangePredicate extends MetaPredicateImpl {

    {
	prefix = 'h';
    }

    public DoubleRangePredicate(String symbol) {
	super(symbol, 1, PredicateType.DOUBLED_RANGE);
    }

}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class OriginalRangePredicate extends MetaPredicateImpl {

    {
	prefix = 'f';
    }

    public OriginalRangePredicate(String symbol) {
	super(symbol, 1, PredicateType.ORIGINAL_RANGE);
    }

}

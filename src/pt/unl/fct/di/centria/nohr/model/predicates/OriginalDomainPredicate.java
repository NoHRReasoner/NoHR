/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class OriginalDomainPredicate extends MetaPredicateImpl {

    public OriginalDomainPredicate(String symbol) {
	super(symbol, 1, PredicateType.ORIGINAL_DOMAIN, 'e');
    }

}

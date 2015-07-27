/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class OriginalRangePredicate extends RoleMetaPredicateImpl {

    OriginalRangePredicate(RolePredicateImpl rolePredicate) {
	super(rolePredicate, PredicateType.ORIGINAL_RANGE);
    }

}

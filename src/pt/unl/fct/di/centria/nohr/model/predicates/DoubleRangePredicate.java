/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class DoubleRangePredicate extends RoleMetaPredicateImpl {

    DoubleRangePredicate(RolePredicateImpl rolePredicate) {
	super(rolePredicate, PredicateType.DOUBLED_RANGE);
    }

}

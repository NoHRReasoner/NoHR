/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class OriginalDomainPredicate extends RoleMetaPredicateImpl {

    OriginalDomainPredicate(RolePredicateImpl rolePredicate) {
	super(rolePredicate, PredicateType.ORIGINAL_DOMAIN);
    }

}

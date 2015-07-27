/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class DoubleDomainPredicate extends RoleMetaPredicateImpl {

    DoubleDomainPredicate(RolePredicateImpl rolePredicate) {
	super(rolePredicate, PredicateType.DOUBLE_DOMAIN);
    }

}

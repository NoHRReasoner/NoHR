/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class RoleMetaPredicateImpl extends MetaPredicateImpl {

    /**
     *
     */
    RoleMetaPredicateImpl(RolePredicateImpl rolePredicateImpl,
	    PredicateType type) {
	super(rolePredicateImpl, type);
	if (type != PredicateType.DOUBLE_DOMAIN
		&& type != PredicateType.DOUBLED_RANGE
		&& type != PredicateType.ORIGINAL_DOMAIN
		&& type != PredicateType.ORIGINAL_RANGE)
	    throw new IllegalArgumentException(
		    "type: must be DOUBLE_DOMAIN, DOUBLE_RANGE, ORIGINAL_DOMAIN or ORIGINAL_RANGE");
    }

}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import pt.unl.fct.di.centria.nohr.reasoner.translation.EncodeVisitor;

/**
 * @author nunocosta
 *
 */
public class MetapredicateImpl extends PredicateImpl implements Metapredicate {

    private static char prefix(PredicateTypes predicateType) {
	switch (predicateType) {
	case ORIGINAL:
	    return EncodeVisitor.ORIGINAL_PREFIX;
	case DOUBLED:
	    return EncodeVisitor.DOUBLED_PREFIX;
	case NEGATION:
	    return EncodeVisitor.CLASSICAL_NEGATION_PREFIX;
	case ORIGINAL_DOMAIN:
	    return EncodeVisitor.ORIGINAL_DOM_PREFIX;
	case ORIGINAL_RANGE:
	    return EncodeVisitor.ORIGINAL_RAN_PREFIX;
	case DOUBLED_DOMAIN:
	    return EncodeVisitor.DOUBLED_DOM_PREFIX;
	case DOUBLED_RANGE:
	    return EncodeVisitor.DOUBLED_RAN_PREFIX;
	}
	return '0';
    }

    private PredicateTypes type;

    /**
     * @param symbol
     * @param arity
     */
    public MetapredicateImpl(String symbol, int arity, PredicateTypes type) {
	super(symbol, arity);
	this.type = type;
    }

    public PredicateTypes getType() {
	return type;
    }

    @Override
    public String toString() {
	return prefix(type) + symbol;
    }
}

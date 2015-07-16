/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class Predicates {

    public static Predicate domPred(String symbol, boolean doub) {
	if (doub)
	    return new DoubleDomainPredicate(symbol);
	else
	    return new OriginalDomainPredicate(symbol);
    }

    public static Predicate doubDomPred(String symbol) {
	return new DoubleDomainPredicate(symbol);
    }

    public static Predicate doubPred(String symbol, int arity) {
	return new DoublePredicate(symbol, arity);
    }

    public static Predicate doubRanPred(String symbol) {
	return new DoubleRangePredicate(symbol);
    }

    public static Predicate negPred(String symbol, int arity) {
	return new NegativePredicate(symbol, arity);
    }

    public static Predicate origDomPred(String symbol) {
	return new OriginalDomainPredicate(symbol);
    }

    public static Predicate origPred(String symbol, int arity) {
	return new OriginalPredicate(symbol, arity);
    }

    public static Predicate origRanPred(String symbol) {
	return new OriginalRangePredicate(symbol);
    }

    public static Predicate pred(String symbol, int arity) {
	return new PredicateImpl(symbol, arity);
    }

    public static Predicate pred(String symbol, int arity, boolean doub) {
	if (doub)
	    return new DoublePredicate(symbol, arity);
	else
	    return new OriginalPredicate(symbol, arity);
    }

    public static Predicate pred(String symbol, int arity, PredicateType type) {
	switch (type) {
	case DOUBLE:
	    return new DoublePredicate(symbol, arity);
	case DOUBLE_DOMAIN:
	    if (arity != 1)
		throw new IllegalArgumentException(type.name()
			+ " must have arity 1");
	    return new DoubleDomainPredicate(symbol);
	case DOUBLED_RANGE:
	    if (arity != 1)
		throw new IllegalArgumentException(type.name()
			+ " must have arity 1");
	    return new DoubleRangePredicate(symbol);
	case NEGATIVE:
	    return new NegativePredicate(symbol, arity);
	case ORIGINAL:
	    return new OriginalPredicate(symbol, arity);
	case ORIGINAL_DOMAIN:
	    if (arity != 1)
		throw new IllegalArgumentException(type.name()
			+ " must have arity 1");
	    return new OriginalDomainPredicate(symbol);
	case ORIGINAL_RANGE:
	    if (arity != 1)
		throw new IllegalArgumentException(type.name()
			+ " must have arity 1");
	    return new OriginalRangePredicate(symbol);
	default:
	    return null;
	}
    }

    public static Predicate ranPred(String symbol, boolean doub) {
	if (doub)
	    return new DoubleRangePredicate(symbol);
	else
	    return new OriginalRangePredicate(symbol);
    }

}

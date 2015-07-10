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

    public static Predicate ranPred(String symbol, boolean doub) {
	if (doub)
	    return new DoubleRangePredicate(symbol);
	else
	    return new OriginalRangePredicate(symbol);
    }

}

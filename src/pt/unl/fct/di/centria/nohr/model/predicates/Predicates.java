/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

/**
 * @author nunocosta
 *
 */
public class Predicates {

    /**
     * @param role
     * @return
     */
    private static OWLProperty<?, ?> atomic(OWLPropertyExpression<?, ?> role) {
	if (role.isObjectPropertyExpression()) {
	    final OWLObjectPropertyExpression ope = (OWLObjectPropertyExpression) role;
	    return ope.getNamedProperty();
	} else if (role.isDataPropertyExpression())
	    return (OWLDataProperty) role;
	else
	    throw new IllegalArgumentException();
    }

    public static Predicate domPred(OWLPropertyExpression<?, ?> role, boolean doub) {
	final RolePredicateImpl rolePredicate = new RolePredicateImpl(atomic(role));
	if (doub)
	    return new DoubleDomainPredicate(rolePredicate);
	else
	    return new OriginalDomainPredicate(rolePredicate);
    }

    public static Predicate doubDomPred(OWLPropertyExpression<?, ?> role) {
	return new DoubleDomainPredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate doubPred(OWLClass concept) {
	return new DoublePredicate(new ConceptPredicateImpl(concept));
    }

    public static Predicate doubPred(OWLPropertyExpression<?, ?> role) {
	return new DoublePredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate doubPred(String symbol, int arity) {
	return new DoublePredicate(new RulePredicateImpl(symbol, arity));
    }

    public static Predicate doubRanPred(OWLPropertyExpression<?, ?> role) {
	return new DoubleRangePredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate negPred(OWLClass concept) {
	return new NegativePredicate(new ConceptPredicateImpl(concept));
    }

    public static Predicate negPred(OWLPropertyExpression<?, ?> role) {
	return new NegativePredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate negPred(Predicate predicate) {
	Predicate pred = predicate;
	if (predicate instanceof MetaPredicateImpl)
	    pred = ((MetaPredicate) predicate).getPredicate();
	return new NegativePredicate(pred);
    }

    public static Predicate negPred(String symbol, int arity) {
	return new NegativePredicate(new RulePredicateImpl(symbol, arity));
    }

    public static Predicate origDomPred(OWLPropertyExpression<?, ?> role) {
	return new OriginalDomainPredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate origPred(OWLClass concept) {
	return new OriginalPredicate(new ConceptPredicateImpl(concept));
    }

    public static Predicate origPred(OWLPropertyExpression<?, ?> role) {
	return new OriginalPredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate origPred(String symbol, int arity) {
	return new OriginalPredicate(new RulePredicateImpl(symbol, arity));
    }

    public static Predicate origRanPred(OWLPropertyExpression<?, ?> role) {
	return new OriginalRangePredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate pred(OWLClass concept) {
	return new ConceptPredicateImpl(concept);
    }

    public static Predicate pred(OWLClass concept, boolean doub) {
	if (doub)
	    return new DoublePredicate(new ConceptPredicateImpl(concept));
	else
	    return new OriginalPredicate(new ConceptPredicateImpl(concept));
    }

    public static Predicate pred(OWLClass concept, PredicateType type) {
	final Predicate predicate = new ConceptPredicateImpl(concept);
	switch (type) {
	case DOUBLE:
	    return new DoublePredicate(predicate);
	case NEGATIVE:
	    return new NegativePredicate(predicate);
	case ORIGINAL:
	    return new OriginalPredicate(predicate);
	default:
	    return null;
	}
    }

    public static Predicate pred(OWLPropertyExpression<?, ?> role) {
	return new RolePredicateImpl(atomic(role));
    }

    public static Predicate pred(OWLPropertyExpression<?, ?> role, boolean doub) {
	if (doub)
	    return new DoublePredicate(new RolePredicateImpl(atomic(role)));
	else
	    return new OriginalPredicate(new RolePredicateImpl(atomic(role)));
    }

    public static Predicate pred(OWLPropertyExpression<?, ?> role, PredicateType type) {
	final RolePredicateImpl predicate = new RolePredicateImpl(atomic(role));
	switch (type) {
	case DOUBLE:
	    return new DoublePredicate(predicate);
	case NEGATIVE:
	    return new NegativePredicate(predicate);
	case ORIGINAL:
	    return new OriginalPredicate(predicate);
	case ORIGINAL_DOMAIN:
	    return new OriginalDomainPredicate(predicate);
	case ORIGINAL_RANGE:
	    return new OriginalRangePredicate(predicate);
	case DOUBLE_DOMAIN:
	    return new DoubleDomainPredicate(predicate);
	case DOUBLED_RANGE:
	    return new DoubleRangePredicate(predicate);
	default:
	    return null;
	}
    }

    public static Predicate pred(String symbol, int arity) {
	return new RulePredicateImpl(symbol, arity);
    }

    public static Predicate pred(String symbol, int arity, boolean doub) {
	if (doub)
	    return new DoublePredicate(new RulePredicateImpl(symbol, arity));
	else
	    return new OriginalPredicate(new RulePredicateImpl(symbol, arity));
    }

    public static Predicate pred(String symbol, int arity, PredicateType type) {
	final Predicate predicate = new RulePredicateImpl(symbol, arity);
	switch (type) {
	case DOUBLE:
	    return new DoublePredicate(predicate);
	case NEGATIVE:
	    return new NegativePredicate(predicate);
	case ORIGINAL:
	    return new OriginalPredicate(predicate);
	default:
	    return null;
	}
    }

    public static Predicate ranPred(OWLPropertyExpression<?, ?> role, boolean doub) {
	if (doub)
	    return new DoubleRangePredicate(new RolePredicateImpl(atomic(role)));
	else
	    return new OriginalRangePredicate(new RolePredicateImpl(atomic(role)));
    }

}

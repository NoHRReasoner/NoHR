package pt.unl.fct.di.centria.nohr.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import pt.unl.fct.di.centria.nohr.model.predicates.DoubleDomainPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.DoublePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.DoubleRangePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.NegativePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.OriginalDomainPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.OriginalPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.OriginalRangePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateImpl;

public class Model {

    public static Answer ans(Query query, TruthValue truthValue,
	    List<Term> values) {
	final Map<Variable, Integer> varsIdx = new HashMap<Variable, Integer>();
	final int i = 0;
	for (final Variable var : query.getVariables())
	    varsIdx.put(var, i);
	return new AnswerImpl(query, truthValue, values, varsIdx);
    }

    public static Answer ans(Query query, TruthValue truthValue,
	    List<Term> values, Map<Variable, Integer> variablesIndex) {
	return new AnswerImpl(query, truthValue, values, variablesIndex);
    }

    public static Atom atom(Predicate predicate, List<Term> arguments) {
	return new AtomImpl(predicate, arguments);
    }

    public static Atom atom(Predicate predicate, Term... arguments) {
	final List<Term> argumentsList = new LinkedList<Term>();
	Collections.addAll(argumentsList, arguments);
	return new AtomImpl(predicate, argumentsList);
    }

    public static Atom atom(String predicate, List<Term> arguments) {
	return new AtomImpl(pred(predicate, arguments.size()), arguments);
    }

    public static Atom atom(String predicate, Term... arguments) {
	final List<Term> argumentsList = new LinkedList<Term>();
	Collections.addAll(argumentsList, arguments);
	return new AtomImpl(pred(predicate, arguments.length), argumentsList);
    }

    public static Constant cons(Number n) {
	return new NumericConstantImpl(n);
    }

    public static Constant cons(String symbol) {
	return new ConstantImpl(symbol);
    }

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

    public static Term list(Term... terms) {
	final List<Term> list = new LinkedList<Term>();
	Collections.addAll(list, terms);
	return new ListTermImpl(list);
    }

    public static NegativeLiteral negLiteral(Atom atom) {
	return new NegativeLiteralImpl(atom);
    }

    public static NegativeLiteral negLiteral(Atom atom, boolean existentially) {
	return new NegativeLiteralImpl(atom, existentially);
    }

    public static NegativeLiteral negLiteral(Predicate pred,
	    boolean existentially, Term... args) {
	final List<Term> argsList = new LinkedList<Term>();
	Collections.addAll(argsList, args);
	return new NegativeLiteralImpl(new AtomImpl(pred, argsList),
		existentially);
    }

    public static NegativeLiteral negLiteral(Predicate pred, Term... args) {
	final List<Term> argsList = new LinkedList<Term>();
	Collections.addAll(argsList, args);
	return new NegativeLiteralImpl(new AtomImpl(pred, argsList));
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

    public static Query query(List<Literal> literalList) {
	final List<Variable> vars = new LinkedList<Variable>();
	for (final Literal literal : literalList)
	    for (final Variable var : literal.getVariables())
		if (!vars.contains(var))
		    vars.add(var);
	return new QueryImpl(literalList, vars);
    }

    public static Query query(List<Variable> vars, Literal... literals) {
	final List<Literal> literalList = new LinkedList<Literal>();
	Collections.addAll(literalList, literals);
	return new QueryImpl(literalList, vars);
    }

    public static Query query(Literal... literals) {
	final List<Literal> literalList = new LinkedList<Literal>();
	new LinkedList<Variable>();
	Collections.addAll(literalList, literals);
	return query(literalList);
    }

    public static Predicate ranPred(String symbol, boolean doub) {
	if (doub)
	    return new DoubleRangePredicate(symbol);
	else
	    return new OriginalRangePredicate(symbol);
    }

    public static Rule rule(Atom head, Literal... body) {
	final List<Literal> bodyList = new LinkedList<Literal>();
	Collections.addAll(bodyList, body);
	return new RuleImpl(head, bodyList);
    }

    public static Rule rule(Atom head, Set<Literal> body) {
	final List<Literal> bodyList = new LinkedList<Literal>(body);
	return new RuleImpl(head, bodyList);
    }

    public static Substitution subs(Map<Variable, Term> map) {
	final SortedMap<Variable, Integer> varsIdx = new TreeMap<Variable, Integer>();
	final Term[] vals = new Term[map.size()];
	int i = 0;
	for (final Entry<Variable, Term> entry : map.entrySet()) {
	    varsIdx.put(entry.getKey(), i);
	    vals[i++] = entry.getValue();
	}
	return new SubstitutionImpl(varsIdx, vals);
    }

    public static Substitution subs(Variable var, Term term) {
	final SortedMap<Variable, Integer> varsIdx = new TreeMap<Variable, Integer>();
	final Term[] vals = new Term[1];
	varsIdx.put(var, 0);
	vals[0] = term;
	return new SubstitutionImpl(varsIdx, vals);
    }

    public static Variable var(String name) {
	return new VariableImpl(name);
    }

}

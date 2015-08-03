package pt.unl.fct.di.centria.nohr.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicates;
import pt.unl.fct.di.centria.nohr.reasoner.OntologyIndex;

public class Model {

    public static Answer ans(Query query, TruthValue truthValue, List<Term> values) {
	final Map<Variable, Integer> varsIdx = new HashMap<Variable, Integer>();
	final int i = 0;
	for (final Variable var : query.getVariables())
	    varsIdx.put(var, i);
	return new AnswerImpl(query, truthValue, values, varsIdx);
    }

    public static Answer ans(Query query, TruthValue truthValue, List<Term> values,
	    Map<Variable, Integer> variablesIndex) {
	return new AnswerImpl(query, truthValue, values, variablesIndex);
    }

    public static Atom atom(Predicate predicate) {
	final List<Term> args = new ArrayList<Term>(predicate.getArity());
	for (int i = 0; i < predicate.getArity(); i++)
	    args.add(var());
	return new AtomImpl(predicate, args);
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
	return new AtomImpl(Predicates.pred(predicate, arguments.size()), arguments);
    }

    public static Atom atom(String predicate, List<Term> arguments, OntologyIndex ontologyIndex) {
	return new AtomImpl(Predicates.pred(predicate, arguments.size(), ontologyIndex), arguments);
    }

    public static Atom atom(String predicate, Term... arguments) {
	final List<Term> argumentsList = new LinkedList<Term>();
	Collections.addAll(argumentsList, arguments);
	return new AtomImpl(Predicates.pred(predicate, arguments.length), argumentsList);
    }

    public static <E extends FormatVisitable> String concat(String sep, E[] objs, FormatVisitor visitor) {
	final StringBuffer sb = new StringBuffer();
	String sepToken = "";
	for (final FormatVisitable obj : objs) {
	    sb.append(sepToken + obj.acept(visitor));
	    sepToken = sep;
	}
	return new String(sb);
    }

    public static <E extends FormatVisitable> String concat(String sep, List<E> objs, FormatVisitor visitor) {
	final StringBuffer sb = new StringBuffer();
	String sepToken = "";
	for (final FormatVisitable obj : objs) {
	    sb.append(sepToken + obj.acept(visitor));
	    sepToken = sep;
	}
	return new String(sb);
    }

    public static Constant cons(Number n) {
	return new NumericConstantImpl(n);
    }

    public static Constant cons(String symbol) {
	return new ConstantImpl(symbol);
    }

    public static Term list(List<Term> terms) {
	return new ListTermImpl(terms);
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

    public static NegativeLiteral negLiteral(Predicate pred, boolean existentially, Term... args) {
	final List<Term> argsList = new LinkedList<Term>();
	Collections.addAll(argsList, args);
	return new NegativeLiteralImpl(new AtomImpl(pred, argsList), existentially);
    }

    public static NegativeLiteral negLiteral(Predicate pred, Term... args) {
	final List<Term> argsList = new LinkedList<Term>();
	Collections.addAll(argsList, args);
	return new NegativeLiteralImpl(new AtomImpl(pred, argsList));
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

    public static Rule rule(Atom head, List<? extends Literal> body) {
	return new RuleImpl(head, body);
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

    public static Variable var() {
	return new VariableImpl("_");
    }

    public static Variable var(String name) {
	return new VariableImpl(name);
    }

}

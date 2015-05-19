package nohr.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Model {

	public static Term cons(String name) {
		return new ConstantImpl(name);
	}

	public static <E> List<E> listOf(E... elems) {
		List<E> list = new LinkedList<E>();
		Collections.addAll(list, elems);
		return list;
	}

	public static NegativeLiteral negLiteral(String pred, Term... args) {
		return new NegativeLiteralImpl(new AtomImpl(new PredicateImpl(pred,
				args.length), listOf(args)));
	}

	public static Term num(Number n) {
		return new NumericConstantImpl(n);
	}

	public static PositiveLiteral posLiteral(String pred, Term... args) {
		return new PositiveLiteralImpl(new AtomImpl(new PredicateImpl(pred,
				args.length), listOf(args)));
	}

	public static Query query(List<Variable> vars, Literal... literals) {
		return new QueryImpl(listOf(literals), vars);
	}

	public static Rule rule(PositiveLiteral head, Literal... body) {
		return new RuleImpl(head, listOf(body));
	}

	public static Term var(String name) {
		return new VariableImpl(name);
	}
	
	public static Term list(Term ... terms) {
		return new ListTermImpl(listOf(terms));
	}

}

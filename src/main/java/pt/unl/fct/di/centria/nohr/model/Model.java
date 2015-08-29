package pt.unl.fct.di.centria.nohr.model;

import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicates;

/**
 * Model factory. For more conciseness statically import (see
 * {@link <a href= https://docs.oracle.com/javase/1.5.0/docs/guide/language/static-import.html>Static Import</a> }) this class if you want to use
 * their factory methods.
 *
 * @author Nuno Costa
 */
public class Model {

	/**
	 * Create an answer for a specified query with a specified truth value and a specified list of values.
	 *
	 * @param query
	 *            the query to which this answer corresponds.
	 * @param truthValue
	 *            the truth value of this answer.
	 * @param values
	 *            the values of each free variable.
	 * @return an answer for {@code query} with truth value {@code truthValue} and values {@code values}
	 */
	public static Answer ans(Query query, TruthValue truthValue, List<Term> values) {
		return new AnswerImpl(query, truthValue, values);
	}

	/**
	 * Create an atom with a specified predicate as functor, with the arity of that predicate, and anonymous variables as arguments.
	 *
	 * @param functor
	 *            the functor predicate, <i>P</i>.
	 * @return an atom <i>P(_,..., _)</i> where {@code _} represents an anonymous variable.
	 */
	public static Atom atom(Predicate functor) {
		final List<Term> args = new ArrayList<Term>(functor.getArity());
		for (int i = 0; i < functor.getArity(); i++)
			args.add(var());
		return atom(functor, args);
	}

	/**
	 * Create an atom with a specified predicate as functor and specified terms as arguments.
	 *
	 * @param functor
	 *            the functor predicate, <i>P</i>.
	 * @param arguments
	 *            a list of terms arguments, <i>t<sub>1</sub>, ..., t <sub>n</sub></i>.
	 * @return an atom with functor {@code functor} and arguments {@code arguments}, <i>P(t<sub>1</sub>, ...,t<sub>n</sub>)</i>.
	 * @throws IllegalArgumentException
	 *             if arguments have a size different from the {@code predicate} 's arity.
	 */
	public static Atom atom(Predicate functor, List<Term> arguments) {
		return new AtomImpl(functor, arguments);
	}

	/**
	 * Create an atom with a specified predicate as functor and specified terms as arguments.
	 *
	 * @param functor
	 *            the functor predicate, <i>P</i>.
	 * @param arguments
	 *            an array with the terms arguments, <i>t<sub>1</sub>, ..., t <sub>n</sub></i>.
	 * @return an atom with functor {@code functor} and arguments {@code arguments}, <i>P(t<sub>1</sub>, ...,t<sub>n</sub>)</i>.
	 * @throws IllegalArgumentException
	 *             if arguments have a size different from the {@code predicate} 's arity.
	 */
	public static Atom atom(Predicate functor, Term... arguments) {
		final List<Term> argumentsList = new LinkedList<Term>();
		Collections.addAll(argumentsList, arguments);
		return atom(functor, argumentsList);
	}

	/**
	 * Create an atom with a predicate with a specified symbol as functor and specified terms as arguments.
	 *
	 * @param functorSymbol
	 *            the functor predicate's symbol, <i>P</i>.
	 * @param arguments
	 *            a list of terms arguments, <i>t<sub>1</sub>, ..., t <sub>n</sub></i>.
	 * @return an atom with a predicate with symbol {@code functorSymbol} as functor and arguments {@code arguments}, <i>P(t<sub>1</sub>, ...,t
	 *         <sub>n</sub>)</i>.
	 */
	public static Atom atom(String functorSymbol, List<Term> arguments) {
		return atom(functorSymbol, null, arguments);
	}

	/**
	 * Create an atom with a predicate with a specified symbol as functor and specified terms as arguments.
	 *
	 * @param functorSymbol
	 *            the functor predicate's symbol, <i>P</i>.
	 * @param arguments
	 *            an array of terms arguments, <i>t<sub>1</sub>, ..., t <sub>n</sub></i>.
	 * @return an atom with a predicate with symbol {@code functorSymbol} as functor and arguments {@code arguments}..
	 */
	public static Atom atom(String functorSymbol, Term... arguments) {
		return atom(functorSymbol, null, arguments);
	}

	/**
	 * Create an atom with the predicate corresponding to a specified symbol, given a {@code VocabularyMapping}, as functor and anonymous variables as
	 * arguments.
	 *
	 * @param functor
	 *            the functor predicate, <i>P</i>.
	 * @return an atom <i>P(_,..., _)</i> where {@code _} represents an anonymous variable.
	 */
	public static Atom atom(String functorSymbol, VocabularyMapping ontologyIndex) {
		return atom(functorSymbol, ontologyIndex, Collections.<Term> emptyList());
	}

	/**
	 * Create an atom with a predicate corresponding to a specified symbol, given a specified {@link VocabularyMapping}, as functor and specified
	 * terms as arguments.
	 *
	 * @param functorSymbol
	 *            the functor predicate's symbol.
	 * @param vocabularyMapping
	 *            an {@link VocabularyMapping}
	 * @param arguments
	 *            a list of terms arguments.
	 * @return an atom with the predicate corresponding to {@code functorSymbol} , given {@code vocabularyMapping}, as functor and arguments
	 *         {@code arguments}.
	 */
	public static Atom atom(String functorSymbol, VocabularyMapping vocabularyMapping, List<Term> arguments) {
		return atom(pred(functorSymbol, arguments.size(), vocabularyMapping), arguments);
	}

	/**
	 * Create an atom with the predicate corresponding to a specified symbol as functor and specified terms as arguments.
	 *
	 * @param functorSymbol
	 *            the functor predicate's symbol
	 * @param arguments
	 *            an array of terms arguments.
	 * @return an atom with a predicate with symbol {@code functorSymbol} as functor and arguments {@code arguments}.
	 */
	public static Atom atom(String predicate, VocabularyMapping vocabularyMapping, Term... arguments) {
		final List<Term> argumentsList = new LinkedList<Term>();
		Collections.addAll(argumentsList, arguments);
		return atom(Predicates.pred(predicate, arguments.length, vocabularyMapping), argumentsList);
	}

	/**
	 * Concatenates the string representations, given a specified {@link FormatVisitor} , of model elements from a specified array.
	 *
	 * @param elements
	 *            the array of elements whose representation is to concatenate.
	 * @param format
	 *            the {@link FormatVisitor} used to obtain the string representation of the elements.
	 * @param separator
	 *            the string that separates the elements representations.
	 * @return the concatenation of the string representations of {@code elements}, given {@code format}, separated by {@code separator}.
	 */
	public static <E extends ModelElement<?>> String concat(E[] elements, FormatVisitor format, String separator) {
		final StringBuffer sb = new StringBuffer();
		String sepToken = "";
		for (final ModelElement<?> obj : elements) {
			sb.append(sepToken + obj.accept(format));
			sepToken = separator;
		}
		return new String(sb);
	}

	/**
	 * Concatenates the string representations, given a specified {@link FormatVisitor} , of model elements from a specified list.
	 *
	 * @param elements
	 *            the list of elements whose representation is to concatenate.
	 * @param format
	 *            the {@link FormatVisitor} used to obtain the string representation of the elements.
	 * @param separator
	 *            the string that separates the elements representations.
	 * @return the concatenation of the string representations of {@code elements}, given {@code format}, separated by {@code separator}.
	 */
	public static <E extends ModelElement<?>> String concat(List<E> elements, FormatVisitor format, String separator) {
		final StringBuffer sb = new StringBuffer();
		String sepToken = "";
		for (final ModelElement<?> obj : elements) {
			sb.append(sepToken + obj.accept(format));
			sepToken = separator;
		}
		return new String(sb);
	}

	/**
	 * Creates a constant representing a specified number.
	 *
	 * @param n
	 *            the number
	 * @return the numeric constant representing {@code n}.
	 */
	public static Constant cons(Number n) {
		return new NumericConstantImpl(n);
	}

	/**
	 * Create a constant representing a specified OWL individual.
	 *
	 * @param individual
	 *            the OWL individual
	 * @return the constant representing {@code individual}.
	 */
	public static Constant cons(OWLIndividual individual) {
		return new IndividualConstantImpl(individual);
	}

	/**
	 * Create a constant representing a specified OWL literal.
	 *
	 * @param literal
	 *            the OWL literal.
	 * @return the constant representing {@code literal}.
	 */
	public static Constant cons(OWLLiteral literal) {
		return new LiteralConstantImpl(literal);
	}

	/**
	 * Create a constant representing a OWL individual or OWL literal.
	 *
	 * @param object
	 *            the OWL individual or OWL literal.
	 * @return the constant representing {@code object}.
	 */
	public static Constant cons(OWLPropertyAssertionObject object) {
		if (object instanceof OWLIndividual)
			return cons((OWLIndividual) object);
		else if (object instanceof OWLLiteral)
			return cons((OWLLiteral) object);
		else
			return null;
	}

	/**
	 * Creates a constant representing a specified symbol. If the symbol is a number, then the created constant is an numeric constant.
	 *
	 * @param symbol
	 *            the symbol.
	 * @return a numeric constant representing {@code symbol} if {@code symbol} is a number; or a rule constant representing {@code symbol},
	 *         otherwise.
	 */

	public static Constant cons(String symbol) {
		try {
			final Double number = Double.valueOf(symbol);
			return cons(number);
		} catch (final NumberFormatException e) {
			return new RuleConstantImpl(symbol);
		}
	}

	/**
	 * Creates a constant representation of a specified symbol, given a specified {@link VocabularyMapping}. If the symbol is a number, then the
	 * created constant is an numeric constant.
	 *
	 * @param symbol
	 *            the symbol.
	 * @param vocabularyMapping
	 *            {@link VocabularyMapping}
	 * @return a numeric constant representing {@code symbol} if {@code symbol} is a number; or a constant representation of {@code symbol}, given
	 *         {@link VocabularyMapping}, otherwise.
	 */

	public static Constant cons(String symbol, VocabularyMapping vocabularyMapping) {
		if (vocabularyMapping != null) {
			final OWLIndividual individual = vocabularyMapping.getIndividual(symbol);
			if (individual != null)
				return cons(individual);
		}
		return cons(symbol);
	}

	/**
	 * Create a negative literal from a specified atom.
	 *
	 * @param atom
	 *            the atom, <i>P(t<sub>1</sub>, ..., t<sub>n</sub>)</i>
	 * @return the negative literal <i><b>not</b> P(t<sub>1</sub>, ..., t <sub>n</sub>)</i>
	 */
	public static NegativeLiteral negLiteral(Atom atom) {
		return negLiteral(atom, false);
	}

	/**
	 * Create a negative literal from a specified atom.
	 *
	 * @param atom
	 *            the atom, <i>P(t<sub>1</sub>, ..., t<sub>n</sub>)</i>
	 * @param exitentially
	 * @return the negative literal <i><b>not</b> P(t<sub>1</sub>, ..., t <sub>n</sub>)</i>
	 */
	public static NegativeLiteral negLiteral(Atom atom, boolean existentially) {
		return new NegativeLiteralImpl(atom, existentially);
	}

	/**
	 * Create a negative literal from a specified functor and list of terms.
	 *
	 * @param functor
	 *            the functor predicate <i>P</i>.
	 * @param args
	 *            the list of the arguments <i>t<sub>1</sub>, ..., t<sub>n</sub></i>.
	 * @return the negative literal <i><b>not</b> P(t<sub>1</sub>, ..., t <sub>n</sub>)</i>.
	 * @throws IllegalArgumentException
	 *             if arguments have a size different from the {@code predicate} 's arity.
	 */
	public static NegativeLiteral negLiteral(Predicate functor, List<Term> args) {
		return negLiteral(atom(functor, args));
	}

	/**
	 * Create a negative literal from a specified functor and array of terms.
	 *
	 * @param functor
	 *            the functor predicate <i>P</i>.
	 * @param args
	 *            the array of the arguments <i>t<sub>1</sub>, ..., t<sub>n</sub></i>.
	 * @return the negative literal <i><b>not</b> P(t<sub>1</sub>, ..., t <sub>n</sub>)</i>.
	 * @throws IllegalArgumentException
	 *             if arguments have a size different from the {@code predicate} 's arity.
	 */
	public static NegativeLiteral negLiteral(Predicate functor, Term... args) {
		final List<Term> argsList = new LinkedList<Term>();
		Collections.addAll(argsList, args);
		return negLiteral(atom(functor, argsList));
	}

	/**
	 * Create a program from an array of rules.
	 *
	 * @param rules
	 *            the array of rules;
	 * @return the program containing the given rules.
	 */
	public static Program program(Rule... rules) {
		return new ProgramImpl(new HashSet<>(Arrays.asList(rules)));
	}

	/**
	 * Create a program from a set of rules.
	 *
	 * @param rules
	 *            the set of rules;
	 * @return the program containing the given rules.
	 */
	public static Program program(Set<Rule> rules) {
		return new ProgramImpl(rules);
	}

	/**
	 * Create a query from a specified list of literals.
	 *
	 * @param literals
	 *            the literals list, <i>A<sub>1</sub>,...,A<sub>m</sub>, <b>not</b> B <sub>1</sub>, ..., <b>not</b> B<sub>n</sub></i>
	 * @return the query <i>q(x<sub>1</sub>,..., x<sub>k</sub>) :- A<sub>1</sub> ,...,A<sub>m</sub>, <b>not</b> B <sub>1</sub>, ..., <b>not</b> B
	 *         <sub>n</sub></i>, where <i>x<sub>1</sub>,...,x<sub>k</sub></i> are all the variables appearing in {@code literals}.
	 */
	public static Query query(List<Literal> literals) {
		final List<Variable> vars = new LinkedList<Variable>();
		for (final Literal literal : literals)
			for (final Variable var : literal.getVariables())
				if (!vars.contains(var))
					vars.add(var);
		return new QueryImpl(literals, vars);
	}

	/**
	 * Create a query from a specified list of variables and a specified list of literals .
	 *
	 * @param variables
	 *            the free variables, <i>x<sub>1</sub>, ..., x<sub>k</sub></i>.
	 * @param literals
	 *            the literals, <i>A<sub>1</sub>,...,A<sub>m</sub>, <b>not</b> B <sub>1</sub>, ..., <b>not</b> B<sub>n</sub></i>.
	 * @return the query <i>q(x<sub>1</sub>,..., x<sub>k</sub>) :- A<sub>1</sub> ,...,A<sub>m</sub>, <b>not</b> B<sub>1</sub>, ..., <b>not</b> B
	 *         <sub>n</sub></i>.
	 * @throws IllegalArgumentException
	 *             if {@code variables} contains some variable that doesn't appear in {@code literals}.
	 */
	public static Query query(List<Variable> variables, Literal... literals) {
		final List<Literal> literalList = new LinkedList<Literal>();
		Collections.addAll(literalList, literals);
		return new QueryImpl(literalList, variables);
	}

	/**
	 * Create a query from the literals of a specified array.
	 *
	 * @param literals
	 *            the array of literals, <i>A<sub>1</sub>,...,A<sub>m</sub>, <b>not</b> B <sub>1</sub>, ..., <b>not</b> B<sub>n</sub></i>
	 * @return the query <i>q(x<sub>1</sub>,..., x<sub>k</sub>) :- A<sub>1</sub> ,...,A<sub>m</sub>, <b>not</b> B <sub>1</sub>, ..., <b>not</b> B
	 *         <sub>n</sub></i>, where <i>x<sub>1</sub>,...,x<sub>k</sub></i> are all the variables appearing in {@code literals}.
	 */
	public static Query query(Literal... literals) {
		final List<Literal> literalList = new LinkedList<Literal>();
		Collections.addAll(literalList, literals);
		return query(literalList);
	}

	/**
	 * Create a rule from a specified head atom and a specified body literal list.
	 *
	 * @param head
	 *            the head atom, <i>H</i>
	 * @param body
	 *            the body literal list, <i>A<sub>1</sub>, ..., A<sub>m</sub>, ..., <b>not</b> B<sub>1</sub>, ..., <b>not</b> B <sub>n</sub></i>
	 * @return the rule <i>H :- A<sub>1</sub>, ..., A<sub>m</sub>, ..., <b>not</b> B<sub>1</sub>, ..., <b>not</b> B <sub>n</sub></i>
	 */
	public static Rule rule(Atom head, List<? extends Literal> body) {
		return new RuleImpl(head, body);
	}

	/**
	 * Create a rule from a specified head atom and a specified array of body literals.
	 *
	 * @param head
	 *            the head atom, <i>H</i>
	 * @param body
	 *            the array of body literals, <i>A<sub>1</sub>, ..., A <sub>m</sub>, ..., <b>not</b> B<sub>1</sub>, ..., <b>not</b> B <sub>n</sub></i>
	 * @return the rule <i>H :- A<sub>1</sub>, ..., A<sub>m</sub>, ..., <b>not</b> B<sub>1</sub>, ..., <b>not</b> B <sub>n</sub></i>
	 */
	public static Rule rule(Atom head, Literal... body) {
		final List<Literal> bodyList = new LinkedList<Literal>();
		Collections.addAll(bodyList, body);
		return new RuleImpl(head, bodyList);
	}

	/**
	 * Create a set of {@link Rule rules}.
	 *
	 * @param rules
	 *            the rules.
	 * @return a set with the rules of {@code rules}.
	 */
	public static Set<Rule> ruleSet(Rule... rules) {
		final Set<Rule> result = new HashSet<Rule>();
		Collections.addAll(result, rules);
		return result;
	}

	/**
	 * Create an anonymous variable.
	 *
	 * @return an anonymous variable.
	 */
	public static Variable var() {
		return new VariableImpl("_");
	}

	/**
	 * Create a variable with a specified symbol.
	 *
	 * @param symbol
	 *            the variable's symbol.
	 * @return a variable with symbol {@code symbol}.
	 */
	public static Variable var(String symbol) {
		return new VariableImpl(symbol);
	}

}

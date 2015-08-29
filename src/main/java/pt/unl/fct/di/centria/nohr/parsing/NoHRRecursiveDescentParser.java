/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import static pt.unl.fct.di.centria.nohr.model.concrete.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.var;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.COMMA;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.ID;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.IF;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.L_PAREN;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.NOT;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.QUESTION_MARK;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.R_PAREN;
import static pt.unl.fct.di.centria.nohr.parsing.TokenType.SYMBOL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;
import pt.unl.fct.di.centria.nohr.model.concrete.Model;

/**
 * A {@link <a href="https://en.wikipedia.org/wiki/Recursive_descent_parser"> recursive descent parser</a>} that implements {@link NoHRParser}.
 *
 * @author Nuno Costa
 */

public class NoHRRecursiveDescentParser implements NoHRParser {

	/** The {@link NoHRScanner} that recoginzes and consumes the tokens. */
	private NoHRScanner scanner;

	/**
	 * The {@link VocabularyMapping} used to recognize {@link OWLClass concepts}, {@link OWLProperty role} and {@link OWLIndividual individual}
	 * symbols.
	 */
	private final VocabularyMapping vocabularyMapping;

	/**
	 * Constructs a {@link NoHRRecursiveDescentParser}.
	 */
	public NoHRRecursiveDescentParser() {
		this(null);
	}

	/**
	 * Constructs a {@link NoHRRecursiveDescentParser}, that recognizes the {@link OWLClass concept}, {@link OWLProperty role}, and
	 * {@link OWLIndividual individual} symbols mapped in a given {@link VocabularyMapping}.
	 *
	 * @param vocabularyMapping
	 *            the {@link VocabularyMapping} used to recognize {@link OWLClass concepts}, {@link OWLProperty roles} and {@link individuals}.
	 */
	public NoHRRecursiveDescentParser(VocabularyMapping vocabularyMapping) {
		this.vocabularyMapping = vocabularyMapping;
	}

	/**
	 * Tries to apply: <br>
	 * <code> atom = symbol ["(" term {"," term} ")"].</code><br>
	 * <br>
	 * In case of success constructs the recognized {@link Atom} and returns it.
	 *
	 * @return the recognized {@link Atom}.
	 * @throws ParseException
	 *             if the parser can't recognize an {@link Atom} at current position.
	 */
	private Atom atom() throws ParseException {
		if (!scanner.next(SYMBOL))
			throw new ParseException(scanner.line(), scanner.position(), scanner.length(), SYMBOL);
		final String predicateSymbol = scanner.token();
		if (!scanner.next(L_PAREN))
			return Model.atom(predicateSymbol, vocabularyMapping);
		else {
			final List<Term> args = new LinkedList<>();
			do
				args.add(term());
			while (scanner.next(COMMA));
			if (!scanner.next(R_PAREN))
				throw new ParseException(scanner.line(), scanner.position(), scanner.position(), COMMA, R_PAREN);
			return Model.atom(predicateSymbol, vocabularyMapping, args);
		}
	}

	/**
	 * Tries to apply: <br>
	 * <code> literal = atom | "not " atom. </code><br>
	 * <br>
	 * In case of success constructs the recognized {@link Literal} and returns it.
	 *
	 * @return the recognized literal.
	 * @throws ParseException
	 *             the parser can't recognize a {@link Literal} at current position.
	 */
	private Literal literal() throws ParseException {
		if (scanner.next(NOT)) {
			final Atom atom = atom();
			return negLiteral(atom);
		} else
			return atom();
	}

	/**
	 * Tries to apply:<br>
	 * <code> literals = literal {"," literal}.</code><br>
	 * <br>
	 * In case of success constructs the recognized {@link Literal} {@link List} and returns it.
	 *
	 * @return the recognized list of literals.
	 * @throws ParseException
	 *             if no {@link Literal} was recognized or there was trailing chars.
	 */
	private List<Literal> literals() throws ParseException {
		final List<Literal> result = new LinkedList<>();
		do
			result.add(literal());
		while (scanner.next(COMMA));
		return result;
	}

	@Override
	public Program parseProgram(File file) throws ParseException, FileNotFoundException {
		scanner = new NoHRScanner(file);
		return program();
	}

	@Override
	public Query parseQuery(String str) throws ParseException {
		scanner = new NoHRScanner(str);
		final Query query = query();
		if (scanner.hasNext())
			throw new ParseException(scanner.line(), scanner.position(), scanner.length());
		return query;
	}

	@Override
	public Rule parseRule(String str) throws ParseException {
		scanner = new NoHRScanner(str);
		final Rule rule = rule();
		if (scanner.hasNext())
			throw new ParseException(scanner.line(), scanner.position(), scanner.length());
		return rule;
	}

	/**
	 * Tries to apply:<br>
	 * <code> program = rule "." { rule "." }.</code><br>
	 * <br>
	 * In case of success constructs the recognized {@link Program} and returns it.
	 *
	 * @return the recognized program.
	 * @throws ParseException
	 *             if no {@link Program program} was recognized or there was trailing chars.
	 */
	private Program program() throws ParseException {
		final Program program = Model.program();
		int l = 0;
		do {
			program.add(rule());
			l++;
			if (!scanner.next(TokenType.DOT))
				throw new ParseException(l, scanner.position(), scanner.position(), TokenType.DOT);
		} while (scanner.hasNext());
		return program;
	}

	/**
	 * Tries to apply: <br>
	 * <code>query = literals.}</code> <br>
	 * <br>
	 * In case of success constructs the recognized {@link Query} and returns it.
	 *
	 * @return the recognized query.
	 * @throws ParseException
	 *             if the parser can't recognize any {@link Query}.
	 */
	private Query query() throws ParseException {
		final List<Literal> literals = literals();
		return Model.query(literals);
	}

	/**
	 * Tries to apply:<br>
	 * <code> rule = atom [":-" literals ]. </code> <br>
	 * <br>
	 * In case of success contructs the recognized {@link Rule} and returns it.
	 *
	 * @return the recognized rule.
	 * @throws ParseException
	 *             if the parser can't recognize any {@link Rule}.
	 */
	private Rule rule() throws ParseException {
		final Atom head = atom();
		if (!scanner.next(IF))
			return Model.rule(head);
		else {
			final List<Literal> body = literals();
			return Model.rule(head, body);
		}
	}

	/**
	 * Tries to apply: <br>
	 * <code> term = variable | constant | list. </code> <br>
	 * <br>
	 * In cases of success constructs the recognized {@link Term} and returns if.
	 *
	 * @throws ParseException
	 *             the parser can't recognize any {@link Term} at the current position.
	 * @retrun the recognized term.
	 */
	private Term term() throws ParseException {
		final Variable variable = variable();
		if (variable != null)
			return variable;
		if (scanner.next(SYMBOL))
			return cons(scanner.token(), vocabularyMapping);
		throw new ParseException(scanner.line(), scanner.position(), scanner.length(), SYMBOL, QUESTION_MARK);
	}

	/**
	 * Tries to apply: <br>
	 * <code> variable = "?" id.</code><br>
	 * <br>
	 * In case of success constructs the recognized {@link Varible} and returns it.
	 *
	 * @return the recognized variable, or {@code null} if none is recognized.
	 * @throws ParseException
	 *             if the parser can recognize an {@link TokenType#QUESTION_MARK} but not an {@link TokenType#ID}.
	 */
	private Variable variable() throws ParseException {
		if (!scanner.next(QUESTION_MARK))
			return null;
		if (!scanner.next(ID))
			throw new ParseException(scanner.line(), scanner.position(), scanner.length(), ID);
		return var(scanner.token());
	}

}

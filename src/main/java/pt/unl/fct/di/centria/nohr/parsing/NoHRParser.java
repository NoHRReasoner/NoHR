/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * A parser that constructs {@link Rule rules} and {@link Query queries} from expressions in the following language (in
 * {@link <a href="https://en.wikipedia.org/wiki/Wirth_syntax_notation"> Wirth syntax notation</a>}), where the symbols {@code symbol} and {@code id}
 * are {@link TokenType tokens}: <br>
 * <br>
 * <code>
 * rule = atom [":-" literals ]. <br>
 * query = literals. <br>
 * literals = literal {"," literal}. <br>
 * literal = atom | "not " atom. <br>
 * atom = symbol ["(" term {"," term} ")"]. <br>
 * term = variable | symbol. <br>
 * variable = "?" id. <br>
 * </code>
 *
 * @author Nuno Costa
 */
public interface NoHRParser {

	/**
	 * Parses a given string and returns the corresponding {@link Query query}, if the string represents a query.
	 *
	 * @param str
	 *            the string to be parsed.
	 * @return the {@link Query query} that {@code str} represents.
	 * @throws ParseException
	 *             {@code str} violates the queries syntax.
	 */
	public Query parseQuery(String str) throws ParseException;

	/**
	 * Parses a given string and returns the corresponding {@link Query query}, if represents some rule.
	 *
	 * @param str
	 *            the string to be parsed.
	 * @return the {@link Query query} that {@code str} represents.
	 * @throws ParseException
	 *             if {@code str} violates the role syntax.
	 */
	public Rule parseRule(String srt) throws ParseException;

}

/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.parsing;

import java.io.File;
import java.io.FileNotFoundException;

import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * A parser that constructs {@link Rule rules} and {@link Query queries} from expressions in the following language (in
 * {@link <a href="https://en.wikipedia.org/wiki/Wirth_syntax_notation"> Wirth syntax notation</a>}), where the symbols {@code symbol} and {@code id}
 * are {@link TokenType tokens}: <br>
 * <br>
 * <code>
 * program = {rule "."}. <br>
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
	 * Parses a given {@link File file} and returns the corresponding {@link Program program}, if the file represents a program.
	 *
	 * @param file
	 *            the file to be parsed.
	 * @return the {@link Program program} that {@code file} represents.
	 * @throws ParseException
	 *             {@code file} violates the queries syntax.
	 * @throws FileNotFoundException
	 */
	public Program parseProgram(File file) throws ParseException, FileNotFoundException;

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
	public Rule parseRule(String str) throws ParseException;

}

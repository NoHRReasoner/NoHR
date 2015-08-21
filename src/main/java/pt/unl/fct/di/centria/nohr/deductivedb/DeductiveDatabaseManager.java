/**
 *
 */
package pt.unl.fct.di.centria.nohr.deductivedb;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;

/**
 * Represents a in-memory deductive database manager. A {@link DeductiveDatabaseManager} maintains a set of logic <i>programs</i> - sets of
 * {@link Rule rules} univocally identified by {@link Object object} keys - to which lets the client add or remove rules, and answers to queries based
 * on that programs.
 *
 * @author Nuno Costa
 */
public interface DeductiveDatabaseManager {

	/**
	 * Adds a given {@link Rule rule} to the <i>program</i> with a given key.
	 *
	 * @param key
	 *            the object that univocally identifies the <i>program</i> where the {@link Rule rule} will be added.
	 * @param rule
	 *            the rule to be added.
	 */
	void add(Object key, Rule rule);

	/**
	 * Deterministically obtains one answer to a given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @return one answer to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	Answer answer(Query query) throws IOException;

	/**
	 * Obtains one answer to a given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @param trueAnswers
	 *            specifies whether the answer valuation will be {@link TruthValue#TRUE true}. The answer will have a {@link TruthValue#TRUE true}
	 *            valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED} valuation if {@code trueAnswers == false}; and any of the
	 *            two if {@code trueAnswers == null}.
	 * @return one answer to {@code query} valuated according to {@code two answers}.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	Answer answer(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Obtains the answers to a given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @return one {@link Iterable} of all the answers to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	Iterable<Answer> answers(Query query) throws IOException;

	/**
	 * Obtains the answers to a given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @param trueAnswers
	 *            specifies whether the answers valuation will be {@link TruthValue#TRUE true}. The answers will have a {@link TruthValue#TRUE true}
	 *            valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED} valuation if {@code trueAnswers == false}; and any of the
	 *            two if {@code trueAnswers == null}.
	 * @return one {@link Iterable} of all the answers to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	Iterable<Answer> answers(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Obtains the valuation of each substitution corresponding to an {@link Answer answer} to given {@link Query query}, based on the loaded
	 * {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @return the {@link Map mapping} between each substitution corresponding to an {@link Answer answer} to {@code query} - represented by the list
	 *         of terms to which each {@code query}'s free variable is mapped, in the same order that those variables appear - and the
	 *         {@link TruthValue valuation} of that answer.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	Map<List<Term>, TruthValue> answersValuations(Query query) throws IOException;

	/**
	 * Obtains the valuation of each substitution corresponding to an {@link Answer answer} to given {@link Query query}, based on the loaded
	 * {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @param trueAnswers
	 *            specifies whether the answers valuations will be {@link TruthValue#TRUE true}. The answers will have a {@link TruthValue#TRUE true}
	 *            valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED undefined} valuation if {@code trueAnswers == false}; and
	 *            any of the two if {@code trueAnswers == null}.
	 * @return the {@link Map mapping} between each substitution corresponding to an {@link Answer answer} to {@code query} - represented by the list
	 *         of terms to which each {@code query}'s free variable is mapped, in the same order that those variables appear - and the
	 *         {@link TruthValue valuation} of that answer.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Dispose all <i>programs</i> and release all the reclaimed resources.
	 */
	void dipose();

	/**
	 * Dispose the <i>program</i> with a given key.
	 *
	 * @param programKey
	 *            the key of the program to be removed.
	 */
	void dispose(Object programKey);

	/**
	 * Checks if there is some answer to given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be checked for answers.
	 * @return true iff there is some answer to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	boolean hasAnswers(Query query) throws IOException;

	/**
	 * Checks if there is some answer to given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be checked for answers.
	 * @param trueAnswers
	 *            specifies whether the checked answers valuation will be {@link TruthValue#TRUE true}. The checked answers will have a
	 *            {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED} valuation if
	 *            {@code trueAnswers == false}; and any of the two if {@code trueAnswers == null}.
	 * @return true iff there is some answer to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	boolean hasAnswers(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Check if the {@link Query queries} will be answered according to the Well Founded Semantic.
	 *
	 * @return true if the {@link Query queries} will be answered according to the Well Founded Semantic.
	 */
	boolean hasWFS();

	/**
	 * Removes a given {@link Rule rule} from the <i>program</i> with a given key.
	 *
	 * @param key
	 *            the object that univocally identifies the <i>program</i> from where the {@link Rule rule} will be removed.
	 * @param rule
	 *            the rule to be removed.
	 */
	void remove(Object key, Rule rule);

}
/**
 *
 */
package pt.unl.fct.di.centria.nohr.deductivedb;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;

/**
 * Maintains a set of {@link Program programs} and can answer queries to the union of that programs (i.e. the logic program formed by all the
 * {@link Rule rules} of those programs).
 *
 * @author Nuno Costa
 */
public interface DeductiveDatabase {

	/**
	 * Deterministically obtains one answer to a given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @return one answer to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
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
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
	 */
	Answer answer(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Obtains the answers to a given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be answered.
	 * @return one {@link Iterable} of all the answers to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
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
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
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
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
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
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
	 */
	Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Creates and loads a new {@link Program program}.
	 *
	 * @return a new {@link Program program}.
	 */
	Program createProgram();

	/**
	 * Dispose all {@link Program programs} and release all the reclaimed resources.
	 */
	void dipose();

	/**
	 * Checks if there is some answer to given query, based on the loaded {@link Program programs}.
	 *
	 * @param query
	 *            the query that will be checked for answers.
	 * @return true iff there is some answer to {@code query}.
	 * @throws IOException
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
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
	 *             if {@link DeductiveDatabase} needed to read or write some file and was unsuccessful.
	 */
	boolean hasAnswers(Query query, Boolean trueAnswers) throws IOException;

	/**
	 * Check if the {@link Query queries} will be answered according to the Well Founded Semantic.
	 *
	 * @return true if the {@link Query queries} will be answered according to the Well Founded Semantic.
	 */
	boolean hasWFS();

}
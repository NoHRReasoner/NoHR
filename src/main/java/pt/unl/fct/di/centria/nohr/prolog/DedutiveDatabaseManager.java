/**
 *
 */
package pt.unl.fct.di.centria.nohr.prolog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;

/**
 * Represents a in-memory dedutive database manager. A {@link DedutiveDatabaseManager} can load {@link Program programs} and answer queries, based on
 * the loaded {@link Program programs}.
 *
 * @author Nuno Costa
 */
public interface DedutiveDatabaseManager {

	Answer answer(Query query) throws IOException;

	Answer answer(Query query, Boolean trueAnswers) throws IOException;

	Iterable<Answer> answers(Query query) throws IOException;

	Iterable<Answer> answers(Query query, Boolean trueAnswers) throws IOException;

	Map<List<Term>, TruthValue> answersValuations(Query query) throws IOException;

	Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) throws IOException;

	void clear();

	boolean hasAnswers(Query query) throws IOException;

	boolean hasAnswers(Query query, Boolean trueAnswers) throws IOException;

	boolean isTrivalued();

	void load(Program program);

}
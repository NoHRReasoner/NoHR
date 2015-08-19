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
 * @author Nuno Costa
 */
public interface DedutiveDatabase {

	void cancelLastIterator();

	void clear();

	void dispose();

	boolean hasAnswers(Query query) throws IOException;

	boolean hasAnswers(Query query, Boolean trueAnswers) throws IOException;

	boolean isTrivalued();

	Iterable<Answer> lazilyQuery(Query query) throws IOException;

	Iterable<Answer> lazilyQuery(Query query, Boolean trueAnswers) throws IOException;

	void load(Program program);

	Answer query(Query query) throws IOException;

	Answer query(Query query, Boolean trueAnswers) throws IOException;

	Map<List<Term>, TruthValue> queryAll(Query query) throws IOException;

	Map<List<Term>, TruthValue> queryAll(Query query, Boolean trueAnswers) throws IOException;

}
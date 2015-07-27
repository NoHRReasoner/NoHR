package pt.unl.fct.di.centria.nohr.xsb;

import static pt.unl.fct.di.centria.nohr.model.Model.ans;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pt.unl.fct.di.centria.nohr.Utils;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;

import com.declarativa.interprolog.AbstractPrologEngine;
import com.declarativa.interprolog.SolutionIterator;
import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

public class XSBDatabase {

    protected final FormatVisitor formatVisitor;

    private SolutionIterator lastSolutionsIterator;

    protected final Path xsbBinDirectory;

    protected AbstractPrologEngine xsbEngine;

    public XSBDatabase(Path xsbPath) throws IPException {
	xsbBinDirectory = xsbPath.toAbsolutePath();
	formatVisitor = new XSBFormatVisitor();
	startXsbEngine();
    }

    public void abolishTables() {
	xsbEngine.deterministicGoal("abolish_all_tables");
    }

    // TODO remove
    public void add(String rule) {
	xsbEngine.deterministicGoal("assert((" + rule + "))");
    }

    private void addAnswer(TermModel valuesList,
	    Map<List<Term>, TruthValue> answers) {
	final TermModel[] termsList = valuesList.flatList();
	final List<Term> vals = new ArrayList<Term>(termsList.length);
	for (int i = 1; i < termsList.length; i++)
	    vals.add(TermModelAdapter.getTerm(termsList[i]));
	final TruthValue truth = TermModelAdapter.getTruthValue(termsList[0]);
	answers.put(vals, truth);

    }

    private Answer answer(Query query, Map<Variable, Integer> varsIdx,
	    TermModel valuesList) {
	final TermModel[] termsList = valuesList.flatList();
	final TruthValue truth = TermModelAdapter.getTruthValue(termsList[0]);
	final List<Term> vals = new ArrayList<Term>(termsList.length);
	for (int i = 1; i <= varsIdx.size(); i++)
	    vals.add(TermModelAdapter.getTerm(termsList[i]));
	return ans(query, truth, vals);
    }

    public void cancelLastIterator() {
	if (lastSolutionsIterator != null)
	    lastSolutionsIterator.cancel();
	lastSolutionsIterator = null;
    }

    public void clear() {
	startXsbEngine();
    }

    public void dispose() {
	xsbEngine.shutdown();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
	super.finalize();
	dispose();
    }

    public boolean hasAnswers(Query query) {
	return hasAnswers(query, null);
    }

    public boolean hasAnswers(Query query, Boolean trueAnswers) {
	String goal;
	if (trueAnswers == null)
	    goal = query.acept(formatVisitor);
	else {
	    final String truth = trueAnswers ? "true" : "undefined";
	    goal = String.format("call_tv((%s),%s)",
		    query.acept(formatVisitor), truth);
	}
	return xsbEngine.deterministicGoal(goal);
    }

    public Iterable<Answer> lazilyQuery(Query query) {
	return lazilyQuery(query, null);
    }

    public Iterable<Answer> lazilyQuery(final Query query, Boolean trueAnswers) {
	if (lastSolutionsIterator != null) {
	    lastSolutionsIterator.cancel();
	    lastSolutionsIterator = null;
	}
	final String vars = Utils.concat(",", query.getVariables());
	String goal;
	if (trueAnswers == null)
	    goal = String.format("detGoal([%s],(%s),TM)", vars,
		    query.acept(formatVisitor));
	else {
	    final String truth = trueAnswers ? "true" : "undefined";
	    goal = String.format("detGoal([%s],(%s),%s,TM)", vars,
		    query.acept(formatVisitor), truth);
	}
	final Map<Variable, Integer> varsIdx = variablesIndex(query
		.getVariables());
	final SolutionIterator solutions = xsbEngine.goal(goal, "[TM]");
	lastSolutionsIterator = solutions;
	final XSBDatabase xsbDatabase = this;
	return new Iterable<Answer>() {

	    @Override
	    public Iterator<Answer> iterator() {
		return new Iterator<Answer>() {

		    private boolean canceled;

		    @Override
		    public boolean hasNext() {
			if (canceled)
			    return false;
			return solutions.hasNext();
		    }

		    @Override
		    public Answer next() {
			final Object[] bindings = solutions.next();
			if (!solutions.hasNext()) {
			    solutions.cancel();
			    canceled = true;
			    xsbDatabase.lastSolutionsIterator = null;
			}
			final TermModel valuesList = (TermModel) bindings[0];
			return answer(query, varsIdx, valuesList);
		    }

		    @Override
		    public void remove() {
			solutions.remove();
		    }
		};
	    }

	};
    }

    public void load(File file) {
	final boolean loaded = xsbEngine.load_dynAbsolute(file);
	if (!loaded)
	    throw new IPException("file not loaded");
    }

    public Answer query(Query query) {
	return query(query, null);
    }

    public Answer query(Query query, Boolean trueAnswers) {
	final String vars = Utils.concat(",", query.getVariables());
	String goal;
	if (trueAnswers == null)
	    goal = String.format("detGoal([%s],(%s),TM)", vars,
		    query.acept(formatVisitor));
	else {
	    final String truth = trueAnswers ? "true" : "undefined";
	    goal = String.format("detGoal([%s],(%s),%s,TM)", vars,
		    query.acept(formatVisitor), truth);
	}
	final Object[] bindings = xsbEngine.deterministicGoal(goal, "[TM]");
	if (bindings == null)
	    return null;
	return answer(query, variablesIndex(query.getVariables()),
		(TermModel) bindings[0]);
    }

    public Map<List<Term>, TruthValue> queryAll(Query query) {
	return queryAll(query, null);
    }

    public Map<List<Term>, TruthValue> queryAll(Query query, Boolean trueAnswers) {
	final Map<List<Term>, TruthValue> answers = new HashMap<List<Term>, TruthValue>();
	final String vars = Utils.concat(",", query.getVariables());
	String goal;
	if (trueAnswers == null)
	    goal = String.format("nonDetGoal([%s],(%s),TM)", vars,
		    query.acept(formatVisitor));
	else {
	    final String truth = trueAnswers ? "true" : "undefined";
	    goal = String.format("nonDetGoal([%s],(%s),%s,TM)", vars,
		    query.acept(formatVisitor), truth);
	}
	final Object[] bindings = xsbEngine.deterministicGoal(goal, "[TM]");
	if (bindings == null)
	    return answers;
	final TermModel ansList = (TermModel) bindings[0];
	for (final TermModel ans : ansList.flatList())
	    addAnswer(ans, answers);
	return answers;
    }

    protected void startXsbEngine() throws IPException {
	if (xsbEngine != null) {
	    xsbEngine.shutdown();
	    xsbEngine = null;
	}
	xsbEngine = new XSBSubprocessEngine(xsbBinDirectory.toString());
	final XSBDatabase self = this;
	xsbEngine.consultFromPackage("startup", self);
	xsbEngine.deterministicGoal("set_prolog_flag(unknown, fail)");
    }

    // TODO remove
    public void table(String predicate) {
	xsbEngine.deterministicGoal("table " + predicate);
    }

    private SortedMap<Variable, Integer> variablesIndex(List<Variable> variables) {
	final SortedMap<Variable, Integer> result = new TreeMap<Variable, Integer>();
	int i = 0;
	for (final Variable var : variables)
	    result.put(var, i++);
	return result;
    }
}

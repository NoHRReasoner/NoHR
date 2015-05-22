package xsb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nohr.model.Answer;
import nohr.model.AnswerImpl;
import nohr.model.Config;
import nohr.model.ModelException;
import nohr.model.Query;
import nohr.model.Rule;
import nohr.model.Term;
import nohr.model.TermModelAdapter;
import nohr.model.TruthValue;
import nohr.model.Variable;
import nohr.model.predicates.Predicate;

import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.XSBSubprocessEngine;

/**
 * The Class QueryEngine.
 */
public class XSBDatabase {

    /** The xsb engine. */
    protected XSBSubprocessEngine engine;

    /** The is engine started. */
    private boolean isEngineStarted = false;

    /**
     * Instantiates a new query engine.
     *
     * @throws Exception
     *             the exception
     */
    public XSBDatabase() throws Exception {

	/**
	 * Env variable which should be responsible for directory where XSB was
	 * installed
	 */
	String xsbBin = System.getenv("XSB_BIN_DIRECTORY");
	printLog("Starting query engine" + Config.NL);
	printLog(Config.TEMP_DIR + Config.NL);

	if (xsbBin != null)
	    xsbBin += "/xsb";
	else
	    throw new Exception("Please, set up your XSB_BIN_DIRECTORY");
	startEngine(xsbBin);

	engine.deterministicGoal("dynamic detGoal/3");
	engine.deterministicGoal("dynamic nonDetGoal/3");
	engine.deterministicGoal("assert((detGoal(Vars,G,TM):-call_tv(G,TV),buildTermModel([TV|Vars],TM)))");
	engine.deterministicGoal("assert((nonDetGoal(Vars,G,ListTM):-findall([TV|Vars],call_tv(G,TV),L),buildTermModel(L,ListTM)))");

    }

    public void abolishTables() {
	engine.command("abolish_all_tables");
    }

    public boolean add(Predicate predicate) {
	return engine.deterministicGoal("dynamic " + predicate);
    }

    public boolean add(Rule rule) {
	for (Predicate pred : rule.getPredicates())
	    add(pred);
	return engine.deterministicGoal(String.format("assert((%s))", rule));
    }

    public boolean add(String rule) {
	return engine.deterministicGoal(String.format("assert((%s))", rule));
    }

    private void addAnswer(TermModel valuesList,
	    Map<List<Term>, TruthValue> answers) {
	try {
	    TermModel[] termsList = valuesList.flatList();
	    List<Term> vals = new ArrayList<Term>(termsList.length);
	    for (int i = 1; i < termsList.length; i++)
		vals.add(TermModelAdapter.getTerm(termsList[i]));
	    TruthValue truth = TermModelAdapter.getTruthValue(termsList[0]);
	    answers.put(vals, truth);
	} catch (ModelException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    private Answer answer(Query query, Map<Variable, Integer> varsIdx,
	    TermModel valuesList) {
	try {
	    TermModel[] termsList = valuesList.flatList();
	    TruthValue truth = TermModelAdapter.getTruthValue(termsList[0]);
	    List<Term> vals = new ArrayList<Term>(termsList.length);
	    for (int i = 1; i < varsIdx.size(); i++)
		vals.add(TermModelAdapter.getTerm(termsList[i]));
	    return new AnswerImpl(query, truth, vals, varsIdx);
	} catch (ModelException e) {
	    e.printStackTrace();
	    System.exit(1);
	    return null;
	}
    }

    /**
     * Deterministic goal.
     *
     * @param detGoal
     *            the det goal
     * @return the object[]
     */
    public Object[] deterministicGoal(String detGoal) {
	return engine.deterministicGoal(detGoal, "[TM]");
    }

    /**
     * Deterministic goal bool.
     *
     * @param command
     *            the command
     * @return true, if successful
     */
    public boolean deterministicGoalBool(String command) {
	return engine.deterministicGoal(command);
    }

    /**
     * Checks if is engine started.
     *
     * @return true, if is engine started
     */
    public boolean isEngineStarted() {
	return isEngineStarted;
    }

    /**
     * Load.
     *
     * @param file
     *            the file
     * @return true, if successful
     */
    public boolean load(File file) {
	return engine.load_dynAbsolute(file);
    }

    /**
     * Prints the log.
     *
     * @param message
     *            the message
     */
    private void printLog(String message) {

    }

    public Answer query(Query query) {
	String goal = String.format("detGoal(%s, %s, TM)",
		query.getVariables(), query);
	Object[] objs = engine.deterministicGoal(goal, "[TM]");
	if (objs == null)
	    return null;
	return answer(query, variablesIndex(query.getVariables()),
		(TermModel) objs[0]);
    }

    public Map<List<Term>, TruthValue> queryAll(Query query) {
	Map<List<Term>, TruthValue> answers = new HashMap<List<Term>, TruthValue>();
	String goal = String.format("nonDetGoal(%s, %s, TM)",
		query.getVariables(), query);
	TermModel ansList = (TermModel) engine.deterministicGoal(goal, "[TM]")[0];
	for (TermModel ans : ansList.flatList())
	    addAnswer(ans, answers);
	return answers;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
	engine.shutdown();
    }

    /**
     * Start engine.
     *
     * @param xsbBin
     *            the xsb bin
     * @throws Exception
     *             the exception
     */
    private void startEngine(String xsbBin) throws Exception {
	if (engine != null) {

	    engine.shutdown();
	    engine = null;
	}

	isEngineStarted = true;
	try {
	    engine = new XSBSubprocessEngine(xsbBin);
	    // _engine.addPrologOutputListener(this);
	    printLog("Engine started" + Config.NL);

	} catch (Exception e) {
	    isEngineStarted = false;
	    throw new Exception("Query Engine was not started" + Config.NL
		    + e.toString() + Config.NL);
	}
    }

    public boolean table(String pred) {
	return engine.deterministicGoal("table " + pred);
    }

    private SortedMap<Variable, Integer> variablesIndex(List<Variable> variables) {
	SortedMap<Variable, Integer> result = new TreeMap<Variable, Integer>();
	int i = 0;
	for (Variable var : variables)
	    result.put(var, i++);
	return result;
    }
}

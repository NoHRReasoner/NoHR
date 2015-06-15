package pt.unl.fct.di.centria.nohr.reasoner;

import static pt.unl.fct.di.centria.nohr.model.Model.ans;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import utils.Tracer;

public class QueryProcessor {

    protected XSBDatabase xsbDatabase;

    public QueryProcessor(XSBDatabase xsbDatabase) {
	this.xsbDatabase = xsbDatabase;
    }

    private TruthValue process(TruthValue originalTruth, TruthValue doubledTruth) {
	if (originalTruth == TruthValue.FALSE)
	    return TruthValue.FALSE;
	else if (originalTruth == TruthValue.UNDEFINED)
	    return doubledTruth;
	else if (originalTruth == TruthValue.TRUE)
	    if (doubledTruth == TruthValue.FALSE)
		return TruthValue.INCONSISTENT;
	    else
		return TruthValue.TRUE;
	else
	    return null;
    }

    public Answer query(Query query) {
	return query(query, true, true, true, true);
    }

    public Answer query(Query query, boolean hasDoubled) {
	return query(query, hasDoubled, true, true, hasDoubled);
    }

    public Answer query(Query query, boolean hasDoubled, boolean trueAnswers,
	    boolean undefinedAnswers, boolean inconsistentAnswers) {
	Tracer.info("query: " + query);
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException(
		    "can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException(
		    "must have at least one truth value enabled");
	Query origQuery = query.getOriginal();
	Tracer.log("undefined original answers");
	// undefined original answers
	if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
	    for (Answer origAns : xsbDatabase.lazilyQuery(origQuery, false)) {
		if (!hasDoubled)
		    return ans(query, TruthValue.UNDEFINED, origAns.getValues());
		Query doubQuery = query.getDouble().apply(origAns.getValues());
		if (xsbDatabase.hasAnswers(doubQuery, false))
		    return ans(query, TruthValue.UNDEFINED, origAns.getValues());
	    }
	Tracer.log("true original answers");
	// true original answers
	if ((trueAnswers || inconsistentAnswers) && !undefinedAnswers)
	    for (Answer origAns : xsbDatabase.lazilyQuery(origQuery, true)) {
		if (trueAnswers && !hasDoubled)
		    return ans(query, TruthValue.TRUE, origAns.getValues());
		Query doubQuery = query.getDouble().apply(origAns.getValues());
		Tracer.log("has doubled answers for " + doubQuery + " ?");
		boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
		Tracer.log(hasDoubAns + "");
		if (trueAnswers && hasDoubAns)
		    return ans(query, TruthValue.TRUE, origAns.getValues());
		else if (inconsistentAnswers && !hasDoubAns)
		    return ans(query, TruthValue.INCONSISTENT,
			    origAns.getValues());
	    }
	Tracer.log("all original answers");
	// all original answers
	for (Answer origAns : xsbDatabase.lazilyQuery(origQuery, null)) {
	    TruthValue origTruth = origAns.getValuation();
	    if ((trueAnswers && origTruth == TruthValue.TRUE || undefinedAnswers
		    && origTruth == TruthValue.UNDEFINED)
		    && !hasDoubled)
		return ans(query, origTruth, origAns.getValues());
	    Query doubQuery = query.getDouble().apply(origAns.getValues());
	    if ((trueAnswers || inconsistentAnswers)
		    && origTruth == TruthValue.TRUE) {
		boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
		if (trueAnswers && hasDoubAns)
		    return ans(query, TruthValue.TRUE, origAns.getValues());
		else if (inconsistentAnswers && !hasDoubAns)
		    return ans(query, TruthValue.INCONSISTENT,
			    origAns.getValues());
	    }
	    if ((trueAnswers && hasDoubled || undefinedAnswers)
		    && origTruth == TruthValue.UNDEFINED) {
		Answer doubAns = xsbDatabase.query(doubQuery);
		if (doubAns != null) {
		    TruthValue doubTruth = doubAns.getValuation();
		    if (trueAnswers && doubTruth == TruthValue.TRUE)
			return ans(query, TruthValue.TRUE, origAns.getValues());
		    else if (undefinedAnswers
			    && doubTruth == TruthValue.UNDEFINED)
			return ans(query, TruthValue.UNDEFINED,
				origAns.getValues());
		}
	    }
	}
	return null;
    }

    public Collection<Answer> queryAll(Query query, boolean hasDoubled,
	    TruthValue... valuations) {
	Collection<Answer> result = new LinkedList<Answer>();
	Map<Variable, Integer> varsIdx = new HashMap<Variable, Integer>();
	int i = 0;
	for (Variable var : query.getVariables())
	    varsIdx.put(var, i++);
	Map<List<Term>, TruthValue> origAnss = xsbDatabase.queryAll(query
		.getOriginal());
	for (Entry<List<Term>, TruthValue> origEntry : origAnss.entrySet()) {
	    List<Term> vals = origEntry.getKey();
	    TruthValue origTruth = origEntry.getValue();
	    TruthValue truth = origTruth;
	    if (hasDoubled) {
		Map<List<Term>, TruthValue> doubAnss = xsbDatabase
			.queryAll(query.getDouble());
		TruthValue doubTruth = doubAnss.get(vals);
		if (doubTruth == null)
		    doubTruth = TruthValue.FALSE;
		truth = process(origTruth, doubTruth);
	    }
	    if (truth != TruthValue.FALSE)
		result.add(ans(query, truth, vals, varsIdx));
	}
	return result;
    }
}

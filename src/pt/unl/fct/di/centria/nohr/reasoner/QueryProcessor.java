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

public class QueryProcessor {

    protected XSBDatabase xsbDatabase;

    public QueryProcessor(XSBDatabase xsbDatabase) {
	this.xsbDatabase = xsbDatabase;
    }

    private boolean isRequiredTruth(TruthValue truth, boolean trueAnswers,
	    boolean undefinedAnswers, boolean inconsistentAnswers) {
	switch (truth) {
	case TRUE:
	    return trueAnswers;
	case UNDEFINED:
	    return undefinedAnswers;
	case INCONSISTENT:
	    return inconsistentAnswers;
	default:
	    return false;
	}
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
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException(
		    "can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException(
		    "must have at least one truth value enabled");
	Query origQuery = query.getOriginal();
	// undefined original answers
	if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
	    for (Answer origAns : xsbDatabase.lazilyQuery(origQuery, false)) {
		if (!hasDoubled)
		    return ans(query, TruthValue.UNDEFINED, origAns.getValues());
		Query doubQuery = query.getDouble().apply(origAns.getValues());
		if (xsbDatabase.hasAnswers(doubQuery, false))
		    return ans(query, TruthValue.UNDEFINED, origAns.getValues());
	    }
	// true original answers
	if (!undefinedAnswers)
	    for (Answer origAns : xsbDatabase.lazilyQuery(origQuery, true)) {
		if (trueAnswers && !hasDoubled)
		    return ans(query, TruthValue.TRUE, origAns.getValues());
		Query doubQuery = query.getDouble().apply(origAns.getValues());
		boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
		if (trueAnswers && hasDoubAns)
		    return ans(query, TruthValue.TRUE, origAns.getValues());
		else if (inconsistentAnswers && !hasDoubAns)
		    return ans(query, TruthValue.INCONSISTENT,
			    origAns.getValues());
	    }

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
	    boolean trueAnswers, boolean undefinedAnswers,
	    boolean inconsistentAnswers) {
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException(
		    "can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException(
		    "must have at least one truth value enabled");
	Collection<Answer> result = new LinkedList<Answer>();
	Map<Variable, Integer> varsIdx = new HashMap<Variable, Integer>();
	int i = 0;
	for (Variable var : query.getVariables())
	    varsIdx.put(var, i++);
	Map<List<Term>, TruthValue> origAnss;
	if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
	    origAnss = xsbDatabase.queryAll(query.getOriginal(), false);
	else
	    origAnss = xsbDatabase.queryAll(query.getOriginal());
	Map<List<Term>, TruthValue> doubAnss = new HashMap<List<Term>, TruthValue>();
	if (hasDoubled)
	    if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
		doubAnss = xsbDatabase.queryAll(query.getDouble(), false);
	    else
		doubAnss = xsbDatabase.queryAll(query.getDouble());
	for (Entry<List<Term>, TruthValue> origEntry : origAnss.entrySet()) {
	    List<Term> vals = origEntry.getKey();
	    TruthValue origTruth = origEntry.getValue();
	    TruthValue truth = origTruth;
	    TruthValue doubTruth = doubAnss.get(vals);
	    if (doubTruth == null)
		doubTruth = TruthValue.FALSE;
	    truth = process(origTruth, doubTruth);
	    if (isRequiredTruth(truth, trueAnswers, undefinedAnswers,
		    inconsistentAnswers))
		result.add(ans(query, truth, vals, varsIdx));
	}
	return result;
    }

}

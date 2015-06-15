package pt.unl.fct.di.centria.nohr.reasoner;

import static pt.unl.fct.di.centria.nohr.model.Model.ans;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.Visitor;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypes;
import pt.unl.fct.di.centria.nohr.reasoner.translation.EncodeVisitor;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;

public class QueryProcessor {

    protected XSBDatabase xsbDatabase;

    public QueryProcessor(XSBDatabase xsbDatabase) {
	this.xsbDatabase = xsbDatabase;
    }

    private TruthValue process(TruthValue originalTruth, TruthValue doubledTruth) {
	if (originalTruth == TruthValue.FALSE)
	    return TruthValue.FALSE;
	else if (originalTruth == TruthValue.UNDEFINED)
	    if (doubledTruth == TruthValue.FALSE)
		return TruthValue.FALSE;
	    else
		return TruthValue.UNDEFINED;
	else if (originalTruth == TruthValue.TRUE)
	    if (doubledTruth == TruthValue.FALSE)
		return TruthValue.INCONSITENT;
	    else
		return TruthValue.TRUE;
	else
	    return null;
    }

    public Answer query(Query query) {
	return query(query, null, true);
    }

    public Answer query(Query query, boolean hasDoubled) {
	return query(query, null, hasDoubled);
    }

    public Answer query(Query query, TruthValue valuation, boolean hasDoubled) {
	if (valuation == TruthValue.FALSE)
	    throw new IllegalArgumentException("valuation can't be false");
	if (valuation == TruthValue.INCONSITENT && hasDoubled == false)
	    throw new IllegalArgumentException(
		    "can't be inconsistent if there is no doubled rules");
	Query origQuery = query.getOriginal();
	if (valuation == TruthValue.INCONSITENT) {
	    for (Answer origAns : xsbDatabase.lazilyQueryAll(origQuery, true)) {
		Query doubQuery = query.getDouble().apply(origAns.getValues());
		if (!xsbDatabase.hasAnswers(doubQuery))
		    return ans(query, TruthValue.INCONSITENT,
			    origAns.getValues());
	    }
	    return null;
	}
	Boolean trueAnsInOrigQuery = null;
	if (valuation == TruthValue.TRUE)
	    trueAnsInOrigQuery = true;
	else if (valuation == TruthValue.UNDEFINED)
	    trueAnsInOrigQuery = false;
	Answer origAns = xsbDatabase.query(origQuery, trueAnsInOrigQuery);
	if (origAns == null)
	    return null;
	TruthValue origTruth = origAns.getValuation();
	TruthValue truth = origTruth;
	if (hasDoubled) {
	    Query doubQuery = query.getDouble().apply(origAns.getValues());
	    Answer doubAns = xsbDatabase.query(doubQuery);
	    if (valuation == TruthValue.TRUE)
		if (!xsbDatabase.hasAnswers(doubQuery))
		    return null;
		else
		    return Model.ans(query, TruthValue.TRUE,
			    origAns.getValues());
	    TruthValue doubTruth = doubAns == null ? TruthValue.FALSE : doubAns
		    .getValuation();
	    truth = process(origTruth, doubTruth);
	}
	return Model.ans(query, truth, origAns.getValues());
    }

    public Collection<Answer> queryAll(Query query, boolean hasDoubled) {
	Visitor originalEncoder = new EncodeVisitor(PredicateTypes.ORIGINAL);
	Map<List<Term>, TruthValue> originalAns = xsbDatabase.queryAll(query
		.acept(originalEncoder));
	Map<List<Term>, TruthValue> doubledAns = null;
	if (hasDoubled) {
	    Visitor doubledEncoder = new EncodeVisitor(PredicateTypes.DOUBLED);
	    doubledAns = xsbDatabase.queryAll(query.acept(doubledEncoder));
	}
	Collection<Answer> result = new LinkedList<Answer>();
	Map<Variable, Integer> varsIdx = new HashMap<Variable, Integer>();
	int i = 0;
	for (Variable var : query.getVariables())
	    varsIdx.put(var, i++);
	for (Entry<List<Term>, TruthValue> originalEntry : originalAns
		.entrySet()) {
	    List<Term> vals = originalEntry.getKey();
	    TruthValue originalTruth = originalEntry.getValue();
	    if (originalTruth == null)
		originalTruth = TruthValue.FALSE;
	    TruthValue truth = originalTruth;
	    if (hasDoubled) {
		TruthValue doubledTruth = doubledAns.get(vals);
		if (doubledTruth == null)
		    doubledTruth = TruthValue.FALSE;
		truth = process(originalTruth, doubledTruth);
	    }
	    if (truth != TruthValue.FALSE)
		result.add(Model.ans(query, truth, vals, varsIdx));
	}
	return result;
    }
}

package nohr.reasoner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nohr.model.Answer;
import nohr.model.Query;
import nohr.model.Substitution;
import nohr.model.TruthValue;
import nohr.model.Visitor;
import nohr.model.predicates.PredicateTypes;
import nohr.reasoner.translation.EncodeVisitor;
import xsb.XSBDatabase;

public class QueryProcessor {

    protected XSBDatabase xsbDatabase;

    public QueryProcessor(XSBDatabase xsbDatabase) {
	this.xsbDatabase = xsbDatabase;
    }

    private Answer process(Answer originalAnswer, Answer doubledAnswer) {
	return null;
    }

    private TruthValue process(TruthValue originalTruth, TruthValue doubledTruth) {
	if (originalTruth == TruthValue.FALSE)
	    return TruthValue.FALSE;
	else if (originalTruth == TruthValue.UNDIFINED)
	    if (doubledTruth == TruthValue.FALSE)
		return TruthValue.FALSE;
	    else
		return TruthValue.UNDIFINED;
	else if (originalTruth == TruthValue.TRUE)
	    if (doubledTruth == TruthValue.FALSE)
		return TruthValue.INCONSITENT;
	    else
		return TruthValue.TRUE;
	else
	    return null;
    }

    public Answer query(Query query) {
	Visitor originalEncoder = new EncodeVisitor(PredicateTypes.ORIGINAL);
	Visitor doubledEncoder = new EncodeVisitor(PredicateTypes.DOUBLED);
	Answer originalAnswer = xsbDatabase.query(query.acept(originalEncoder));
	Answer doubledAnswer = xsbDatabase.query(query.acept(doubledEncoder));
	return process(originalAnswer, doubledAnswer);
    }

    public Map<Substitution, TruthValue> queryAll(Query query) {
	Visitor originalEncoder = new EncodeVisitor(PredicateTypes.ORIGINAL);
	Visitor doubledEncoder = new EncodeVisitor(PredicateTypes.DOUBLED);
	Map<Substitution, TruthValue> originalAns = xsbDatabase.queryAll(query
		.acept(originalEncoder));
	Map<Substitution, TruthValue> doubledAns = xsbDatabase.queryAll(query
		.acept(doubledEncoder));
	Map<Substitution, TruthValue> result = new HashMap<Substitution, TruthValue>();
	for (Entry<Substitution, TruthValue> originalEntry : originalAns
		.entrySet()) {
	    Substitution substitution = originalEntry.getKey();
	    TruthValue originalTruth = originalEntry.getValue();
	    if (originalTruth == null)
		originalTruth = TruthValue.FALSE;
	    TruthValue doubledTruth = doubledAns.get(substitution);
	    if (doubledTruth == null)
		doubledTruth = TruthValue.FALSE;
	    TruthValue truth = process(originalTruth, doubledTruth);
	    if (truth != TruthValue.FALSE)
		result.put(substitution, truth);
	}
	return result;
    }
}

package pt.unl.fct.di.centria.nohr.reasoner;

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
import pt.unl.fct.di.centria.xsb.XSBDatabase;

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

    public Collection<Answer> queryAll(Query query) {
	Visitor originalEncoder = new EncodeVisitor(PredicateTypes.ORIGINAL);
	Visitor doubledEncoder = new EncodeVisitor(PredicateTypes.DOUBLED);
	Map<List<Term>, TruthValue> originalAns = xsbDatabase.queryAll(query
		.acept(originalEncoder));
	Map<List<Term>, TruthValue> doubledAns = xsbDatabase.queryAll(query
		.acept(doubledEncoder));
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
	    TruthValue doubledTruth = doubledAns.get(vals);
	    if (doubledTruth == null)
		doubledTruth = TruthValue.FALSE;
	    TruthValue truth = process(originalTruth, doubledTruth);
	    if (truth != TruthValue.FALSE)
		result.add(Model.answer(query, truth, vals, varsIdx));
	}
	return result;
    }
}

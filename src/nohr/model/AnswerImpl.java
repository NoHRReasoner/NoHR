package nohr.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import other.Utils;

public class AnswerImpl implements Answer {
	
	private Query query;
	
	private TruthValue truthValue;
	
	private Term [] values;
	
	private Map<Variable, Integer> variablesIndex;
	
	public AnswerImpl(Query query, TruthValue truthValue, Term[] values,
			Map<Variable, Integer> variablesIndex) {
		this.query = query;
		this.truthValue = truthValue;
		this.values = values;
		this.variablesIndex = variablesIndex;
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public TruthValue getValuation() {
		return truthValue;
	}

	@Override
	public Term getValue(Variable var) {
		return values[variablesIndex.get(var)];
	}
	
	@Override
	public String toString() {
		Map<Variable, Term> substitution = new HashMap<Variable, Term>();
		for(Entry<Variable, Integer> entry : variablesIndex.entrySet())
			substitution.put(entry.getKey(), values[entry.getValue()]);	
		List<Literal> literals = new LinkedList<Literal>();
		for(Literal literal : query.getLiterals())
			literals.add(literal.apply(substitution));
		return Utils.concat(",", literals);
	}

}

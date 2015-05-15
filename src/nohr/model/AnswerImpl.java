package nohr.model;

import java.util.Map;

public class AnswerImpl implements Answer {
	
	private Map<Variable, Integer> varsIndex;
	private TruthValue truthValue;
	private Term [] values;
	
	public AnswerImpl(Map<Variable, Integer> varsIndex, TruthValue truthValue, Term [] values) {
		this.varsIndex = varsIndex;
		this.truthValue = truthValue;	
		this.values = values;
	}

	@Override
	public TruthValue getValuation() {
		return truthValue;
	}

	@Override
	public Term getValue(Variable var) {
		int i = varsIndex.get(var);
		return values[i];
	}
	
	

}

package nohr.model;

import java.util.List;

import other.Utils;

public class QueryImpl implements Query {
	
	private List<Literal> literals;
	
	private List<Variable> variables;
	
	public QueryImpl(List<Literal> literals, List<Variable> variables) {
		this.literals = literals;
		this.variables = variables;
	}

	@Override
	public List<Literal> getLiterals() {
		return literals;
	}

	@Override
	public List<Variable> getVariables() {
		return variables;
	}
	
	@Override
	public String toString() {
		return Utils.concat(",", literals);	
	}

}

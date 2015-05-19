package nohr.model;

import java.util.List;

public interface Query {

	@Override
	public boolean equals(Object obj);
	
	public List<Literal> getLiterals();
	
	public List<Variable> getVariables();
	
	@Override
	public String toString();

}

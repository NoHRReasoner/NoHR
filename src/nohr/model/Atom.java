package nohr.model;

import java.util.List;
import java.util.Map;

public interface Atom {
	
	@Override
	public boolean equals(Object obj);
	
	public List<Term> getArguments();
	
	public int getArity();
	
	public Predicate getPredicate();
	
	public boolean isGrounded();
	
	public Atom apply(Variable var, Term term);	
	
	public Atom apply(Map<Variable, Term> substitution);

	@Override
	public String toString();
	
}

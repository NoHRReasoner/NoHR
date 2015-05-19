package nohr.model;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import other.Utils;

public class AtomImpl implements Atom {
	
	private List<Term> arguments;

	private Predicate predicate;
	
	public AtomImpl(Predicate predicate, List<Term> arguments) {
		this.predicate = predicate;
		this.arguments = arguments;
	}

	@Override
	public Atom apply(Variable var, Term term) {
		List<Term> args = new LinkedList<Term>(arguments);
		ListIterator<Term> argsIt = args.listIterator();
		while(argsIt.hasNext()) {
			Term t = argsIt.next();
			if (t.equals(var)) {
				argsIt.remove();
				argsIt.add(term);
			}
		}
		return new AtomImpl(predicate, args);
	}
	
	@Override
	public Atom apply(Map<Variable, Term> substitution) {
		List<Term> args = new LinkedList<Term>(arguments);
		ListIterator<Term> argsIt = args.listIterator();
		while(argsIt.hasNext()) {
			Term t = argsIt.next();
			if (substitution.containsKey(t)) {
				argsIt.remove();
				argsIt.add(substitution.get(t));
			}
		}
		return new AtomImpl(predicate, args);
	}

	@Override
	public List<Term> getArguments() {
		return arguments;
	}

	@Override
	public int getArity() {
		return predicate.getArity();
	}

	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	@Override
	public boolean isGrounded() {
		for(Term term : arguments)
			if (term.isVariable())
				return false;
		return true;
	}

	@Override
	public String toString() {	
		return predicate.getSymbol() + "(" + Utils.concat(",", arguments) + ")";
	}

}

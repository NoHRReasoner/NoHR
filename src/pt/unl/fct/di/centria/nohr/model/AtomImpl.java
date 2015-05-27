package pt.unl.fct.di.centria.nohr.model;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import other.Utils;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public class AtomImpl implements Atom {

    private List<Term> arguments;

    private Predicate predicate;

    AtomImpl(Predicate predicate, List<Term> arguments) {
	this.predicate = predicate;
	this.arguments = arguments;
    }

    @Override
    public Atom acept(Visitor visitor) {
	Predicate pred = predicate.acept(visitor);
	List<Term> args = new LinkedList<Term>();
	for (Term term : arguments)
	    args.add(term.acept(visitor));
	return new AtomImpl(pred, args);
    }

    @Override
    public Atom apply(Map<Variable, Term> substitution) {
	List<Term> args = new LinkedList<Term>(arguments);
	ListIterator<Term> argsIt = args.listIterator();
	while (argsIt.hasNext()) {
	    Term t = argsIt.next();
	    if (substitution.containsKey(t)) {
		argsIt.remove();
		argsIt.add(substitution.get(t));
	    }
	}
	return new AtomImpl(predicate, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nohr.model.Atom#apply(nohr.model.Substitution)
     */
    @Override
    public Atom apply(Substitution sub) {
	List<Term> args = new LinkedList<Term>(arguments);
	ListIterator<Term> argsIt = args.listIterator();
	while (argsIt.hasNext()) {
	    Term t = argsIt.next();
	    if (sub.getVariables().contains(t)) {
		argsIt.remove();
		argsIt.add(sub.getValue((Variable) t));
	    }
	}
	return new AtomImpl(predicate, args);
    }

    @Override
    public Atom apply(Variable var, Term term) {
	List<Term> args = new LinkedList<Term>(arguments);
	ListIterator<Term> argsIt = args.listIterator();
	while (argsIt.hasNext()) {
	    Term t = argsIt.next();
	    if (t.equals(var)) {
		argsIt.remove();
		argsIt.add(term);
	    }
	}
	return new AtomImpl(predicate, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof AtomImpl))
	    return false;
	AtomImpl other = (AtomImpl) obj;
	if (arguments == null) {
	    if (other.arguments != null)
		return false;
	} else if (!arguments.equals(other.arguments))
	    return false;
	if (predicate == null) {
	    if (other.predicate != null)
		return false;
	} else if (!predicate.equals(other.predicate))
	    return false;
	return true;
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

    /*
     * (non-Javadoc)
     *
     * @see nohr.model.Atom#getVariables()
     */
    @Override
    public List<Variable> getVariables() {
	List<Variable> result = new LinkedList<Variable>();
	for (Term arg : arguments)
	    if (arg.isVariable() && !result.contains(arg))
		result.add(arg.asVariable());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ (arguments == null ? 0 : arguments.hashCode());
	result = prime * result
		+ (predicate == null ? 0 : predicate.hashCode());
	return result;
    }

    @Override
    public boolean isGrounded() {
	for (Term term : arguments)
	    if (term.isVariable())
		return false;
	return true;
    }

    @Override
    public String toString() {
	return predicate.getSymbol() + "(" + Utils.concat(",", arguments) + ")";
    }

}

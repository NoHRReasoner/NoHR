package pt.unl.fct.di.centria.nohr.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import other.Utils;

public class QueryImpl implements Query {

    private List<Literal> literals;

    private List<Variable> variables;

    QueryImpl(List<Literal> literals, List<Variable> variables) {
	this.literals = literals;
	this.variables = variables;
    }

    @Override
    public Query acept(Visitor visitor) {
	List<Literal> lits = new LinkedList<Literal>();
	List<Variable> vars = new LinkedList<Variable>();
	for (Literal literal : literals)
	    lits.add(visitor.visit(literal));
	for (Variable var : variables)
	    vars.add(visitor.visit(var));
	return new QueryImpl(lits, vars);
    }

    /*
     * (non-Javadoc)
     *
     * @see nohr.model.Query#apply(java.util.List)
     */
    @Override
    public Query apply(List<Term> list) {
	Map<Variable, Term> map = new HashMap<Variable, Term>();
	Iterator<Variable> varsIt = variables.iterator();
	Iterator<Term> listIt = list.iterator();
	while (varsIt.hasNext() && listIt.hasNext())
	    map.put(varsIt.next(), listIt.next());
	List<Literal> lits = new LinkedList<Literal>();
	List<Variable> args = new LinkedList<Variable>(variables);
	for (Literal literal : literals)
	    lits.add(literal.apply(map));
	return new QueryImpl(lits, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nohr.model.Query#apply(nohr.model.Substitution)
     */
    @Override
    public Query apply(Substitution sub) {
	List<Literal> lits = new LinkedList<Literal>();
	List<Variable> args = new LinkedList<Variable>(variables);
	for (Literal literal : literals)
	    lits.add(literal.apply(sub));
	return new QueryImpl(lits, args);
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
	if (!(obj instanceof QueryImpl))
	    return false;
	QueryImpl other = (QueryImpl) obj;
	if (literals == null) {
	    if (other.literals != null)
		return false;
	} else if (!literals.equals(other.literals))
	    return false;
	if (variables == null) {
	    if (other.variables != null)
		return false;
	} else if (!variables.equals(other.variables))
	    return false;
	return true;
    }

    @Override
    public List<Literal> getLiterals() {
	return literals;
    }

    @Override
    public List<Variable> getVariables() {
	return variables;
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
	result = prime * result + (literals == null ? 0 : literals.hashCode());
	result = prime * result
		+ (variables == null ? 0 : variables.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return Utils.concat(",", literals);
    }

}

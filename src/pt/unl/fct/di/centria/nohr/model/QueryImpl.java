package pt.unl.fct.di.centria.nohr.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.Utils;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.reasoner.translation.EncodeVisitor;

public class QueryImpl implements Query {

    private final List<Literal> literals;

    private final List<Variable> variables;

    QueryImpl(List<Literal> literals, List<Variable> variables) {
	this.literals = literals;
	this.variables = variables;
    }

    @Override
    public Query acept(Visitor visitor) {
	final List<Literal> lits = new LinkedList<Literal>();
	final List<Variable> vars = new LinkedList<Variable>();
	for (final Literal literal : literals)
	    lits.add(visitor.visit(literal));
	for (final Variable var : variables)
	    vars.add(visitor.visit(var));
	return new QueryImpl(lits, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nohr.model.Query#apply(java.util.List)
     */
    @Override
    public Query apply(List<Term> termList) {
	if (termList.size() != variables.size())
	    throw new IllegalArgumentException(
		    "termList size must have the same size that the number of variables of the query");
	final Map<Variable, Term> map = new HashMap<Variable, Term>();
	final Iterator<Variable> varsIt = variables.iterator();
	final Iterator<Term> listIt = termList.iterator();
	while (varsIt.hasNext() && listIt.hasNext())
	    map.put(varsIt.next(), listIt.next());
	final List<Literal> lits = new LinkedList<Literal>();
	for (final Literal literal : literals)
	    lits.add(literal.apply(map));
	return new QueryImpl(lits, new LinkedList<Variable>());
    }

    /*
     * (non-Javadoc)
     *
     * @see nohr.model.Query#apply(nohr.model.Substitution)
     */
    @Override
    public Query apply(Substitution sub) {
	final List<Literal> lits = new LinkedList<Literal>();
	final Set<Variable> vars = new HashSet<Variable>();
	for (final Literal literal : literals) {
	    final Literal lit = literal.apply(sub);
	    lits.add(lit);
	    vars.addAll(lit.getVariables());
	}
	final List<Variable> args = new LinkedList<Variable>(vars);
	return new QueryImpl(lits, args);
    }

    private Query encode(PredicateType predicateType) {
	final Visitor encoder = new EncodeVisitor(predicateType);
	return acept(encoder);
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
	final QueryImpl other = (QueryImpl) obj;
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

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.Query#getDoubled()
     */
    @Override
    public Query getDouble() {
	return encode(PredicateType.DOUBLE);
    }

    @Override
    public List<Literal> getLiterals() {
	return literals;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.unl.fct.di.centria.nohr.model.Query#getOriginal()
     */
    @Override
    public Query getOriginal() {
	return encode(PredicateType.ORIGINAL);
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

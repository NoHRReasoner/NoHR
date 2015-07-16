package pt.unl.fct.di.centria.nohr.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.Utils;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public class RuleImpl implements Rule {

    // TODO positive body vs negative body
    private final Literal[] body;

    private final Atom head;

    RuleImpl(Atom head, List<Literal> body) {
	this.head = head;
	this.body = body.toArray(new Literal[] {});
    }

    @Override
    public Rule acept(Visitor visitor) {
	final List<Literal> body = new LinkedList<Literal>();
	for (final Literal literal : this.body)
	    body.add(visitor.visit(literal));
	return new RuleImpl(visitor.visit(head), body);
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
	if (!(obj instanceof RuleImpl))
	    return false;
	final RuleImpl other = (RuleImpl) obj;
	if (body == null) {
	    if (other.body != null)
		return false;
	} else if (!body.equals(other.body))
	    return false;
	if (head == null) {
	    if (other.head != null)
		return false;
	} else if (!head.equals(other.head))
	    return false;
	return true;
    }

    @Override
    public List<Literal> getBody() {
	final List<Literal> result = new LinkedList<Literal>();
	Collections.addAll(result, body);
	return result;
    }

    @Override
    public Atom getHead() {
	return head;
    }

    @Override
    public List<Literal> getNegativeBody() {
	final List<Literal> result = new ArrayList<Literal>(body.length);
	for (final Literal literal : body)
	    if (literal.isNegative())
		result.add(literal);
	return result;
    }

    @Override
    public List<Atom> getPositiveBody() {
	final List<Atom> result = new ArrayList<Atom>(body.length);
	for (final Literal literal : body)
	    if (literal.isPositive())
		result.add(literal.asPositiveLiteral());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nohr.model.Rule#getPredicates()
     */
    @Override
    public Set<Predicate> getPredicates() {
	final Set<Predicate> predicates = new HashSet<Predicate>();
	predicates.add(head.getAtom().getPredicate());
	for (final Literal literal : body)
	    predicates.add(literal.getAtom().getPredicate());
	return predicates;
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
	result = prime * result + (body == null ? 0 : body.hashCode());
	result = prime * result + (head == null ? 0 : head.hashCode());
	return result;
    }

    @Override
    public boolean isFact() {
	return body.length == 0;
    }

    @Override
    public String toString() {
	return head + (body.length == 0 ? "" : ":-") + Utils.concat(",", body);
    }

}

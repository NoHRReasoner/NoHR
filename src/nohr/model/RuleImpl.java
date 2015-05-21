package nohr.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nohr.model.predicates.Predicate;
import other.Utils;

public class RuleImpl implements Rule {

    private PositiveLiteral head;

    private List<Literal> body;

    public RuleImpl(PositiveLiteral head, List<Literal> body) {
	this.head = head;
	this.body = body;
    }

    @Override
    public Rule acept(Visitor visitor) {
	List<Literal> body = new LinkedList<Literal>();
	for (Literal literal : this.body)
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
	RuleImpl other = (RuleImpl) obj;
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
	return body;
    }

    @Override
    public PositiveLiteral getHead() {
	return head;
    }

    /*
     * (non-Javadoc)
     *
     * @see nohr.model.Rule#getPredicates()
     */
    @Override
    public Set<Predicate> getPredicates() {
	Set<Predicate> predicates = new HashSet<Predicate>();
	predicates.add(head.getAtom().getPredicate());
	for (Literal literal : body)
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
	return body.isEmpty();
    }

    @Override
    public String toString() {
	return head + (body.isEmpty() ? "" : ":-") + Utils.concat(",", body);
    }

}

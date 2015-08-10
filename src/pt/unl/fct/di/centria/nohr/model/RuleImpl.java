package pt.unl.fct.di.centria.nohr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.StringUtils;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * Implementation of {@link Rule}.
 *
 * @author Nuno Costa
 *
 */
public class RuleImpl implements Rule {

    // TODO positive body vs negative body
    /** The literals at the body of the rule. */
    private final Literal[] body;

    /** The head of the rule */
    private final Atom head;

    /**
     * Constructs a rule with a specified atom as head a literals list has body.
     *
     * @param head
     *            the head of the rule.
     * @param body
     *            the list of literals at the body of the rule.
     */
    RuleImpl(Atom head, List<? extends Literal> body) {
	this.head = head;
	this.body = body.toArray(new Literal[] {});
    }

    @Override
    public String accept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Rule acept(ModelVisitor visitor) {
	final List<Literal> body = new LinkedList<Literal>();
	for (final Literal literal : this.body)
	    body.add(visitor.visit(literal));
	return new RuleImpl(visitor.visit(head).getAtom(), body);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof RuleImpl))
	    return false;
	final RuleImpl other = (RuleImpl) obj;
	if (head == null) {
	    if (other.head != null)
		return false;
	} else if (!head.equals(other.head))
	    return false;
	if (body == null) {
	    if (other.body != null)
		return false;
	} else if (!Arrays.equals(body, other.body))
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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (body == null ? 0 : Arrays.hashCode(body));
	result = prime * result + (head == null ? 0 : head.hashCode());
	return result;
    }

    @Override
    public boolean isFact() {
	return body.length == 0;
    }

    @Override
    public String toString() {
	return head + (isFact() ? "" : " :- ") + StringUtils.concat(", ", body);
    }
}

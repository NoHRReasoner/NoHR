package pt.unl.fct.di.centria.nohr.model;

import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.StringUtils;

/**
 * Implementation of {@link ListTerm}.
 *
 * @author Nuno Costa
 *
 */
public class ListTermImpl implements ListTerm {

    /** The terms list in this list term. */
    private final List<Term> termList;

    /**
     * Constructs a list term with the specified terms list.
     *
     * @param termList
     *            the list of terms.
     */
    ListTermImpl(List<Term> termList) {
	this.termList = termList;
    }

    @Override
    public String accept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public ListTerm acept(ModelVisitor visitor) {
	final List<Term> list = new LinkedList<Term>();
	for (final Term term : termList)
	    list.add(visitor.visit(term));
	return new ListTermImpl(list);
    }

    @Override
    public Constant asConstant() {
	throw new ClassCastException();
    }

    @Override
    public List<Term> asList() {
	return termList;
    }

    @Override
    public Variable asVariable() {
	throw new ClassCastException();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof ListTermImpl))
	    return false;
	final ListTermImpl other = (ListTermImpl) obj;
	if (termList == null) {
	    if (other.termList != null)
		return false;
	} else if (!termList.equals(other.termList))
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	return termList.hashCode();
    }

    @Override
    public boolean isConstant() {
	return false;
    }

    @Override
    public boolean isList() {
	return true;
    }

    @Override
    public boolean isVariable() {
	return false;
    }

    @Override
    public String toString() {
	return "[" + StringUtils.concat(",", termList) + "]";
    }

}

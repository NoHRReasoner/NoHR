package pt.unl.fct.di.centria.nohr.model;

import java.util.LinkedList;
import java.util.List;

import other.Utils;

public class ListTermImpl implements Term {

    private List<Term> termList;

    public ListTermImpl(List<Term> termList) {
	this.termList = termList;
    }

    @Override
    public Term acept(Visitor visitor) {
	List<Term> list = new LinkedList<Term>();
	for (Term term : termList)
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
	ListTermImpl other = (ListTermImpl) obj;
	if (termList == null) {
	    if (other.termList != null)
		return false;
	} else if (!termList.equals(other.termList))
	    return false;
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
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
	return "[" + Utils.concat(",", termList) + "]";
    }

}

package pt.unl.fct.di.novalincs.nohr.model;

import java.util.LinkedList;
import java.util.List;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

class ListTermImpl implements ListTerm {

    private final List<Term> head;
    private final Term tail;

    ListTermImpl() {
        this(null, null);
    }

    ListTermImpl(List<Term> head) {
        this(head, null);
    }

    ListTermImpl(List<Term> head, Term tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Term accept(ModelVisitor visitor) {
        final List<Term> h = new LinkedList<>();

        if (this.head == null) {
            return new ListTermImpl(null, null);
        }

        for (final Term term : this.head) {
            h.add(term.accept(visitor));
        }

        if (this.tail == null) {
            return new ListTermImpl(h, null);
        }
    
        final Term t = this.tail.accept(visitor);

        return new ListTermImpl(h, t);
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public List<Term> getHead() {
        return head;
    }

    @Override
    public Term getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return "[" + (head != null && head.size() > 0 ? StringUtils.concat(",", head) + (tail != null ? "|" + tail.toString() : "") : "") + "]";
    }

}

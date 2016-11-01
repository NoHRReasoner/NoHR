package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

class ParenthesisTermImpl implements ParenthesisTerm {

    private final Term term;

    ParenthesisTermImpl(Term term) {
        this.term = term;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public ParenthesisTerm accept(ModelVisitor visitor) {
        return new ParenthesisTermImpl(term.accept(visitor));
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public Term getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "(" + term.toString() + ")";
    }

}

package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;

public class PrologOperatorPredicateImpl implements PrologPredicate {

    private final int arity;
    private final String symbol;

    PrologOperatorPredicateImpl(String symbol, int arity) {
        this.symbol = symbol;
        this.arity = arity;
    }

    @Override
    public PrologPredicate accept(ModelVisitor modelVisitor) {
        return (PrologPredicate) modelVisitor.visit(this);
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public String getSignature() {
        return asString() + "/" + getArity();
    }

    @Override
    public String asString() {
        return symbol.trim();
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return symbol;
    }

}

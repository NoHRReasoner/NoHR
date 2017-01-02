package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

public class PrologOperatorPredicateImpl implements Predicate {

    private final int arity;
    private final String symbol;

    PrologOperatorPredicateImpl(String symbol, int arity) {
        this.symbol = symbol;
        this.arity = arity;
    }

    @Override
    public Predicate accept(ModelVisitor modelVisitor) {
        return modelVisitor.visit(this);
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

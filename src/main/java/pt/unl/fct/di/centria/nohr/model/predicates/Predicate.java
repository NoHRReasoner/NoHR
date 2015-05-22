package pt.unl.fct.di.centria.nohr.model.predicates;

import pt.unl.fct.di.centria.nohr.model.Visitor;

public interface Predicate {

    public Predicate acept(Visitor visitor);

    @Override
    public boolean equals(Object obj);

    public int getArity();

    public String getName();

    public String getSymbol();

    @Override
    public int hashCode();

    @Override
    public String toString();

}

package pt.unl.fct.di.centria.nohr.model;

public interface Variable extends Term, Comparable<Variable> {

    @Override
    public Variable acept(Visitor visitor);

    @Override
    public boolean equals(Object obj);

    public String getSymbol();

    @Override
    public int hashCode();

    @Override
    public String toString();

}

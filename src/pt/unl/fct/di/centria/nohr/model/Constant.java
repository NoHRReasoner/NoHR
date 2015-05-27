package pt.unl.fct.di.centria.nohr.model;

public interface Constant extends Term {

    @Override
    public Constant acept(Visitor visitor);

    public Number asNumber();

    public String asString();

    public TruthValue asTruthValue() throws ModelException;

    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

    public boolean isNumber();

    public boolean isTruthValue();

    @Override
    public String toString();

}

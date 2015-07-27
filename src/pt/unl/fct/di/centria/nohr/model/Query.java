package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public interface Query extends FormatVisitable {

    public Query acept(Visitor visitor);

    /**
     * @param list
     * @return
     */
    public Query apply(List<Term> list);

    /**
     * @param sub
     * @return
     */
    public Query apply(Substitution sub);

    @Override
    public boolean equals(Object obj);

    public Query getDouble();

    public List<Literal> getLiterals();

    public Query getOriginal();

    public List<Variable> getVariables();

    @Override
    public String toString();

}

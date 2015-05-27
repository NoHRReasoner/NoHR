package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public interface Answer {

    /**
     * @param deHashVisitor
     * @return
     */
    public Answer acept(Visitor visitor);

    @Override
    public boolean equals(Object obj);

    public Query getQuery();

    public TruthValue getValuation();

    public Term getValue(Variable var);

    public List<Term> getValues();

    @Override
    public String toString();

}

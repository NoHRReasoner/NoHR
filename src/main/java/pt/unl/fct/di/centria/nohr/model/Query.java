package nohr.model;

import java.util.List;

public interface Query {

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
    public Object apply(Substitution sub);

    @Override
    public boolean equals(Object obj);

    public List<Literal> getLiterals();

    public List<Variable> getVariables();

    @Override
    public String toString();

}

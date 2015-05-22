package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Map;

public interface Literal {

    public Literal acept(Visitor visitor);

    public Literal apply(Map<Variable, Term> substitution);

    /**
     * @param sub
     * @return
     */
    public Literal apply(Substitution sub);

    public Literal apply(Variable var, Term term);

    public NegativeLiteral asNegativeLiteral() throws ModelException;

    public PositiveLiteral asPositiveLiteral() throws ModelException;

    @Override
    public boolean equals(Object obj);

    public Atom getAtom();

    /**
     * @return
     */
    public List<Variable> getVariables();

    public boolean isGrounded();

    public boolean isNegative();

    public boolean isPositive();

    @Override
    public String toString();

}

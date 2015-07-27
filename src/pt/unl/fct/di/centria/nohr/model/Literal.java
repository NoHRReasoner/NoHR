package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface Literal extends FormatVisitable {

    public Literal acept(Visitor visitor);

    public Literal apply(Map<Variable, Term> substitution);

    /**
     * @param sub
     * @return
     */
    public Literal apply(Substitution sub);

    public Literal apply(Variable var, Term term);

    public NegativeLiteral asNegativeLiteral();

    public Atom asPositiveLiteral();

    @Override
    public boolean equals(Object obj);

    public Atom getAtom();

    public Predicate getPredicate();

    public List<Variable> getVariables();

    public boolean isGrounded();

    public boolean isNegative();

    public boolean isPositive();

    @Override
    public String toString();

}

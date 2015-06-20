package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface Atom extends Literal {

    @Override
    public Atom acept(Visitor visitor);

    @Override
    public Atom apply(Map<Variable, Term> substitution);

    /**
     * @param sub
     * @return
     */
    @Override
    public Atom apply(Substitution sub);

    @Override
    public Atom apply(Variable var, Term term);

    @Override
    public boolean equals(Object obj);

    public List<Term> getArguments();

    public int getArity();

    public Predicate getPredicate();

    /**
     * @return
     *
     */
    @Override
    public List<Variable> getVariables();

    @Override
    public boolean isGrounded();

    @Override
    public String toString();

}

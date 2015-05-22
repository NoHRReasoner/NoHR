package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface Atom {

    public Atom acept(Visitor visitor);

    public Atom apply(Map<Variable, Term> substitution);

    /**
     * @param sub
     * @return
     */
    public Atom apply(Substitution sub);

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
    public List<Variable> getVariables();

    public boolean isGrounded();

    @Override
    public String toString();

}

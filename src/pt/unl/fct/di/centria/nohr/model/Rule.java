package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface Rule {

    public Rule acept(Visitor visitor);

    @Override
    public boolean equals(Object obj);

    public List<Literal> getBody();

    public PositiveLiteral getHead();

    public Set<Predicate> getPredicates();

    public boolean isFact();

    @Override
    public String toString();
}

package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface Rule extends FormatVisitable {

    public Rule acept(Visitor visitor);

    @Override
    public boolean equals(Object obj);

    public List<Literal> getBody();

    public Atom getHead();

    public List<Literal> getNegativeBody();

    public List<Atom> getPositiveBody();

    public Set<Predicate> getPredicates();

    public boolean isFact();

    @Override
    public String toString();
}

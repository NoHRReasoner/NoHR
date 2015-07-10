package pt.unl.fct.di.centria.nohr.model;

import java.util.Set;

public interface Substitution {

    @Override
    public boolean equals(Object obj);

    public Term getValue(Variable variable);

    public Set<Variable> getVariables();

    @Override
    public int hashCode();

    @Override
    public String toString();

}
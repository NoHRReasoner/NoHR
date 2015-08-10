package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitable;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

public interface Predicate extends FormatVisitable {

    @Override
    public String accept(FormatVisitor visitor);

    public Predicate acept(ModelVisitor visitor);

    public OWLClass asConcept();

    public OWLProperty<?, ?> asRole();

    public String asRulePredicate();

    @Override
    public boolean equals(Object obj);

    public int getArity();

    public String getName();

    public String getSymbol();

    @Override
    public int hashCode();

    public boolean isConcept();

    public boolean isRole();

    public boolean isRulePredicate();

    @Override
    public String toString();

}

package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

public interface Constant extends Term {

    @Override
    public Constant acept(Visitor visitor);

    public Number asNumber();

    public OWLIndividual asOWLIndividual();

    public OWLLiteral asOWLLiteral();

    public String asString();

    public TruthValue asTruthValue();

    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

    public boolean isNumber();

    public boolean isOWLIndividual();

    public boolean isOWLLiteral();

    public boolean isString();

    public boolean isTruthValue();

    @Override
    public String toString();

}

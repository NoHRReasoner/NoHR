package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public interface AtomTerm extends Term {

    @Override
    AtomTerm accept(ModelVisitor visitor);
    
    Atom getAtom();
}

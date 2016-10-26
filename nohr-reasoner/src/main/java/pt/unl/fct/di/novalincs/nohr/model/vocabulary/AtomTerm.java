package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public interface AtomTerm extends Term {

    @Override
    AtomTerm accept(ModelVisitor visitor);
    
    Atom getAtom();
}

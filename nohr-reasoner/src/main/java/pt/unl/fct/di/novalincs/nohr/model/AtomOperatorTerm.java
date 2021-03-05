package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public interface AtomOperatorTerm extends LiteralTerm {

    @Override
    AtomOperatorTerm accept(ModelVisitor visitor);   
    
    AtomOperator getAtomOperator();
}

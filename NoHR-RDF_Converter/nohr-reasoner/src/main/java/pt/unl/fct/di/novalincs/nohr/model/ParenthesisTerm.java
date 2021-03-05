package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public interface ParenthesisTerm extends Term {

    @Override
    ParenthesisTerm accept(ModelVisitor visitor);
    
    Term getTerm();
}

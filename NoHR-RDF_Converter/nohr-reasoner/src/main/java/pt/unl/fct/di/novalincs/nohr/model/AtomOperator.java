package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public interface AtomOperator extends Atom {

    @Override
    AtomOperator accept(ModelVisitor visitor);

    Term getLeft();

    Term getRight();
}

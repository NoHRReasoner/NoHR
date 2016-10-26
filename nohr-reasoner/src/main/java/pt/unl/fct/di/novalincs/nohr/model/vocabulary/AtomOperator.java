package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public interface AtomOperator extends Atom {

    @Override
    AtomOperator accept(ModelVisitor visitor);

    Term getLeft();

    Term getRight();
}

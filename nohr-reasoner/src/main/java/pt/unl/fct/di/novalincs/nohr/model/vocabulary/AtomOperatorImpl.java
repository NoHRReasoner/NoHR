package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.Variable;

public class AtomOperatorImpl implements AtomOperator {

    private final Atom atom;

    public AtomOperatorImpl(Predicate predicate, Term left, Term right) {
        final List<Term> args = new ArrayList<>(2);

        args.add(left);
        args.add(right);

        atom = Model.atom(predicate, args);
    }

    private AtomOperatorImpl(Atom atom) {
        this.atom = atom;
    }

    @Override
    public AtomOperator accept(ModelVisitor model) {
        return new AtomOperatorImpl(this.atom.accept(model));
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Literal apply(Map<Variable, Term> substitution) {
        return new AtomOperatorImpl((Atom) this.atom.apply(substitution));
    }

    @Override
    public Literal apply(Variable variable, Term term) {
        return new AtomOperatorImpl((Atom) this.atom.apply(variable, term));
    }

    @Override
    public List<Term> getArguments() {
        return this.atom.getArguments();
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public Atom getAtom() {
        return this;
    }

    @Override
    public Predicate getFunctor() {
        return atom.getFunctor();
    }

    @Override
    public Term getLeft() {
        return atom.getArguments().get(0);
    }

    @Override
    public Term getRight() {
        return atom.getArguments().get(1);
    }

    @Override
    public List<Variable> getVariables() {
        return atom.getVariables();
    }

    @Override
    public boolean isGrounded() {
        return atom.isGrounded();
    }

    @Override
    public String toString() {
        return atom.getArguments().get(0).toString() + atom.getFunctor().toString() + atom.getArguments().get(1).toString();
    }

}

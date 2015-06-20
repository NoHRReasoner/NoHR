package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public abstract class LiteralImpl implements Literal {

    protected Atom atom;

    LiteralImpl(Atom atom) {
	this.atom = atom;
    }

    @Override
    public abstract NegativeLiteral asNegativeLiteral();

    @Override
    public abstract Atom asPositiveLiteral();

    @Override
    public Atom getAtom() {
	return atom;
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
    public abstract boolean isNegative();

    @Override
    public abstract boolean isPositive();

}

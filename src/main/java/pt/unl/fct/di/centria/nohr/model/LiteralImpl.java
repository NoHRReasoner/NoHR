package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

public abstract class LiteralImpl implements Literal {

	protected Atom atom;

	LiteralImpl(Atom atom) {
		this.atom = atom;
	}

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

}

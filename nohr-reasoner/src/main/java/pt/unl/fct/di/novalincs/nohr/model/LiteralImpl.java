package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;
import java.util.Objects;

abstract class LiteralImpl implements Literal {

	protected Atom atom;

	LiteralImpl(Atom atom) {
		Objects.requireNonNull(atom);
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

package pt.unl.fct.di.centria.nohr.model.concrete;

import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Variable;

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

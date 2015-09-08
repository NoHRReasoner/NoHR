package pt.unl.fct.di.novalincs.nohr.model;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

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

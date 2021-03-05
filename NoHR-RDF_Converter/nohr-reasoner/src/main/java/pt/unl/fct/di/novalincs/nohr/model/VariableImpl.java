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

import java.util.Objects;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * Implementation of {@link Variable}.
 *
 * @author Nuno Costa
 */
class VariableImpl implements Variable {

	/**
	 * The symbol that represents this variable.
	 */
	private final String symbol;

	/**
	 * Constructs a symbol with a specified symbol
	 *
	 * @param symbol
	 */
	VariableImpl(String symbol) {
		Objects.requireNonNull(symbol);
		this.symbol = symbol;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Variable accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String asString() {
		return symbol;
	}

	@Override
	public int compareTo(Variable o) {
		return symbol.compareTo(o.asString());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariableImpl))
			return false;
		final VariableImpl other = (VariableImpl) obj;
		if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + symbol.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "?" + symbol;
	}

}

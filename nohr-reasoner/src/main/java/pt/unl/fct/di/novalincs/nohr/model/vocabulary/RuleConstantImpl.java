package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

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

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;

/**
 * Implementation of rule {@link Constant}.
 *
 * @author Nuno Costa
 */
class RuleConstantImpl implements HybridConstant {

	/**
	 * The symbol that represents this constant.
	 */
	private final String symbol;

	/**
	 * Constructs a rule constant with a specified symbol.
	 *
	 * @param symbol
	 *            the symbol.
	 */
	RuleConstantImpl(String symbol) {
		Objects.requireNonNull(symbol);
		this.symbol = symbol;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Constant accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public OWLIndividual asIndividual() {
		throw new ClassCastException();
	}

	@Override
	public OWLLiteral asLiteral() {
		throw new ClassCastException();
	}

	@Override
	public Number asNumber() {
		throw new ClassCastException();
	}

	@Override
	public String asString() {
		return symbol;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RuleConstantImpl))
			return false;
		final RuleConstantImpl other = (RuleConstantImpl) obj;
		if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return symbol.hashCode();
	}

	@Override
	public boolean isIndividual() {
		return false;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public String toString() {
		return symbol;
	}

}

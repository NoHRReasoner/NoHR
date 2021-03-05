/**
 *
 */
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
 * Implementation of ontology literal {@link Constant}.
 *
 * @author Nuno Costa
 */
class LiteralConstantImpl implements HybridConstant {

	/**
	 * The OWL literal.
	 */
	private final OWLLiteral literal;

	/**
	 * Constructs a constant with a specified OWL literal.
	 *
	 * @param literal
	 *            the OWL literal
	 */
	LiteralConstantImpl(OWLLiteral literal) {
		Objects.requireNonNull(literal);
		this.literal = literal;
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
		return literal;
	}

	@Override
	public Number asNumber() {
		throw new ClassCastException();
	}

	@Override
	public String asString() {
		String result = literal.getLiteral();
		if (literal.getLang() != null && !literal.getLang().isEmpty())
			result += "@" + literal.getLang();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LiteralConstantImpl other = (LiteralConstantImpl) obj;
		if (!literal.getLang().equals(other.literal.getLang()))
			return false;
		if (!literal.getLiteral().equals(other.literal.getLiteral()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (literal.getLang() == null ? 0 : literal.getLang().hashCode());
		result = prime * result + literal.getLiteral().hashCode();
		return result;
	}

	@Override
	public boolean isIndividual() {
		return false;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public String toString() {
		return asString();
	}

}

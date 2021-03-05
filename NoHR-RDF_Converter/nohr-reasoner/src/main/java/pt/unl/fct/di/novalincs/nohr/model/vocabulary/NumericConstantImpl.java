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
 * Implementation of a numeric {@link Constant}.
 *
 * @author Nuno Costa
 */
class NumericConstantImpl implements HybridConstant {

	/** The number that this constant represents */
	private final Number number;

	/**
	 * Constructs a numeric constant with a specified number.
	 *
	 * @param number
	 *            the number.
	 */
	NumericConstantImpl(Number number) {
		Objects.requireNonNull(number);
		final double dval = number.doubleValue();
		if (number.shortValue() == dval)
			number = number.shortValue();
		else if (number.intValue() == dval)
			number = number.intValue();
		else if (number.longValue() == dval)
			number = number.longValue();
		else if (number.floatValue() == dval)
			number = number.floatValue();
		else
			number = number.doubleValue();
		this.number = number;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Constant accept(ModelVisitor visit) {
		return visit.visit(this);
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
		return number;
	}

	@Override
	public String asString() {
		return String.valueOf(number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NumericConstantImpl other = (NumericConstantImpl) obj;
		if (number.doubleValue() != other.number.doubleValue())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return number.hashCode();
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
		return true;
	}

	@Override
	public String toString() {
		return asString();
	}
}

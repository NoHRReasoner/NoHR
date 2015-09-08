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

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;

/**
 * An wrapper to {@link HybridConstant} that allow the concrete type of a {@link HybridConstant constant} ({@link IndividualConstantImpl},
 * {@link LiteralConstantImpl} or {@link RuleConstantImpl}) vary according to what it (extrinsically) associated concrete representation represents at
 * each moment.
 *
 * @see Vocabulary
 * @author Nuno Costa
 */
class HybridConstantWrapper implements HybridConstant {

	private HybridConstant wrappee;

	HybridConstantWrapper(HybridConstant wrappee) {
		if (wrappee instanceof HybridConstantWrapper)
			throw new IllegalArgumentException("wrapee: can't be a HybridConstantWrapper");
		this.wrappee = wrappee;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return wrappee.accept(visitor);
	}

	@Override
	public Constant accept(ModelVisitor visitor) {
		return wrappee.accept(visitor);
	}

	@Override
	public OWLIndividual asIndividual() {
		return wrappee.asIndividual();
	}

	@Override
	public OWLLiteral asLiteral() {
		return wrappee.asLiteral();
	}

	@Override
	public Number asNumber() {
		return wrappee.asNumber();
	}

	boolean changeWrappe(HybridConstant wrappee) {
		if (wrappee instanceof HybridConstantWrapper)
			throw new IllegalArgumentException("wrapee: can't be a HybridConstantWrapper");
		final boolean changed = !wrappee.equals(this.wrappee);
		this.wrappee = wrappee;
		return changed;
	}

	@Override
	public String asString() {
		return wrappee.asString();
	}

	HybridConstant getWrappe() {
		return wrappee;
	}

	@Override
	public boolean isIndividual() {
		return wrappee.isIndividual();
	}

	@Override
	public boolean isLiteral() {
		return wrappee.isLiteral();
	}

	@Override
	public boolean isNumber() {
		return wrappee.isNumber();
	}

	@Override
	public String toString() {
		return wrappee.toString();
	}

}

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

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * An wrapper to {@link HybridPredicate} that allow the concrete type of a {@link HybridPredicate predicate} ({@link ConceptPredicateImpl},
 * {@link RolePredicateImpl} or {@link RulePredicateImpl}) vary according to what it (extrinsically) associated concrete representation represents at
 * each moment.
 *
 * @see Vocabulary
 * @author Nuno Costa
 */
class HybridPredicateWrapper implements HybridPredicate {

	private HybridPredicate wrappee;

	HybridPredicateWrapper(HybridPredicate wrappee) {
		if (wrappee instanceof HybridPredicateWrapper)
			throw new IllegalArgumentException("wrappe: shouldn't be an HybridPredicateWrapper.");
		this.wrappee = wrappee;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return wrappee.accept(visitor);
	}

	@Override
	public Predicate accept(ModelVisitor visitor) {
		return wrappee.accept(visitor);
	}

	@Override
	public OWLClass asConcept() {
		return wrappee.asConcept();
	}

	@Override
	public OWLProperty asRole() {
		return wrappee.asRole();
	}

	boolean changeWrapee(HybridPredicate wrappee) {
		if (wrappee instanceof HybridPredicateWrapper)
			throw new IllegalArgumentException("wrappe: shouldn't be an HybridPredicateWrapper.");
		final boolean changed = !wrappee.equals(this.wrappee);
		this.wrappee = wrappee;
		return changed;
	}

	@Override
	public int getArity() {
		return wrappee.getArity();
	}

	@Override
	public String getSignature() {
		return wrappee.getSignature();
	}

	@Override
	public String asString() {
		return wrappee.asString();
	}

	HybridPredicate getWrapee() {
		return wrappee;
	}

	@Override
	public boolean isConcept() {
		return wrappee.isConcept();
	}

	@Override
	public boolean isRole() {
		return wrappee.isRole();
	}

	@Override
	public String toString() {
		return wrappee.toString();
	}

}

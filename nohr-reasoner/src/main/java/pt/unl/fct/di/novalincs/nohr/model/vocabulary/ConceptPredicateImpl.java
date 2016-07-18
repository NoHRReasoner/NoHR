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

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * Implementation of a {@link HybridPredicate} representing a concept.
 *
 * @author Nuno Costa
 */
class ConceptPredicateImpl implements HybridPredicate {

	/** The concept represented by this predicate. */
	private final OWLClass concept;

	/** The preferred (user-friendly) concrete representation of the concept represented by this predicate. Can change over the time. */
	private String label;

	/**
	 * Constructs a predicate representing a specified concept.
	 *
	 * @param concept
	 *            the concept represented by the predicate. Must have a IRI fragment.
	 * @throws IllegalArgumentException
	 *             if {@code concept} hasn't a IRI fragment;
	 */
	ConceptPredicateImpl(OWLClass concept) {
		Objects.requireNonNull(concept);
		this.concept = concept;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Predicate accept(ModelVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public OWLClass asConcept() {
		return concept;
	}

	@Override
	public OWLProperty asRole() {
		throw new ClassCastException();
	}

	@Override
	public String asString() {
		return concept.getIRI().toQuotedString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ConceptPredicateImpl))
			return false;
		final ConceptPredicateImpl other = (ConceptPredicateImpl) obj;
		if (!concept.getIRI().equals(other.concept.getIRI()))
			return false;
		return true;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public String getSignature() {
		return asString() + "/" + getArity();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + concept.getIRI().hashCode();
		return result;
	}

	@Override
	public boolean isConcept() {
		return true;
	}

	@Override
	public boolean isRole() {
		return false;
	}

	/** Set the preferred (user-friendly) concrete representation of the concept represented by this predicate. */
	void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		if (label != null)
			return label;
		final String fragment = concept.getIRI().toURI().getFragment();
		if (fragment != null)
			return fragment;
		else
			return concept.getIRI().toString();
	}

}

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

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * Encode special predicates that can distinguish different views, in a KB translation, of the same {@link Predicate}. These predicates can, among
 * other things, represent some (Description Logic) constructors that otherwise can not be expressed with rules, like classic negation and right-hand
 * side existential (see {@link <a href= "http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf"> <i>Next Step for NoHR: OWL 2
 * QL</i></a>, <b>Definition 5.</b>}). Each meta-predicate refers to a (non-meta) predicate and has a specific {@link PredicateType}, associated with
 * a specific propose in a KB translation.
 *
 * @author Nuno Costa
 */
public interface MetaPredicate extends Predicate {

	@Override
	public String accept(FormatVisitor visitor);

	@Override
	public MetaPredicate accept(ModelVisitor modelVisitor);

	/**
	 * Returns the predicate that this meta-predicate refers.
	 *
	 * @return the predicate that this meta-predicate refers.
	 */
	public Predicate getPredicate();

	/**
	 * Returns the symbol that represents this meta-predicate, i.e. the symbol of the {@link Predicate} that it refer prefixed with the marker of his
	 * {@link PredicateType} (the same as {@code getType().marker() + getPredicate().getSymbol()}).
	 *
	 * @return the symbol that represents this meta-predicate.
	 */
	@Override
	public String asString();

	/**
	 * Returns the type of this meta-predicate.
	 *
	 * @return the type of this meta-predicate.
	 */
	public PredicateType getType();

	/**
	 * Returns true iff this meta-predicate as a specified type.
	 *
	 * @param type
	 *            the type.
	 * @return true iff this meta-predicate as the type {@code type}.
	 */
	public boolean hasType(PredicateType type);

}
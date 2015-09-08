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

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * Represents a predicate.
 *
 * @author Nuno Costa
 */
public interface Predicate extends Symbol {

	@Override
	public Predicate accept(ModelVisitor modelVisitor);

	/**
	 * Returns the arity of this predicate.
	 *
	 * @return the arity of this predicate.
	 */
	public int getArity();

	/**
	 * Returns the signature of this predicate, i.e. the pair {@code symbol/arity}.
	 *
	 * @return the signature of this predicate.
	 */
	public String getSignature();

}

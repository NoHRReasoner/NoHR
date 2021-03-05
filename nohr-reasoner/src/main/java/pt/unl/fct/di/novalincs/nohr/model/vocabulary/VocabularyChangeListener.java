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

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * {@link Vocabulary} changes listener.
 */
public interface VocabularyChangeListener {

	/**
	 * Called when an constant was changed.
	 *
	 * @param constant
	 *            the constant that changed.
	 */
	void constantChanged(Constant constant);

	/**
	 * Called when a predicate was changed.
	 *
	 * @param predicate
	 *            the constant that changed.
	 */
	void predicateChanged(Predicate predicate);

}

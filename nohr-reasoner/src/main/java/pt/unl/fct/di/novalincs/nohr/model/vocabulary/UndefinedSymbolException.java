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

/**
 * An exception thrown by {@link Vocabulary} when a given concept, role or individual isn't registered, i.e. it doesn't appear in the ontology of
 * which the vocabulary is vocabulary.
 *
 * @author Nuno Costa
 */
public class UndefinedSymbolException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1342663784045136518L;

}

/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.utils;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.util.Collection;

/**
 * Represents a multiset.
 *
 * @author Nuno Costa
 * @param <E>
 *            the type of the elements.
 */
public interface Multiset<E> extends Collection<E> {

	/**
	 * Returns the multiplicity of a given element (i.e. the number of times that the element occurs in the multiset).
	 *
	 * @param element
	 *            the element.
	 * @return the multiplicity of {@code element}.
	 */
	int multiplicity(E element);

}
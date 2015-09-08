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

import java.util.Set;

/**
 * Represents a transitive closure of a certain graph.
 *
 * @param <T>
 *            the type of the vertices.
 * @author Nuno Costa
 */

public interface GraphClosure<T> {

	/**
	 * Obtains all the ancestors of a given vertex.
	 *
	 * @param v
	 *            a vertex.
	 * @return all the ancestors of {@code v} in the graph that this {@link GraphClosure} closes.
	 */
	public Set<T> getAncestors(T v);

}
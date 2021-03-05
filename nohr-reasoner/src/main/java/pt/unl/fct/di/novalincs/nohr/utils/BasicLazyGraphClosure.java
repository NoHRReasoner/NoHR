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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link GraphClosure} with the algorithm <b>Closure</b> presented in the <b>Section 3.1</b> of
 * {@link <a href="http://www.madgik.di.uoa.gr/sites/default/files/acm_tods_v18.3.pp512-576.pdf">Transitive Closure Algorithms Based on Graph
 * Traversal</a>}. This implementation is lazy in the sense that the ancestors will be computed as they are requested.
 *
 * @author Nuno Costa
 * @param <T>
 *            the type of the vertices.
 */
public class BasicLazyGraphClosure<T> implements GraphClosure<T> {

	private final Map<T, Set<T>> markedAncestors; // M
	private final Map<T, Set<T>> unmarkedAncestors;// U

	/**
	 * Constructs an {@link GraphClosure} from a specified adjacency list.
	 *
	 * @param predecessors
	 *            a mapping from vertices to the set of their predecessors.
	 */

	public BasicLazyGraphClosure(Map<T, Set<T>> predecessors) {
		this.markedAncestors = new HashMap<T, Set<T>>();
		this.unmarkedAncestors = new HashMap<T, Set<T>>(predecessors);
	}

	@Override
	public Set<T> getAncestors(T v) {
		final Set<T> uv = unmarkedAncestors.get(v);
		if (uv == null)
			return null;
		Set<T> mv = markedAncestors.get(v);
		if (mv == null) {
			mv = new HashSet<T>();
			markedAncestors.put(v, mv);
		}
		while (!uv.isEmpty()) {
			final T u = uv.iterator().next();
			final Set<T> mu = markedAncestors.get(u);
			final Set<T> uu = unmarkedAncestors.get(u);
			if (mu != null)
				mv.addAll(mu);
			mv.add(u);
			if (uu != null)
				uv.addAll(uu);
			uv.removeAll(mv);
		}
		return new HashSet<T>(mv);
	}

}

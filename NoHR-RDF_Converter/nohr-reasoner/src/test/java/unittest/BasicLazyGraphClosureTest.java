package unittest;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import pt.unl.fct.di.novalincs.nohr.utils.BasicLazyGraphClosure;
import pt.unl.fct.di.novalincs.nohr.utils.GraphClosure;

public class BasicLazyGraphClosureTest {

	private Set<Integer> set(int... elems) {
		final Set<Integer> result = new HashSet<Integer>();
		for (final int e : elems)
			result.add(e);
		return result;

	}

	@Test
	public final void testGetAncestors() {
		// Data
		final Map<Integer, Set<Integer>> predecessors = new HashMap<Integer, Set<Integer>>();
		predecessors.put(9, set(8));
		predecessors.put(8, set(7));
		predecessors.put(7, set(5, 9));
		predecessors.put(6, set(5));
		predecessors.put(5, set(3, 4, 5));
		predecessors.put(4, set(1));
		predecessors.put(3, set(1));
		predecessors.put(2, set(1, 6));
		// Test
		final GraphClosure<Integer> graph = new BasicLazyGraphClosure<Integer>(predecessors);
		assertEquals(set(1, 6, 5, 3, 4), graph.getAncestors(2));
		assertEquals(set(1), graph.getAncestors(3));
		assertEquals(set(1), graph.getAncestors(4));
		assertEquals(set(3, 4, 1, 5), graph.getAncestors(5));
		assertEquals(set(5, 4, 3, 1), graph.getAncestors(6));
		assertEquals(set(9, 8, 7, 5, 4, 3, 1), graph.getAncestors(7));
		assertEquals(set(9, 8, 7, 5, 3, 4, 1), graph.getAncestors(8));
	}

}

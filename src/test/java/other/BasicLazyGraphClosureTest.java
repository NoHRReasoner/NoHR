package other;


import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nohr.reasoner.translation.ontology.ql.BasicLazyGraphClosure;
import nohr.reasoner.translation.ontology.ql.GraphClosure;

import org.junit.Test;

public class BasicLazyGraphClosureTest {
	
	private <T> Set<T> set(T... elems) {
		Set<T> result = new HashSet<T>();
		for (T e : elems)
			result.add(e);
		return result;

	}

	@Test
	public final void testGetAncestors() {
		//Data
		Map<Integer, Set<Integer>> predecessors = new HashMap<Integer, Set<Integer>>();
		predecessors.put(9, set(8));
		predecessors.put(8, set(7));
		predecessors.put(7, set(5, 9));
		predecessors.put(6, set(5));
		predecessors.put(5, set(3, 4, 5));
		predecessors.put(4, set(1));
		predecessors.put(3, set(1));
		predecessors.put(2, set(1, 6));
		//Test
		GraphClosure<Integer> graph = new BasicLazyGraphClosure<Integer>(predecessors);
		assertEquals(set(1,6,5,3,4), graph.getAncestors(2));
		assertEquals(set(1), graph.getAncestors(3));
		assertEquals(set(1), graph.getAncestors(4));
		assertEquals(set(3, 4, 1, 5), graph.getAncestors(5));
		assertEquals(set(5, 4, 3, 1), graph.getAncestors(6));
		assertEquals(set(9, 8, 7, 5, 4, 3, 1), graph.getAncestors(7));
		assertEquals(set(9, 8, 7, 5, 3, 4, 1), graph.getAncestors(8));
	}

}

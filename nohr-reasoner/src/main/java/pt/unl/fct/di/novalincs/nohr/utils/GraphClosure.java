package pt.unl.fct.di.novalincs.nohr.utils;

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
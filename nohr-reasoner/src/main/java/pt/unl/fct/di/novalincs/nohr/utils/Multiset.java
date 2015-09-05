/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.utils;

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
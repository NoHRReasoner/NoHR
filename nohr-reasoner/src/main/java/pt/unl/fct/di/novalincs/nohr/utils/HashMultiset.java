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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Represents a multiset.
 *
 * @author Nuno Costa
 */
public class HashMultiset<E> implements Set<E>, Multiset<E> {

	/** The multiplicities of the elements in the set */
	private final Map<E, Integer> multiplicities;

	public HashMultiset() {
		multiplicities = new HashMap<E, Integer>();
	}

	@Override
	public boolean add(E element) {
		final Integer oldMultiplicity = multiplicities.get(element);
		final int newMultiplicity = oldMultiplicity == null ? 1 : oldMultiplicity + 1;
		multiplicities.put(element, newMultiplicity);
		return oldMultiplicity == null || newMultiplicity != oldMultiplicity;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		boolean changed = false;
		for (final E element : collection)
			if (add(element))
				changed = true;
		return changed;
	}

	@Override
	public void clear() {
		multiplicities.clear();
	}

	@Override
	public boolean contains(Object obj) {
		return multiplicities.containsKey(obj);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return multiplicities.keySet().containsAll(c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HashMultiset<?> other = (HashMultiset<?>) obj;
		if (multiplicities == null) {
			if (other.multiplicities != null)
				return false;
		} else if (!multiplicities.equals(other.multiplicities))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (multiplicities == null ? 0 : multiplicities.hashCode());
		return result;
	}

	@Override
	public boolean isEmpty() {
		return multiplicities.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return multiplicities.keySet().iterator();
	}

	@Override
	public int multiplicity(E element) {
		final Integer result = multiplicities.get(element);
		if (result == null)
			return 0;
		else
			return result;
	}

	@Override
	public boolean remove(Object obj) {
		final Integer oldMultiplicity = multiplicities.get(obj); // 1. if obj isn't instance of E, then oldMultiplicity == null, since multiplicities
																	// can't have non E keys (it maps E to Integer).
		if (oldMultiplicity == null)
			return false; // 2. by 1 and this line, if obj is not instance of E, then the method returns.
		@SuppressWarnings("unchecked")
		final E element = (E) obj; // 3. by 2, if obj is not instance of E, then method already returned. Therefore obj can't have a non
									// E type, and the cast is safe.
		final int newMultiplicity = oldMultiplicity - 1;
		if (newMultiplicity == 0)
			multiplicities.remove(element);
		else
			multiplicities.put(element, newMultiplicity);
		return oldMultiplicity != null && newMultiplicity != oldMultiplicity;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean changed = false;
		for (final Object element : collection)
			if (remove(element))
				changed = true;
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return multiplicities.keySet().retainAll(c);
	}

	@Override
	public int size() {
		return multiplicities.size();
	}

	@Override
	public Object[] toArray() {
		return multiplicities.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return multiplicities.keySet().toArray(a);
	}

	@Override
	public String toString() {
		return multiplicities.toString();
	}

}

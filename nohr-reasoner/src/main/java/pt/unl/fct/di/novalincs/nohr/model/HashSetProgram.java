/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Implementation of {@link Program}.
 *
 * @author Nuno Costa
 */
public class HashSetProgram implements Program {

	private final Set<ProgramChangeListener> listeners;

	private final Set<Rule> rules;

	public HashSetProgram(Set<Rule> rules) {
		if (rules != null)
			this.rules = new LinkedHashSet<Rule>(rules);
		else
			this.rules = new LinkedHashSet<Rule>();
		listeners = new HashSet<ProgramChangeListener>();
	}

	@Override
	public boolean add(Rule rule) {
		final boolean added = rules.add(rule);
		if (added)
			for (final ProgramChangeListener listener : listeners)
				listener.added(rule);
		return added;
	}

	@Override
	public boolean addAll(Collection<? extends Rule> c) {
		boolean changed = false;
		for (final Rule rule : c)
			if (add(rule))
				changed = true;
		return changed;
	}

	@Override
	public void addListener(ProgramChangeListener listner) {
		listeners.add(listner);
	}

	@Override
	public void clear() {
		if (!rules.isEmpty())
			for (final ProgramChangeListener listner : listeners)
				listner.cleared();
		rules.clear();
	}

	@Override
	public boolean contains(Object obj) {
		return rules.contains(obj);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return rules.containsAll(c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HashSetProgram other = (HashSetProgram) obj;
		if (!rules.equals(other.rules))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rules.hashCode();
		return result;
	}

	@Override
	public boolean isEmpty() {
		return rules.isEmpty();
	}

	@Override
	public Iterator<Rule> iterator() {
		return rules.iterator();
	}

	@Override
	public boolean remove(Object rule) {
		final boolean removed = rules.remove(rule);
		if (removed)
			for (final ProgramChangeListener listener : listeners)
				listener.removed((Rule) rule);
		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (final Object obj : c)
			if (remove(obj))
				changed = true;
		return changed;
	}

	@Override
	public void removeListener(ProgramChangeListener listener) {
		listeners.remove(listener);
	}

	public Set<Rule> getRules(){
		return this.rules;
	}
	
	/**
	 * Not supported.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return rules.size();
	}

	@Override
	public Object[] toArray() {
		return rules.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return rules.toArray(a);
	}

	@Override
	public boolean update(Rule oldRule, Rule newRule) {
		if (!rules.contains(oldRule) || rules.contains(newRule))
			return false;
		remove(oldRule);
		add(newRule);
		for (final ProgramChangeListener listener : listeners)
			listener.updated(oldRule, newRule);
		return true;
	}
}
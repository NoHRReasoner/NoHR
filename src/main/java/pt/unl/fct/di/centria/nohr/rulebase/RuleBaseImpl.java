/**
 *
 */
package pt.unl.fct.di.centria.nohr.rulebase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * Implementation of {@link RuleBase}.
 *
 * @author Nuno Costa
 */
public class RuleBaseImpl implements RuleBase {

	private final Set<RuleBaseListener> listeners;

	private final Set<Rule> rules;

	public RuleBaseImpl() {
		rules = new HashSet<Rule>();
		listeners = new HashSet<RuleBaseListener>();
	}

	@Override
	public boolean add(Rule rule) {
		final boolean added = rules.add(rule);
		if (added)
			for (final RuleBaseListener listener : listeners)
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
	public void addListner(RuleBaseListener listner) {
		listeners.add(listner);
	}

	@Override
	public void clear() {
		if (!rules.isEmpty())
			for (final RuleBaseListener listner : listeners)
				listner.cleaned();
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
			for (final RuleBaseListener listener : listeners)
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
	public void removeListener(RuleBaseListener listener) {
		listeners.remove(listener);
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
		for (final RuleBaseListener listener : listeners)
			listener.updated(oldRule, newRule);
		return true;
	}
}
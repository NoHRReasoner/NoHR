package pt.unl.fct.di.novalincs.nohr.model;



import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of {@link DBMappingSet}.
 *
 * @author Vedran Kasalica
 */
public class HashSetDBMappingSet implements DBMappingSet {

	private final Set<DBMappingsSetChangeListener> listeners;

	private final Set<DBMapping> dBMappings;

	public HashSetDBMappingSet(Set<DBMapping> dbMappings) {
		if (dbMappings != null)
			this.dBMappings = new HashSet<DBMapping>(dbMappings);
		else
			this.dBMappings = new HashSet<DBMapping>();
		listeners = new HashSet<DBMappingsSetChangeListener>();
	}

	@Override
	public boolean add(DBMapping dBMapping) {
		final boolean added = dBMappings.add(dBMapping);
		if (added)
			for (final DBMappingsSetChangeListener listener : listeners)
				listener.added(dBMapping);
		return added;
	}

	@Override
	public boolean addAll(Collection<? extends DBMapping> c) {
		boolean changed = false;
		for (final DBMapping dBMapping : c)
			if (add(dBMapping))
				changed = true;
		return changed;
	}

	@Override
	public void addListener(DBMappingsSetChangeListener listner) {
		listeners.add(listner);
	}

	@Override
	public void clear() {
		if (!dBMappings.isEmpty())
			for (final DBMappingsSetChangeListener listner : listeners)
				listner.cleared();
		dBMappings.clear();
	}

	@Override
	public boolean contains(Object obj) {
		return dBMappings.contains(obj);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return dBMappings.containsAll(c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HashSetDBMappingSet other = (HashSetDBMappingSet) obj;
		if (!dBMappings.equals(other.dBMappings))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dBMappings.hashCode();
		return result;
	}

	@Override
	public boolean isEmpty() {
		return dBMappings.isEmpty();
	}

	@Override
	public Iterator<DBMapping> iterator() {
		return dBMappings.iterator();
	}

	@Override
	public boolean remove(Object dBMapping) {
		final boolean removed = dBMappings.remove(dBMapping);
		if (removed)
			for (final DBMappingsSetChangeListener listener : listeners)
				listener.removed((DBMapping) dBMapping);
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
	public void removeListener(DBMappingsSetChangeListener listener) {
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
		return dBMappings.size();
	}

	@Override
	public Object[] toArray() {
		return dBMappings.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return dBMappings.toArray(a);
	}

	@Override
	public boolean update(DBMapping oldDBMapping, DBMapping newDBMapping) {
		if (!dBMappings.contains(oldDBMapping) || dBMappings.contains(newDBMapping))
			return false;
		remove(oldDBMapping);
		add(newDBMapping);
		for (final DBMappingsSetChangeListener listener : listeners)
			listener.updated(oldDBMapping, newDBMapping);
		return true;
	}

	public Collection<? extends DBMapping> getDBMppings() {
		return dBMappings;
	}

}
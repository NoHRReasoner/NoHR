package pt.unl.fct.di.centria.nohr.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WeakValuedHashMap<K, V> implements Map<K, V> {

	class WeakEntry extends WeakReference<V> {

		private final K key;

		WeakEntry(K key, V referent) {
			super(referent, referenceQueue);
			assert key != null;
			this.key = key;
		}

	}

	private final int cleanUpInterval;

	private final ReferenceQueue<V> referenceQueue;

	private final Map<K, WeakEntry> references;

	private final Thread cleanUpThread = new Thread() {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(cleanUpInterval);
				} catch (final InterruptedException e) {
				}
				WeakValuedHashMap.this.cleanUp();
			}
		}
	};

	public WeakValuedHashMap(int cleanUpInterval) {
		referenceQueue = new ReferenceQueue<>();
		references = new ConcurrentHashMap<>();
		this.cleanUpInterval = cleanUpInterval;
		cleanUpThread.start();
	}

	public WeakValuedHashMap(int cleanUpInterval, int capacity) {
		referenceQueue = new ReferenceQueue<>();
		references = new ConcurrentHashMap<>(capacity);
		this.cleanUpInterval = cleanUpInterval;
		cleanUpThread.start();
	}

	public WeakValuedHashMap(int cleanUpInterval, int capacity, float loadFactor) {
		referenceQueue = new ReferenceQueue<>();
		references = new ConcurrentHashMap<>(capacity, loadFactor);
		this.cleanUpInterval = cleanUpInterval;
		cleanUpThread.start();
	}

	@SuppressWarnings("unchecked")
	private void cleanUp() {
		WeakEntry ref = (WeakValuedHashMap<K, V>.WeakEntry) referenceQueue.poll();
		while (ref != null) {
			references.remove(ref.key);
			ref = (WeakValuedHashMap<K, V>.WeakEntry) referenceQueue.poll();
		}
	}

	@Override
	public void clear() {
		references.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return references.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		cleanUp();
		final Set<Entry<K, V>> entrySet = new HashSet<>();
		for (final Entry<K, WeakEntry> entry : references.entrySet()) {
			final V value = entry.getValue().get();
			if (value != null)
				entrySet.add(new AbstractMap.SimpleEntry<K, V>(entry.getKey(), value));
		}
		return entrySet;
	}

	@Override
	public V get(Object key) {
		cleanUp();
		V value = null;
		final WeakEntry ref = references.get(key);
		if (ref != null)
			value = ref.get();
		return value;
	}

	@Override
	public boolean isEmpty() {
		return references.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return references.keySet();
	}

	@Override
	public V put(K key, V value) {
		cleanUp();
		final WeakEntry entry = new WeakEntry(key, value);
		final WeakEntry oldEntry = references.put(key, entry);
		V oldValue = null;
		if (oldEntry != null)
			oldValue = oldEntry.get();
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (final Entry<? extends K, ? extends V> entry : m.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public V remove(Object key) {
		cleanUp();
		final WeakEntry oldEntry = references.remove(key);
		V oldValue = null;
		if (oldEntry != null)
			oldValue = oldEntry.get();
		return oldValue;
	}

	@Override
	public int size() {
		cleanUp();
		return references.size();
	}

	@Override
	public Collection<V> values() {
		cleanUp();
		final Set<V> values = new HashSet<>();
		for (final WeakEntry entry : references.values()) {
			V value = null;
			if (entry != null)
				value = entry.get();
			if (value != null)
				values.add(value);
		}
		return values;
	}

}

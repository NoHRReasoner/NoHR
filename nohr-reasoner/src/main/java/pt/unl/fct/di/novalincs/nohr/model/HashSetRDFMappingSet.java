package pt.unl.fct.di.novalincs.nohr.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HashSetRDFMappingSet implements RDFMappingSet {

    private final Set<RDFMappingsSetChangeListener> listeners;

    private final Set<RDFMapping> rdfMappings;

    public HashSetRDFMappingSet(Set<RDFMapping> rdfMappings) {
        if (rdfMappings != null)
            this.rdfMappings = new HashSet<>(rdfMappings);
        else
            this.rdfMappings = new HashSet<>();
        listeners = new HashSet<>();
    }

    @Override
    public void addListener(RDFMappingsSetChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(RDFMappingsSetChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean update(RDFMapping oldRDFMapping, RDFMapping newRDFMapping) {
        if (!rdfMappings.contains(oldRDFMapping) || rdfMappings.contains(newRDFMapping))
            return false;
        remove(oldRDFMapping);
        add(newRDFMapping);
        for (final RDFMappingsSetChangeListener listener : listeners) {
            listener.update(oldRDFMapping, newRDFMapping);
        }
        return true;

    }

    @Override
    public int size() {
        return rdfMappings.size();
    }

    @Override
    public boolean isEmpty() {
        return rdfMappings.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return rdfMappings.contains(o);
    }

    @Override
    public Iterator<RDFMapping> iterator() {
        return rdfMappings.iterator();
    }

    @Override
    public Object[] toArray() {
        return rdfMappings.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return rdfMappings.toArray(a);
    }

    @Override
    public boolean add(RDFMapping rdfMapping) {
        final boolean added = rdfMappings.add(rdfMapping);
        if (added)
            for (final RDFMappingsSetChangeListener listener : listeners)
                listener.added(rdfMapping);
        return added;
    }

    @Override
    public boolean remove(Object o) {
        final boolean removed = rdfMappings.remove(o);
        if (removed)
            for (final RDFMappingsSetChangeListener listener : listeners)
                listener.removed((RDFMapping) o);
        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return rdfMappings.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends RDFMapping> c) {
        boolean changed = false;
        for (final RDFMapping rdfMapping : c)
            if (add(rdfMapping))
                changed = true;
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
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
    public void clear() {
        if (!rdfMappings.isEmpty())
            for (final RDFMappingsSetChangeListener listener : listeners)
                listener.cleared();
        rdfMappings.clear();
    }

    public Collection<? extends RDFMapping> getRDFMappings(){
        return rdfMappings;
    }
}

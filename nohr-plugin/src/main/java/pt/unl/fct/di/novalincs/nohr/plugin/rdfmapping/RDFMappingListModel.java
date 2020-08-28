package pt.unl.fct.di.novalincs.nohr.plugin.rdfmapping;

import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import pt.unl.fct.di.novalincs.nohr.model.HashSetRDFMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.RDFMapping;
import pt.unl.fct.di.novalincs.nohr.model.RDFMappingSet;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RDFMappingListModel extends AbstractListModel<Object> {

    private static final MListSectionHeader HEADER = new MListSectionHeader() {
        @Override
        public String getName() {
            return "RDF Mappings";
        }

        @Override
        public boolean canAdd() {
            return true;
        }
    };

    private static final long serialVersionUID = 1L;

    private final RDFMappingSet rdfMappingSet;

    private final RDFMappingEditor rdfMappingEditor;

    private final List<Object> rdfMappingItems;

    private final RDFMappingSetPersistenceManager rdfMappingSetPersistenceManager;

    public RDFMappingListModel(OWLEditorKit editorKit, RDFMappingEditor rdfMappingEditor, RDFMappingSetPersistenceManager rdfMappingSetPersistenceManager, RDFMappingSet rdfMappingSet) {
        super();
        this.rdfMappingSetPersistenceManager = rdfMappingSetPersistenceManager;
        this.rdfMappingEditor = rdfMappingEditor;
        this.rdfMappingSet = rdfMappingSet;
        rdfMappingItems = new ArrayList<>(rdfMappingSet.size());
        rdfMappingItems.add(HEADER);
        if (rdfMappingSet.size() > 0)
            for (final RDFMapping rdfMapping : rdfMappingSet) {
                rdfMappingItems.add(new RDFMappingListItem(rdfMappingItems.size() - 1, this, rdfMapping));
            }
    }

    boolean add(RDFMapping rdfMapping) {
        final boolean added = rdfMappingSet.add(rdfMapping);

        System.out.println("RDFMappingListModel.add() called");

        if (added) {
            System.out.println("RDF add Confirmed!");
            final int index = rdfMappingItems.size();
            rdfMappingItems.add(new RDFMappingListItem(index, this, rdfMapping));
            super.fireIntervalAdded(this, index, index);
        }
        return added;
    }

    public void clear() {
        final int size = rdfMappingItems.size();
        rdfMappingSet.clear();
        rdfMappingItems.clear();
        rdfMappingItems.add(HEADER);
        super.fireIntervalRemoved(this, 1, size);
    }

    @Override
    public int getSize() {
        return rdfMappingItems.size();
    }

    @Override
    public Object getElementAt(int index) {
        return rdfMappingItems.get(index);
    }

    public RDFMapping edit(int index, RDFMapping rdfMapping) {
        rdfMappingEditor.setRDFMapping(rdfMapping);
        final RDFMapping newRDFMapping = rdfMappingEditor.show();
        boolean updated = false;
        if (newRDFMapping != null) {
            updated = rdfMappingSet.update(rdfMapping, newRDFMapping);
        }
        fireContentsChanged(this, index, index);
        return updated ? newRDFMapping : null;
    }

    boolean remove(int index, RDFMapping rdfMapping) {
        final boolean removed = rdfMappingSet.remove(rdfMapping);

        if (removed) {

            if (index < rdfMappingItems.size() - 1) {
                for (int i = index + 1; i <= rdfMappingItems.size() - 1; i++) {
                    ((RDFMappingListItem) getElementAt(i)).setIndex(i - 1);
                }
            }
            rdfMappingItems.remove(index);
            super.fireIntervalRemoved(this, index, index);
        }
        return removed;
    }

    public void load(File file) throws IOException {
        RDFMappingSet temRDFMapping = new HashSetRDFMappingSet(Collections.<RDFMapping>emptySet());
        rdfMappingSetPersistenceManager.load(file, temRDFMapping);
        final int size = rdfMappingSet.size();
        rdfMappingSet.clear();
        rdfMappingSet.addAll(((HashSetRDFMappingSet) temRDFMapping).getRDFMappings());
        rdfMappingItems.clear();
        rdfMappingItems.add(HEADER);

        for (final RDFMapping rdfMapping : rdfMappingSet) {
            rdfMappingItems.add(new RDFMappingListItem(rdfMappingItems.size(), this, rdfMapping));
        }
        super.fireContentsChanged(this, 0, Math.max(rdfMappingSet.size() - 1, size - 1));
    }

    public void save(File file) throws IOException {
        RDFMappingSetPersistenceManager.write(rdfMappingSet, file);
    }
}

/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import org.protege.editor.core.ui.list.MListItem;
import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;

/**
 * A {@link DBMapping dbMapping} {@link MListItem item} of a certain {@link RulesList}.**
 *
 * @author Vedran Kasalica
 */
class DBMappingListItem implements MListItem {

    /**
     * The index of the item in the {@link DBMappingListModel} that it belongs.
     */
    private int index;

    /**
     * The {@link DBMappingListModel} to which the item belongs.
     */
    private final DBMappingListModel model;

    private DBMapping dbMapping;

    /**
     * @param model the {@link DBMappingListModel} to which the item belongs.
     * @param index the index of the item in the {@link DBMappingListModel} that it
     * belongs .
     */
    DBMappingListItem(int index, DBMappingListModel model, DBMapping dbMapping) {
        this.index = index;
        this.model = model;
        this.dbMapping = dbMapping;
    }

    public DBMapping getDBMapping() {
        return dbMapping;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getTooltip() {
        return dbMapping.accept(new NoHRFormatVisitor(true));
    }

    @Override
    public boolean handleDelete() {
        return model.remove(index, dbMapping);
    }

    @Override
    public void handleEdit() {
        final DBMapping newDBMapping = model.edit(index, dbMapping);

        if (newDBMapping != null) {
        	dbMapping = newDBMapping;
        }
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public String toString() {
        return dbMapping.accept(new NoHRFormatVisitor(model.getShowIRIs()));
    }
}

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.protege.editor.core.ui.list.MList;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * An {@link MList list} of {@link Rule rules}.
 *
 * @author Vedran Kasalica
 */
public class DBMappingList extends MList {

    /**
     *
     */
    private static final long serialVersionUID = 302913958066431253L;

    private final DBMappingListModel model;

    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                handleEdit();
            }
        }
    };

    private final DBMappingEditor dbMappingEditor;


    public DBMappingList(DBMappingEditor dbMappingEditor, DBMappingListModel model) {
        this.model = model;
        this.dbMappingEditor = dbMappingEditor;
        setModel(model);
        addMouseListener(mouseListener);
    }
    
//    handling the addition a new mapping
    @Override
    protected void handleAdd() {
    	dbMappingEditor.clear();
        final DBMapping newDBMapping = dbMappingEditor.show();
        if (newDBMapping != null) {
        	System.out.println("New mapping added.");
            model.add(newDBMapping);
        }
    }

    @SuppressWarnings("unchecked")
    public void setModel(DBMappingListModel model) {
        super.setModel(model);
    }
}

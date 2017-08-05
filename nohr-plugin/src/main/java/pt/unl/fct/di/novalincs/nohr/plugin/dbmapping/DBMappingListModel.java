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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.DBMappingSetPersistenceManager;
import pt.unl.fct.di.novalincs.nohr.plugin.ProgramPersistenceManager;

/**
 * An {@link ListModel list model} of {@link DBMapping DBMappings}.
 *
 * @author Vedran Kasalica
 */

public class DBMappingListModel extends AbstractListModel<Object> {

	protected static final Logger log = Logger.getLogger(DBMappingListModel.class);

	private static final MListSectionHeader HEADER = new MListSectionHeader() {

		@Override
		public boolean canAdd() {
			return true;
		}

		@Override
		public String getName() {
			return "Database Mappings";
		}
	};

	private static final long serialVersionUID = -5766699966244129502L;

	private final DBMappingSet dbMappingSet;

	private final DBMappingEditor dbMappingEditor;

	private final List<Object> dbMappingItems;

	private final DBMappingSetPersistenceManager dbMappingSetPersistenceManager;

	private boolean showIRIs;

	/**
	 *
	 */
	public DBMappingListModel(OWLEditorKit editorKit, DBMappingEditor dbMappingEditor,
			DBMappingSetPersistenceManager dbMappingSetPersistenceManager, DBMappingSet dbMappingSet) {
		super();
		this.dbMappingSetPersistenceManager = dbMappingSetPersistenceManager;

		this.showIRIs = false;
		this.dbMappingEditor = dbMappingEditor;
		this.dbMappingSet = dbMappingSet;
		dbMappingItems = new ArrayList<Object>(dbMappingSet.size());
		dbMappingItems.add(HEADER);
		if (dbMappingSet.size() > 0)
			for (final DBMapping dbMapping : dbMappingSet) {
				dbMappingItems.add(new DBMappingListItem(dbMappingItems.size() - 1, this, dbMapping));
			}
	}

	boolean add(DBMapping dbMapping) {
		final boolean added = dbMappingSet.add(dbMapping);

		if (added) {
			final int index = dbMappingItems.size();
			dbMappingItems.add(new DBMappingListItem(index, this, dbMapping));
			super.fireIntervalAdded(this, index, index);
		}

		return added;
	}

	public void clear() {
		final int size = dbMappingItems.size();
		dbMappingSet.clear();
		dbMappingItems.clear();
		dbMappingItems.add(HEADER);
		super.fireIntervalRemoved(this, 1, size);
	}

	DBMapping edit(int index, DBMapping dbMapping) {
		dbMappingEditor.setDBMapping(dbMapping);
		final DBMapping newDBMapping = dbMappingEditor.show();
		boolean updated = false;
		if (newDBMapping != null) {
			updated = dbMappingSet.update(dbMapping, newDBMapping);
		}
		fireContentsChanged(this, index, index);
		return updated ? newDBMapping : null;
	}

	@Override
	public Object getElementAt(int index) {
		return dbMappingItems.get(index);
	}

	public boolean getShowIRIs() {
		return showIRIs;
	}

	@Override
	public int getSize() {
		return dbMappingItems.size();
	}

	public void load(File file) throws IOException, PrologParserException, ParseException {
		final int size = dbMappingSet.size();
		dbMappingSet.clear();
		dbMappingSetPersistenceManager.load(file, dbMappingSet);
		dbMappingItems.clear();
		dbMappingItems.add(HEADER);

		for (final DBMapping dbMapping : dbMappingSet) {
			dbMappingItems.add(new DBMappingListItem(dbMappingItems.size(), this, dbMapping));

		}
		super.fireContentsChanged(this, 0, Math.max(dbMappingSet.size() - 1, size - 1));
	}

	boolean remove(int index, DBMapping dbMapping) {
		final boolean removed = dbMappingSet.remove(dbMapping);
		if (removed) {

			// We also need to alter the indices of elements following the one
			// to be deleted
			if (index < dbMappingItems.size() - 1) {
				for (int i = index + 1; i <= dbMappingItems.size() - 1; i++) {
					((DBMappingListItem) getElementAt(i)).setIndex(i - 1);
				}
			}
			dbMappingItems.remove(index);
			super.fireIntervalRemoved(this, index, index);
		}
		return removed;
	}

	public void save(File file) throws IOException {
		DBMappingSetPersistenceManager.write(dbMappingSet, file);
	}

	public void setShowIRIs(boolean value) {
		this.showIRIs = value;
		fireContentsChanged(this, 0, dbMappingItems.size());
	}
}

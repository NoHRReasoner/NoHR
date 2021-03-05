/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.HashSetDBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.DBMappingSetPersistenceManager;

/**
 * ListModel for our DBMapping. models the whole mapping setup.
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

//	used to parse mapping file into protege
	private final DBMappingSetPersistenceManager dbMappingSetPersistenceManager;


	/**
	 *
	 */
	
	public DBMappingListModel(OWLEditorKit editorKit, DBMappingEditor dbMappingEditor,
			DBMappingSetPersistenceManager dbMappingSetPersistenceManager, DBMappingSet dbMappingSet) {
		super();
		this.dbMappingSetPersistenceManager = dbMappingSetPersistenceManager;

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

		System.out.println("DBMappingListModel.add() called");
		if (added) {
			System.out.println("Confirmed!");
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



	@Override
	public int getSize() {
		return dbMappingItems.size();
	}


	public void load(File file) throws IOException, ParseException {
		HashSetDBMappingSet tempMappings = new HashSetDBMappingSet(Collections.<DBMapping>emptySet());
		dbMappingSetPersistenceManager.load(file, tempMappings);
		final int size = dbMappingSet.size();
		dbMappingSet.clear();
		dbMappingSet.addAll(tempMappings.getDBMppings());
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

}

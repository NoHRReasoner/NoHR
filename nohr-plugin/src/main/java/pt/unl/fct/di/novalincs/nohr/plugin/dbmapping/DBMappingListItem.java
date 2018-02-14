/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;


import org.protege.editor.core.ui.list.MListItem;
import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;

/**
 * Defining a single item of the DBMappingsList
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
    	return "NoHR Database Mapping";
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
    	 return dbMapping.toString();
    }
}

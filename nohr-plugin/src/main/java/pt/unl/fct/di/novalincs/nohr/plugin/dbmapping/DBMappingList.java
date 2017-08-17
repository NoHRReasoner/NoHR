package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.protege.editor.core.ui.list.MList;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;

/**
 *  Defining the list of mappings shown in the Database mappings view.
 * 
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

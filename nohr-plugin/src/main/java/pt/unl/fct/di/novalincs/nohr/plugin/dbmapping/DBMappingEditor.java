package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.Container;

import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.plugin.DBMappingViewComponent;

/**
 * An editor, showed in an dialog, that can be used to edit text containing database
 * mappings.
 *
 * @author Vedran Kasalica
 */
public class DBMappingEditor {

    
    private final DBMappingEditForm editor;

    private final OWLEditorKit editorKit;
    
    private final Container dbMappingViewComponent;

    /**
     * 
     *
     */ 
    public DBMappingEditor(OWLEditorKit editorKit, DBMappingViewComponent dbMappingViewComponent) {
        this.editorKit = editorKit;
        editor = new DBMappingEditForm();
        this.dbMappingViewComponent=dbMappingViewComponent;
    }

    public void clear() {
        editor.setTableText("");
        editor.setColumnsText("");
        editor.setPredicateText("");
    }

    public void setDBMapping(DBMapping dbMapping) {
    	editor.setTableText(dbMapping.getTable());
        editor.setColumnsText(dbMapping.getColumnsString());
        editor.setPredicateText(dbMapping.getPredicate());
    }

    public DBMapping show() {
        final UIHelper uiHelper = new UIHelper(editorKit);
        final int ret = uiHelper.showDialog("Database-Mapping Editor", editor, null);

        if (ret == JOptionPane.OK_OPTION) {
        	DBMappingImpl tmp=new DBMappingImpl(editor.getTableText(), editor.getColumnsText(), editor.getPredicateText());
        	System.out.println("DBMappingEditor.show() - "+tmp.toString());
            return tmp;
        }

        return null;
    }

}

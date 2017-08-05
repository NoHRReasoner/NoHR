/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.Container;
import java.awt.Dialog;

/*
 * nohr-reasoner
 * %%
 * Copyright (C) 2016 - 2017 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owlapi.model.OWLException;
import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.plugin.DBMappingViewComponent;

/**
 * An editor, showed in an dialog, that can be used to edit text containing database
 * mappings. The editor is backed by a parser that checks that the text is
 * well formed and provides feedback if the text is not well formed.
 *
 * @author Vedran Kasalica
 */
public class DBMappingEditor {

    private final ExpressionEditor<DBMapping> editor;

    private final OWLEditorKit editorKit;
    
    private final Container dbMappingViewComponent;

    /**
     * @param dbMappingViewComponent 
     *
     */
    public DBMappingEditor(OWLEditorKit editorKit, NoHRParser parser, DBMappingViewComponent dbMappingViewComponent) {
        this.editorKit = editorKit;
        editor = new ExpressionEditor<>(editorKit, new DBMappingExpressionChecker(parser));
        this.dbMappingViewComponent=dbMappingViewComponent;
    }

    public void clear() {
        editor.setText("");
    }

    public void setDBMapping(DBMapping dbMapping) {
        editor.setText(dbMapping.accept(new NoHRFormatVisitor()));
    }

    public DBMapping show() {
        final UIHelper uiHelper = new UIHelper(editorKit);
        final int ret = uiHelper.showDialog("Database-Mapping Editor", editor, null);

        if (ret == JOptionPane.OK_OPTION) {
            try {
                return editor.createObject();
            } catch (final OWLException e) { // e is an
                // OWLExpresionParserException
                return null;
            }
        }

        return null;
    }

}

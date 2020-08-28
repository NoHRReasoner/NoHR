package pt.unl.fct.di.novalincs.nohr.plugin.rdfmapping;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import pt.unl.fct.di.novalincs.nohr.model.RDFMapping;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.plugin.RDFMappingViewComponent;

import javax.swing.*;
import java.awt.*;

public class RDFMappingEditor {

    private final RDFMappingEditorForm editor;

    private final Vocabulary vocabulary;

    private final Container rdfMappingViewComponent;

    public RDFMappingEditor(Vocabulary vocabulary, RDFMappingViewComponent rdfMappingViewComponent) {
        this.vocabulary = vocabulary;
        editor = new RDFMappingEditorForm(vocabulary);
        this.rdfMappingViewComponent = rdfMappingViewComponent;
    }

    public void clear() {
        editor.setMapping(null);
    }

    public void setRDFMapping(RDFMapping rdfMapping) {
        editor.setMapping(rdfMapping);
    }

    public RDFMapping show() {

        RDFMapping tmp = null;
        int ret;
        do {
            ret = JOptionPaneEx.showConfirmDialog(rdfMappingViewComponent, "RDF-Mapping Editor", editor,
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

            if (ret == JOptionPane.OK_OPTION)
                tmp = editor.getRDFMapping();
        } while (tmp == null && ret == JOptionPane.OK_OPTION);

        if (ret == JOptionPane.OK_OPTION) {
            System.out.println("RDFMappingEditor.show() - " + tmp.toString());
            return tmp;
        }
        return null;
    }
}

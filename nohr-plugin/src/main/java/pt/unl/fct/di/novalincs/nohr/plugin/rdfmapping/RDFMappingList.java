package pt.unl.fct.di.novalincs.nohr.plugin.rdfmapping;

import org.protege.editor.core.ui.list.MList;
import pt.unl.fct.di.novalincs.nohr.model.RDFMapping;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RDFMappingList extends MList {

    private static final long serialVersionUID = 1L;

    private final RDFMappingListModel model;

    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2)
                handleEdit();
        }
    };

    private final RDFMappingEditor rdfMappingEditor;

    public RDFMappingList(RDFMappingEditor rdfMappingEditor, RDFMappingListModel model) {
        this.model = model;
        this.rdfMappingEditor = rdfMappingEditor;
        setModel(model);
        addMouseListener(mouseListener);
    }

    @Override
    protected void handleAdd() {
        rdfMappingEditor.clear();
        final RDFMapping newRdfMapping = rdfMappingEditor.show();
        if (newRdfMapping != null) {
            System.out.println("New rdf mapping added");
            model.add(newRdfMapping);
        }
    }

    @SuppressWarnings("unchecked")
    public void setModel(RDFMappingListModel model) {
        super.setModel(model);
    }
}

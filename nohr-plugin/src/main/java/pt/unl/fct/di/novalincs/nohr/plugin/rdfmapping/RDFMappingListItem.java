package pt.unl.fct.di.novalincs.nohr.plugin.rdfmapping;

import org.protege.editor.core.ui.list.MListItem;
import pt.unl.fct.di.novalincs.nohr.model.RDFMapping;

class RDFMappingListItem  implements MListItem {

    private int index;

    private RDFMappingListModel model;

    private RDFMapping rdfMapping;

    public RDFMappingListItem(int index, RDFMappingListModel rdfMappingListModel, RDFMapping rdfMapping) {
        this.index = index;
        this.model = rdfMappingListModel;
        this.rdfMapping = rdfMapping;
    }

    public RDFMapping getRdfMapping() {
        return rdfMapping;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public void handleEdit() {
        final RDFMapping newRDFMapping = model.edit(index, rdfMapping);

        if (newRDFMapping != null) {
            rdfMapping = newRDFMapping;
        }
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public boolean handleDelete() {
        return model.remove(index,rdfMapping);
    }

    @Override
    public String getTooltip() {
        return "NoHR RDF Mapping";
    }
}

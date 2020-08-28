package pt.unl.fct.di.novalincs.nohr.model;

//TODO Comment this
public interface RDFMappingsSetChangeListener {


    void added(RDFMapping rdfMapping);

    void cleared();

    void removed(RDFMapping rdfMapping);

    void update(RDFMapping oldRDFMapping, RDFMapping newDBMapping);

}

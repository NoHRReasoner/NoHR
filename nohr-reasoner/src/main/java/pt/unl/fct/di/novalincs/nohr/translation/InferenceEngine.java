package pt.unl.fct.di.novalincs.nohr.translation;

import org.semanticweb.owlapi.model.OWLOntology;

public interface InferenceEngine {

    void computeInferences(OWLOntology ontology);

}

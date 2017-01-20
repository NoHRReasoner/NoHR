package pt.unl.fct.di.novalincs.nohr.translation.inference;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public interface ELInferenceEngine {

    public Set<OWLClass> getConceptAssertions();
    
    public Set<OWLSubClassOfAxiom> getSubClassOfAxioms();
}

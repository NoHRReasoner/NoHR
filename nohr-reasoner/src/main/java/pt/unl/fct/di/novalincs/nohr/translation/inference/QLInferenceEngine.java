package pt.unl.fct.di.novalincs.nohr.translation.inference;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

public interface QLInferenceEngine {

    public Set<OWLProperty> getIrreflexiveRoles();

    public Set<OWLClass> getUnsatisfiableConcepts();

    public Set<OWLProperty> getUnsatisfiableRoles();

}

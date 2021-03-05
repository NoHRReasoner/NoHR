package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public interface Normalizer<T extends OWLAxiom> {

    boolean addNormalization(T axiom, Set<T> newAxioms);

}

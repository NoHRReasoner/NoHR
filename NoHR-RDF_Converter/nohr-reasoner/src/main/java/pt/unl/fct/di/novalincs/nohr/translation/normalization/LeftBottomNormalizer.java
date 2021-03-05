package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class LeftBottomNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        return axiom.getSubClass().isOWLNothing();
    }

}

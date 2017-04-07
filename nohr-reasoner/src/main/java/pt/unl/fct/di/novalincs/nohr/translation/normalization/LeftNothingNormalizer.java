package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class LeftNothingNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;

    public LeftNothingNormalizer(OntologyUtil util) {
        this.util = util;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression subClass = axiom.getSubClass();

        return subClass.containsConjunct(util.nothing());
    }

}

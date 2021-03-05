package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class LeftUnionOfNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;

    public LeftUnionOfNormalizer(OntologyUtil util) {
        this.util = util;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final Set<OWLClassExpression> subClassDisjunctSet = axiom.getSubClass().asDisjunctSet();
        final OWLClassExpression superClass = axiom.getSuperClass();
        boolean changed = false;

        if (subClassDisjunctSet.size() > 1) {
            changed = true;

            for (final OWLClassExpression i : subClassDisjunctSet) {
                newAxioms.add(util.subClassOf(i, superClass));
            }
        }

        return changed;
    }
}

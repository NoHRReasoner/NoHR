package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class ClassAssertionNormalizer implements Normalizer<OWLClassAssertionAxiom> {

    private final OntologyUtil util;

    public ClassAssertionNormalizer(OntologyUtil util) {
        this.util = util;
    }

    @Override
    public boolean addNormalization(OWLClassAssertionAxiom axiom, Set<OWLClassAssertionAxiom> newAxioms) {
        final Set<OWLClassExpression> classConjunctSet = axiom.getClassExpression().asConjunctSet();
        final OWLIndividual individual = axiom.getIndividual();

        if (classConjunctSet.size() > 1) {
            for (final OWLClassExpression i : classConjunctSet) {
                newAxioms.add(util.assertion(i, individual));
            }
            
            return true;
        }

        return false;
    }
}

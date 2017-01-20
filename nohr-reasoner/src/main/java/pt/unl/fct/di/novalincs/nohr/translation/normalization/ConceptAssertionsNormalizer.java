package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class ConceptAssertionsNormalizer implements Normalizer<OWLClassAssertionAxiom> {

    private final OWLOntology ontology;

    public ConceptAssertionsNormalizer(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean addNormalization(OWLClassAssertionAxiom assertion, Set<OWLClassAssertionAxiom> newAssertions) {
        final Set<OWLClassExpression> ceConj = assertion.getClassExpression().asConjunctSet();
        final OWLIndividual i = assertion.getIndividual();

        if (ceConj.size() > 1) {
            for (final OWLClassExpression ci : ceConj) {
                if (!ci.isTopEntity()) {
                    newAssertions.add(DLUtils.assertion(ontology, ci, i));
                }
            }

            return true;
        }

        return false;
    }
}

package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class RightConjunctionNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OWLOntology ontology;

    public RightConjunctionNormalizer(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        boolean changed = false;
        final OWLClassExpression ce1 = axiom.getSubClass();
        final Set<OWLClassExpression> ce2Conj = axiom.getSuperClass().asConjunctSet();

        if (ce2Conj.size() > 1) {
            for (final OWLClassExpression ci : ce2Conj) {
                newAxioms.add(DLUtils.subsumption(ontology, ce1, ci));
            }

            changed = true;
        }

        return changed;
    }
}

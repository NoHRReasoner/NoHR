package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class LeftConjunctionNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OWLOntology ontology;
    private final Vocabulary vocabulary;

    public LeftConjunctionNormalizer(OWLOntology ontology, Vocabulary vocabulary) {
        this.ontology = ontology;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        boolean changed = false;
        final Set<OWLClassExpression> ce1Conj = axiom.getSubClass().asConjunctSet();
        final OWLClassExpression ce2 = axiom.getSuperClass();

        if (ce1Conj.size() > 1) {
            final Set<OWLClassExpression> normCe1Conj = new HashSet<>();

            for (final OWLClassExpression ci : ce1Conj) {
                if (DLUtils.isExistential(ci)) {
                    final OWLClass anew = vocabulary.generateNewConcept();

                    newAxioms.add(DLUtils.subsumption(ontology, ci, anew));
                    normCe1Conj.add(anew);
                    changed = true;
                } else {
                    normCe1Conj.add(ci);
                }
            }

            if (changed) {
                newAxioms.add(DLUtils.subsumption(ontology, DLUtils.conjunction(ontology, normCe1Conj), ce2));
            }
        }
        return changed;
    }

}

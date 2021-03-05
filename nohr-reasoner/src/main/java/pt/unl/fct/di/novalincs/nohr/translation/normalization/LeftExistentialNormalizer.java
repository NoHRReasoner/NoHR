package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class LeftExistentialNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OWLOntology ontology;
    private final Vocabulary vocabulary;

    public LeftExistentialNormalizer(OWLOntology ontology, Vocabulary vocabulary) {
        this.ontology = ontology;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        boolean changed = false;
        final OWLClassExpression ce1 = axiom.getSubClass();
        final OWLClassExpression ce2 = axiom.getSuperClass();

        if (DLUtils.isExistential(ce1)) {
            final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce1;
            final OWLObjectPropertyExpression ope = some.getProperty();
            final Set<OWLClassExpression> fillerConj = some.getFiller().asConjunctSet();
            final Set<OWLClassExpression> normFillerConj = new HashSet<>();

            for (final OWLClassExpression ci : fillerConj) {
                if (DLUtils.isExistential(ci)) {
                    final OWLClass anew = vocabulary.generateNewConcept();

                    newAxioms.add(DLUtils.subsumption(ontology, ci, anew));
                    normFillerConj.add(anew);
                    changed = true;
                } else {
                    normFillerConj.add(ci);
                }
            }

            if (changed) {
                newAxioms.add(DLUtils.subsumption(ontology, DLUtils.some(ontology, ope, DLUtils.conjunction(ontology, normFillerConj)), ce2));
            }
        }
        
        return changed;
    }
}

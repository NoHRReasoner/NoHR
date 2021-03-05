package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class RightIntersectionOfNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;
    private final Vocabulary vocabulary;

    public RightIntersectionOfNormalizer(OntologyUtil util, Vocabulary vocabulary) {
        this.util = util;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression subClass = axiom.getSubClass();
        final Set<OWLClassExpression> superClassConjunctSet = axiom.getSuperClass().asConjunctSet();
        boolean changed = false;

        if (superClassConjunctSet.size() > 1) {
            changed = true;

            if (subClass.isAnonymous()) {
                final OWLClassExpression newClass = vocabulary.generateNewConcept();
                newAxioms.add(util.subClassOf(subClass, newClass));

                for (final OWLClassExpression i : superClassConjunctSet) {
                    newAxioms.add(util.subClassOf(newClass, i));
                }
            } else {
                for (final OWLClassExpression i : superClassConjunctSet) {
                    newAxioms.add(util.subClassOf(subClass, i));
                }

            }
        }

        return changed;
    }
}

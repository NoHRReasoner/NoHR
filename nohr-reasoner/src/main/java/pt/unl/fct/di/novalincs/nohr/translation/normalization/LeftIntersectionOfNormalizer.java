package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class LeftIntersectionOfNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;
    private final Vocabulary vocabulary;

    public LeftIntersectionOfNormalizer(OntologyUtil util, Vocabulary vocabulary) {
        this.util = util;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final Set<OWLClassExpression> subClassConjunctSet = axiom.getSubClass().asConjunctSet();
        final OWLClassExpression superClass = axiom.getSuperClass();
        boolean changed = false;

        if (subClassConjunctSet.size() > 1) {
            final Set<OWLClassExpression> normalizedSubClassConjunctSet = new HashSet<>();

            for (final OWLClassExpression i : subClassConjunctSet) {
                if (i.isAnonymous()) {
                    final OWLClass newClass = vocabulary.generateNewConcept();

                    newAxioms.add(util.subClassOf(i, newClass));
                    normalizedSubClassConjunctSet.add(newClass);
                    changed = true;
                } else {
                    normalizedSubClassConjunctSet.add(i);
                }
            }

            if (changed) {
                newAxioms.add(util.subClassOf(util.intersectionOf(normalizedSubClassConjunctSet), superClass));
            }
        }

        return changed;
    }

}

package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class LeftSomeValuesFromNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;
    private final Vocabulary vocabulary;

    public LeftSomeValuesFromNormalizer(OntologyUtil util, Vocabulary vocabulary) {
        this.util = util;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression subClass = axiom.getSubClass();
        final OWLClassExpression superClass = axiom.getSuperClass();
        boolean changed = false;

        if (subClass instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom someValuesFrom = (OWLObjectSomeValuesFrom) subClass;
            final OWLObjectPropertyExpression property = someValuesFrom.getProperty();
            final Set<OWLClassExpression> fillerConjunctSet = someValuesFrom.getFiller().asConjunctSet();
            final Set<OWLClassExpression> normalizedFillerConjunctSet = new HashSet<>();

            for (final OWLClassExpression i : fillerConjunctSet) {
                if (i.isAnonymous()) {
                    final OWLClass newClass = vocabulary.generateNewConcept();

                    newAxioms.add(util.subClassOf(i, newClass));
                    normalizedFillerConjunctSet.add(newClass);
                    changed = true;
                } else {
                    normalizedFillerConjunctSet.add(i);
                }
            }

            if (changed) {
                newAxioms.add(util.subClassOf(util.someValuesFrom(property, util.intersectionOf(normalizedFillerConjunctSet)), superClass));
            }
        }

        return changed;
    }
}

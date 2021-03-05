package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class RightAllValuesFromNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;
    private final Vocabulary vocabulary;

    public RightAllValuesFromNormalizer(OntologyUtil util, Vocabulary vocabulary) {
        this.util = util;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression subClass = axiom.getSubClass();
        final OWLClassExpression superClass = axiom.getSuperClass();
        boolean changed = false;

        if (superClass instanceof OWLObjectAllValuesFrom) {
            OWLObjectAllValuesFrom allValuesFrom = (OWLObjectAllValuesFrom) superClass;
            OWLObjectPropertyExpression property = allValuesFrom.getProperty();
            OWLClassExpression filler = allValuesFrom.getFiller();
            OWLClassExpression newClass = vocabulary.generateNewConcept();

            changed = true;

            newAxioms.add(util.subClassOf(subClass, newClass));

            while (filler instanceof OWLObjectAllValuesFrom) {
                final OWLClassExpression lastClass = newClass;
                newClass = vocabulary.generateNewConcept();

                newAxioms.add(util.subClassOf(util.someValuesFrom(util.inverseOf(property), lastClass), newClass));

                property = allValuesFrom.getProperty();
                filler = allValuesFrom.getFiller();
            }

            newAxioms.add(util.subClassOf(util.someValuesFrom(util.inverseOf(property), newClass), filler));
        }

        return changed;
    }
}

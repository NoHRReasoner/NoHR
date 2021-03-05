package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class AnonymousSubClassOfNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;
    private final Vocabulary vocabulary;

    public AnonymousSubClassOfNormalizer(OntologyUtil util, Vocabulary vocabulary) {
        this.util = util;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression subClass = axiom.getSubClass();
        final OWLClassExpression superClass = axiom.getSuperClass();

        if (subClass.isAnonymous() && superClass.isAnonymous()) {
            final OWLClassExpression newClass = vocabulary.generateNewConcept();

            newAxioms.add(util.subClassOf(subClass, newClass));
            newAxioms.add(util.subClassOf(newClass, superClass));

            return true;
        }

        return false;
    }
}

package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class ComplexSidesNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OWLOntology ontology;
    private final Vocabulary vocabulary;

    public ComplexSidesNormalizer(OWLOntology ontology, Vocabulary vocabulary) {
        this.ontology = ontology;
        this.vocabulary = vocabulary;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression ce1 = axiom.getSubClass();
        final OWLClassExpression ce2 = axiom.getSuperClass();

        if (ce1.isAnonymous() && DLUtils.hasExistential(ce2)) {
            final OWLClass anew = vocabulary.generateNewConcept();

            newAxioms.add(DLUtils.subsumption(ontology, ce1, anew));
            newAxioms.add(DLUtils.subsumption(ontology, anew, ce2));

            return true;
        }

        return false;
    }

}

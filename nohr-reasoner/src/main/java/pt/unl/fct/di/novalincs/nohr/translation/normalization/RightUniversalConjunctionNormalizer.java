package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class RightUniversalConjunctionNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OWLOntology ontology;

    public RightUniversalConjunctionNormalizer(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression c = axiom.getSubClass();
        OWLClassExpression d = axiom.getSuperClass();

        List<OWLObjectPropertyExpression> propertyExpressions = new LinkedList<>();

        while (d instanceof OWLObjectAllValuesFrom) {
            final OWLObjectAllValuesFrom allValuesFrom = (OWLObjectAllValuesFrom) d;
            final OWLClassExpression filler = allValuesFrom.getFiller();

            propertyExpressions.add(0, allValuesFrom.getProperty());

            if (filler instanceof OWLObjectIntersectionOf) {
                final Set<OWLClassExpression> conjuctSet = filler.asConjunctSet();

                for (OWLClassExpression newAxiom : conjuctSet) {
                    for (OWLObjectPropertyExpression i : propertyExpressions) {
                        newAxiom = DLUtils.only(ontology, i, newAxiom);
                    }

                    newAxioms.add(DLUtils.subsumption(ontology, c, newAxiom));
                }

                return true;
            } else if (filler instanceof OWLObjectComplementOf) {
                OWLClassExpression newAxiom = ((OWLObjectComplementOf) filler).getOperand();

                for (OWLObjectPropertyExpression i : propertyExpressions) {
                    newAxiom = DLUtils.some(ontology, i, newAxiom);
                }

                newAxioms.add(DLUtils.subsumption(ontology, c, DLUtils.not(ontology, newAxiom)));

                return true;
            }

            d = filler;
        }

        return false;
    }
}

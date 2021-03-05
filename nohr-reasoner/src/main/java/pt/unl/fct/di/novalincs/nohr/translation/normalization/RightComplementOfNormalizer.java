package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;

public class RightComplementOfNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OntologyUtil util;

    public RightComplementOfNormalizer(OntologyUtil util) {
        this.util = util;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression subClass = axiom.getSubClass();
        final OWLClassExpression superClass = axiom.getSuperClass();
        boolean changed = false;

        if (superClass instanceof OWLObjectComplementOf) {
            final OWLObjectComplementOf complementOf = (OWLObjectComplementOf) superClass;
            final OWLClassExpression operand = complementOf.getOperand();

            changed = true;

            newAxioms.add(util.subClassOf(util.intersectionOf(subClass, operand), util.nothing()));
        }

        return changed;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.owlgraph.OWLSubClassOfNode;

public class GraphNormalizer implements Normalizer<OWLSubClassOfAxiom> {

    private final OWLOntology ontology;

    public GraphNormalizer(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        for (OWLSubClassOfAxiom j : new OWLSubClassOfNode(axiom).asSubClassOfAxiom()) {
            if (j.getSuperClass() instanceof OWLObjectComplementOf) {
                final OWLClassExpression c = j.getSubClass();
                final OWLClassExpression d = ((OWLObjectComplementOf) j.getSuperClass()).getOperand();

                final OWLObjectIntersectionOf objectIntersectionOf = DLUtils.conjunction(ontology, c, d);

                newAxioms.add(DLUtils.subsumption(ontology, objectIntersectionOf, DLUtils.bottom(ontology)));
            } else {
                newAxioms.add(j);
            }
        }

        return true;
    }

}

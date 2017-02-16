package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

public class RightComplementNormalizer implements Normalizer<OWLSubClassOfAxiom> {
    
    private final OWLOntology ontology;
    
    public RightComplementNormalizer(OWLOntology ontology) {
        this.ontology = ontology;
    }
    
    @Override
    public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
        final OWLClassExpression c = axiom.getSubClass();
        final OWLClassExpression d = axiom.getSuperClass();
        
        if (d instanceof OWLObjectComplementOf) {
            final OWLClassExpression operand = ((OWLObjectComplementOf) d).getOperand();
            
            newAxioms.add(DLUtils.subsumption(ontology, DLUtils.conjunction(ontology, operand, c), DLUtils.bottom(ontology)));
            
            return true;
        }
        
        return false;
    }
    
}

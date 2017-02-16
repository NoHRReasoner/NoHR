package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import static pt.unl.fct.di.novalincs.owlgraph.OWLNode.ontologyManager;

public class OWLObjectComplementOfNode extends OWLClassExpressionNode {

    public OWLObjectComplementOfNode(OWLObjectComplementOf objectComplementOf) {
        children.add(OWLClassExpressionNode.create(objectComplementOf.getOperand()));
    }

    @Override
    public Set<OWLClassExpression> asClassExpression(boolean subClass) {
        Set<OWLClassExpression> classExpressions = new HashSet<>();

        for (OWLClassExpressionNode i : children) {
            for (OWLClassExpression j : i.asClassExpression(false)) {
                classExpressions.add(ontologyManager.getOWLDataFactory().getOWLObjectComplementOf(j));
            }
        }

        return classExpressions;
    }

}

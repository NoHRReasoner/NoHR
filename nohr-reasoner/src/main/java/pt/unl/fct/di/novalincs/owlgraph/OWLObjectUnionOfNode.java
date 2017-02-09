package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import static pt.unl.fct.di.novalincs.owlgraph.OWLNode.ontologyManager;

public class OWLObjectUnionOfNode extends OWLClassExpressionNode {

    public OWLObjectUnionOfNode(OWLObjectUnionOf objectUnionOf) {
        for (OWLClassExpression i : objectUnionOf.asDisjunctSet()) {
            children.add(OWLClassExpressionNode.create(i));
        }
    }

    @Override
    public Set<OWLClassExpression> asClassExpression(boolean subClass) {
        Set<OWLClassExpression> classExpressions = new HashSet<>();

        if (subClass) {
            for (OWLClassExpressionNode i : children) {
                classExpressions.addAll(i.asClassExpression(subClass));
            }
        } else {
            for (OWLClassExpressionNode i : children) {
                classExpressions.add(ontologyManager.getOWLDataFactory().getOWLObjectUnionOf(i.asClassExpression(subClass)));
            }
        }

        return classExpressions;
    }

}

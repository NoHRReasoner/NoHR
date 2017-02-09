package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

public class OWLObjectIntersectionOfNode extends OWLClassExpressionNode {

    public OWLObjectIntersectionOfNode(OWLObjectIntersectionOf owlObjectIntersectionOf) {
        for (OWLClassExpression i : owlObjectIntersectionOf.asConjunctSet()) {
            children.add(OWLClassExpressionNode.create(i));
        }
    }

    @Override
    public Set<OWLClassExpression> asClassExpression(boolean subClass) {
        Set<OWLClassExpression> classExpressions = new HashSet<>();

        if (subClass) {
            Set<OWLClassExpression> intersection = new HashSet<>();

            for (OWLClassExpressionNode i : children) {
                intersection.addAll(i.asClassExpression(subClass));
            }

            classExpressions.add(ontologyManager.getOWLDataFactory().getOWLObjectIntersectionOf(intersection));
        } else {
            for (OWLClassExpressionNode i : children) {
                classExpressions.addAll(i.asClassExpression(subClass));
            }
        }

        return classExpressions;
    }

}

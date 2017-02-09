package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class OWLObjectAllValuesFromNode extends OWLClassExpressionNode {

    private final OWLObjectPropertyExpression propertyExpression;

    public OWLObjectAllValuesFromNode(OWLObjectAllValuesFrom owlce) {
        propertyExpression = owlce.getProperty();

        children.add(OWLClassExpressionNode.create(owlce.getFiller()));
    }

    @Override
    public Set<OWLClassExpression> asClassExpression(boolean subClass) {
        Set<OWLClassExpression> classExpressions = new HashSet<>();

        for (OWLClassExpressionNode i : children) {
            for (OWLClassExpression j : i.asClassExpression(subClass)) {
                classExpressions.add(ontologyManager.getOWLDataFactory().getOWLObjectAllValuesFrom(propertyExpression, j));
            }
        }

        return classExpressions;
    }
}

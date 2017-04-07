package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import static pt.unl.fct.di.novalincs.owlgraph.OWLNode.ontologyManager;

public class OWLObjectSomeValuesFromNode extends OWLClassExpressionNode {

    private final OWLObjectPropertyExpression propertyExpression;

    public OWLObjectSomeValuesFromNode(OWLObjectSomeValuesFrom owlce) {
        propertyExpression = owlce.getProperty();

        children.add(OWLClassExpressionNode.create(owlce.getFiller()));
    }

    @Override
    public Set<OWLClassExpression> asClassExpression(boolean subClass) {
        Set<OWLClassExpression> classExpressions = new HashSet<>();

        for (OWLClassExpressionNode i : children) {
            for (OWLClassExpression j : i.asClassExpression(subClass)) {
                classExpressions.add(ontologyManager.getOWLDataFactory().getOWLObjectSomeValuesFrom(propertyExpression, j));
            }
        }

        return classExpressions;
    }

}

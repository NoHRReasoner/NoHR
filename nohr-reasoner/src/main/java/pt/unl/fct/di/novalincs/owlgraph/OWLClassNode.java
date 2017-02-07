package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class OWLClassNode extends OWLClassExpressionNode {

    private final OWLClass owlc;

    public OWLClassNode(OWLClass owlc) {
        this.owlc = owlc;
    }

    @Override
    public Set<OWLClassExpression> asClassExpression(boolean subClass) {
        Set<OWLClassExpression> classExpressions = new HashSet<>();

        classExpressions.add(owlc);

        return classExpressions;
    }
}

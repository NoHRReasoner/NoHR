package pt.unl.fct.di.novalincs.owlgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

public abstract class OWLClassExpressionNode extends OWLNode {

    protected final Set<OWLClassExpressionNode> children;

    public OWLClassExpressionNode() {
        this.children = new HashSet<>();
    }

    public OWLClassExpressionNode(Collection<OWLClassExpressionNode> children) {
        this.children = new HashSet<>(children);
    }

    public abstract Set<OWLClassExpression> asClassExpression(boolean subClass);

    public static OWLClassExpressionNode create(OWLClassExpression owlce) {
        if (owlce instanceof OWLClass) {
            return new OWLClassNode((OWLClass) owlce);
        } else if (owlce instanceof OWLObjectAllValuesFrom) {
            return new OWLObjectAllValuesFromNode((OWLObjectAllValuesFrom) owlce);
        } else if (owlce instanceof OWLObjectIntersectionOf) {
            return new OWLObjectIntersectionOfNode((OWLObjectIntersectionOf) owlce);
        } else if (owlce instanceof OWLObjectSomeValuesFrom) {
            return new OWLObjectSomeValuesFromNode((OWLObjectSomeValuesFrom) owlce);
        } else if (owlce instanceof OWLObjectUnionOf) {
            return new OWLObjectUnionOfNode((OWLObjectUnionOf) owlce);
        } else if (owlce instanceof OWLObjectComplementOf) {
            return new OWLObjectComplementOfNode((OWLObjectComplementOf) owlce);
        }

        throw new IllegalArgumentException("Usupported class expression " + owlce.toString());
    }

    public Set<OWLClassExpressionNode> getChildren() {
        return children;
    }

}

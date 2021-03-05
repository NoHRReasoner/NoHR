package pt.unl.fct.di.novalincs.nohr.translation;

import org.semanticweb.owlapi.model.OWLClassExpression;

public class UnsupportedClassExpression extends Exception {

    private final OWLClassExpression classExpression;

    public UnsupportedClassExpression(OWLClassExpression classExpression) {
        super("Unsupported class expression: " + classExpression.toString());
        this.classExpression = classExpression;
    }

    public OWLClassExpression getClassExpression() {
        return classExpression;
    }

}

package pt.unl.fct.di.novalincs.owlgraph;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class OWLSubClassOfNode extends OWLAxiomNode {

    private OWLClassExpressionNode subClass;
    private OWLClassExpressionNode superClass;

    public OWLSubClassOfNode(OWLClassExpressionNode subClass, OWLClassExpressionNode superClass) {
        this.subClass = subClass;
        this.superClass = superClass;
    }

    public OWLSubClassOfNode(OWLSubClassOfAxiom axiom) {
        this.subClass = OWLClassExpressionNode.create(axiom.getSubClass());
        this.superClass = OWLClassExpressionNode.create(axiom.getSuperClass());
    }

    public Iterable<OWLAxiom> asAxioms() {
        Iterable<OWLAxiom> axioms = new HashSet<>();

        for (OWLClassExpression i : subClass.asClassExpression(true)) {
            for (OWLClassExpression j : superClass.asClassExpression(false)) {
                ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(i, j);
            }
        }

        return axioms;
    }

    public static Iterable<OWLSubClassOfAxiom> flattenAxiom(OWLSubClassOfAxiom axiom) {
        OWLClassExpressionNode subClass = OWLClassExpressionNode.create(axiom.getSubClass());
        OWLClassExpressionNode superClass = OWLClassExpressionNode.create(axiom.getSuperClass());

        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();

        for (OWLClassExpression i : subClass.asClassExpression(true)) {
            for (OWLClassExpression j : superClass.asClassExpression(false)) {
                axioms.add(ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(i, j));
            }
        }

        return axioms;
    }

    public OWLClassExpressionNode getSubClass() {
        return subClass;
    }

    public OWLClassExpressionNode getSuperClass() {
        return superClass;
    }

    public void setSubClass(OWLClassExpressionNode subClass) {
        this.subClass = subClass;
    }

    public void setSuperClass(OWLClassExpressionNode superClass) {
        this.superClass = superClass;
    }

}

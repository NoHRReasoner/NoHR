package pt.unl.fct.di.novalincs.nohr.translation;

import org.semanticweb.owlapi.model.OWLAxiom;

public class UnsupportedAxiomException extends Exception {

    private final OWLAxiom axiom;

    public UnsupportedAxiomException(OWLAxiom axiom) {
        super("Unsupported axiom: " + axiom.toString());
        this.axiom = axiom;
    }

    public OWLAxiom getAxiom() {
        return axiom;
    }
}

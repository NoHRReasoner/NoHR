package pt.unl.fct.di.novalincs.nohr.translation.dl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.nohr.translation.InferenceEngine;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public final class KoncludeInferenceEngine implements InferenceEngine {

    private final String koncludePath;

    public KoncludeInferenceEngine(String koncludePath) {
        this.koncludePath = koncludePath;
    }

    @Override
    public OWLOntology computeInferences(OWLOntology ontology) {
        RuntimesLogger.start("[NoHR DL (Konclude)] ontology inference");
        Logger.getLogger("de.derivo.konclude").setLevel(Level.ERROR);

        try {
            KoncludeReasonerWrapper konclude = new KoncludeReasonerWrapper(koncludePath, ontology);

            if (!konclude.consistency()) {
                throw new InconsistentOntologyException();
            }

            OWLOntologyManager manager = ontology.getOWLOntologyManager();

            for (OWLAxiom axiom : konclude.classification().getAxioms(AxiomType.SUBCLASS_OF)) {
                manager.addAxiom(ontology, axiom);
            }

            for (OWLAxiom axiom : konclude.realization().getAxioms(AxiomType.CLASS_ASSERTION)) {
                manager.addAxiom(ontology, axiom);
            }

            for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(DLUtils.top(ontology))) {
                if (axiom.getSuperClass() instanceof OWLObjectComplementOf) {
                    final OWLObjectComplementOf a = (OWLObjectComplementOf) axiom.getSuperClass();

                    manager.addAxiom(ontology, DLUtils.subsumption(ontology, a.getOperand(), DLUtils.bottom(ontology)));
                }
            }
        } catch (OWLOntologyStorageException ex) {
            throw new RuntimeException();
        } finally {
            RuntimesLogger.stop("[NoHR DL (Konclude)] ontology inference", "loading");
        }

        return ontology;
    }
}

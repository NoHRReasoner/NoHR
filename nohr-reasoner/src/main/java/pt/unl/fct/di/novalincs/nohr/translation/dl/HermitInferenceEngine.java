package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import pt.unl.fct.di.novalincs.nohr.translation.InferenceEngine;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public final class HermitInferenceEngine implements InferenceEngine {

    public HermitInferenceEngine() {
    }

    @Override
    public OWLOntology computeInferences(OWLOntology ontology) {
        RuntimesLogger.start("[NoHR DL (HermiT)] ontology inference");
        Logger.getLogger("org.semanticweb.hermit").setLevel(Level.ERROR);

        final OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        final OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS, InferenceType.CLASS_HIERARCHY);

        final List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<>(2);

        generators.add(new InferredClassAssertionAxiomGenerator());
        generators.add(new InferredSubClassAxiomGenerator());

        final InferredOntologyGenerator inferredOntologyGenerator = new InferredOntologyGenerator(reasoner, generators);

        inferredOntologyGenerator.fillOntology(ontology.getOWLOntologyManager().getOWLDataFactory(), ontology);

        reasoner.dispose();

        RuntimesLogger.stop("[NoHR DL (HermiT)] ontology inference", "loading");

        return ontology;
    }
}

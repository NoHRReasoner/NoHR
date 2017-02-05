package pt.unl.fct.di.novalincs.nohr.plugin;

import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import static pt.unl.fct.di.novalincs.nohr.plugin.AbstractNoHRViewComponent.LOG;

class NoHRInstance {

    private static NoHRInstance instance;

    private DisposableHybridKB hybridKB;

    private NoHRInstance() {
    }

    public DisposableHybridKB getHybridKB() {
        return hybridKB;
    }

    public static NoHRInstance getInstance() {
        if (instance == null) {
            instance = new NoHRInstance();
        }

        return instance;
    }

    public boolean isStarted() {
        return hybridKB != null;
    }

    public void restart() throws UnsupportedAxiomsException, OWLProfilesViolationsException, PrologEngineCreationException {
        if (!isStarted()) {
            return;
        }

        LOG.info("Restarting NoHR");

        final OWLOntology ontology = hybridKB.getOntology();
        final Program program = hybridKB.getProgram();
        final Vocabulary vocabulary = hybridKB.getVocabulary();

        start(NoHRPreferences.getInstance().getConfiguration(), ontology, program, vocabulary);
    }

    public void start(NoHRHybridKBConfiguration configuration, OWLOntology ontology, Program program, Vocabulary vocabulary) throws UnsupportedAxiomsException, OWLProfilesViolationsException, PrologEngineCreationException {
        LOG.info("Starting NoHR");

        hybridKB = new DisposableHybridKB(configuration, ontology, program, vocabulary);

    }

    public void stop() {
        LOG.info("Stopping NoHR");

        if (isStarted()) {
            hybridKB.dispose();
        }
    }

}

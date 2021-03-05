package pt.unl.fct.di.novalincs.nohr.plugin;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import static pt.unl.fct.di.novalincs.nohr.plugin.AbstractNoHRViewComponent.LOG;

class NoHRInstance {

    private static NoHRInstance instance;

    private DisposableHybridKB hybridKB;
    private Set<NoHRInstanceChangedListener> listeners;

    private NoHRInstance() {
        listeners = new HashSet<>();
    }

    public void addListener(NoHRInstanceChangedListener listener) {
        listeners.add(listener);
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

    public void requestRestart() {
        for (NoHRInstanceChangedListener i : listeners) {
            i.instanceChanged(new NoHRInstanceChangedEventImpl(NoHRInstanceChangedEventType.REQUEST_RESTART));
        }
    }

    public void removeListener(NoHRInstanceChangedListener listener) {
        listeners.remove(listener);
    }

    public void restart() throws UnsupportedAxiomsException, OWLProfilesViolationsException, PrologEngineCreationException {
        if (!isStarted()) {
            return;
        }

        LOG.info("Restarting NoHR");

        final OWLOntology ontology = hybridKB.getOntology();
        final Program program = hybridKB.getProgram();
        final DBMappingSet dbMappingsSet = hybridKB.getDBMappings();
        final Vocabulary vocabulary = hybridKB.getVocabulary();

        stop();
        start(NoHRPreferences.getInstance().getConfiguration(), ontology, program, dbMappingsSet, vocabulary);
    }

    public void start(NoHRHybridKBConfiguration configuration, OWLOntology ontology, Program program,DBMappingSet dbMappingsSet, Vocabulary vocabulary) throws UnsupportedAxiomsException, OWLProfilesViolationsException, PrologEngineCreationException {
        LOG.info("Starting NoHR");

        hybridKB = new DisposableHybridKB(configuration, ontology, program, dbMappingsSet,  vocabulary);
    }

    public void stop() {
        LOG.info("Stopping NoHR");

        if (isStarted()) {
            hybridKB.dispose();
            hybridKB = null;
        }
    }

}

package pt.unl.fct.di.novalincs.nohr.benchmark.lightweight;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;

public class BenchmarkLoading extends Benchmark {

    private NoHRHybridKBConfiguration configuration;
    private OWLOntology ontology;
    private Program program;
    private DBMappingSet mappings;
    private Vocabulary vocabulary;

    public BenchmarkLoading(String[] args) throws IOException, OWLOntologyCreationException, ParseException {
        super(args, "loading");
    }

    @Override
    public void prepare() {
        configuration = this.getNoHRConfig();
        ontology = this.getResources().getOntology();
        program = this.getResources().getProgram();
        mappings = this.getResources().getDBMappings();
        vocabulary = this.getResources().getVocabulary();

        getMetrics().add("load");
    }

    @Override
    public void singleRun() {
        try {
            final long time = System.nanoTime();
            final NoHRHybridKB kb = new NoHRHybridKB(configuration, ontology, program, mappings, vocabulary, null);
            final long endtime = System.nanoTime();

            getMetrics().add("load", endtime - time);
            kb.dispose();
        } catch (UnsupportedAxiomsException | PrologEngineCreationException ex) {
            Logger.getLogger(BenchmarkLoading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose() {
    }

    public static void main(String[] args) {
        try {
            (new BenchmarkLoading(args)).run();
        } catch (Exception ex) {
            Logger.getLogger(BenchmarkLoading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

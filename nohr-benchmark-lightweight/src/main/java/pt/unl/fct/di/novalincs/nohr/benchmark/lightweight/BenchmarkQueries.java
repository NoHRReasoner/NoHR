package pt.unl.fct.di.novalincs.nohr.benchmark.lightweight;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;

public class BenchmarkQueries extends Benchmark {

    private NoHRHybridKBConfiguration configuration;
    private OWLOntology ontology;
    private Program program;
    private DBMappingSet mappings;
    private Vocabulary vocabulary;

    private NoHRHybridKB kb;
    private NoHRParser parser;

    public BenchmarkQueries(String[] args) throws IOException, OWLOntologyCreationException, ParseException {
        super(args, "queries");
    }

    @Override
    public void prepare() throws Exception {
        configuration = this.getNoHRConfig();
        ontology = this.getResources().getOntology();
        program = this.getResources().getProgram();
        mappings = this.getResources().getDBMappings();
        vocabulary = this.getResources().getVocabulary();

        for (EvaluationQuery i : this.getResources().getQueries()) {
            getMetrics().add(i.getName());
        }
        kb = new NoHRHybridKB(configuration, ontology, program, mappings, vocabulary, null);
        parser = new NoHRRecursiveDescentParser(vocabulary);
    }

    @Override
    public void singleRun() throws Exception {

        for (EvaluationQuery i : this.getResources().getQueries()) {
            final List<Answer> answers;
            final pt.unl.fct.di.novalincs.nohr.model.Query query = parser.parseQuery(i.getQuery());

            final long time = System.nanoTime();
            answers = kb.allAnswers(query);
            final long endtime = System.nanoTime();

            getMetrics().add(i.getName(), endtime - time);
        }
    }

    @Override
    public void dispose() throws Exception {
        kb.dispose();
    }

    public static void main(String[] args) {
        try {
            (new BenchmarkQueries(args)).run();
        } catch (Exception ex) {
            Logger.getLogger(BenchmarkQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

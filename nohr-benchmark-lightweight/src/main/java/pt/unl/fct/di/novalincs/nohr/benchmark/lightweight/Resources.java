package pt.unl.fct.di.novalincs.nohr.benchmark.lightweight;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;

public class Resources {

    private OWLOntology ontology;
    private Program program;
    private DBMappingSet mappings;
    private List<EvaluationQuery> queries;
    private Vocabulary vocabulary;

    public OWLOntology getOntology() {
        return ontology;
    }

    public Program getProgram() {
        return program;
    }
    
    public DBMappingSet getDBMappings() {
        return mappings;
    }

    public List<EvaluationQuery> getQueries() {
        return queries;
    }
    
    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public Resources() {
        queries = new LinkedList<>();
    }

    public void loadAll(File... dirs) throws IOException, OWLOntologyCreationException, ParseException {
        List<File> d = new ArrayList<>(dirs.length);

        d.addAll(Arrays.asList(dirs));

        try {
            System.out.println("Loading ontologies...");
            loadOntology(d, "*.owl");
            System.out.println("Loading ontologies...done.");
            System.out.println("Loading programs...");
            loadProgram(d, "*.nohr");
            System.out.println("Loading programs...done.");
            System.out.println("Loading database mappings...");
            loadDBMappings(d, "*.map");
            System.out.println("Loading database mappings...done.");
            System.out.println("Loading queries...");
            loadQuery(d, "*.q");
            System.out.println("Loading queries...done.");
        } catch (IOException | OWLOntologyCreationException | ParseException ex) {
            System.out.println("Failure loading benchmark resources!");
            throw ex;
        }
    }

    public OWLOntology loadOntology(List<File> dirs, String filter) throws IOException, OWLOntologyCreationException {
        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        for (File i : dirs) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(i.toPath(), filter)) {
                for (Path j : stream) {
                    manager.loadOntologyFromOntologyDocument(j.toFile());
                }
            } catch (IOException | OWLOntologyCreationException ex) {
                throw ex;
            }
        }

        OWLOntologyMerger merger = new OWLOntologyMerger(manager);
        ontology = merger.createMergedOntology(manager, IRI.generateDocumentIRI());
        vocabulary = new DefaultVocabulary(ontology);

        return ontology;
    }

    public Program loadProgram(List<File> dir, String filter) throws IOException, ParseException {
        NoHRParser parser = new NoHRRecursiveDescentParser(vocabulary);

        program = Model.program();

        for (File i : dir) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(i.toPath(), filter)) {
                for (Path j : stream) {
                    parser.parseProgram(j.toFile(), program);
                }
            } catch (IOException | ParseException ex) {
                throw ex;
            }
        }

        return program;
    }
    
    public Program loadDBMappings(List<File> dir, String filter) throws IOException, ParseException {
        NoHRParser parser = new NoHRRecursiveDescentParser(vocabulary);

        mappings = Model.dbMappingSet();

        for (File i : dir) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(i.toPath(), filter)) {
                for (Path j : stream) {
                    parser.parseDBMappingSet(j.toFile(), mappings);
                }
            } catch (IOException | ParseException ex) {
                throw ex;
            }
        }

        return program;
    }

    public List<EvaluationQuery> loadQuery(List<File> dir, String filter) throws IOException {
        queries = new LinkedList<>();

        for (File i : dir) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(i.toPath(), filter)) {
                for (Path j : stream) {
                    for (String k : Files.readAllLines(j)) {
                        queries.add(new EvaluationQuery(k));
                    }
                }
            } catch (IOException ex) {
                throw ex;
            }
        }

        return queries;
    }
}

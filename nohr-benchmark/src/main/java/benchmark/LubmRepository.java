package benchmark;

/*
 * #%L
 * nohr-benchmark
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import com.declarativa.interprolog.util.IPException;

import benchmark.ubt.api.QueryResult;
import benchmark.ubt.api.QuerySpecification;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class LubmRepository {

    private final Path data;

    private String lastQuery;

    private pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB hybridKB;

    private final File resultsDirectory;

    Integer universities = 0;

    private final Profile profiles;

    private NoHRParser parser;
    private final boolean forceDL;

    public LubmRepository(Path data, File resultsDirectory, Profile profile, boolean forceDL) {
        this.resultsDirectory = resultsDirectory;
        this.data = data;
        profiles = profile;
        this.forceDL = forceDL;
    }

    public void clear() {
        hybridKB = null;
    }

    public void close() {
        hybridKB = null;
        lastQuery = null;
    }

    public QueryResult issueQuery(QuerySpecification querySpecification) throws IOException, Exception {
        final String queryId = querySpecification.id_;
        RuntimesLogger.setIteration("query", queryId);
        final String queryStr = querySpecification.query_.getString();
        final boolean sameQuery = queryStr.equals(lastQuery);
        final Collection<Answer> result = hybridKB.allAnswers(parser.parseQuery(queryStr));
        RuntimesLogger.info(String.valueOf(result.size()) + " answers");
        if (!sameQuery && resultsDirectory != null) {
            logResults(querySpecification, result);
        }
        lastQuery = queryStr;
        if (!sameQuery) {
        }
        return new NoHRQueryResult(result);
    }

    public boolean load(Integer universities) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException, ParseException {
        this.universities = universities;
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        RuntimesLogger.start("ontology loading");
        final OWLOntology ontology;
        if (universities == null) {
            ontology = ontologyManager.loadOntologyFromOntologyDocument(data.toFile());
        } else {
            loadDirectory(universities, ontologyManager);
            final OWLOntologyMerger merger = new OWLOntologyMerger(ontologyManager);
            ontology = merger.createMergedOntology(ontologyManager, IRI.generateDocumentIRI());
        }
        RuntimesLogger.stop("ontology loading", "loading");
        ontologyManager = null;

        NoHRHybridKBConfiguration configuration = new NoHRHybridKBConfiguration();

        configuration.getOntologyTranslationConfiguration().setDLInferenceEngineEL(forceDL);
        configuration.getOntologyTranslationConfiguration().setDLInferenceEngineQL(forceDL);

        Vocabulary vocabulary = new DefaultVocabulary(ontology);

        parser = new NoHRRecursiveDescentParser(vocabulary);

        Program program;

        if (profiles == Profile.OWL2_QL) {
            program = parser.parseProgram(new File(System.getenv("NOHR_RULES")));
        } else {
            program = Model.program();
        }

        hybridKB = new NoHRHybridKB(configuration, ontology, program, vocabulary, profiles);

        System.gc();
        return true;
    }

    private void loadDirectory(int universities, OWLOntologyManager ontologyManager)
            throws OWLOntologyCreationException, IOException {
        for (int u = 0; u < universities; u++) {
            final DirectoryStream<Path> stream = Files.newDirectoryStream(data, "University" + u + "_*.owl");
            for (final Path entry : stream) {
                RuntimesLogger.info("loading " + entry.getFileName().toString());
                if (entry.getFileName().toString().endsWith(".owl")) {
                    final File file = entry.toFile();
                    ontologyManager.loadOntologyFromOntologyDocument(file);
                }
            }
            stream.close();
        }

    }

    private void logResults(QuerySpecification querySpecification, Collection<Answer> result2) {
        if (resultsDirectory == null) {
            return;
        }
        final Charset charset = Charset.defaultCharset();
        final String fileName = universities + "." + querySpecification.id_ + ".txt";
        final Path path = FileSystems.getDefault().getPath(resultsDirectory.getAbsolutePath(), fileName);
        try {
            final BufferedWriter writer = Files.newBufferedWriter(path, charset);
            if (result2.size() >= 1) {
                for (final Answer result : result2) {
                    for (final Term val : result.getValues()) {
                        final String valStr = val.toString();
                        writer.write(valStr, 0, valStr.length());
                        writer.write(9);
                    }
                    writer.newLine();
                }
            }
            writer.close();
        } catch (final IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public void setOntology(String ontology) {
        final Path path = FileSystems.getDefault().getPath(ontology).toAbsolutePath();
        new File(path.toString());
    }

}

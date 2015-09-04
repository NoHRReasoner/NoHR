
package benchmark;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;
import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import benchmark.ubt.api.QueryConfigParser;
import benchmark.ubt.api.QuerySpecification;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ProgramPresistenceManager;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class RulesTest {

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		if (args.length != 4) {
			System.out.println("expected arguments: <ontology> <programs directory> <queries file> <runs>");
			System.exit(1);
		}

		final QueryConfigParser queriesParser = new QueryConfigParser();
		final Path queriesFile = FileSystems.getDefault().getPath(args[2]).toAbsolutePath();
		Vector<?> queries = null;
		try {
			queries = queriesParser.createQueryList(queriesFile.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		final int runs = Integer.valueOf(args[3]);
		RuntimesLogger.open("loading", "queries");
		for (int run = 1; run <= runs; run++) {
			RuntimesLogger.setRun(run);
			final OWLOntologyManager om = OWLManager.createOWLOntologyManager();
			final Path ontologyFile = FileSystems.getDefault().getPath(args[0]).toAbsolutePath();
			final Path progsDir = FileSystems.getDefault().getPath(args[1]).toAbsolutePath();

			final String name = ontologyFile.getFileName().toString().replaceFirst(".owl", "");
			RuntimesLogger.setDataset(name);
			RuntimesLogger.start("ontology loading");
			OWLOntology ontology = null;
			try {
				ontology = om.loadOntologyFromOntologyDocument(ontologyFile.toFile());
			} catch (final OWLOntologyCreationException e) {
				e.printStackTrace();
			}
			RuntimesLogger.stop("ontology loading", "loading");
			final HybridKB nohr = new NoHRHybridKB(new File(System.getenv("XSB_BIN_DIRECTORY")), ontology,
					Model.program());
			final NoHRParser parser = new NoHRRecursiveDescentParser(nohr.getVocabulary());
			final ProgramPresistenceManager programPresistenceManager = new ProgramPresistenceManager(
					nohr.getVocabulary());
			final Iterator<?> queriesIt1 = queries.iterator();
			while (queriesIt1.hasNext()) {
				final QuerySpecification querySpecification = (QuerySpecification) queriesIt1.next();
				RuntimesLogger.setIteration("query", querySpecification.id_);
				try {
					final Collection<Answer> ans = nohr
							.allAnswers(parser.parseQuery(querySpecification.query_.getString()));
					RuntimesLogger.info(ans.size() + " answers");
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			final List<Path> files = new ArrayList<>();
			try (DirectoryStream<Path> progsStream = Files.newDirectoryStream(progsDir)) {
				for (final Path p : progsStream)
					files.add(p);
				Collections.sort(files, new Comparator<Path>() {
					@Override
					public int compare(Path o1, Path o2) {
						try {
							return Integer.valueOf(o1.getFileName().toString().replaceFirst(".p", ""))
									.compareTo(Integer.valueOf(o2.getFileName().toString().replaceFirst(".p", "")));
						} catch (final NumberFormatException e) {
							System.err.println("program names must be numbers");
							System.exit(1);
							return -2;
						}
					}
				});
				for (final Path progFile : files) {
					RuntimesLogger.setDataset(name + "+" + progFile.getFileName().toString().replaceFirst(".p", ""));
					try {
						RuntimesLogger.start("rules parsing");
						final Program program = programPresistenceManager.read(progFile.toFile());
						RuntimesLogger.stop("rules parsing", "loading");
						nohr.getProgram().addAll(program);
					} catch (final PrologParserException e) {
						System.err.println(
								"syntax error at line " + e.getLineNumber() + "column " + e.getStringPosition());
					}
					queries.iterator();
					for (int i = 0; i < queries.size(); i++) {
						final QuerySpecification querySpecification = (QuerySpecification) queries.get(i);
						RuntimesLogger.setIteration("query", querySpecification.id_);
						final Collection<Answer> ans = nohr
								.allAnswers(parser.parseQuery(querySpecification.query_.getString()));
						RuntimesLogger.info(ans.size() + " answers");
					}
					nohr.getProgram().clear();
				}
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
		}
		RuntimesLogger.close();
		System.out.println("Consult loading times at loading.csv");
		System.out.println("Consult query times at queries.csv");
		System.exit(0);

	}

}

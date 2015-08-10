import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.parsing.StandarPrologParser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.OntologyIndexImpl;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.Profiles;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabaseCreationException;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;
import ubt.api.QueryConfigParser;
import ubt.api.QuerySpecification;

import com.declarativa.interprolog.util.IPException;
import com.igormaznitsa.prologparser.exceptions.PrologParserException;

public class RulesTest {

    private static void loadRules(HybridKB nohr, StandarPrologParser parser, Path path)
	    throws IOException, PrologParserException {
	File file = path.toFile();
	if (file.exists()) {
	    final FileInputStream fstream = new FileInputStream(file);
	    final DataInputStream in = new DataInputStream(fstream);
	    final BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    // Read File Line By Line
	    int l = 0;
	    while ((strLine = br.readLine()) != null)
		if (strLine.length() > 0) {
		    final Rule rule = parser.parseRule(strLine);
		    nohr.getRuleBase().add(rule);
		    l++;
		}
	    in.close();
	    RuntimesLogger.info("additional rule file: " + file.getName());
	    RuntimesLogger.info(l + " rules added");
	}
	// ontology.setResultFileName(file.getName());
	file = null;
    }

    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException,
	    OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
	    IPException, XSBDatabaseCreationException {
	if (args.length != 3) {
	    System.out.println("expected arguments: <ontology> <programs directory> <queries file>");
	    System.exit(1);
	}

	AbstractOntologyTranslation.profile = Profiles.OWL2_QL;

	final QueryConfigParser queriesParser = new QueryConfigParser();
	final Path queriesFile = FileSystems.getDefault().getPath(args[2]).toAbsolutePath();
	Vector<?> queries = null;
	try {
	    queries = queriesParser.createQueryList(queriesFile.toString());
	} catch (final Exception e) {
	    e.printStackTrace();
	}

	RuntimesLogger.open("loading", "queries");
	for (int run = 1; run <= 5; run++) {
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
	    final HybridKB nohr = new HybridKB(new File(System.getProperty("XSB_BIN_DIRECTORY")), ontology.getAxioms());
	    final StandarPrologParser parser = new StandarPrologParser(new OntologyIndexImpl(ontology));
	    final Iterator<?> queriesIt1 = queries.iterator();
	    while (queriesIt1.hasNext()) {
		final QuerySpecification querySpecification = (QuerySpecification) queriesIt1.next();
		RuntimesLogger.setIteration("query", querySpecification.id_);
		try {
		    final Collection<Answer> ans = nohr
			    .queryAll(parser.parseQuery(querySpecification.query_.getString()));
		    RuntimesLogger.info(ans.size() + " answers");
		} catch (final IOException e) {
		    e.printStackTrace();
		} catch (final PrologParserException e) {
		    e.printStackTrace();
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
		    loadRules(nohr, parser, progFile);
		    queries.iterator();
		    for (int i = 0; i < queries.size(); i++) {
			final QuerySpecification querySpecification = (QuerySpecification) queries.get(i);
			RuntimesLogger.setIteration("query", querySpecification.id_);
			final Collection<Answer> ans = nohr
				.queryAll(parser.parseQuery(querySpecification.query_.getString()));
			RuntimesLogger.info(ans.size() + " answers");
		    }
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

    public RulesTest() {
	// TODO Auto-generated constructor stub
    }
}

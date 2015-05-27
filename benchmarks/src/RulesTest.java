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

import other.Config;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.plugin.Rules;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.TranslationAlgorithm;
import ubt.api.QueryConfigParser;
import ubt.api.QuerySpecification;
import utils.Tracer;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

public class RulesTest {

    private static void loadRules(Path path) throws IOException {
	Rules.resetRules();
	File file = path.toFile();
	if (file.exists()) {
	    FileInputStream fstream = new FileInputStream(file);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    // Read File Line By Line
	    int l = 0;
	    while ((strLine = br.readLine()) != null)
		if (strLine.length() > 0) {
		    Rules.addRule(strLine);
		    l++;
		}
	    in.close();
	    Tracer.info("additional rule file: " + file.getName());
	    Tracer.info(l + " rules added");
	}
	// ontology.setResultFileName(file.getName());
	file = null;
    }

    public static void main(String[] args) {
	if (args.length != 3) {
	    System.out
		    .println("expected arguments: <ontology> <programs directory> <queries file>");
	    System.exit(1);
	}

	Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;

	QueryConfigParser queriesParser = new QueryConfigParser();
	Path queriesFile = FileSystems.getDefault().getPath(args[2])
		.toAbsolutePath();
	Vector queries = null;
	try {
	    queries = queriesParser.createQueryList(queriesFile.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}

	utils.Tracer.open("loading", "queries");
	for (int run = 1; run <= 5; run++) {
	    Tracer.setRun(run);
	    OWLOntologyManager om = OWLManager.createOWLOntologyManager();
	    Path ontologyFile = FileSystems.getDefault().getPath(args[0])
		    .toAbsolutePath();
	    Path progsDir = FileSystems.getDefault().getPath(args[1])
		    .toAbsolutePath();

	    String name = ontologyFile.getFileName().toString()
		    .replaceFirst(".owl", "");
	    Tracer.setDataset(name);
	    Tracer.start("ontology loading");
	    OWLOntology ontology = null;
	    try {
		ontology = om.loadOntologyFromOntologyDocument(ontologyFile
			.toFile());
	    } catch (OWLOntologyCreationException e) {
		e.printStackTrace();
	    }
	    Tracer.stop("ontology loading", "loading");
	    HybridKB nohr = new HybridKB(ontology);
	    Rules.resetRules();
	    Iterator queriesIt1 = queries.iterator();
	    nohr.resetQueryCount();
	    Parser parser = new Parser();
	    while (queriesIt1.hasNext()) {
		QuerySpecification querySpecification = (QuerySpecification) queriesIt1
			.next();
		try {
		    Collection<Answer> ans = nohr.queryAll(parser
			    .parseQuery(querySpecification.query_.getString()));
		    Tracer.info(ans.size() + " answers");
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (PrologParserException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    List<Path> files = new ArrayList<>();
	    try (DirectoryStream<Path> progsStream = Files
		    .newDirectoryStream(progsDir)) {
		for (Path p : progsStream)
		    files.add(p);
		Collections.sort(files, new Comparator<Path>() {
		    @Override
		    public int compare(Path o1, Path o2) {
			try {
			    return Integer.valueOf(
				    o1.getFileName().toString()
					    .replaceFirst(".p", "")).compareTo(
				    Integer.valueOf(o2.getFileName().toString()
					    .replaceFirst(".p", "")));
			} catch (NumberFormatException e) {
			    System.err.println("program names must be numbers");
			    System.exit(1);
			    return -2;
			}
		    }
		});
		for (Path progFile : files) {
		    Tracer.setDataset(name
			    + "+"
			    + progFile.getFileName().toString()
				    .replaceFirst(".p", ""));
		    loadRules(progFile);
		    queries.iterator();
		    nohr.resetQueryCount();
		    for (int i = 0; i < queries.size(); i++) {
			QuerySpecification querySpecification = (QuerySpecification) queries
				.get(i);

			Collection<Answer> ans = nohr.queryAll(parser
				.parseQuery(querySpecification.query_
					.getString()));
			Tracer.info(ans.size() + " answers");
		    }
		}
	    } catch (Exception e1) {
		e1.printStackTrace();
	    }
	}
	utils.Tracer.close();
	System.out.println("Consult loading times at loading.csv");
	System.out.println("Consult query times at queries.csv");
	System.exit(0);
    }

    public RulesTest() {
	// TODO Auto-generated constructor stub
    }
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.parsing.XSBParser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.OntologyIndexImpl;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.Profiles;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabaseCreationException;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;
import ubt.api.QueryResult;
import ubt.api.QuerySpecification;

import com.declarativa.interprolog.util.IPException;
import com.igormaznitsa.prologparser.exceptions.PrologParserException;

public class LubmRepository {

    private Path data;

    private String lastQuery;

    private pt.unl.fct.di.centria.nohr.reasoner.HybridKB nohrQuery;

    private File resultsDirectory;

    int universities = 0;

    private XSBParser parser;

    public LubmRepository() {
    }

    public LubmRepository(Path data, File resultsDirectory) {
	this.resultsDirectory = resultsDirectory;
	this.data = data;
    }

    public void clear() {
	nohrQuery = null;
    }

    public void close() {
	nohrQuery = null;
	lastQuery = null;
    }

    public QueryResult issueQuery(QuerySpecification querySpecification)
	    throws IOException, PrologParserException, Exception {
	final String queryId = querySpecification.id_;
	RuntimesLogger.setIteration("query", queryId);
	String queryStr = querySpecification.query_.getString();
	if (!queryStr.endsWith("."))
	    queryStr = queryStr + ".";
	final boolean sameQuery = queryStr.equals(lastQuery);
	final Collection<Answer> result = nohrQuery.queryAll(parser.parseQuery(queryStr));
	RuntimesLogger.info(String.valueOf(result.size()) + " answers");
	if (!sameQuery && resultsDirectory != null)
	    logResults(querySpecification, result);
	lastQuery = queryStr;
	if (!sameQuery) {
	}
	return new NoHRQueryResult(result);
    }

    public boolean load(int universities) throws OWLOntologyCreationException, OWLOntologyStorageException,
	    OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
	    IPException, XSBDatabaseCreationException {
	this.universities = universities;
	OWLOntologyManager inManager = OWLManager.createOWLOntologyManager();
	OWLOntologyManager outManager = OWLManager.createOWLOntologyManager();

	RuntimesLogger.start("ontology loading");
	for (int u = 0; u < universities; u++)
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(data, "University" + u + "_*.owl")) {
		for (final Path entry : stream) {
		    RuntimesLogger.info("loading " + entry.getFileName().toString());
		    if (entry.getFileName().toString().endsWith(".owl")) {
			final File file = entry.toFile();
			inManager.loadOntologyFromOntologyDocument(file);
		    }
		}
	    } catch (final IOException x) {
		System.err.println(x);
		return false;
	    } catch (final OWLOntologyCreationException e) {
		PrintStream s;
		try {
		    s = new PrintStream("log");
		    e.printStackTrace(s);
		    s.close();
		} catch (final FileNotFoundException e1) {
		    e1.printStackTrace();
		}
		e.printStackTrace();
		return false;
	    }
	RuntimesLogger.stop("ontology loading", "loading");
	OWLOntology outOntology = outManager.createOntology(IRI.generateDocumentIRI(), inManager.getOntologies(), true);
	inManager = null;
	outManager = null;
	nohrQuery = new HybridKB(new File(System.getProperty("XSB_BIN_DIRECTORY")), outOntology.getAxioms());
	parser = new XSBParser(new OntologyIndexImpl(outOntology));
	outOntology = null;
	System.gc();
	return true;

    }

    private void logResults(QuerySpecification querySpecification, Collection<Answer> result2) {
	if (resultsDirectory == null)
	    return;
	final Charset charset = Charset.defaultCharset();
	final String fileName = universities + "." + querySpecification.id_ + ".txt";
	final Path path = FileSystems.getDefault().getPath(resultsDirectory.getAbsolutePath(), fileName);
	try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
	    if (result2.size() >= 1)
		for (final Answer result : result2) {
		    for (final Term val : result.getValues()) {
			final String valStr = val.toString();
			writer.write(valStr, 0, valStr.length());
			writer.write(9);
		    }
		    writer.newLine();
		}
	} catch (final IOException x) {
	    System.err.format("IOException: %s%n", x);
	}
    }

    public void open(String database) {
	AbstractOntologyTranslation.profile = Profiles.OWL2_QL;
    }

    public void setOntology(String ontology) {
	final Path path = FileSystems.getDefault().getPath(ontology).toAbsolutePath();
	new File(path.toString());
    }

}

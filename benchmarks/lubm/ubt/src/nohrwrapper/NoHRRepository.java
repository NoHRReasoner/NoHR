package nohrwrapper;

import hybrid.query.views.Rules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import local.translate.Config;
import local.translate.TranslationAlgorithm;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import utils.Tracer;
import edu.lehigh.swat.bench.ubt.api.Query;
import edu.lehigh.swat.bench.ubt.api.QueryResult;
import edu.lehigh.swat.bench.ubt.api.Repository;

public class NoHRRepository implements Repository {

	private hybrid.query.model.Query nohrQuery;

	private String lastQuery;

	private IRI mainOntologyIRI;

	private String dataset;

	private int queryCount;

	public NoHRRepository() {
		this.queryCount = 1;
	}

	@Override
	public void open(String database) {
		Tracer.open("loading", "queries");
		Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
	}

	@Override
	public void close() {
		Tracer.close();
		nohrQuery = null;
		mainOntologyIRI = null;
		lastQuery = null;
	}

	private void loadRules(Path path) throws IOException {
		File file = path.toFile();
		if (file.exists()) {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine.length() > 0) {
					Rules.addRule(strLine);
				}
			}
			in.close();

		}
		System.out.println("Additional rule file: " + file.getName());
		// ontology.setResultFileName(file.getName());
		file = null;
	}

	@Override
	public boolean load(String dataDir) {
		this.dataset = dataDir;
		Path dir = FileSystems.getDefault().getPath(dataDir);
		Tracer.setDataset(dir.getFileName().toString());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
				"*.{owl,p}")) {
			OWLOntologyManager inManager = OWLManager
					.createOWLOntologyManager();
			OWLOntologyManager outManager = OWLManager
					.createOWLOntologyManager();
			inManager.loadOntology(mainOntologyIRI);
			Tracer.start("ontology loading");
			for (Path entry : stream) {
				if (entry.getFileName().toString().endsWith(".owl")) {
					File file = entry.toFile();
					inManager.loadOntologyFromOntologyDocument(file);
				} else
					loadRules(entry);
			}
			Tracer.stop("ontology loading", "loading");
			OWLOntology outOntology = outManager.createOntology(
					IRI.generateDocumentIRI(), inManager.getOntologies(), true);
			inManager = null;
			outManager = null;
			nohrQuery = new hybrid.query.model.Query(outOntology);
			outOntology = null;
			System.gc();
			return true;
		} catch (IOException x) {
			System.err.println(x);
			return false;
		} catch (OWLOntologyCreationException e) {
			PrintStream s;
			try {
				s = new PrintStream("log");
				e.printStackTrace(s);
				s.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}
	}

	private void logResults(String query, ArrayList<ArrayList<String>> results) {
		if (!dataset.endsWith("/1"))
			return;
		Charset charset = Charset.defaultCharset();
		String fileName = queryCount + ".out";
		Path path = FileSystems.getDefault().getPath(fileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
			if (results.size() >= 1) {
				for (ArrayList<String> result : results.subList(1,
						results.size())) {
					for (String var : result.subList(1, result.size())) {
						writer.write(var, 0, var.length());
						writer.write(9);
					}
					writer.newLine();
				}
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void setOntology(String ontology) {
		mainOntologyIRI = IRI.create(ontology);
	}

	@Override
	public QueryResult issueQuery(Query query) {
		String[] data = this.dataset.split("/");
		String queryStr = query.getString();
		boolean sameQuery = queryStr.equals(lastQuery);	
		String[] queryStruct = queryStr.split(":-");
		if (queryStruct.length == 2 && !queryStr.equals(lastQuery)) {
			Rules.addRule(query.getString());
			System.out.println("Added rule: " + queryStr);
		}
		if (!sameQuery)
			nohrQuery.abolishTables();

		ArrayList<ArrayList<String>> result = nohrQuery.query(queryStruct[0]);
//		if (!sameQuery)
//			logResults(query.getString(), result);
		lastQuery = queryStr;
		if (!sameQuery) {
			queryCount++;
		}
		return new NoHRQueryResult(result);
	}

	@Override
	public void clear() {
		Tracer.close();
		nohrQuery = null;
	}

}

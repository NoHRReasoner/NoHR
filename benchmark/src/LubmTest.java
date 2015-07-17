import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.Profiles;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;
import ubt.api.QueryConfigParser;
import ubt.api.QuerySpecification;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

public class LubmTest {

    interface Test {

	@Option(longName = "data-dir", description = "data directory")
	File getDataDir();

	@Option(helpRequest = true)
	boolean getHelp();

	@Option(longName = "max-univs", description = "maximum number of universities")
	int getMaxUniversities();

	@Option(longName = "output-dir", description = "output directory")
	File getOutputDir();

	@Option(longName = "profile", description = "OWL profile")
	String getProfile();

	@Option(longName = "queries-file", description = "queries file")
	File getQueriesFile();

	@Option(longName = "step", description = "number of universities added at each run", defaultValue = "1")
	int getStep();

	@Option(longName = "univs-list", description = "list of numbers of universities")
	List<Integer> getUnivs();

	boolean isMaxUniversities();

	boolean isOutputDir();

	boolean isProfile();

	boolean isUnivs();

    }

    public static void main(String[] args) throws Exception {
	Test test;
	try {
	    test = CliFactory.parseArguments(Test.class, args);
	} catch (final ArgumentValidationException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	    return;
	}
	final Path data = test.getDataDir().toPath();
	final QueryConfigParser queryParser = new QueryConfigParser();
	final Vector<QuerySpecification> queries = queryParser
		.createQueryList(test.getQueriesFile().getAbsolutePath());
	if (test.isProfile())
	    if (test.getProfile().equals("QL"))
		AbstractOntologyTranslation.profile = Profiles.OWL2_QL;
	    else if (test.getProfile().equals("EL"))
		AbstractOntologyTranslation.profile = Profiles.OWL2_EL;
	RuntimesLogger.info("warm up");
	run(test, data, queries, 1);
	RuntimesLogger.open("loading", "queries");
	if (test.isMaxUniversities())
	    for (int u = 1; u <= test.getMaxUniversities(); u += test.getStep())
		run(test, data, queries, u);
	if (test.isUnivs())
	    for (final int u : test.getUnivs())
		run(test, data, queries, u);
	RuntimesLogger.close();
	System.out.println("Consult loading times at loading.csv");
	System.out.println("Consult query times at queries.csv");
	System.exit(0);
    }

    private static void run(final Test test, final Path data,
	    final Vector<QuerySpecification> queries, int u)
		    throws OWLOntologyCreationException, OWLOntologyStorageException,
		    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
		    UnsupportedAxiomTypeException, PrologParserException, Exception {
	RuntimesLogger.setDataset(String.valueOf(u));
	final LubmRepository nohrRepository = new LubmRepository(data,
		test.getOutputDir());
	nohrRepository.load(u);
	final Iterator<QuerySpecification> queriesIt = queries.iterator();
	while (queriesIt.hasNext())
	    nohrRepository.issueQuery(queriesIt.next());
    }

}

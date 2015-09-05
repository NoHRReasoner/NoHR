
package benchmark;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import benchmark.ubt.api.QueryConfigParser;
import benchmark.ubt.api.QuerySpecification;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class LubmTest {

	interface Test {

		@Option(longName = "data-dir", description = "data directory")
		File getDataDir();

		@Option(longName = "data-file", description = "data file")
		File getDataFile();

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

		@Option(longName = "warmup-file")
		File getWarmupFile();

		boolean isDataDir();

		boolean isDataFile();

		boolean isMaxUniversities();

		boolean isOutputDir();

		boolean isProfile();

		boolean isStep();

		boolean isUnivs();

		boolean isWarmupFile();

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
		final Path data;
		Path warmUpFile = null;
		if (test.isDataDir())
			data = test.getDataDir().toPath();
		else if (test.isDataFile()) {
			data = test.getDataFile().toPath();
			if (test.isWarmupFile())
				warmUpFile = test.getWarmupFile().toPath();
			else {
				System.err.println("must have: --warmup-file");
				System.exit(0);
				return;
			}

		} else {
			System.err.println("must have one of: --data-dir or --data-file");
			System.exit(0);
			return;
		}
		final QueryConfigParser queryParser = new QueryConfigParser();
		final Vector<QuerySpecification> queries = queryParser.createQueryList(test.getQueriesFile().getAbsolutePath());
		Profile profile = null;
		if (test.isProfile())
			if (test.getProfile().equals("QL"))
				profile = Profile.OWL2_QL;
			else if (test.getProfile().equals("EL"))
				profile = Profile.OWL2_EL;
		RuntimesLogger.info("warm up");
		if (test.isDataDir()) {
			run(test, data, queries, 1, profile);
			RuntimesLogger.open("loading", "queries");
			if (test.isMaxUniversities())
				for (int u = 1; u <= test.getMaxUniversities(); u += test.getStep())
					run(test, data, queries, u, profile);
			if (test.isUnivs())
				for (final int u : test.getUnivs())
					run(test, data, queries, u, profile);
		} else {
			run(test, warmUpFile, queries, null, profile);
			run(test, data, queries, null, profile);
		}
		RuntimesLogger.close();
		System.out.println("Consult loading times at loading.csv");
		System.out.println("Consult query times at queries.csv");
		System.exit(0);
	}

	private static void run(final Test test, final Path data, final Vector<QuerySpecification> queries, Integer u,
			Profile profile)
					throws OWLOntologyCreationException, OWLOntologyStorageException, OWLProfilesViolationsException,
					IOException, CloneNotSupportedException, UnsupportedAxiomsException, Exception {
		if (u != null)
			RuntimesLogger.setDataset(String.valueOf(u));
		else
			RuntimesLogger.setDataset(data.getFileName().toString());
		final LubmRepository nohrRepository = new LubmRepository(data, test.getOutputDir(), profile);
		nohrRepository.load(u);
		final Iterator<QuerySpecification> queriesIt = queries.iterator();
		while (queriesIt.hasNext())
			nohrRepository.issueQuery(queriesIt.next());
	}

}

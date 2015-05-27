import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Vector;

import other.Config;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.TranslationAlgorithm;
import ubt.api.QueryConfigParser;
import ubt.api.QuerySpecification;
import utils.Tracer;

public class LubmTest {

    public static void main(String[] args) {
	try {
	    if (args.length != 5 && args.length != 7) {
		System.err
			.println("expected args: [-o results directory] <profile> <data directory> <queries file> <max universities> <step>");
		System.exit(1);
	    }
	    String outDir = null;
	    int i = 0;
	    if (args[i++].equals("-o"))
		outDir = args[i++];
	    String profile = args[i++];
	    if (profile.equals("QL"))
		Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
	    else if (profile.equals("EL"))
		Config.translationAlgorithm = TranslationAlgorithm.EL;
	    else {
		System.err.println("incorrect profile");
		System.exit(1);
	    }
	    // String ontology = args[i++];
	    String dataDir = args[i++];
	    String queriesFile = args[i++];
	    int maxUniversities = Integer.valueOf(args[i++]);
	    int step = Integer.valueOf(args[i++]);
	    Path data = FileSystems.getDefault().getPath(dataDir);
	    QueryConfigParser queryParser = new QueryConfigParser();
	    Vector queries = queryParser.createQueryList(queriesFile);
	    Tracer.open("loading", "queries");
	    for (int u = 1; u <= maxUniversities; u += step) {
		Tracer.setDataset(String.valueOf(u));
		LubmRepository nohrRepository = new LubmRepository(data, outDir);
		// nohrRepository.setOntology(ontology);
		nohrRepository.load(u);
		Iterator<QuerySpecification> queriesIt = queries.iterator();
		while (queriesIt.hasNext())
		    nohrRepository.issueQuery(queriesIt.next().query_);
	    }
	    Tracer.close();
	    System.out.println("Consult loading times at loading.csv");
	    System.out.println("Consult query times at queries.csv");
	    System.exit(0);
	} catch (NumberFormatException e) {
	    System.err.println("data directories names must be numbers");
	    System.exit(1);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }
}

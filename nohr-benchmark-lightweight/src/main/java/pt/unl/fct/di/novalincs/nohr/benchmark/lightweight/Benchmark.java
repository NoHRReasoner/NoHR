package pt.unl.fct.di.novalincs.nohr.benchmark.lightweight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

/**
 * Base class for running benchmarks. This class provides the ability 
 * @author Carlos Lopes
 */
public abstract class Benchmark {

    private final NoHRHybridKBConfiguration nohrConfig;
    private final Metrics metrics;
    private final Resources resources;

    private final File[] inputDirectories;
    private final File outputDirectory;

    private final String name;
    
    public Benchmark(String[] args) throws IOException, OWLOntologyCreationException, ParseException {
        this(args, "benchmark");
    }
 
    public Benchmark(String[] args, String testName) throws IOException, OWLOntologyCreationException, ParseException {
        final Map<String, String> env = System.getenv();

        final String NOHR_XSB_DIRECTORY = env.get("NOHR_XSB_DIRECTORY");
        final String NOHR_KONCLUDE_BINARY = env.get("NOHR_KONCLUDE_BINARY");
        final String NOHR_ODBC_DRIVERS = env.get("ODBCINI");
        
        if(args.length < 6) {
        	new IOException("Parameters were not provided correctly.");
        }
        String[] inputDirs = args[0].split(",");
        inputDirectories = new File[inputDirs.length];
        for(int i=0;i<inputDirs.length;i++) {
        	inputDirectories[i] = new File(inputDirs[i]);
        }
        resources = new Resources(NOHR_ODBC_DRIVERS);
        String owlStructure = null;
        if(args.length > 6  && !args[6].matches("")) {
        	owlStructure = args[6];
        }
        resources.loadAll(owlStructure, inputDirectories);

        outputDirectory = new File(args[1]);
        this.name = testName + "_" +args[2];
        
        int repeat = Integer.parseInt(args[3]);
        metrics = new Metrics(repeat);

        boolean dl = Boolean.parseBoolean(args[4]);
        
        DLInferenceEngine dLInferenceEngine = DLInferenceEngine.getDLInferenceEngine(args[5]);

        Files.createDirectories(outputDirectory.toPath());

        nohrConfig = new NoHRHybridKBConfiguration(
                new File(NOHR_XSB_DIRECTORY),
                new File(NOHR_KONCLUDE_BINARY),
                dl,
                dl,
                dl,
                dLInferenceEngine
        );
        
    }

    public NoHRHybridKBConfiguration getNoHRConfig() {
        return nohrConfig;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public Resources getResources() {
        return resources;
    }

    public String getName() {
        return name;
    }

    public abstract void prepare() throws Exception;

    public abstract void singleRun() throws Exception;

    public abstract void dispose() throws Exception;

    public void printMetrics() throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new File(outputDirectory, name + "-metrics.csv"))) {
            writer.write(metrics.toString());
            writer.flush();
        }
    }

    public void run() throws Exception {
        try {
            prepare();

            for (int i = 0; i < metrics.getMeasureCount(); i++) {
                System.gc();
                singleRun();
            }

            dispose();
            printMetrics();

            System.out.println("Done!");
            System.exit(0); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Benchmark.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
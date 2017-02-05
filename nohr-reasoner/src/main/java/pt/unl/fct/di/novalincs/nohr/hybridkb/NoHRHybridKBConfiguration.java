package pt.unl.fct.di.novalincs.nohr.hybridkb;

import java.io.File;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorConfiguration;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLMode;

public class NoHRHybridKBConfiguration {

    private final OntologyTranslatorConfiguration ontologyTranslationConfiguration;
    private File xsbDirectory;

    public NoHRHybridKBConfiguration() {
        this(new File(System.getenv("XSB_DIR")), new File(System.getenv("KONCLUDE_BIN")), false, false, DLMode.HERMIT);
    }

    public NoHRHybridKBConfiguration(File xsbDirectory, File koncludeBinary, boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, DLMode dLInferenceEngine) {
        this.ontologyTranslationConfiguration = new OntologyTranslatorConfiguration(dLInferenceEngine, dLInferenceEngineEL, dLInferenceEngineQL, koncludeBinary);
        this.xsbDirectory = xsbDirectory;
    }

    public OntologyTranslatorConfiguration getOntologyTranslationConfiguration() {
        return ontologyTranslationConfiguration;
    }

    public File getXsbDirectory() {
        return xsbDirectory;
    }

    public void setXsbDirectory(File xsbBin) {
        this.xsbDirectory = xsbBin;
    }

}

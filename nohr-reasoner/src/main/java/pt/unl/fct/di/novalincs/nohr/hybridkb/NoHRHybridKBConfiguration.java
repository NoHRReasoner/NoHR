package pt.unl.fct.di.novalincs.nohr.hybridkb;

import java.io.File;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorConfiguration;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

public class NoHRHybridKBConfiguration {

    private final OntologyTranslatorConfiguration ontologyTranslationConfiguration;
    private File xsbDirectory;

    public NoHRHybridKBConfiguration() {
        this(new File(System.getenv("XSB_BIN_DIRECTORY")), new File(System.getenv("KONCLUDE_BIN")), false, false, false, DLInferenceEngine.HERMIT);
    }

    public NoHRHybridKBConfiguration(boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, boolean dLInferenceEngineRL, DLInferenceEngine dLInferenceEngine) {
        this(new File(System.getenv("XSB_BIN_DIRECTORY")), new File(System.getenv("KONCLUDE_BIN")), dLInferenceEngineEL, dLInferenceEngineQL, dLInferenceEngineRL, dLInferenceEngine);
    }

    public NoHRHybridKBConfiguration(File xsbDirectory, File koncludeBinary, boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, boolean dLInferenceEngineRL, DLInferenceEngine dLInferenceEngine) {
        this.ontologyTranslationConfiguration = new OntologyTranslatorConfiguration(dLInferenceEngine, dLInferenceEngineEL, dLInferenceEngineQL, dLInferenceEngineRL, koncludeBinary);
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

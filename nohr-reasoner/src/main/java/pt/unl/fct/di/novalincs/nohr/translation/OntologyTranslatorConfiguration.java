package pt.unl.fct.di.novalincs.nohr.translation;

import java.io.File;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLMode;

public class OntologyTranslatorConfiguration {

    private DLMode dLInferenceEngine;
    private boolean dLInferenceEngineEL;
    private boolean dLInferenceEngineQL;
    private File koncludeBinary;

    private boolean changed;

    public OntologyTranslatorConfiguration() {
        this(DLMode.HERMIT, false, false, new File(System.getenv("KONCLUDE_BIN")));
    }

    public OntologyTranslatorConfiguration(DLMode dLInferenceEngine, boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, File koncludeBinary) {
        this.dLInferenceEngine = dLInferenceEngine;
        this.dLInferenceEngineEL = dLInferenceEngineEL;
        this.dLInferenceEngineQL = dLInferenceEngineQL;
        this.koncludeBinary = koncludeBinary;

        changed = true;
    }

    public DLMode getDLInferenceEngine() {
        return dLInferenceEngine;
    }

    public boolean getDLInferenceEngineEL() {
        return dLInferenceEngineEL;
    }

    public boolean getDLInferenceEngineQL() {
        return dLInferenceEngineQL;
    }

    public File getKoncludeBinary() {
        return koncludeBinary;
    }

    public boolean hasChanged() {
        return changed;
    }

    public void handledChanges() {
        changed = false;
    }

    public void setDLInferenceEngine(DLMode value) {
        this.dLInferenceEngine = value;
        changed = true;
    }

    public void setDLInferenceEngineEL(boolean value) {
        this.dLInferenceEngineEL = value;
        changed = true;
    }

    public void setDLInferenceEngineQL(boolean value) {
        this.dLInferenceEngineQL = value;
        changed = true;
    }

    public void setKoncludeBinary(File value) {
        this.koncludeBinary = value;
        changed = true;
    }

}

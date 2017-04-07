package pt.unl.fct.di.novalincs.nohr.translation;

import java.io.File;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

public class OntologyTranslatorConfiguration {

    private DLInferenceEngine dLInferenceEngine;
    private boolean dLInferenceEngineEL;
    private boolean dLInferenceEngineQL;
    private boolean dLInferenceEngineRL;
    private File koncludeBinary;

    private boolean changed;

    public OntologyTranslatorConfiguration() {
        this(DLInferenceEngine.HERMIT, false, false, new File(System.getenv("KONCLUDE_BIN")));
    }

    public OntologyTranslatorConfiguration(DLInferenceEngine dLInferenceEngine, boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, File koncludeBinary) {
        this.dLInferenceEngine = dLInferenceEngine;
        this.dLInferenceEngineEL = dLInferenceEngineEL;
        this.dLInferenceEngineQL = dLInferenceEngineQL;
        this.dLInferenceEngineRL = false;
        this.koncludeBinary = koncludeBinary;

        changed = true;
    }

    public OntologyTranslatorConfiguration(DLInferenceEngine dLInferenceEngine, boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, boolean dLInferenceEngineRL, File koncludeBinary) {
        this.dLInferenceEngine = dLInferenceEngine;
        this.dLInferenceEngineEL = dLInferenceEngineEL;
        this.dLInferenceEngineQL = dLInferenceEngineQL;
        this.dLInferenceEngineRL = dLInferenceEngineRL;
        this.koncludeBinary = koncludeBinary;

        changed = true;
    }

    public DLInferenceEngine getDLInferenceEngine() {
        return dLInferenceEngine;
    }

    public boolean getDLInferenceEngineEL() {
        return dLInferenceEngineEL;
    }

    public boolean getDLInferenceEngineQL() {
        return dLInferenceEngineQL;
    }

    public boolean getDLInferenceEngineRL() {
        return dLInferenceEngineRL;
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

    public void setDLInferenceEngine(DLInferenceEngine value) {
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

    public void setDLInferenceEngineRL(boolean value) {
        this.dLInferenceEngineQL = value;
        changed = true;
    }

    public void setKoncludeBinary(File value) {
        this.koncludeBinary = value;
        changed = true;
    }

}

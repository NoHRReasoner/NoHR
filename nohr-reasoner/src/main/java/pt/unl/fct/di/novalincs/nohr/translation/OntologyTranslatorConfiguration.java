package pt.unl.fct.di.novalincs.nohr.translation;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

public class OntologyTranslatorConfiguration {

    private DLInferenceEngine dLInferenceEngine;
    private boolean dLInferenceEngineEL;
    private boolean dLInferenceEngineQL;
    private boolean dLInferenceEngineRL;
    private boolean ignoreAllUnsupported;
    private final Set<AxiomType<?>> ignoredUnsupportedAxioms;
    private File koncludeBinary;

    private boolean changed;

    public OntologyTranslatorConfiguration() {
        this(DLInferenceEngine.HERMIT, false, false, false, new File(System.getenv("KONCLUDE_BIN")));
    }

    public OntologyTranslatorConfiguration(DLInferenceEngine dLInferenceEngine, boolean dLInferenceEngineEL, boolean dLInferenceEngineQL, boolean dLInferenceEngineRL, File koncludeBinary) {
        this.dLInferenceEngine = dLInferenceEngine;
        this.dLInferenceEngineEL = dLInferenceEngineEL;
        this.dLInferenceEngineQL = dLInferenceEngineQL;
        this.dLInferenceEngineRL = dLInferenceEngineRL;
        this.koncludeBinary = koncludeBinary;

        this.ignoreAllUnsupported = Boolean.parseBoolean(System.getenv("IGNORE_UNSUPPORTED"));
        this.ignoredUnsupportedAxioms = new HashSet<>();

        final boolean ignoreUnsupportedAxioms = System.getenv().containsKey("IGNORED_UNSUPPORTED_AXIOMS");

        if (ignoreUnsupportedAxioms) {
            final String ignoreUnsupportedAxiomsString = System.getenv("IGNORED_UNSUPPORTED_AXIOMS");
            final String[] ignoreUnsupportedAxiomsStrings = ignoreUnsupportedAxiomsString.split(",");

            for (String i : ignoreUnsupportedAxiomsStrings) {
                this.ignoredUnsupportedAxioms.add(AxiomType.getAxiomType(i));
            }
        }
        
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

    public Set<AxiomType<?>> getIgnoredUnsupportedAxioms() {
        return ignoredUnsupportedAxioms;
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

    public boolean ignoreAllUnsupportedAxioms() {
        return ignoreAllUnsupported;
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

    public void setIgnoreAllunsupportedAxioms(boolean value) {
        this.ignoreAllUnsupported = value;
        changed = true;
    }

    public void setKoncludeBinary(File value) {
        this.koncludeBinary = value;
        changed = true;
    }

}

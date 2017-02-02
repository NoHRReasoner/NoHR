package pt.unl.fct.di.novalincs.nohr.hybridkb;

import java.io.File;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorConfiguration;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLMode;

public class NoHRHybridKBConfiguration {

    private final OntologyTranslatorConfiguration ontologyTranslationConfiguration;
    private File xsbBin;

    public NoHRHybridKBConfiguration() {
        this(new File(System.getenv("XSB_DIR")), new File(System.getenv("KONCLUDE_BIN")), false, false, DLMode.HERMIT);
    }

    public NoHRHybridKBConfiguration(File xsbBin, File koncludeBin, boolean useDlForEl, boolean useDlForQl, DLMode dlMode) {
        this.ontologyTranslationConfiguration = new OntologyTranslatorConfiguration(dlMode, useDlForEl, useDlForQl, koncludeBin);
        this.xsbBin = xsbBin;
    }

    public OntologyTranslatorConfiguration getOntologyTranslationConfiguration() {
        return ontologyTranslationConfiguration;
    }

    public File getXsbBin() {
        return xsbBin;
    }

    public void setXsbBin(File xsbBin) {
        this.xsbBin = xsbBin;
    }

}

package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class KoncludeReasonerWrapper {

    private final String koncludeFilename;
    private final File ontologyFile;

    public KoncludeReasonerWrapper(String koncludeFilename, String ontologyFilename) throws OWLOntologyCreationException, OWLOntologyStorageException {
        this(koncludeFilename, OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File(ontologyFilename)));
    }

    public KoncludeReasonerWrapper(String koncludeFilename, OWLOntology ontology) throws OWLOntologyStorageException {
        try {
            this.koncludeFilename = koncludeFilename;
            this.ontologyFile = File.createTempFile("konclude_input_", ".owl");
            this.ontologyFile.deleteOnExit();

            ontology.getOWLOntologyManager().saveOntology(ontology, new OWLXMLDocumentFormat(), new FileOutputStream(ontologyFile));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private enum Command {
        CLASSIFICATION,
        CONSISTENCY,
        REALIZATION;

        @Override
        public String toString() {
            switch (this) {
                case CLASSIFICATION:
                    return "classification";
                case CONSISTENCY:
                    return "consistency";
                case REALIZATION:
                    return "realization";
                default:
                    throw new AssertionError(this.name());
            }
        }
    }

    public OWLOntology classification() {
        try {
            File output = File.createTempFile("konclude_classification_", ".owl");
            output.deleteOnExit();

            run(Command.CLASSIFICATION, ontologyFile, output);

            return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(output);
        } catch (IOException | OWLOntologyCreationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean consistency() {
        try {
            File output = File.createTempFile("konclude_consistency_", ".bool");
            output.deleteOnExit();

            run(Command.CONSISTENCY, ontologyFile, output);

            return parseConsistencyResponse(output);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean parseConsistencyResponse(File output) {
        boolean ret;

        try (FileInputStream fis = new FileInputStream(output); InputStreamReader isr = new InputStreamReader(fis); BufferedReader br = new BufferedReader(isr);) {
            ret = Boolean.parseBoolean(br.readLine());
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return ret;
    }

    public OWLOntology realization() {
        try {
            File output = File.createTempFile("konclude_realization_", ".owl");
            output.deleteOnExit();

            run(Command.REALIZATION, ontologyFile, output);

            return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(output);
        } catch (IOException | OWLOntologyCreationException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void run(Command command, File input, File output) {
        try {
            Process p = Runtime.getRuntime().exec(koncludeFilename + " " + command.toString() + " -w AUTO -i " + input.getAbsolutePath() + " -o " + output.getAbsoluteFile());

            p.waitFor();
            
            p.destroy();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}

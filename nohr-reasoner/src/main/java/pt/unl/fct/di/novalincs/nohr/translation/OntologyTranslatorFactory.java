package pt.unl.fct.di.novalincs.nohr.translation;

import java.io.File;
import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLMode;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLOntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.dl.HermitInferenceEngine;
import pt.unl.fct.di.novalincs.nohr.translation.dl.KoncludeInferenceEngine;
import pt.unl.fct.di.novalincs.nohr.translation.el.ELOntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.ql.QLOntologyTranslator;

public class OntologyTranslatorFactory {

    private final File koncludeBinary;
    private final boolean preferDLEngineOverEL;
    private final boolean preferDLEngineOverQL;
    private final DLMode preferredDLEngine;

    public OntologyTranslatorFactory(OntologyTranslatorConfiguration configuration) {
        this.koncludeBinary = configuration.getKoncludeBinary();
        this.preferDLEngineOverEL = configuration.getDLInferenceEngineEL();
        this.preferDLEngineOverQL = configuration.getDLInferenceEngineQL();
        this.preferredDLEngine = configuration.getDLInferenceEngine();
    }

    public OntologyTranslator createOntologyTranslator(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase dedutiveDatabase, Profile profile)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {

        if (profile == null) {
            profile = Profile.getProfile(ontology);
        }

        switch (profile) {
            case OWL2_EL:
                if (preferDLEngineOverEL) {
                    return new DLOntologyTranslator(ontology, vocabulary, dedutiveDatabase, getDLInferenceEngine());
                } else {
                    return new ELOntologyTranslator(ontology, vocabulary, dedutiveDatabase);
                }
            case OWL2_QL:
                if (preferDLEngineOverQL) {
                    return new DLOntologyTranslator(ontology, vocabulary, dedutiveDatabase, getDLInferenceEngine());
                } else {
                    return new QLOntologyTranslator(ontology, vocabulary, dedutiveDatabase);
                }
            case NOHR_DL:
                return new DLOntologyTranslator(ontology, vocabulary, dedutiveDatabase, getDLInferenceEngine());
            default:
                throw new OWLProfilesViolationsException();
        }
    }

    private InferenceEngine getDLInferenceEngine() {
        if (preferredDLEngine == DLMode.KONCLUDE) {
            return new KoncludeInferenceEngine(koncludeBinary.getAbsolutePath());
        } else {
            return new HermitInferenceEngine();
        }
    }

    public boolean isPreferred(OntologyTranslator translator, OWLOntology ontology) {
        final Profile translatorProfile = translator.getProfile();
        final Profile ontologyProfile = Profile.getProfile(ontology);

        return translatorProfile == ontologyProfile
                || translatorProfile == Profile.NOHR_DL
                && (ontologyProfile == Profile.OWL2_EL && preferDLEngineOverEL
                || ontologyProfile == Profile.OWL2_QL && preferDLEngineOverQL);
    }
}

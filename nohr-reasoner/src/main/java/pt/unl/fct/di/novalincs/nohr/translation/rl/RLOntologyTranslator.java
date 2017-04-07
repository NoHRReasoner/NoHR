package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.nohr.translation.UnsupportedAxiomException;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class RLOntologyTranslator extends OntologyTranslatorImpl {

    private RLOntologyNormalization normalizedOntology;
    private final RLOriginalAxiomTranslator originalAxiomTranslator;
    private final RLDoubledAxiomTranslator doubledAxiomTranslator;

    public RLOntologyTranslator(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase deductiveDatabase) throws UnsupportedAxiomsException {
        super(ontology, vocabulary, deductiveDatabase);

        originalAxiomTranslator = new RLOriginalAxiomTranslator(vocabulary);
        doubledAxiomTranslator = new RLDoubledAxiomTranslator(vocabulary);

//        prepareUpdate();
    }

    @Override
    public Profile getProfile() {
        return Profile.OWL2_RL;
    }

    @Override
    public boolean requiresDoubling() {
        return normalizedOntology == null || normalizedOntology.hasDisjunctions();
    }

    private void prepareUpdate() throws UnsupportedAxiomsException {
        RuntimesLogger.start("[OWL RL] ontology normalization");
        normalizedOntology = new RLOntologyNormalizationImpl(ontology, vocabulary);
        RuntimesLogger.stop("[OWL RL] ontology normalization", "loading");
    }

    private void translate(RLAxiomTranslator axiomTranslator) {
        for (final OWLClassAssertionAxiom axiom : normalizedOntology.conceptAssertions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLSubClassOfAxiom axiom : normalizedOntology.conceptSubsumptions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLDataPropertyAssertionAxiom axiom : normalizedOntology.dataAssertions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLDisjointDataPropertiesAxiom axiom : normalizedOntology.dataDisjunctions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLSubDataPropertyOfAxiom axiom : normalizedOntology.dataSubsumptions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLIrreflexiveObjectPropertyAxiom axiom : normalizedOntology.irreflexiveRoles()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLObjectPropertyAssertionAxiom axiom : normalizedOntology.roleAssertions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLDisjointObjectPropertiesAxiom axiom : normalizedOntology.roleDisjunctions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (final OWLSubObjectPropertyOfAxiom axiom : normalizedOntology.roleSubsumptions()) {
            try {
                translation.addAll(axiomTranslator.translate(axiom));
            } catch (UnsupportedAxiomException ex) {
                Logger.getLogger(RLOntologyTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void updateTranslation() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        prepareUpdate();
        translation.clear();

        RuntimesLogger.start("[OWL RL] ontology translation");

        translate(originalAxiomTranslator);

        if (normalizedOntology.hasDisjunctions()) {
            translate(doubledAxiomTranslator);
        }

        RuntimesLogger.stop("[OWL RL] ontology translation", "loading");
    }

}

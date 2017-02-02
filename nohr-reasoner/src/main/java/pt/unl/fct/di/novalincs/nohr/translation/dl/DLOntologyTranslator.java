package pt.unl.fct.di.novalincs.nohr.translation.dl;

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
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.InferenceEngine;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class DLOntologyTranslator extends OntologyTranslatorImplementor {

    private DLOntologyNormalization normalizedOntology;
    private final DLOriginalAxiomTranslator axiomTranslator;
    private final DLDoubledAxiomTranslator doubledAxiomTranslator;
    private final InferenceEngine engine;

    public DLOntologyTranslator(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase dedutiveDatabase, InferenceEngine engine) throws UnsupportedAxiomsException {
        super(ontology, vocabulary, dedutiveDatabase);

        axiomTranslator = new DLOriginalAxiomTranslator(vocabulary);
        doubledAxiomTranslator = new DLDoubledAxiomTranslator(vocabulary);
        this.engine = engine;

        RuntimesLogger.start("[NOHR DL] ontology normalization");
        prepareUpdate();
        RuntimesLogger.stop("[NOHR DL] ontology normalization", "loading");

    }

    @Override
    public void updateTranslation() throws UnsupportedAxiomsException {
        prepareUpdate();
        translation.clear();

        RuntimesLogger.start("[NOHR DL] ontology translation");

        translate(axiomTranslator);

        if (normalizedOntology.hasDisjunctions()) {
            translate(doubledAxiomTranslator);
        }

        RuntimesLogger.stop("[NOHR DL] ontology translation", "loading");
    }

    @Override
    public Profile getProfile() {
        return Profile.NOHR_DL;
    }

    @Override
    public boolean hasDisjunctions() {
        return normalizedOntology.hasDisjunctions();
    }

    private void prepareUpdate() throws UnsupportedAxiomsException {
        normalizedOntology = new DLOntologyNormailzationImpl(ontology, vocabulary, engine);
    }

    private void translate(DLAxiomTranslator axiomTranslator) {
        for (final OWLSubPropertyChainOfAxiom axiom : normalizedOntology.chainSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLClassAssertionAxiom axiom : normalizedOntology.conceptAssertions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubClassOfAxiom axiom : normalizedOntology.conceptSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDataPropertyAssertionAxiom axiom : normalizedOntology.dataAssertions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDisjointDataPropertiesAxiom axiom : normalizedOntology.dataDisjunctions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubDataPropertyOfAxiom axiom : normalizedOntology.dataSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLIrreflexiveObjectPropertyAxiom axiom : normalizedOntology.irreflexiveRoles()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLObjectPropertyAssertionAxiom axiom : normalizedOntology.roleAssertions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDisjointObjectPropertiesAxiom axiom : normalizedOntology.roleDisjunctions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubObjectPropertyOfAxiom axiom : normalizedOntology.roleSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }
    }
}

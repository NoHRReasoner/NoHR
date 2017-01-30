package pt.unl.fct.di.novalincs.nohr.translation.dl;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class DLOntologyTranslator extends OntologyTranslatorImplementor {

    private DLOntologyNormalization normalizedOntology;
    private final DLOriginalAxiomsTranslator originalAxiomsTranslator;
    private final DLDoubleAxiomsTranslator doubleAxiomsTranslator;

    public DLOntologyTranslator(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase dedutiveDatabase) throws UnsupportedAxiomsException {
        super(ontology, vocabulary, dedutiveDatabase);

        originalAxiomsTranslator = new DLOriginalAxiomsTranslator(vocabulary);
        doubleAxiomsTranslator = new DLDoubleAxiomsTranslator(vocabulary);

        RuntimesLogger.start("[NOHR DL (HermiT)] ontology normalization");

        normalizedOntology = new HermiTDLOntologyNormalization(ontology, vocabulary);

        RuntimesLogger.stop("[NOHR DL (HermiT)] ontology normalization", "loading");
    }

    @Override
    public void updateTranslation() throws UnsupportedAxiomsException {
        prepareUpdate();
        translation.clear();

        RuntimesLogger.start("[NOHR DL (HermiT)] ontology translation");

        translate(originalAxiomsTranslator);

        if (normalizedOntology.hasDisjunctions()) {
            translate(doubleAxiomsTranslator);
        }

        RuntimesLogger.stop("[NOHR DL (HermiT)] ontology translation", "loading");
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
        normalizedOntology = new HermiTDLOntologyNormalization(ontology, vocabulary);
    }

    private void translate(DLAxiomsTranslator axiomTranslator) {
        for (final OWLSubPropertyChainOfAxiom axiom : normalizedOntology.chainSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }

        for (final OWLClassAssertionAxiom assertion : normalizedOntology.conceptAssertions()) {
            translation.addAll(axiomTranslator.translation(assertion));
        }

        for (final OWLSubClassOfAxiom axiom : normalizedOntology.conceptSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }

        for (final OWLDataPropertyAssertionAxiom assertion : normalizedOntology.dataAssertions()) {
            translation.addAll(axiomTranslator.translation(assertion));
        }

        for (final OWLSubDataPropertyOfAxiom axiom : normalizedOntology.dataSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }

        for (final OWLSubObjectPropertyOfAxiom axiom : normalizedOntology.roleSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }

        for (final OWLObjectPropertyAssertionAxiom assertion : normalizedOntology.roleAssertions()) {
            translation.addAll(axiomTranslator.translation(assertion));
        }

        for (final OWLSubObjectPropertyOfAxiom axiom : normalizedOntology.roleSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }
    }

}

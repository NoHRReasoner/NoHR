package pt.unl.fct.di.novalincs.nohr.translation.rl;

import pt.unl.fct.di.novalincs.nohr.translation.dl.*;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
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
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.InferenceEngine;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public final class RLOntologyTranslation extends OntologyTranslatorImpl {

    private final DLOriginalAxiomTranslator originalAxiomTranslator;
    private final DLDoubledAxiomTranslator doubledAxiomTranslator;
    private boolean requiresDoubling;

    public RLOntologyTranslation(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase deductiveDatabase) throws UnsupportedAxiomsException {
        super(ontology, vocabulary, deductiveDatabase);

        originalAxiomTranslator = new DLOriginalAxiomTranslator(vocabulary);
        doubledAxiomTranslator = new DLDoubledAxiomTranslator(vocabulary);
    }

    private boolean evaluateDoubling(OWLOntology ontology) {
        if (ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION) > 0
                || ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION) > 0) {
            return true;
        }

        if (ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES) > 0
                || ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES) > 0
                || ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY) > 0) {
            return true;
        }

        for (final OWLSubClassOfAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            for (final OWLClassExpression i : axiom.getSuperClass().asConjunctSet()) {
                if (i.isOWLNothing()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Profile getProfile() {
        return Profile.OWL2_RL;
    }

    private OWLOntology normalizedOntology() throws UnsupportedAxiomsException {
        RuntimesLogger.start("[NOHR DL] ontology normalization");
        final DLReducedOntology reducedOntology = new DLReducedOntologyImpl(ontology, vocabulary);
        RuntimesLogger.stop("[NOHR DL] ontology normalization", "loading");

        return reducedOntology.getReducedOWLOntology();
    }

    @Override
    public boolean requiresDoubling() {
        return requiresDoubling;
    }

    private void translate(OWLOntology ontology, DLAxiomTranslator axiomTranslator) {
        for (final OWLClassAssertionAxiom axiom : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDataPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDisjointDataPropertiesAxiom axiom : ontology.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDisjointObjectPropertiesAxiom axiom : ontology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLIrreflexiveObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubClassOfAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubDataPropertyOfAxiom axiom : ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubPropertyChainOfAxiom axiom : ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubObjectPropertyOfAxiom axiom : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
            translation.addAll(axiomTranslator.translate(axiom));
        }
    }

    @Override
    public void updateTranslation() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        final OWLOntology normalizedOntology = normalizedOntology();
        final boolean doubling = evaluateDoubling(normalizedOntology);
        requiresDoubling = doubling;

        translation.clear();

        RuntimesLogger.start("[NOHR DL] ontology translation");

        translate(normalizedOntology, originalAxiomTranslator);

        if (doubling) {
            translate(normalizedOntology, doubledAxiomTranslator);
        }

        RuntimesLogger.stop("[NOHR DL] ontology translation", "loading");
    }
}

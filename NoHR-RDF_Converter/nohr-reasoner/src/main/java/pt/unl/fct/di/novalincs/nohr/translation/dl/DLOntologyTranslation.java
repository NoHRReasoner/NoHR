package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.Collections;
import java.util.Set;
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

public final class DLOntologyTranslation extends OntologyTranslatorImpl {

    private final static AxiomType<?>[] SUPPORTED_AXIOM_TYPES = new AxiomType<?>[]{
        AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
        AxiomType.CLASS_ASSERTION,
        AxiomType.DATA_PROPERTY_ASSERTION,
        AxiomType.DATA_PROPERTY_DOMAIN,
        AxiomType.DECLARATION,
        AxiomType.DISJOINT_CLASSES,
        AxiomType.DISJOINT_DATA_PROPERTIES,
        AxiomType.DISJOINT_OBJECT_PROPERTIES,
        AxiomType.EQUIVALENT_CLASSES,
        AxiomType.EQUIVALENT_DATA_PROPERTIES,
        AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
        AxiomType.INVERSE_OBJECT_PROPERTIES,
        AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
        AxiomType.OBJECT_PROPERTY_ASSERTION,
        AxiomType.OBJECT_PROPERTY_DOMAIN,
        AxiomType.OBJECT_PROPERTY_RANGE,
        AxiomType.SUB_DATA_PROPERTY,
        AxiomType.SUB_OBJECT_PROPERTY,
        AxiomType.SUB_PROPERTY_CHAIN_OF,
        AxiomType.SYMMETRIC_OBJECT_PROPERTY,
        AxiomType.SUBCLASS_OF,
        AxiomType.TRANSITIVE_OBJECT_PROPERTY
    };

    private final DLOriginalAxiomTranslator originalAxiomTranslator;
    private final DLDoubledAxiomTranslator doubledAxiomTranslator;
    private final InferenceEngine inferenceEngine;
    private boolean requiresDoubling;

    public DLOntologyTranslation(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase deductiveDatabase, InferenceEngine inferenceEngine) throws UnsupportedAxiomsException {
        this(ontology, vocabulary, deductiveDatabase, inferenceEngine, false, Collections.EMPTY_SET);
    }

    public DLOntologyTranslation(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase deductiveDatabase, InferenceEngine inferenceEngine, boolean ignoreAllUnsupportedAxioms, Set<AxiomType<?>> ignoredUnsupportedAxioms) throws UnsupportedAxiomsException {
        super(ontology, vocabulary, deductiveDatabase, ignoreAllUnsupportedAxioms, ignoredUnsupportedAxioms);

        this.inferenceEngine = inferenceEngine;

        originalAxiomTranslator = new DLOriginalAxiomTranslator(vocabulary);
        doubledAxiomTranslator = new DLDoubledAxiomTranslator(vocabulary);
    }

    private boolean evaluateDoubling(OWLOntology inferredOntology) {
        if (ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION) > 0
                || ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION) > 0) {
            return true;
        }

        if (inferredOntology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES) > 0
                || inferredOntology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES) > 0
                || inferredOntology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY) > 0) {
            return true;
        }

        for (final OWLSubClassOfAxiom axiom : inferredOntology.getAxioms(AxiomType.SUBCLASS_OF)) {
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
        return Profile.NOHR_DL;
    }

    @Override
    public AxiomType<?>[] getSupportedAxioms() {
        return SUPPORTED_AXIOM_TYPES;
    }

    private OWLOntology inferredOntology() throws UnsupportedAxiomsException {
        RuntimesLogger.start("[NOHR DL] ontology normalization");
        final DLReducedOntology reducedOntology = new DLReducedOntologyImpl(ontology, vocabulary);
        RuntimesLogger.stop("[NOHR DL] ontology normalization", "loading");

        RuntimesLogger.start("[NOHR DL] ontology inference");
        final OWLOntology inferredOntology = inferenceEngine.computeInferences(reducedOntology.getReducedOWLOntology());
        RuntimesLogger.stop("[NOHR DL] ontology inference", "loading");

        return inferredOntology;
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
        evaluateOntologySupport();

        final OWLOntology inferredOntology = inferredOntology();
        final boolean doubling = evaluateDoubling(inferredOntology);
        requiresDoubling = doubling;

        translation.clear();

        RuntimesLogger.start("[NOHR DL] ontology translation");

        translate(inferredOntology, originalAxiomTranslator);

        if (doubling) {
            translate(inferredOntology, doubledAxiomTranslator);
        }

        RuntimesLogger.stop("[NOHR DL] ontology translation", "loading");
    }

}

package pt.unl.fct.di.novalincs.nohr.translation.el;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
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
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link OntologyTranslator} for the
 * {@link Profile#OWL2_EL EL} profile, according to {@link <a>A Correct EL
 * Oracle for NoHR (Technical Report)</a>}.
 *
 * @author Nuno Costa
 */
public class ELOntologyTranslator extends OntologyTranslatorImpl {

    public static final AxiomType<?>[] SUPPORTED_AXIOM_TYPES = new AxiomType<?>[]{
        AxiomType.CLASS_ASSERTION,
        AxiomType.DATA_PROPERTY_ASSERTION,
        AxiomType.DATA_PROPERTY_DOMAIN,
        AxiomType.DECLARATION,
        AxiomType.DISJOINT_CLASSES,
        AxiomType.EQUIVALENT_CLASSES,
        AxiomType.EQUIVALENT_DATA_PROPERTIES,
        AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
        AxiomType.OBJECT_PROPERTY_ASSERTION,
        AxiomType.OBJECT_PROPERTY_DOMAIN,
        AxiomType.SUB_DATA_PROPERTY,
        AxiomType.SUB_DATA_PROPERTY,
        AxiomType.SUB_OBJECT_PROPERTY,
        AxiomType.SUB_PROPERTY_CHAIN_OF,
        AxiomType.SUBCLASS_OF,
        AxiomType.TRANSITIVE_OBJECT_PROPERTY
    };

    /**
     * The {@link ELAxiomsTranslator} that obtain the double rules of this
     * {@link OntologyTranslator}.
     */
    private final ELDoubleAxiomsTranslator doubleAxiomsTranslator;

    /**
     * The {@link ELAxiomsTranslator} that obtain the original rules of this
     * {@link OntologyTranslator}.
     */
    private final ELOriginalAxiomsTranslator originalAxiomsTranslator;

    /**
     * The {@link ELOntologyReduction reduction} of the ontology that this
     * translation refer.
     */
    private ELOntologyReduction reducedOntology;

    /**
     * Constructs an {@link OntologyTranslator} of a given OWL 2 EL ontology.
     *
     * @param ontology an OWL 2 EL ontology.
     * @param v
     * @param dedutiveDatabase
     * @param ignoreAllUnsupportedAxioms
     * @param ignoredUnsupportedAxioms
     * @throws UnsupportedAxiomsException if {@code ontology} contains some
     * axioms of unsupported types.
     */
    public ELOntologyTranslator(OWLOntology ontology, Vocabulary v, DeductiveDatabase dedutiveDatabase, boolean ignoreAllUnsupportedAxioms, Set<AxiomType<?>> ignoredUnsupportedAxioms)
            throws UnsupportedAxiomsException {
        super(ontology, v, dedutiveDatabase, ignoreAllUnsupportedAxioms, ignoredUnsupportedAxioms);

        originalAxiomsTranslator = new ELOriginalAxiomsTranslator(v);
        doubleAxiomsTranslator = new ELDoubleAxiomsTranslator(v);
    }

    @Override
    public Profile getProfile() {
        return Profile.OWL2_EL;
    }

    @Override
    public AxiomType<?>[] getSupportedAxioms() {
        return SUPPORTED_AXIOM_TYPES;
    }

    @Override
    public boolean requiresDoubling() {
        return reducedOntology.hasDisjunctions();
    }

    private void prepareUpdate() throws UnsupportedAxiomsException {
        RuntimesLogger.start("[OWL EL] ontology normalization");
        reducedOntology = new StaticELOntologyReduction(ontology, vocabulary);
        RuntimesLogger.stop("[OWL EL] ontology normalization", "loading");
    }

    /**
     * Translate the ontology that this ontology refers with a given
     * {@link ELAxiomsTranslator}. The resulting translation is added to
     * {@code rules}.
     *
     * @param axiomTranslator the {@link ELAxiomsTranslator} that will be used.
     */
    private void translate(ELAxiomsTranslator axiomTranslator) {
        for (final OWLSubPropertyChainOfAxiom axiom : reducedOntology.chainSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }
        for (final OWLClassAssertionAxiom assertion : reducedOntology.conceptAssertions()) {
            translation.addAll(axiomTranslator.translation(assertion));
        }
        for (final OWLSubClassOfAxiom axiom : reducedOntology.conceptSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }
        for (final OWLDataPropertyAssertionAxiom assertion : reducedOntology.dataAssertion()) {
            translation.addAll(axiomTranslator.translation(assertion));
        }
        for (final OWLSubDataPropertyOfAxiom axiom : reducedOntology.dataSubsuptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }
        for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.roleSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }
        for (final OWLObjectPropertyAssertionAxiom assertion : reducedOntology.roleAssertions()) {
            translation.addAll(axiomTranslator.translation(assertion));
        }
        for (final OWLSubObjectPropertyOfAxiom axiom : reducedOntology.roleSubsumptions()) {
            translation.addAll(axiomTranslator.translation(axiom));
        }
    }

    @Override
    public void updateTranslation() throws UnsupportedAxiomsException {
        evaluateOntologySupport();

        prepareUpdate();
        translation.clear();
        RuntimesLogger.start("[OWL EL] ontology translation");
        translate(originalAxiomsTranslator);
        if (reducedOntology.hasDisjunctions()) {
            translate(doubleAxiomsTranslator);
        }
        RuntimesLogger.stop("[OWL EL] ontology translation", "loading");
    }

}

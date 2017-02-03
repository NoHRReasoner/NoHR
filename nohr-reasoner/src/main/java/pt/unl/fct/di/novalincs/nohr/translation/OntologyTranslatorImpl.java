/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.Objects;

import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseProgram;

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

/**
 * The implementation of the {@link OntologyTranslator} <i>abstraction</i> (see {@link
 * <a href="http://www.oodesign.com/bridge-pattern.html">Bridge Pattern</a>},
 * and note that here {@link OntologyTranslator} is simultaneously the
 * <i>abstraction</i> interface and the <i>implementor</i>
 * interface). The <i>concrete implementor</i> is chosen according to the
 * ontology preferred {@link Profile OWL profile}, and changed appropriately in
 * each {@link #updateTranslation() translation}.
 *
 * @author Nuno Costa
 */
public abstract class OntologyTranslatorImpl implements OntologyTranslator {

    /**
     * The {@link DeductiveDatabase} where the translation is maintained.
     */
    private final DeductiveDatabase dedutiveDatabase;

    /**
     * The {@link DatabaseProgram program} where the translation is maintained.
     */
    protected final DatabaseProgram translation;

    /**
     * The translated ontology.
     */
    protected final OWLOntology ontology;
    
    protected final Vocabulary vocabulary;

    /**
     * Constructs an {@link OntologyTranslator} for a given
     * {@link OWLOntology ontology}.
     *
     * @param ontology the ontology that will be translated.
     * @param vocabulary
     * @param dedutiveDatabase the {@link DeductiveDatabase} where the
     * translation will be maintained.
     * @param profile the {@link Profile profile} that this
     * {@link OntologyTranslator} will handle. If none is specified (i.e. if it
     * is {@code null} ), the preferred ontology's profile will be chosen.
     * @param ontologyTranlatorConfiguration
     * @throws OWLProfilesViolationsException if {@code profile!=null} and the
     * ontology isn't in {@code profile}, or {@code profile==null} and the
     * ontology isn't in any supported profile.
     * @throws UnsupportedAxiomsException if {@code ontology} has some axioms of
     * an unsupported type.
     */
    protected OntologyTranslatorImpl(
            OWLOntology ontology,
            Vocabulary vocabulary,
            DeductiveDatabase dedutiveDatabase)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(vocabulary);
        Objects.requireNonNull(dedutiveDatabase);
        
        this.ontology = ontology;
        this.vocabulary = vocabulary;
        this.dedutiveDatabase = dedutiveDatabase;
        
        translation = dedutiveDatabase.createProgram();
    }
    
    @Override
    public void clear() {
        translation.clear();
    }
    
    public static final OntologyTranslator createOntologyTranslator(
            OntologyTranslatorConfiguration configuration,
            OWLOntology ontology,
            Vocabulary vocabulary,
            DeductiveDatabase dedutiveDatabase)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        
        return createOntologyTranslator(configuration, ontology, vocabulary, dedutiveDatabase, null);
    }

    /**
     * Create an {@link OntologyTranslator} of a given ontology that can handle
     * this profile.
     *
     * @param configuration
     * @param ontology the ontology whose
     * {@link OntologyTranslator ontology translation} will be created.
     * @param vocabulary
     * @param dedutiveDatabase the {@link DeductiveDatabase dedutive database}
     * where the ontology translation will be loaded.
     * @param profile
     * @return the {@link OntologyTranslator ontology translation} of
     * {@code ontology} for this profile.
     * @throws UnsupportedAxiomsException if {@code ontology} has some axiom of
     * a type that isn't supported in this profile.
     * @throws OWLProfilesViolationsException if {@code ontology} isn't in this
     * profile.
     */
    public static final OntologyTranslator createOntologyTranslator(
            OntologyTranslatorConfiguration configuration,
            OWLOntology ontology,
            Vocabulary vocabulary,
            DeductiveDatabase dedutiveDatabase,
            Profile profile)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        
        if (profile == null) {
            profile = Profile.getProfile(ontology);
        }
        
        switch (profile) {
            case OWL2_EL:
                if (configuration.getDLInferenceEngineEL()) {
                    return new DLOntologyTranslator(ontology, vocabulary, dedutiveDatabase, getDLInferenceEngine(configuration));
                } else {
                    return new ELOntologyTranslator(ontology, vocabulary, dedutiveDatabase);
                }
            case OWL2_QL:
                if (configuration.getDLInferenceEngineQL()) {
                    return new DLOntologyTranslator(ontology, vocabulary, dedutiveDatabase, getDLInferenceEngine(configuration));
                } else {
                    return new QLOntologyTranslator(ontology, vocabulary, dedutiveDatabase);
                }
            case NOHR_DL:
                return new DLOntologyTranslator(ontology, vocabulary, dedutiveDatabase, getDLInferenceEngine(configuration));
            default:
                throw new OWLProfilesViolationsException();
        }
    }
    
    @Override
    public DeductiveDatabase getDedutiveDatabase() {
        return dedutiveDatabase;
    }
    
    private static InferenceEngine getDLInferenceEngine(OntologyTranslatorConfiguration configuration) {
        if (configuration.getDLInferenceEngine() == DLMode.KONCLUDE) {
            return new KoncludeInferenceEngine(configuration.getKoncludeBinary().getAbsolutePath());
        } else {
            return new HermitInferenceEngine();
        }
    }
    
    @Override
    public OWLOntology getOntology() {
        return ontology;
    }
    
    public boolean isSuitable(OWLOntology ontology) {
        final Profile profile = Profile.getProfile(ontology);
        
        return (getProfile() == Profile.NOHR_DL || getProfile() == profile);
    }
}

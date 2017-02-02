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
public class OntologyTranslatorImpl implements OntologyTranslator {

    private final Vocabulary v;

    /**
     * The {@link Profile profile} that this {@link OntologyTranslator} will
     * handle. If none is specified (i.e. if it is {@code null} ), the preferred
     * ontology's profile will be chosen.
     */
    private final Profile profile;

    /**
     * The <i>concrete implementor</i> of this <i>abstraction</i>.
     */
    private OntologyTranslator implementor;

    private final OntologyTranslatorConfiguration ontologyTranlatorConfiguration;

    /**
     * Constructs an {@link OntologyTranslator} for a given
     * {@link OWLOntology ontology}.
     *
     * @param ontology the ontology that will be translated.
     * @param v
     * @param dedutiveDatabaseManager the {@link DeductiveDatabase} where the
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
    public OntologyTranslatorImpl(OntologyTranslatorConfiguration ontologyTranlatorConfiguration, OWLOntology ontology, Vocabulary v, DeductiveDatabase dedutiveDatabaseManager, Profile profile)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {

        Objects.requireNonNull(ontology);
        Objects.requireNonNull(v);
        Objects.requireNonNull(dedutiveDatabaseManager);

        this.v = v;

        if (profile == null) {
            this.profile = Profile.getProfile(ontology);
        } else {
            this.profile = profile;
        }

        this.ontologyTranlatorConfiguration = ontologyTranlatorConfiguration;

        implementor = createOntologyTranslator(ontology, v, dedutiveDatabaseManager);
        ontologyTranlatorConfiguration.handledChanges();
    }

    @Override
    public void clear() {
        implementor.clear();
    }

    /**
     * Create an {@link OntologyTranslator} of a given ontology that can handle
     * this profile.
     *
     * @param ontology the ontology whose
     * {@link OntologyTranslator ontology translation} will be created.
     * @param v
     * @param dedutiveDatabase the {@link DeductiveDatabase dedutive database}
     * where the ontology translation will be loaded.
     * @return the {@link OntologyTranslator ontology translation} of
     * {@code ontology} for this profile.
     * @throws UnsupportedAxiomsException if {@code ontology} has some axiom of
     * a type that isn't supported in this profile.
     * @throws OWLProfilesViolationsException if {@code ontology} isn't in this
     * profile.
     */
    public final OntologyTranslator createOntologyTranslator(OWLOntology ontology,
            Vocabulary v,
            DeductiveDatabase dedutiveDatabase)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        // final OWLProfileReport report = owlProfile().checkOntology(ontology);
        // final String ignoreUnsupported = System.getenv("IGNORE_UNSUPPORTED");
        // if (!report.isInProfile() && (ignoreUnsupported == null || !ignoreUnsupported.equals("true")))
        // throw new OWLProfilesViolationsException(report);

        switch (profile) {
            case OWL2_EL:
                if (ontologyTranlatorConfiguration.getDLInferenceEngineEL()) {
                    return new DLOntologyTranslator(ontology, v, dedutiveDatabase, getDLInferenceEngine());
                } else {
                    return new ELOntologyTranslator(ontology, v, dedutiveDatabase);
                }
            case OWL2_QL:
                if (ontologyTranlatorConfiguration.getDLInferenceEngineQL()) {
                    return new DLOntologyTranslator(ontology, v, dedutiveDatabase, getDLInferenceEngine());
                } else {
                    return new QLOntologyTranslator(ontology, v, dedutiveDatabase);
                }
            case NOHR_DL:
                return new DLOntologyTranslator(ontology, v, dedutiveDatabase, getDLInferenceEngine());
            default:
                throw new OWLProfilesViolationsException();
        }
    }

    @Override
    public DeductiveDatabase getDedutiveDatabase() {
        return implementor.getDedutiveDatabase();
    }

    private InferenceEngine getDLInferenceEngine() {
        if (ontologyTranlatorConfiguration.getDLInferenceEngine() == DLMode.KONCLUDE) {
            return new KoncludeInferenceEngine(ontologyTranlatorConfiguration.getKoncludeBinary().getAbsolutePath());
        } else {
            return new HermitInferenceEngine();
        }
    }

    @Override
    public OWLOntology getOntology() {
        return implementor.getOntology();
    }

    @Override
    public Profile getProfile() {
        return implementor.getProfile();
    }

    @Override
    public boolean hasDisjunctions() {
        return implementor.hasDisjunctions();
    }

    @Override
    public void updateTranslation() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        Profile newProfile = profile;

        if (newProfile == null) {
            newProfile = Profile.getProfile(getOntology());
        }

        final Profile implementorProfile = getProfile();

        if ((implementorProfile != Profile.NOHR_DL && newProfile != implementorProfile) || ontologyTranlatorConfiguration.hasChanged()) {
            implementor.clear();
            implementor = createOntologyTranslator(getOntology(), v, getDedutiveDatabase());
            ontologyTranlatorConfiguration.handledChanges();
        }

        implementor.updateTranslation();
    }

}

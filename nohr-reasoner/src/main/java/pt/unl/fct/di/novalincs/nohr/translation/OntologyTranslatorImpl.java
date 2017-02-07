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
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;
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
    private final DeductiveDatabase deductiveDatabase;

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
     * @param deductiveDatabase the {@link DeductiveDatabase} where the
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
            DeductiveDatabase deductiveDatabase)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {

        Objects.requireNonNull(ontology);
        Objects.requireNonNull(vocabulary);
        Objects.requireNonNull(deductiveDatabase);

        this.ontology = ontology;
        this.vocabulary = vocabulary;
        this.deductiveDatabase = deductiveDatabase;

        translation = deductiveDatabase.createProgram();
    }

    @Override
    public void clear() {
        translation.clear();
    }

    @Override
    public DeductiveDatabase getDedutiveDatabase() {
        return deductiveDatabase;
    }

    @Override
    public OWLOntology getOntology() {
        return ontology;
    }

}

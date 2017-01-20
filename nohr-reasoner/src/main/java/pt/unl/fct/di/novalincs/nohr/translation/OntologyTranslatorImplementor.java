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
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseProgram;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

/**
 * A <i>concrete implementor</i> of {@link OntologyTranslator} (see {@link
 * <a href="http://www.oodesign.com/bridge-pattern.html">Bridge Pattern</a>},
 * and note that here {@link OntologyTranslator} is simultaneously the
 * <i>abstraction</i> interface and the <i>implementor</i> interface), for a
 * specific {@link Profile}.
 *
 * @author Nuno Costa
 */
public abstract class OntologyTranslatorImplementor implements OntologyTranslator {

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
     * Constructs a {@link OntologyTranslatorImplementor}, appropriately
     * initializing its state.
     *
     * @param ontology the ontology to translate.
     * @param vocabulary
     * @param dedutiveDatabase the {@link DeductiveDatabase} where the ontology
     * translation will be mantained.
     */
    public OntologyTranslatorImplementor(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase dedutiveDatabase) {
        this.ontology = ontology;
        this.vocabulary = vocabulary;
        this.dedutiveDatabase = dedutiveDatabase;

        translation = dedutiveDatabase.createProgram();
    }

    @Override
    public void clear() {
        translation.clear();
    }

    @Override
    public DeductiveDatabase getDedutiveDatabase() {
        return dedutiveDatabase;
    }

    @Override
    public OWLOntology getOntology() {
        return ontology;
    }

    @Override
    public abstract void updateTranslation() throws UnsupportedAxiomsException;

}

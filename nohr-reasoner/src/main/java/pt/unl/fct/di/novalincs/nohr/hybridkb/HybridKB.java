/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import com.declarativa.interprolog.util.IPPrologError;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.TruthValue;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

/**
 * Represents an <i> MKNF Hybrid Knowledge Base </i> (see {@link
 * <a href="http://knoesis.wright.edu/pascal/resources/publications/mknftheo.pdf">Local
 * Closed World Reasoning with Description Logics under the Well-Founded
 * Semantics</a>}) that can be queried.
 *
 * @author Nuno Costa
 */
public interface HybridKB {

    /**
     * Obtains all answers to a given query.
     *
     * @param query the query.
     * @throws OWLProfilesViolationsException if the <i>ontology</i> component
     * isn't in any supported profile.
     * @throws UnsupportedAxiomsException if the <i>ontology</i> has some axiom
     * of an unsupported type.
     * @return the list of all answers {@code query}.
     */
    List<Answer> allAnswers(Query query) throws OWLProfilesViolationsException, UnsupportedAxiomsException, IPPrologError;

    /**
     * Obtains all answers to a given query.
     *
     * @param query the query.
     * @param trueAnswers specifies whether to obtain
     * {@link TruthValue#TRUE TRUE true} answers.
     * @param undefinedAnswers specifies whether to obtain
     * {@link TruthValue#UNDEFINED undefined} answers.
     * @param inconsistentAnswers specifies whether to obtain
     * {@link TruthValue#INCONSISTENT inconsistent} answers.
     * @throws OWLProfilesViolationsException if the <i>ontology</i> component
     * isn't in any supported profile.
     * @throws UnsupportedAxiomsException if the <i>ontology</i> has some axiom
     * of an unsupported type.
     * @return the list of all answers {@code query} valued according to the
     * {@code trueAnswers}, {@code undefinedAnswers} and
     * {@code inconsistentAnswers} flags.
     */
    List<Answer> allAnswers(Query query, boolean trueAnswers, boolean undefinedAnswers, boolean inconsistentAnswers)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException, IPPrologError;

    /**
     * Release all the resources reclaimed by this {@link HybridKB}.
     */
    void dispose();

    /**
     * Returns the <i>ontology</i> component of this {@link HybridKB <i>Hybrid
     * MKNF knowledge base</i>}.
     *
     * @return the <i>ontology</i> component of this {@link HybridKB <i>Hybrid
     * MKNF knowledge base</i>}.
     */
    OWLOntology getOntology();

    /**
     * Returns the <i>program</i> component of this {@link HybridKB <i>Hybrid
     * MKNF knowledge base</i>}.
     *
     * @return returns the <i>program</i> component of this {@link HybridKB
     * <i>Hybrid MKNF knowledge base</i>}
     */
    Program getProgram();

    /**
     * Returns the <i>database mappings</i> component of this {@link HybridKB <i>Hybrid
     * MKNF knowledge base</i>}.
     *
     * @return returns the <i>database mappings</i> component of this {@link HybridKB
     * <i>Hybrid MKNF knowledge base</i>}
     */
    
    
    DBMappingSet getDBMappings();
    
    /**
     * Returns the {@link Vocabulary} that this {@link HybridKB} applies.
     *
     * @return the {@link Vocabulary} that this {@link HybridKB} applies.
     */
    Vocabulary getVocabulary();

    /**
     * Checks if there is some answer to a given query.
     *
     * @param query the query.
     * @return true iff there is at least one answer to {@code query}.
     * @throws OWLProfilesViolationsException if the <i>ontology</i> component
     * isn't in any supported profile.
     * @throws UnsupportedAxiomsException if the <i>ontology</i> has some axiom
     * of an unsupported type.
     */
    boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException;

    boolean hasDisjunctions();

    /**
     * Obtains one answers to a given query.
     *
     * @param query the query.
     * @return one answer to {@code query}.
     * @throws OWLProfilesViolationsException if the <i>ontology</i> component
     * isn't in any supported profile.
     * @throws UnsupportedAxiomsException if the <i>ontology</i> has some axiom
     * of an unsupported type.
     */
    Answer oneAnswer(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, UnsupportedAxiomsException;

    /**
     * Obtains one answers to a given query.
     *
     * @param query the query.
     * @param trueAnswers specifies whether to obtain a
     * {@link TruthValue#TRUE true} answers.
     * @param undefinedAnswers specifies whether to obtain a
     * {@link TruthValue#UNDEFINED undefined} answers.
     * @param inconsistentAnswers specifies whether to obtain a
     * {@link TruthValue#INCONSISTENT inconsistent} answers.
     * @return one answer to {@code query} valued according to the
     * {@code trueAnswers}, {@code undefinedAnswers} and
     * {@code inconsistentAnswers} flags.
     * @throws OWLProfilesViolationsException if the <i>ontology</i> component
     * isn't in any supported profile.
     * @throws UnsupportedAxiomsException if the <i>ontology</i> has some axiom
     * of an unsupported type.
     */
    Answer oneAnswer(Query query, boolean trueAnswers, boolean undefinedAnswers, boolean inconsistentAnswers)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException;


}

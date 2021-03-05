/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.deductivedb;

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
import java.util.Map;

import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.TruthValue;

/**
 * Maintains a set of {@link DatabaseProgram programs} and can answer queries to
 * the union of that programs (i.e. the logic program formed by all the
 * {@link Rule rules} of those programs).
 *
 * @author Nuno Costa
 */
public interface DeductiveDatabase {

    /**
     * Deterministically obtains one answer to a given query, based on the
     * loaded {@link DatabaseProgram programs}.
     *
     * @param query the query that will be answered.
     * @return one answer to {@code query}. @ if {@link DeductiveDatabase}
     * needed to read or write some file and was unsuccessful.
     */
    Answer answer(Query query);

    /**
     * Obtains one answer to a given query, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be answered.
     * @param trueAnswers specifies whether the answer valuation will be
     * {@link TruthValue#TRUE true}. The answer will have a
     * {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a
     * {@link TruthValue#UNDEFINED} valuation if {@code trueAnswers == false};
     * and any of the two if {@code trueAnswers == null}.
     * @return one answer to {@code query} valuated according to
     * {@code two answers}. @ if {@link DeductiveDatabase} needed to read or
     * write some file and was unsuccessful.
     */
    Answer answer(Query query, Boolean trueAnswers);

    /**
     * Obtains the answers to a given query, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be answered.
     * @return one {@link Iterable} of all the answers to {@code query}. @ if
     * {@link DeductiveDatabase} needed to read or write some file and was
     * unsuccessful.
     */
    Iterable<Answer> answers(Query query);

    void commit();
    /**
     * Obtains the answers to a given query, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be answered.
     * @param trueAnswers specifies whether the answers valuation will be
     * {@link TruthValue#TRUE true}. The answers will have a
     * {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a
     * {@link TruthValue#UNDEFINED} valuation if {@code trueAnswers == false};
     * and any of the two if {@code trueAnswers == null}.
     * @return one {@link Iterable} of all the answers to {@code query}. @ if
     * {@link DeductiveDatabase} needed to read or write some file and was
     * unsuccessful.
     */
    Iterable<Answer> answers(Query query, Boolean trueAnswers);

    /**
     * Obtains the valuation of each substitution corresponding to an
     * {@link Answer answer} to given {@link Query query}, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be answered.
     * @return the {@link Map mapping} between each substitution corresponding
     * to an {@link Answer answer} to {@code query} - represented by the list of
     * terms to which each {@code query}'s free variable is mapped, in the same
     * order that those variables appear - and the {@link TruthValue valuation}
     * of that answer. @ if {@link DeductiveDatabase} needed to read or write
     * some file and was unsuccessful.
     */
    Map<List<Term>, TruthValue> answersValuations(Query query) throws IPPrologError;

    /**
     * Obtains the valuation of each substitution corresponding to an
     * {@link Answer answer} to given {@link Query query}, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be answered.
     * @param trueAnswers specifies whether the answers valuations will be
     * {@link TruthValue#TRUE true}. The answers will have a
     * {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a
     * {@link TruthValue#UNDEFINED undefined} valuation if
     * {@code trueAnswers == false}; and any of the two if
     * {@code trueAnswers == null}.
     * @return the {@link Map mapping} between each substitution corresponding
     * to an {@link Answer answer} to {@code query} - represented by the list of
     * terms to which each {@code query}'s free variable is mapped, in the same
     * order that those variables appear - and the {@link TruthValue valuation}
     * of that answer. @ if {@link DeductiveDatabase} needed to read or write
     * some file and was unsuccessful.
     */
    Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) throws IPPrologError;

    /**
     * Creates and loads a new {@link DatabaseProgram program}.
     *
     * @return a new {@link DatabaseProgram program}.
     */
    DatabaseProgram createProgram();
    
    
    /**
     * Creates and loads a new {@link DatabaseDBMappings dbMappings}.
     *
     * @return a new {@link DatabaseDBMappings dbMappings}.
     */
    DatabaseDBMappings createDBMappings();
    

    /**
     * Dispose all {@link DatabaseProgram programs} and release all the
     * reclaimed resources.
     */
    void dispose();

    /**
     * Checks if there is some answer to given query, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be checked for answers.
     * @return true iff there is some answer to {@code query}. @ if
     * {@link DeductiveDatabase} needed to read or write some file and was
     * unsuccessful.
     */
    boolean hasAnswers(Query query);

    /**
     * Checks if there is some answer to given query, based on the loaded
     * {@link DatabaseProgram programs}.
     *
     * @param query the query that will be checked for answers.
     * @param trueAnswers specifies whether the checked answers valuation will
     * be {@link TruthValue#TRUE true}. The checked answers will have a
     * {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a
     * {@link TruthValue#UNDEFINED} valuation if {@code trueAnswers == false};
     * and any of the two if {@code trueAnswers == null}.
     * @return true iff there is some answer to {@code query}. @ if
     * {@link DeductiveDatabase} needed to read or write some file and was
     * unsuccessful.
     */
    boolean hasAnswers(Query query, Boolean trueAnswers);

    /**
     * Check if the {@link Query queries} will be answered according to the Well
     * Founded Semantic.
     *
     * @return true if the {@link Query queries} will be answered according to
     * the Well Founded Semantic.
     */
    boolean hasWFS();

}

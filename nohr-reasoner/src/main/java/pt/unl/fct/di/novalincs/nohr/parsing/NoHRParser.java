/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.parsing;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

/**
 * A parser that constructs {@link Rule rules} and {@link Query queries} from
 * expressions in the following language (in
 * {@link <a href="https://en.wikipedia.org/wiki/Wirth_syntax_notation"> Wirth
 * syntax notation</a>}), where the symbols {@code symbol} and {@code id} are
 * {@link TokenType tokens}: <br>
 * <br>
 * <code>
 * program = {rule "."}. <br>
 * rule = atom [":-" literals ]. <br>
 * query = literals. <br>
 * literals = literal {"," literal}. <br>
 * literal = atom | "not " atom. <br>
 * atom = symbol ["(" term {"," term} ")"]. <br>
 * term = variable | symbol. <br>
 * variable = "?" id. <br>
 * </code>
 * 
 * In addition, parser can parse NoHR mappings, according to our own XML specification.
 *
 * @author Nuno Costa
 */
public interface NoHRParser {

    public Vocabulary getVocabulary();

    /**
     * Parses a given {@link File file} and returns the corresponding
     * {@link Program program}, if the file represents a program.
     *
     * @param file the file to be parsed.
     * @return the {@link Program program} that {@code file} represents.
     * @throws ParseException {@code file} violates the queries syntax.
     * @throws FileNotFoundException
     */
    public Program parseProgram(File file) throws ParseException, FileNotFoundException;

    public Program parseProgram(File file, Program program) throws ParseException, FileNotFoundException;

    /**
     * Parses a given string and returns the corresponding {@link Query query},
     * if the string represents a query.
     *
     * @param str the string to be parsed.
     * @return the {@link Query query} that {@code str} represents.
     * @throws ParseException {@code str} violates the queries syntax.
     */
    public Query parseQuery(String str) throws ParseException;

    /**
     * Parses a given string and returns the corresponding {@link Query query},
     * if represents some rule.
     *
     * @param str the string to be parsed.
     * @return the {@link Query query} that {@code str} represents.
     * @throws ParseException if {@code str} violates the role syntax.
     */
    public Rule parseRule(String str) throws ParseException;

    /**
     * Parses a given string and returns the corresponding {@link DBMapping dbMapping},
     * if represents some DBMapping.
     *
     * @param str the string to be parsed.
     * @return the {@link DBMapping dbMapping} that {@code str} represents.
     * @throws ParseException if {@code str} violates the DBMapping syntax.
     */
    public DBMapping parseDBMapping(String str) throws ParseException;
    
    public void parseDBMappingSet(File file, DBMappingSet dbMappingSet, List<ODBCDriver> list) throws ParseException, IOException;
    
    public void setVocabulary(Vocabulary vocabulary);

	


}

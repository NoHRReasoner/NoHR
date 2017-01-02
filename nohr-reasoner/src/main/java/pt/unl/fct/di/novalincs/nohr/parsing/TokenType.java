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
import java.util.regex.Pattern;
import pt.unl.fct.di.novalincs.nohr.utils.PrologSyntax;

/**
 * The types of the {@link
 * <a href="https://en.wikipedia.org/wiki/Lexical_analysis#Token">tokens</a>}
 * recognized by {@link NoHRScanner}.
 *
 * @author Nuno Costa
 */
public enum TokenType {

    /**
     * The comma.
     */
    COMMA(",", true), /**
     * Any string started by a capital letter followed by letters, numbers or
     * underscores.
     */
    DOT("\\.(\\n|\\r)*", "."), /**
     * The dot (at the end of the line)
     */
    // ^note that the java Scanner doesn't consume the end of line.
    ID("[A-Z]\\w*+", "Id"), /**
     * The Prolog operator {@literal :-}.
     */
    IF(":-", true), /**
     * The left bracket.
     */
    L_BRACK("\\[", "["), /**
     * The left parenthesis.
     */
    L_PAREN("\\(", "("), /**
     * The {@literal not } operator.
     */
    NOT("not\\s", "not"), /**
     * The question mark.
     */
    QUESTION_MARK("\\?", "?"), /**
     * The right bracket.
     */
    R_BRACK("\\]", "]"), /**
     * The right parenthesis.
     */
    R_PAREN("\\)", ")"), /**
     * Any string that doesn't contain any unescaped (i.e. not preceded by a
     * slash) slash, comma, parenthesis, bracket, question mark, nor any of the
     * strings "{@literal :-}" or "{@literal not}".
     */
    //SYMBOL("([^'\\s\\.,\\[(?\\])\\\\]|(?!-):|(\\\\\\\\)*\\\\.|\\.(?!\\n|\\r))++|'[^']+?'", "Symbol"),
    PROLOG_PREFIX("#"),
    PIPE("\\|", "|", true),
    PROLOG_PREDICATE_SYMBOL(PrologSyntax.PREDICATES_REGEX, "Prolog Predicate"),
    PROLOG_BINARY_OPERATOR(PrologSyntax.OPERATORS_REGEX, "Prolog Operator", true),
    CONSTANT("([A-Za-z][A-Za-z0-9_]*|(\\-)?\\d+(\\.\\d+)?|'([^']|'')+')", "Constant"),
    NUMERIC_CONSTANT("(\\-)?\\d+(\\.\\d+)?", "Numeric Constant"),
    NON_NUMERIC_CONSTANT("([A-Za-z][A-Za-z0-9_]*|'([^']|'')+')", "Non-Numeric Constant"),
    FUNCTOR("([A-Za-z][A-Za-z0-9_]*|'([^']|'')+')(?=\\()", "Predicate"),
    VARIABLE("[A-Z][A-Za-z0-9_]*", "Variable");

    /**
     * The regular expression that matches the tokens of this {@link TokenType}.
     */
    private final Pattern pattern;

    /**
     * An user friendly representation of the token.
     */
    private final String representation;

    /**
     * Constructs a {@link TokenType} corresponding to a given regular language.
     *
     * @param regex the regular expression that matches the tokens of that type.
     */
    TokenType(String regex) {
        this(regex, regex, false);
    }

    /**
     * Constructs a {@link TokenType} corresponding to a given regular language.
     *
     * @param regex the regular expression that matches the tokens of this type.
     * @param separator whether the tokens of this type can be surrounded by
     * space characters.
     */
    TokenType(String regex, boolean separator) {
        this(regex, regex, separator);
    }

    /**
     * Constructs a {@link TokenType} corresponding to a given regular language.
     *
     * @param regex the regular expression that matches the tokens of this type.
     * @param representation an user friendly representation of the token.
     */
    TokenType(String regex, String representation) {
        this(regex, representation, false);
    }

    /**
     * Constructs a {@link TokenType} corresponding to a given regular language.
     *
     * @param regex the regular expression that matches the tokens of this type.
     * @param separator whether the tokens of this type can be surrounded by
     * space characters.
     * @param representation an user friendly representation of the token.
     */
    TokenType(String regex, String representation, boolean separator) {
        regex = separator ? "\\s*" + regex + "\\s*" : regex;
        pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
        this.representation = representation;
    }

    /**
     * Returns the regular expression that matches the tokens of this
     * {@link TokenType}.
     *
     * @return the regular expression that matches the tokens of this
     * {@link TokenType}.
     */
    public Pattern pattern() {
        return pattern;
    }

    /**
     * Returns an user friendly representation of the token.
     *
     * @return user friendly representation of the token.
     */
    @Override
    public String toString() {
        return representation;
    }

}

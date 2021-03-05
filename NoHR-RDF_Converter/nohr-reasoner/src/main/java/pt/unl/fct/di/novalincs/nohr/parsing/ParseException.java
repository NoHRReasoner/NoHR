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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * An exception thrown when a {@link NoHRParser} failed to recognize a {@link Query} or {@link Rule}.
 *
 * @author Nuno Costa
 */
public class ParseException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 222524131072855061L;

	/** The expected tokens. */
	private final List<TokenType> expectedTokens;
	/** The begin of the next token. */
	private final int begin;
	/** The expected end of the expected token, if it was a fixed length, or the end of the parsed string, otherwise. */
	private final int end;

	/** The line where the syntax error was found. */
	private final int line;

	/**
	 * Constructs a {@link ParseException}.
	 *
	 * @param line
	 *            the line where the syntax error was found.
	 * @param begin
	 *            the begin of the next token.
	 * @param end
	 *            the expected end of the expected token, if it was a fixed length, or the end of the parsed string, otherwise
	 * @param expectedTokens
	 *            the expected tokens.
	 */
	public ParseException(int line, int begin, int end, TokenType... expectedTokens) {
		this.line = line;
		this.begin = begin;
		this.end = end;
		this.expectedTokens = Arrays.asList(expectedTokens);
	}

	/**
	 * Constructs a {@link ParseException}.
	 *
	 * @param line
	 *            the line where the syntax error was found.
	 * @param begin
	 *            the begin of the next token.
	 * @param end
	 *            the expected end of the expected token, if it was a fixed length, or the end of the parsed string, otherwise
	 * @param expectedToken
	 *            the expected token.
	 */
	public ParseException(int line, int begin, int end, TokenType expectedToken) {
		this.line = line;
		this.begin = begin;
		this.end = end;
		expectedTokens = Collections.singletonList(expectedToken);
	}

	/**
	 * Constructs a {@link ParseException}.
	 *
	 * @param begin
	 *            the begin of the next token.
	 * @param end
	 *            the expected end of the expected token, if it was a fixed length, or the end of the parsed string, otherwise
	 * @param expectedTokens
	 *            the expected tokens.
	 */
	public ParseException(int begin, int end, TokenType... expectedTokens) {
		this(0, begin, end, expectedTokens);
	}

	/**
	 * The begin of the next token.
	 *
	 * @return he begin of the next token.
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * The expected end of the expected token, if it was a fixed length, or the end of the parsed string, otherwise.
	 *
	 * @return the expected end of the expected token, if it was a fixed length, or the end of the parsed string, otherwise.
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * The expected tokens.
	 *
	 * @return the expected tokens.
	 */
	public List<TokenType> getExpectedTokens() {
		return expectedTokens;
	}

	/**
	 * Returns the line where the syntax error was found.
	 *
	 * @return the line where the syntax error was found.
	 */
	public int getLine() {
		return line;
	}

}

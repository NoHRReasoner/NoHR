/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A basic {@link <a href="https://en.wikipedia.org/wiki/Lexical_analysis#Scanner">Scanner</a>} implemented on top of {@link Scanner}. The recognized
 * tokens are those that are specified by {@link TokenType}.
 *
 * @author Nuno Costa
 */
public class NoHRScanner {

	private int position;

	/** The underlying {@link Scanner} */
	private final Scanner scanner;

	/** The length of the scanned {@link String}. */
	private final int length;

	/**
	 * Constructs a {@link NoHRScanner} for a given {@link String string}.
	 *
	 * @param the
	 *            string that will be scanned.
	 */
	public NoHRScanner(String str) {
		scanner = new Scanner(str);
		length = str.length();
	}

	/**
	 * The final last position of the scanned string.
	 *
	 * @return the last position of the scanned string.
	 */
	public int end() {
		return length - 1;
	}

	public boolean ended() {
		return !scanner.hasNext();
	}

	/**
	 * Try to consume a token of a given {@link TokenType type}. If no token of the given type is found, maintains the current position. Then value of
	 * the consumed token is obtained calling {@link #token()}.
	 *
	 * @param type
	 *            the type of the token to consume.
	 * @return true iff a token of the type {@code type} was found.
	 */
	public boolean next(TokenType type) {
		try {
			scanner.skip(type.pattern());
			position = scanner.match().end();
		} catch (final NoSuchElementException e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the current position of this {@link NoHRScanner}.
	 *
	 * @return he current position of this {@link NoHRScanner}.
	 */
	public int position() {
		return position;
	}

	/**
	 * Returns the last consumed token.
	 *
	 * @return the last consumed token.
	 */
	public String token() {
		return scanner.match().group();
	}
}

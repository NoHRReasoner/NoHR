/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A basic {@link <a href="https://en.wikipedia.org/wiki/Lexical_analysis#Scanner">Scanner</a>} implemented on top of {@link Scanner}. The recognized
 * tokens are those that are specified by {@link TokenType}.
 *
 * @author Nuno Costa
 */
public class NoHRScanner {

	/** The position of the scanner in the current line. */
	private int position;

	/** The current line. */
	private int line;

	/** The underlying {@link Scanner} */
	private final Scanner scanner;

	/** The length of the scanned {@link String}. */
	private final int length;

	/**
	 * Constructs a {@link NoHRScanner} for a given {@link File file}.
	 *
	 * @param file
	 *            the file that will be scanned.
	 * @throws FileNotFoundException
	 */
	public NoHRScanner(File file) throws FileNotFoundException {
		scanner = new Scanner(file);
		length = 0;
		position = 0;
		line = 1;
	}

	/**
	 * Constructs a {@link NoHRScanner} for a given {@link String string}.
	 *
	 * @param str
	 *            the string that will be scanned.
	 */
	public NoHRScanner(String str) {
		scanner = new Scanner(str);
		length = str.length();
		position = 0;
	}

	/**
	 * Returns true if this scanner has another token in its input. This method may block while waiting for input to scan.
	 *
	 * @return true if and only if this scanner has another token.
	 */
	public boolean hasNext() {
		return scanner.hasNext();
	}

	/**
	 * Returns true if there is another line in the input of this scanner. This method may block while waiting for input.
	 *
	 * @return true if and only if this scanner has another line of input.
	 */
	public boolean hasNextLine() {
		return scanner.hasNextLine();
	}

	/**
	 * The the length of the scanned string.
	 *
	 * @return the length of the scanned string.
	 */
	public int length() {
		return length;
	}

	/**
	 * Returns the current line.
	 *
	 * @return the current line.
	 */
	public int line() {
		return line;
	}

	/**
	 * Try to consume a token of a given {@link TokenType type}. If no token of the given type is found, maintains the current position. The value of
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
		} catch (final NoSuchElementException | IllegalStateException e) {
			return false;
		}
		return true;
	}

	/**
	 * Advances this scanner past the current line.
	 */
	public void nextLine() {
		scanner.nextLine();
		line++;
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

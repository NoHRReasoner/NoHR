/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class NoHRScanner {

	private final Scanner scanner;

	/**
	 *
	 */
	public NoHRScanner(String str) {
		scanner = new Scanner(str);
	}

	public String getToken() {
		return scanner.match().group();
	}

	public boolean next(TokenType type) {
		try {
			scanner.skip(type.pattern());
		} catch (final NoSuchElementException e) {
			return false;
		}
		return true;
	}

	public int position() {
		return scanner.match().end();
	}

}

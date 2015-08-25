/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import java.util.regex.Pattern;

/**
 * The types of the {@link <a href="https://en.wikipedia.org/wiki/Lexical_analysis#Token">tokens</a>} recognized by {@link NoHRScanner}.
 *
 * @author Nuno Costa
 */
public enum TokenType {

	/** The comma. */
	COMMA(",", true), /** Any string started by a capital letter followed by letters, numbers or underscores. */
	ID("[A-Z]\\w*+"), /** The Prolog operator {@literal :-}. */
	IF(":-", true), /** The left bracket. */
	L_BRACK("\\["), /** The left parenthesis. */
	L_PAREN("\\("), /** The {@literal not } operator. */
	NOT("not\\s"), /** The question mark. */
	QUESTION_MARK("\\?"), /** The right bracket. */
	R_BRACK("\\]"), /** The right parenthesis. */
	R_PAREN("\\)"), /**
					 * Any string that doesn't contain any unescaped (i.e. not preceded by a slash) slash, space, comma, parenthesis, bracket,
					 * question mark, nor any of the strings "{@literal :-}" or "{@literal not}".
					 */
	SYMBOL("([^,\\[(?\\])\\s\\\\]|(?!-):|(\\\\\\\\)*\\\\.)++");

	/** The regular expression that matches the tokens of this {@link TokenType}. */
	private final Pattern pattern;

	/**
	 * Constructs a {@link TokenType} corresponding to a given regular language.
	 *
	 * @param regex
	 *            the regular expression that matches the tokens of that type.
	 */
	TokenType(String regex) {
		this(regex, false);
	}

	/**
	 * Constructs a {@link TokenType} corresponding to a given regular language.
	 *
	 * @param regex
	 *            the regular expression that matches the tokens of this type.
	 * @param separator
	 *            whether the tokens of this type can be surrounded by space characters.
	 */
	TokenType(String regex, boolean separator) {
		regex = separator ? "\\s*" + regex + "\\s*" : regex;
		pattern = Pattern.compile(regex);
	}

	/**
	 * Returns the regular expression that matches the tokens of this {@link TokenType}.
	 *
	 * @return the regular expression that matches the tokens of this {@link TokenType}.
	 */
	public Pattern pattern() {
		return pattern;
	}

}

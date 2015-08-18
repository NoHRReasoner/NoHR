/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import java.util.regex.Pattern;

/**
 * @author nunocosta
 */
public enum TokenType {

	COMMA(",", true), ID("[A-Z]\\w*+"), IF(":-", true), L_BRACK("\\["), L_PAREN("\\("), NOT("not\\s"), QUESTION_MARK(
			"\\?"), R_BRACK("\\]"), R_PAREN("\\)"), SYMBOL("([^,\\[(?\\])\"' \\\\]|(?!-):|(\\\\\\\\)*\\\\.)++");

	private final Pattern pattern;

	TokenType(String regex) {
		this(regex, false);
	}

	TokenType(String regex, boolean separator) {
		regex = separator ? "\\s*" + regex + "\\s*" : regex;
		pattern = Pattern.compile(regex);
	}

	public Pattern pattern() {
		return pattern;
	}

}

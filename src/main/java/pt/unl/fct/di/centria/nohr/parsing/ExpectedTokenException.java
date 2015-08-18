/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

/**
 * @author nunocosta
 */
public class ExpectedTokenException extends Exception {

	private static final long serialVersionUID = 5038240032097572762L;

	private final String nonTerminalSymbol;
	private final TokenType[] expectedTokens;
	private final int position;

	/**
	 *
	 */
	public ExpectedTokenException(String nonTerminalSymbol, int position, TokenType... expectedTokens) {
		this.nonTerminalSymbol = nonTerminalSymbol;
		this.expectedTokens = expectedTokens;
		this.position = position;
	}

	/**
	 * @return the expectedTokens
	 */
	public TokenType[] getExpectedTokens() {
		return expectedTokens;
	}

	/**
	 * @return the nonTerminalSymbol
	 */
	public String getNonTerminalSymbol() {
		return nonTerminalSymbol;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

}

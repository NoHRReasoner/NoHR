/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

/**
 * @author nunocosta
 */
public class TokenImpl<T> implements Token<T> {

	private final TokenType type;
	private final T content;
	private final int start;
	private final int end;

	/**
	 *
	 */
	public TokenImpl(TokenType type, T content, int start, int end) {
		this.type = type;
		this.content = content;
		this.start = start;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.parsing.Token#getContent()
	 */
	@Override
	public T getContent() {
		return content;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.parsing.Token#getEnd()
	 */
	@Override
	public int getEnd() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.parsing.Token#getStart()
	 */
	@Override
	public int getStart() {
		return start;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.parsing.Token#getType()
	 */
	@Override
	public TokenType getType() {
		return type;
	}

}

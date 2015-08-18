/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

/**
 * @author nunocosta
 */
public interface Token<T> {

	public T getContent();

	public int getEnd();

	public int getStart();

	public TokenType getType();

}

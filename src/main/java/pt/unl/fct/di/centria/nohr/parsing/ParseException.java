/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

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

	private final int begin;
	private final int end;

	public ParseException(Object expectedToken, int begin, int end) {
		this.begin = begin;
		this.end = end;
	}

	/**
	 * @return the begin
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

}

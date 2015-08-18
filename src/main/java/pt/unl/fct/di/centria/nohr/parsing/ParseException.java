/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

/**
 * @author nunocosta
 */
public class ParseException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 222524131072855061L;

	private final int begin;
	private final int end;

	/**
	 * @param message
	 */
	public ParseException(String message, int begin, int end) {
		super(message);
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

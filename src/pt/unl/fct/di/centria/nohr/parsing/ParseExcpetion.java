/**
 * 
 */
package pt.unl.fct.di.centria.nohr.parsing;

/**
 * @author nunocosta
 *
 */
public class ParseExcpetion extends Exception {

    /**
     * 
     */
    public ParseExcpetion() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ParseExcpetion(String message) {
	super(message);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ParseExcpetion(Throwable cause) {
	super(cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ParseExcpetion(String message, Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ParseExcpetion(String message, Throwable cause,
	    boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
	// TODO Auto-generated constructor stub
    }

}

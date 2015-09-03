/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.deductivedb;

/**
 * The {@link Exception} thrown when an Prolog engine creation timed out.
 *
 * @author Nuno Costa
 */
public class PrologEngineCreationException extends Exception {

	private static final long serialVersionUID = -5376164449185180022L;

	private final Throwable cause;

	public PrologEngineCreationException(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

}

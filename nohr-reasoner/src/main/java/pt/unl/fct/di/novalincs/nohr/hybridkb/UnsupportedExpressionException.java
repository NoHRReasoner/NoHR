/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 * @author Nuno Costa
 */
public class UnsupportedExpressionException extends RuntimeException {

	private static final long serialVersionUID = 4306711165888446473L;

	private final OWLClassExpression expression;

	/**
	 *
	 */
	public UnsupportedExpressionException(OWLClassExpression expression) {
		this.expression = expression;
	}

	/**
	 * @return the expression
	 */
	public OWLClassExpression getExpression() {
		return expression;
	}

}

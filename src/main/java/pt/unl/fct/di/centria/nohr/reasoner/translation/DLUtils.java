package pt.unl.fct.di.centria.nohr.reasoner.translation;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

/**
 * Provides some DL (Description Logic) utility functions.
 *
 * @author Nuno Costa
 */
public class DLUtils {

	/**
	 * Gets the atomic role of a given basic DL-Lite<sub>R</sub>basic DL-Lite<sub>R</sub> role expression.
	 *
	 * @param role
	 *            a role expression <i>P</i> or <i>P<sup>-</i>.
	 * @return <i>P</i>.
	 */
	public static OWLProperty<?, ?> atomic(OWLPropertyExpression<?, ?> role) {
		if (role.isObjectPropertyExpression()) {
			final OWLObjectPropertyExpression ope = (OWLObjectPropertyExpression) role;
			return ope.getNamedProperty();
		} else if (role.isDataPropertyExpression())
			return (OWLDataProperty) role;
		else
			throw new IllegalArgumentException();
	}

}

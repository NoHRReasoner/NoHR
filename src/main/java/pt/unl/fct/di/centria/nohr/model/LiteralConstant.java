/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * @author Nuno Costa
 */
public interface LiteralConstant extends Constant {

	OWLLiteral getOWLLiteral();

}
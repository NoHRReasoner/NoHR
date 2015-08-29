/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.concrete;

import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.centria.nohr.model.Constant;

/**
 * @author Nuno Costa
 */
public interface LiteralConstant extends Constant {

	OWLLiteral getOWLLiteral();

}
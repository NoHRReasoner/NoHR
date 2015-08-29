/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.concrete;

import org.semanticweb.owlapi.model.OWLIndividual;

import pt.unl.fct.di.centria.nohr.model.Constant;

/**
 * @author Nuno Costa
 */
public interface IndividualConstant extends Constant {

	OWLIndividual getOWLIndividual();

}
/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;

/**
 * @author Nuno Costa
 */
public interface IndividualConstant extends Constant {

	@Override
	String accept(FormatVisitor visitor);

	@Override
	IndividualConstant accept(ModelVisitor visitor);

	OWLIndividual getOWLIndividual();

}
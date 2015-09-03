package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.novalincs.nohr.model.Constant;

/**
 * A {@link Constant} of an Hybrid KB. Can represent a number, an ontology individual or an ontology literal.
 *
 * @author Nuno Costa
 */

public interface HybridConstant extends Constant {

	/**
	 * Returns the ontology individual that this constant represents, if it represent some individual.
	 *
	 * @throws ClassCastException
	 *             if this constant doesn't represent an ontology individual.
	 * @return the ontology individual that this constant represents.
	 */
	OWLIndividual asIndividual();

	/**
	 * Returns the OWL literal corresponding to this constant, if it corresponds to some OWL literal.
	 *
	 * @throws ClassCastException
	 *             if this constant doesn't corresponds to a OWL literal.
	 * @return the OWL literal that this constant corresponds.
	 */
	OWLLiteral asLiteral();

	/**
	 * Returns the number that this constant represents, if it represent some number.
	 *
	 * @throws ClassCastException
	 *             if this constant doesn't represent a number.
	 * @return the number that this constant represents.
	 */
	Number asNumber();

	/**
	 * Returns true iff this constant represents an ontology individual.
	 *
	 * @return true iff this constant represents an ontology individual.
	 */
	boolean isIndividual();

	/**
	 * Returns true iff this constant corresponds to a OWL literal.
	 *
	 * @return true iff this constant corresponds to a OWL literal
	 */
	boolean isLiteral();

	/**
	 * Returns true iff this constant represents a number.
	 *
	 * @return true iff this constant represents a number.
	 */
	boolean isNumber();

}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import org.semanticweb.owlapi.model.AxiomType;

/**
 * @author nunocosta
 *
 */
public class UnsupportedAxiomTypeException extends Exception {

    private final AxiomType type;

    public UnsupportedAxiomTypeException(AxiomType type) {
	super();
	this.type = type;
    }

}

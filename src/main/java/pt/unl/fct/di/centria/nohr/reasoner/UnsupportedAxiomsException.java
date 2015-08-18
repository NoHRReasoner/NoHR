/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author nunocosta
 */
public class UnsupportedAxiomsException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1487281045709804735L;

	private final Set<OWLAxiom> unsupportedAxioms;

	public UnsupportedAxiomsException(Set<OWLAxiom> unsupportedAxioms) {
		super();
		this.unsupportedAxioms = unsupportedAxioms;
	}

	/**
	 * @return the unsupportedAxioms
	 */
	public Set<OWLAxiom> getUnsupportedAxioms() {
		return unsupportedAxioms;
	}

}

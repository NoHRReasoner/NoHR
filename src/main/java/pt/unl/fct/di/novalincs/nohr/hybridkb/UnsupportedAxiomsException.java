/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Represents the presence of axioms of an unsupported type.
 *
 * @author Nuno Costa
 */
public class UnsupportedAxiomsException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1487281045709804735L;

	/**
	 * The set of unsupported axioms.
	 */
	private final Set<OWLAxiom> unsupportedAxioms;

	/**
	 * Constructs an {@link UnsupportedAxiomsException} from a given set of unsupported axioms.
	 *
	 * @param unsupportedAxioms
	 *            the set of unsupported axioms.
	 */
	public UnsupportedAxiomsException(Set<OWLAxiom> unsupportedAxioms) {
		super();
		this.unsupportedAxioms = unsupportedAxioms;
	}

	/**
	 * Returns the set of unsupported axioms.
	 *
	 * @return the set of unsupported axioms.
	 */
	public Set<OWLAxiom> getUnsupportedAxioms() {
		return unsupportedAxioms;
	}

}

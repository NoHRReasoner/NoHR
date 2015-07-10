/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.List;

import org.semanticweb.owlapi.profiles.OWLProfileViolation;

/**
 * @author nunocosta
 *
 */
public class UnsupportedOWLProfile extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -5537995982642676635L;

    private final List<OWLProfileViolation> violations;

    public UnsupportedOWLProfile(List<OWLProfileViolation> violations) {
	this.violations = violations;
    }

    @Override
    public String getMessage() {
	return violations.toString();
    }

}

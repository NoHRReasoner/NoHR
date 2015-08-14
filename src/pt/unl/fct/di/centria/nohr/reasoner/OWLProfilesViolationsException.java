/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import pt.unl.fct.di.centria.nohr.StringUtils;

/**
 * @author nunocosta
 */
public class OWLProfilesViolationsException extends UnsupportedAxiomsException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5537995982642676635L;

	/**
	 * @param reports
	 * @return
	 */
	private static Set<OWLAxiom> unsupportedAxioms(List<OWLProfileReport> reports) {
		final Set<OWLAxiom> result = new HashSet<>();
		int minViolations = Integer.MAX_VALUE;
		OWLProfileReport minReport = null;
		for (final OWLProfileReport report : reports)
			if (report.getViolations().size() < minViolations) {
				minViolations = report.getViolations().size();
				minReport = report;
			}
		for (final OWLProfileViolation violation : minReport.getViolations())
			result.add(violation.getAxiom());
		return result;
	}

	private final List<OWLProfileReport> reports;

	public OWLProfilesViolationsException() {
		this(Collections.<OWLProfileReport> emptyList());
	}

	public OWLProfilesViolationsException(List<OWLProfileReport> reports) {
		super(unsupportedAxioms(reports));
		this.reports = reports;
	}

	public OWLProfilesViolationsException(OWLProfileReport... reports) {
		super(unsupportedAxioms(Arrays.asList(reports)));
		this.reports = Arrays.asList(reports);
	}

	@Override
	public String getMessage() {
		return StringUtils.concat(",", reports);
	}

	public List<OWLProfileReport> getReports() {
		return reports;
	}

}

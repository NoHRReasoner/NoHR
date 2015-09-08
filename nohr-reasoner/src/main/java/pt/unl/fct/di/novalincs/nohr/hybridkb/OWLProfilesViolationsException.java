/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * Represents violations to the supported {@link Profile profiles}.
 *
 * @author Nuno Costa
 */
public class OWLProfilesViolationsException extends UnsupportedAxiomsException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5537995982642676635L;

	/**
	 * Returns the set of axioms that are violations in a given set of {@link OWLProfileReport profile reports}.
	 *
	 * @param reports
	 *            the {@link OWLProfileReport violation reports}.
	 * @return Returns the set of axioms that are violations in a given {@code reports}.
	 */
	private static Set<OWLAxiom> violations(List<OWLProfileReport> reports) {
		final Set<OWLAxiom> result = new HashSet<OWLAxiom>();
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

	/** The set of {@link OWLProfileReport profile reports} that describe the profiles violations. */
	private final List<OWLProfileReport> reports;

	/**
	 * Constructs a {@link OWLProfilesViolationsException}.
	 */
	public OWLProfilesViolationsException() {
		this(Collections.<OWLProfileReport> emptyList());
	}

	/**
	 * Constructs a {@link OWLProfilesViolationsException} for the violations described by a given set of {@link OWLProfileReport profile reports}.
	 *
	 * @param reports
	 *            the set of {@link OWLProfileReport profile violations}.
	 */
	public OWLProfilesViolationsException(List<OWLProfileReport> reports) {
		super(violations(reports));
		this.reports = reports;
	}

	/**
	 * Constructs a {@link OWLProfilesViolationsException} for the violations described by a given set of {@link OWLProfileReport profile reports}.
	 *
	 * @param reports
	 *            the array of {@link OWLProfileReport profile violations}.
	 */
	public OWLProfilesViolationsException(OWLProfileReport... reports) {
		super(violations(Arrays.asList(reports)));
		this.reports = Arrays.asList(reports);
	}

	@Override
	public String getMessage() {
		return StringUtils.concat(",", reports);
	}

	/**
	 * Returns the set of {@link OWLProfileReport profile reports} that describe the profiles violations.
	 *
	 * @return the set of {@link OWLProfileReport profile reports} that describe the profiles violations
	 */
	public List<OWLProfileReport> getReports() {
		return reports;
	}

}

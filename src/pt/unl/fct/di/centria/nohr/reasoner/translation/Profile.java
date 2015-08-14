/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;

import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLOntologyTranslation;

/**
 * Represents the types of the supported OWL profiles. The order of enumeration corresponds to the preferred order. Each profile check if a given
 * ontology is in that profile and return an {@link OntologyTranslation} of that ontology in that profile.
 *
 * @author Nuno Costa
 */
// NOTE: change this Enum if you want to support a new OWL Profile, adding a
// corresponding enum element and handling the
// corresponding
// case in owlProfile() and createOntologyTranslation().
public enum Profile {
	OWL2_QL, OWL2_EL;

	/**
	 * Returns the preferred, in terms of translation, OWL profile of a given ontology.
	 *
	 * @param ontology
	 *            an ontology
	 * @return the preferred OWL profile of {@code ontology}.
	 * @throws OWLProfilesViolationsException
	 *             if {@code ontology} isn't in any of the supported profiles.
	 */
	public static Profile getProfile(OWLOntology ontology) throws OWLProfilesViolationsException {
		final List<OWLProfileReport> reports = new LinkedList<>();
		for (final Profile profile : Profile.values()) {
			final OWLProfileReport report = profile.owlProfile().checkOntology(ontology);
			if (report.isInProfile())
				return profile;
			reports.add(report);
		}
		throw new OWLProfilesViolationsException(reports);
	}

	/**
	 * Create an {@link OntologyTranslation} of a given ontology that can handle this profile.
	 *
	 * @param ontology
	 *            the ontology whose {@link OntologyTranslation ontology translation} will be created.
	 * @return the {@link OntologyTranslation ontology translation} of {@code ontology} for this profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some axiom of a type that isn't supported in this profile.
	 * @throws OWLProfilesViolationsException
	 *             if {@code ontology} isn't in this profile.
	 */
	public OntologyTranslation createOntologyTranslation(OWLOntology ontology)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		final OWLProfileReport report = owlProfile().checkOntology(ontology);
		if (!report.isInProfile())
			throw new OWLProfilesViolationsException(report);
		switch (this) {
		case OWL2_QL:
			return new QLOntologyTranslation(ontology);
		case OWL2_EL:
			return new ELOntologyTranslation(ontology);
		default:
			throw new OWLProfilesViolationsException();
		}
	}

	/**
	 * Returns an {@link OWLProfile} corresponding to this {@link Profile}.
	 *
	 * @return an {@link OWLProfile} corresponding to this {@link Profile}.
	 */
	private OWLProfile owlProfile() {
		switch (this) {
		case OWL2_QL:
			return new OWL2QLProfile();
		case OWL2_EL:
			return new OWL2ELProfile();
		default:
			return null;
		}
	}
}

/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.el.ELOntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.ql.QLOntologyTranslator;

/**
 * Represents the types of the supported OWL profiles. The order of enumeration corresponds to the preferred order. Each profile check if a given
 * ontology is in that profile and return an {@link OntologyTranslator} of that ontology in that profile.
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
		int minViolations = Integer.MAX_VALUE;
		Profile minViolationsProfile = Profile.values()[0];
		for (final Profile profile : Profile.values()) {
			final OWLProfileReport report = profile.owlProfile().checkOntology(ontology);
			if (report.isInProfile())
				return profile;
			if (report.getViolations().size() < minViolations) {
				minViolations = report.getViolations().size();
				minViolationsProfile = profile;
			}
			reports.add(report);
		}
		final String ignoreUnsupported = System.getenv("IGNORE_UNSUPPORTED");
		if (ignoreUnsupported != null && ignoreUnsupported.equals("true"))
			return minViolationsProfile;
		else
			throw new OWLProfilesViolationsException(reports);
	}

	/**
	 * Create an {@link OntologyTranslator} of a given ontology that can handle this profile.
	 *
	 * @param ontology
	 *            the ontology whose {@link OntologyTranslator ontology translation} will be created.
	 * @param dedutiveDatabase
	 *            the {@link DeductiveDatabase dedutive database} where the ontology translation will be loaded.
	 * @return the {@link OntologyTranslator ontology translation} of {@code ontology} for this profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some axiom of a type that isn't supported in this profile.
	 * @throws OWLProfilesViolationsException
	 *             if {@code ontology} isn't in this profile.
	 */
	public OntologyTranslator createOntologyTranslator(OWLOntology ontology, Vocabulary v,
			DeductiveDatabase dedutiveDatabase) throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		final OWLProfileReport report = owlProfile().checkOntology(ontology);
		final String ignoreUnsupported = System.getenv("IGNORE_UNSUPPORTED");
		if (!report.isInProfile() && (ignoreUnsupported == null || !ignoreUnsupported.equals("true")))
			throw new OWLProfilesViolationsException(report);
		switch (this) {
		case OWL2_QL:
			return new QLOntologyTranslator(ontology, v, dedutiveDatabase);
		case OWL2_EL:
			return new ELOntologyTranslator(ontology, v, dedutiveDatabase);
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

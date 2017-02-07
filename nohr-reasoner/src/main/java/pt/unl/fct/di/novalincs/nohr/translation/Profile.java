/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;

/**
 * Represents the types of the supported OWL profiles. The order of enumeration
 * corresponds to the preferred order. Each profile check if a given ontology is
 * in that profile and return an {@link OntologyTranslator} of that ontology in
 * that profile.
 *
 * @author Nuno Costa
 */
// NOTE: change this Enum if you want to support a new OWL Profile, adding a
// corresponding enum element and handling the
// corresponding
// case in owlProfile() and createOntologyTranslation().
public enum Profile {

    OWL2_EL, OWL2_QL, OWL2_RL, NOHR_DL;

    /**
     * Returns the preferred, in terms of translation, OWL profile of a given
     * ontology.
     *
     * @param ontology an ontology
     * @return the preferred OWL profile of {@code ontology}.
     */
    public static Profile getProfile(OWLOntology ontology) {
//        final List<OWLProfileReport> reports = new LinkedList<>();
        int minViolations = Integer.MAX_VALUE;
        Profile minViolationsProfile = Profile.values()[0];

        for (final Profile profile : Profile.values()) {
            OWLProfile owlProfile = profile.owlProfile();

            if (owlProfile != null) {
                final OWLProfileReport report = owlProfile.checkOntology(ontology);

                if (report.isInProfile()) {
                    return profile;
                }

                if (report.getViolations().size() < minViolations) {
                    minViolations = report.getViolations().size();
                    minViolationsProfile = profile;
                }

//                reports.add(report);
            }
        }

        if (minViolations > 0) {
            return NOHR_DL;
        }

        // final String ignoreUnsupported = System.getenv("IGNORE_UNSUPPORTED");
        // if (ignoreUnsupported != null && ignoreUnsupported.equals("true"))
        return minViolationsProfile;
        // else
        // throw new OWLProfilesViolationsException(reports);
    }

    /**
     * Returns an {@link OWLProfile} corresponding to this {@link Profile}.
     *
     * @return an {@link OWLProfile} corresponding to this {@link Profile}.
     */
    private OWLProfile owlProfile() {
        switch (this) {
            case OWL2_RL:
                return new OWL2RLProfile();
            case OWL2_QL:
                return new OWL2QLProfile();
            case OWL2_EL:
                return new OWL2ELProfile();
            default:
                return null;
        }
    }
}

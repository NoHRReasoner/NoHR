/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

/**
 * The implementation of the {@link OntologyTranslator} <i>abstraction</i> (see {@link <a href="http://www.oodesign.com/bridge-pattern.html">Bridge
 * Pattern</a>}, and note that here {@link OntologyTranslator} is simultaneously the <i>abstraction</i> interface and the <i>implementor</i>
 * interface). The <i>concrete implementor</i> is chosen according to the ontology preferred {@link Profile OWL profile}, and changed appropriately in
 * each {@link #translate() translation}.
 *
 * @author Nuno Costa
 */
public class OntologyTranslatorImpl implements OntologyTranslator {

	/**
	 * {@link OntologyTranslator}'s factory method. Creates an {@link OntologyTranslator ontology translation} of a specified ontology choosing
	 * appropriately the {@link OntologyTranslator} implementation that can handle a specified OWL profile if the ontology is in that profile, or the
	 * preferred ontology's profile (the same that is returned by {@link #getProfile()}) if none is specified.
	 *
	 * @param ontology
	 *            the ontology to translate.
	 * @param dedutiveDatabaseManager
	 *            the {@link DeductiveDatabaseManager} where the ontology translation will be loaded.
	 * @param profile
	 *            the {@link Profile profile} that {@link OntologyTranslator} will handle. If none is specified (i.e. if it is {@code null} ), the
	 *            preferred ontology's profile will be chosen.
	 * @return an {@link OntologyTranslator ontology translation} of {@code ontology}.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile!=null} and the ontology isn't in {@code profile}, or {@code profile==null} and the ontology isn't in any
	 *             supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some axioms of an unsupported type.
	 */
	private static OntologyTranslator createOntologyTranslation(OWLOntology ontology,
			DeductiveDatabaseManager dedutiveDatabaseManager, Profile profile)
					throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (profile != null)
			return profile.createOntologyTranslation(ontology, dedutiveDatabaseManager);
		return Profile.getProfile(ontology).createOntologyTranslation(ontology, dedutiveDatabaseManager);
	}

	/**
	 * The {@link Profile profile} that this {@link OntologyTranslator} will handle. If none is specified (i.e. if it is {@code null} ), the preferred
	 * ontology's profile will be chosen.
	 */
	private final Profile profile;

	/** The <i>concrete implementor</i> of this <i>abstraction</i>. */
	private OntologyTranslator implementor;

	/**
	 * Constructs an {@link OntologyTranslator} for a given {@link OWLOntolgy ontology}.
	 *
	 * @param ontology
	 *            the ontoloty that will be translated.
	 * @param dedutiveDatabaseManager
	 *            the {@link DeductiveDatabaseManager} where the translation will be loaded.
	 * @param profile
	 *            the {@link Profile profile} that this {@link OntologyTranslator} will handle. If none is specified (i.e. if it is {@code null} ),
	 *            the preferred ontology's profile will be chosen.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile!=null} and the ontology isn't in {@code profile}, or {@code profile==null} and the ontology isn't in any
	 *             supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some axioms of an unsupported type.
	 */
	public OntologyTranslatorImpl(OWLOntology ontology, DeductiveDatabaseManager dedutiveDatabaseManager,
			Profile profile) throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		this.profile = profile;
		implementor = createOntologyTranslation(ontology, dedutiveDatabaseManager, profile);
	}

	@Override
	public DeductiveDatabaseManager getDedutiveDatabase() {
		return implementor.getDedutiveDatabase();
	}

	@Override
	public OWLOntology getOntology() {
		return implementor.getOntology();
	}

	@Override
	public Profile getProfile() {
		return implementor.getProfile();
	}

	@Override
	public boolean hasDisjunctions() {
		return implementor.hasDisjunctions();
	}

	@Override
	public void translate() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		implementor = createOntologyTranslation(getOntology(), getDedutiveDatabase(), profile);
		implementor.translate();
	}

}

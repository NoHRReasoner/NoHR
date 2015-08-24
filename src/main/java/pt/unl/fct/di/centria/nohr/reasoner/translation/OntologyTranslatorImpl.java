/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

/**
 * The implementation of the {@link OntologyTranslator} <i>abstraction</i> (see {@link <a href="http://www.oodesign.com/bridge-pattern.html">Bridge
 * Pattern</a>}, and note that here {@link OntologyTranslator} is simultaneously the <i>abstraction</i> interface and the <i>implementor</i>
 * interface). The <i>concrete implementor</i> is chosen according to the ontology preferred {@link Profile OWL profile}, and changed appropriately in
 * each {@link #updateTranslation() translation}.
 *
 * @author Nuno Costa
 */
public class OntologyTranslatorImpl implements OntologyTranslator {

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
	 *            the ontology that will be translated.
	 * @param dedutiveDatabaseManager
	 *            the {@link DeductiveDatabase} where the translation will be maintained.
	 * @param profile
	 *            the {@link Profile profile} that this {@link OntologyTranslator} will handle. If none is specified (i.e. if it is {@code null} ),
	 *            the preferred ontology's profile will be chosen.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile!=null} and the ontology isn't in {@code profile}, or {@code profile==null} and the ontology isn't in any
	 *             supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some axioms of an unsupported type.
	 */
	public OntologyTranslatorImpl(OWLOntology ontology, DeductiveDatabase dedutiveDatabaseManager, Profile profile)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		this.profile = profile;
		if (profile == null)
			profile = Profile.getProfile(ontology);
		implementor = profile.createOntologyTranslator(ontology, dedutiveDatabaseManager);
	}

	@Override
	public void clear() {
		implementor.clear();
	}

	@Override
	public DeductiveDatabase getDedutiveDatabase() {
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
	public void updateTranslation() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		Profile newProfile = profile;
		if (newProfile == null)
			newProfile = Profile.getProfile(getOntology());
		if (newProfile != getProfile()) {
			implementor.clear();
			implementor = newProfile.createOntologyTranslator(getOntology(), getDedutiveDatabase());
		}
		implementor.updateTranslation();
	}

}

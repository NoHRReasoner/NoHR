package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLOntologyTranslation;

/**
 * An partial implementation of {@link OntologyTranslation} that translate an
 * ontology applying the methods {@link #computeNegativeHeadsPredicates()} and
 * {@link #computeRules()}.
 *
 * @author Nuno Costa
 *
 */
public abstract class OWLOntologyTranslation implements OntologyTranslation {

    /**
     * {@link OntologyTranslation}'s factory method. Creates an
     * {@link OntologyTranslation ontology translation} of a specified ontology
     * choosing the {@link OntologyTranslation} implementation that can handle
     * the ontology's OWL profile.
     *
     * @param ontology
     *            the ontology to translate.
     * @return an {@link OntologyTranslation ontology translation} of
     *         {@code ontology}.
     * @throws OWLProfilesViolationsException
     *             if the ontology isn't in any supported OWL profile.
     * @throws UnsupportedAxiomsException
     *             if the ontolgy is in a supported OWL profile, but has some
     *             axioms of an unsupported type.
     */
    public static OntologyTranslation createOntologyTranslation(OWLOntology ontology, Profile profile)
	    throws OWLProfilesViolationsException, UnsupportedAxiomsException {
	profile = profile == null ? getProfile(ontology) : profile;
	switch (profile) {
	case OWL2_QL:
	    return new QLOntologyTranslation(ontology);
	case OWL2_EL:
	    return new ELOntologyTranslation(ontology);
	default:
	    throw new OWLProfilesViolationsException();
	}
    }

    /**
     * Returns the OWL profile of a given ontology.
     *
     * @param ontology
     *            an ontology
     * @return the OWL profile of {@code ontology}
     * @throws OWLProfilesViolationsException
     *             if {@code ontology} isn't in any of the supported profiles.
     */
    public static Profile getProfile(OWLOntology ontology) throws OWLProfilesViolationsException {
	final OWL2ELProfile elProfile = new OWL2ELProfile();
	final OWL2QLProfile qlProfile = new OWL2QLProfile();
	final OWLProfileReport elReport = elProfile.checkOntology(ontology);
	final OWLProfileReport qlRerport = qlProfile.checkOntology(ontology);
	if (!qlRerport.isInProfile() && !elReport.isInProfile())
	    throw new OWLProfilesViolationsException(qlRerport, elReport);
	if (qlRerport.getViolations().size() <= elReport.getViolations().size())
	    return Profile.OWL2_QL;
	else
	    return Profile.OWL2_EL;
    }

    /**
     * The set of negative meta-predicates appearing at the head of some rule of
     * this translation.
     */
    protected final Set<Predicate> negativeHeadsPredicates;

    /**
     * The translated ontology.
     */
    protected final OWLOntology ontology;

    /**
     * The set of rules corresponding to this translation.
     */
    protected final Set<Rule> rules;

    /** The set of predicates that need to be tabled */
    protected final Set<Predicate> predicatesToTable;

    /**
     * Constructs a {@link OWLOntologyTranslation}, appropriately
     * initializing its state.
     *
     * @param ontology
     *            the ontology to translate.
     */
    public OWLOntologyTranslation(OWLOntology ontology) {
	this.ontology = ontology;
	rules = new HashSet<Rule>();
	predicatesToTable = new HashSet<Predicate>();
	negativeHeadsPredicates = new HashSet<Predicate>();
    }

    protected void addTabledPredicates(Rule rule) {
	final Predicate headPred = rule.getHead().getFunctor();
	predicatesToTable.add(headPred);
	for (final Literal negLiteral : rule.getNegativeBody())
	    predicatesToTable.add(negLiteral.getFunctor());
    }

    /**
     * Computes the set of negative meta-predicates that will appear at the head
     * of some rule of this translation.
     */
    protected abstract void computeNegativeHeadsPredicates();

    /**
     * Computes the set of predicates that need to be tabled.
     */
    private void computePredicatesToTable() {
	for (final Rule rule : rules)
	    addTabledPredicates(rule);
    }

    /**
     * Computes the rules corresponding to the translation of the ontology that
     * these {@link OWLOntologyTranslation} refer.
     */
    protected abstract void computeRules();

    @Override
    public Set<Predicate> getNegativeHeadsPredicates() {
	return negativeHeadsPredicates;
    }

    @Override
    public Set<Predicate> getPredicatesToTable() {
	return predicatesToTable;
    }

    @Override
    public Set<Rule> getRules() {
	return rules;
    }

    /**
     * Translates the ontology that this {@link OWLOntologyTranslation}
     * refer.
     */
    protected void translate() {
	if (hasDisjunctions())
	    computeNegativeHeadsPredicates();
	computeRules();
	computePredicatesToTable();
    }

}
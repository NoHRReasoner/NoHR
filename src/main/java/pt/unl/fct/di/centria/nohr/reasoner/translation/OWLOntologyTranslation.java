package pt.unl.fct.di.centria.nohr.reasoner.translation;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.fail;
import static pt.unl.fct.di.centria.nohr.model.Model.prog;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.table;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

/**
 * An partial implementation of {@link OntologyTranslation} that translate an ontology applying the methods {@link #computeNegativeHeadFunctors()} and
 * {@link #computeRules()}.
 *
 * @author Nuno Costa
 */
public abstract class OWLOntologyTranslation implements OntologyTranslation {

	/**
	 * {@link OntologyTranslation}'s factory method. Creates an {@link OntologyTranslation ontology translation} of a specified ontology choosing
	 * appropriately the {@link OntologyTranslation} implementation that can handle a specified OWL profile if the ontology is in that profile, or the
	 * preferred ontology's profile (the same that is returned by {@link #getProfile()}) if none is specified.
	 *
	 * @param ontology
	 *            the ontology to translate.
	 * @param profile
	 *            the {@link Profile profile} that {@link OntologyTranslation} will handle. If none is specified (i.e. if it is {@code null} ), the
	 *            preferred ontology's profile will be choosen.
	 * @return an {@link OntologyTranslation ontology translation} of {@code ontology}.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile!=null} and the ontology isn't in {@code profile}, or {@code profile==null} and the ontology isn't in any
	 *             supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some axioms of an unsupported type.
	 */
	public static OntologyTranslation createOntologyTranslation(OWLOntology ontology, Profile profile)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (profile != null)
			return profile.createOntologyTranslation(ontology);
		return Profile.getProfile(ontology).createOntologyTranslation(ontology);
	}

	/**
	 * The set of negative meta-predicates appearing at the head of some rule of this translation.
	 */
	protected final Set<Predicate> negativeHeadFunctors;

	/**
	 * The translated ontology.
	 */
	protected final OWLOntology ontology;

	/**
	 * The set of rules corresponding to this translation.
	 */
	protected final Set<Rule> rules;

	/** The set of table directives */
	private final Set<TableDirective> tableDirectives;

	/**
	 * Constructs a {@link OWLOntologyTranslation}, appropriately initializing its state.
	 *
	 * @param ontology
	 *            the ontology to translate.
	 */
	public OWLOntologyTranslation(OWLOntology ontology) {
		this.ontology = ontology;
		rules = new HashSet<>();
		tableDirectives = new HashSet<>();
		negativeHeadFunctors = new HashSet<>();
	}

	/**
	 * Computes the set of predicates whose atoms must be failed (i.e. must have a false truth value) and the correspondig rules.
	 */
	private void computeFailedAtoms() {
		for (final TableDirective tableDirective : tableDirectives)
			if (isFailed(tableDirective.getPredicate()))
				rules.add(rule(atom(tableDirective.getPredicate()), fail()));
	}

	/**
	 * Computes the set of negative meta-predicates that will appear at the head of some rule of this translation.
	 */
	protected abstract void computeNegativeHeadFunctors();

	/**
	 * Computes the rules corresponding to the translation of the ontology that these {@link OWLOntologyTranslation} refer.
	 */
	protected abstract void computeRules();

	/**
	 * Computes the set of predicates that need to be tabled.
	 */
	private void computeTabledDirectives() {
		for (final Rule rule : rules)
			computeTabledDirectives(rule);
	}

	private void computeTabledDirectives(Rule rule) {
		final Predicate headPred = rule.getHead().getFunctor();
		tableDirectives.add(table(headPred));
		for (final Literal negLiteral : rule.getNegativeBody())
			tableDirectives.add(table(negLiteral.getFunctor()));
	}

	@Override
	public Program getProgram() {
		return prog(tableDirectives, rules);
	}

	private boolean isFailed(Predicate predicate) {
		return predicate.isMetaPredicate() && predicate.asMetaPredicate().hasType(PredicateType.NEGATIVE)
				&& !isNegativeHeadFunctor(predicate);
	}

	@Override
	public boolean isNegativeHeadFunctor(Predicate predicate) {
		if (!predicate.isMetaPredicate() || !predicate.asMetaPredicate().hasType(PredicateType.NEGATIVE))
			throw new IllegalArgumentException("predicate: should be a negative meta-predicate");
		return negativeHeadFunctors.contains(predicate);
	}

	/**
	 * Translates the ontology that this {@link OWLOntologyTranslation} refer.
	 */
	protected void translate() {
		if (hasDisjunctions())
			computeNegativeHeadFunctors();
		computeRules();
		computeTabledDirectives();
		if (hasDisjunctions())
			computeFailedAtoms();
	}

}
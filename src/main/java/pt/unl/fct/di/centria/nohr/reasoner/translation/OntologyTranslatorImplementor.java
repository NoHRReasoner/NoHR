package pt.unl.fct.di.centria.nohr.reasoner.translation;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.fail;
import static pt.unl.fct.di.centria.nohr.model.Model.prog;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.table;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;

/**
 * A <i>concrete implementor</i> of {@link OntologyTranslator} (see {@link <a href="http://www.oodesign.com/bridge-pattern.html">Bridge Pattern</a>},
 * and note that here {@link OntologyTranslator} is simultaneously the <i>abstraction</i> interface and the <i>implementor</i> interface), for a
 * specific {@link Profile}.
 *
 * @author Nuno Costa
 */
public abstract class OntologyTranslatorImplementor implements OntologyTranslator {

	protected final DeductiveDatabaseManager dedutiveDatabaseManager;

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
	 * Constructs a {@link OntologyTranslatorImplementor}, appropriately initializing its state.
	 *
	 * @param ontology
	 *            the ontology to translate.
	 * @param dedutiveDatabase
	 *            the {@link DeductiveDatabaseManager} where the ontology translation will be loaded.
	 */
	public OntologyTranslatorImplementor(OWLOntology ontology, DeductiveDatabaseManager dedutiveDatabase) {
		this.ontology = ontology;
		dedutiveDatabaseManager = dedutiveDatabase;
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
	 * Computes the rules corresponding to the translation of the ontology that these {@link OntologyTranslatorImplementor} refer.
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
	public DeductiveDatabaseManager getDedutiveDatabase() {
		return dedutiveDatabaseManager;
	}

	@Override
	public OWLOntology getOntology() {
		return ontology;
	}

	private boolean isFailed(Predicate predicate) {
		return predicate.isMetaPredicate() && predicate.asMetaPredicate().hasType(PredicateType.NEGATIVE)
				&& !isNegativeHeadFunctor(predicate);
	}

	private boolean isNegativeHeadFunctor(Predicate predicate) {
		if (!predicate.isMetaPredicate() || !predicate.asMetaPredicate().hasType(PredicateType.NEGATIVE))
			throw new IllegalArgumentException("predicate: should be a negative meta-predicate");
		return negativeHeadFunctors.contains(predicate);
	}

	@Override
	public void translate() {
		if (hasDisjunctions())
			computeNegativeHeadFunctors();
		computeRules();
		computeTabledDirectives();
		if (hasDisjunctions())
			computeFailedAtoms();
		dedutiveDatabaseManager.load(prog(this, tableDirectives, rules));
	}

}
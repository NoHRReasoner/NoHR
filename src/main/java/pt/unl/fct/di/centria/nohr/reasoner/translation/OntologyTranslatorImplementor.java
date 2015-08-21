package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * A <i>concrete implementor</i> of {@link OntologyTranslator} (see {@link <a href="http://www.oodesign.com/bridge-pattern.html">Bridge Pattern</a>},
 * and note that here {@link OntologyTranslator} is simultaneously the <i>abstraction</i> interface and the <i>implementor</i> interface), for a
 * specific {@link Profile}.
 *
 * @author Nuno Costa
 */
public abstract class OntologyTranslatorImplementor implements OntologyTranslator {

	/**
	 * The key of the <i>program</i> (see {@link DeductiveDatabaseManager}) where the translation rules are added.
	 */
	private static final String PROGRAM_ID = "ontology_translation";

	/**
	 * The {@link DeductiveDatabaseManager} to where the translation rules are added.
	 */
	private final DeductiveDatabaseManager dedutiveDatabaseManager;

	/**
	 * The translated ontology.
	 */
	protected final OWLOntology ontology;

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
	}

	/**
	 * Add a given rule to the underlying {@link DeductiveDatabaseManager}.
	 *
	 * @param rule
	 *            a rule.
	 */
	protected void add(Rule rule) {
		dedutiveDatabaseManager.add(PROGRAM_ID, rule);
	}

	/**
	 * Add a given set of rules to the underlying {@link DeductiveDatabaseManager}.
	 *
	 * @param rule
	 *            a rule set.
	 */
	protected void addAll(Set<Rule> rules) {
		for (final Rule rule : rules)
			dedutiveDatabaseManager.add(PROGRAM_ID, rule);
	}

	/**
	 * Execute the translation of the ontology that these {@link OntologyTranslatorImplementor} refer. The rules must be added to the underlying
	 * {@link DeductiveDatabaseManager} calling {@link #add(Rule)} or {@link #addAll(Set)}.
	 */
	protected abstract void execute();

	@Override
	public DeductiveDatabaseManager getDedutiveDatabase() {
		return dedutiveDatabaseManager;
	}

	@Override
	public OWLOntology getOntology() {
		return ontology;
	}

	@Override
	public void translate() {
		dedutiveDatabaseManager.dispose(PROGRAM_ID);
		execute();
	}

}
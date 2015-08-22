package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabase;
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
	 * The {@link DeductiveDatabase} to where the translation rules are added.
	 */
	private final DeductiveDatabase dedutiveDatabaseManager;

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
	 *            the {@link DeductiveDatabase} where the ontology translation will be loaded.
	 */
	public OntologyTranslatorImplementor(OWLOntology ontology, DeductiveDatabase dedutiveDatabase) {
		this.ontology = ontology;
		dedutiveDatabaseManager = dedutiveDatabase;
	}

	/**
	 * Add a given rule to the underlying {@link DeductiveDatabase}.
	 *
	 * @param rule
	 *            a rule.
	 */
	protected void add(Rule rule) {
		dedutiveDatabaseManager.add(getProgramKey(), rule);
	}

	/**
	 * Add a given set of rules to the underlying {@link DeductiveDatabase}.
	 *
	 * @param rule
	 *            a rule set.
	 */
	protected void addAll(Set<Rule> rules) {
		for (final Rule rule : rules)
			dedutiveDatabaseManager.add(getProgramKey(), rule);
	}

	/**
	 * Execute the translation of the ontology that these {@link OntologyTranslatorImplementor} refer. The rules must be added to the underlying
	 * {@link DeductiveDatabase} calling {@link #add(Rule)} or {@link #addAll(Set)}.
	 */
	protected abstract void execute();

	@Override
	public DeductiveDatabase getDedutiveDatabase() {
		return dedutiveDatabaseManager;
	}

	@Override
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * Returns the key of the <i>program</i> (see {@link DeductiveDatabase}) where the translation rules are added.
	 *
	 * @return returns the key of the <i>program</i> (see {@link DeductiveDatabase}) where the translation rules are added.
	 */
	private String getProgramKey() {
		return getOntology().getOntologyID().getOntologyIRI().toURI().toString();
	}

	@Override
	public void translate() {
		dedutiveDatabaseManager.dispose(getProgramKey());
		execute();
	}

}
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.centria.nohr.deductivedb.Program;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

/**
 * A <i>concrete implementor</i> of {@link OntologyTranslator} (see {@link <a href="http://www.oodesign.com/bridge-pattern.html">Bridge Pattern</a>},
 * and note that here {@link OntologyTranslator} is simultaneously the <i>abstraction</i> interface and the <i>implementor</i> interface), for a
 * specific {@link Profile}.
 *
 * @author Nuno Costa
 */
public abstract class OntologyTranslatorImplementor implements OntologyTranslator {

	/**
	 * The {@link DeductiveDatabase} where the translation is maintained.
	 */
	private final DeductiveDatabase dedutiveDatabase;

	/** The {@link Program program} where the translation is maintained. */
	protected final Program translation;

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
	 *            the {@link DeductiveDatabase} where the ontology translation will be mantained.
	 */
	public OntologyTranslatorImplementor(OWLOntology ontology, DeductiveDatabase dedutiveDatabase) {
		this.ontology = ontology;
		this.dedutiveDatabase = dedutiveDatabase;
		translation = dedutiveDatabase.createProgram();
	}

	@Override
	public void clear() {
		translation.clear();
	}

	@Override
	public DeductiveDatabase getDedutiveDatabase() {
		return dedutiveDatabase;
	}

	@Override
	public OWLOntology getOntology() {
		return ontology;
	}

	@Override
	public abstract void updateTranslation() throws UnsupportedAxiomsException;

}
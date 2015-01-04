package local.translate;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.declarativa.interprolog.TermModel;

public class PredicateCodifier {

	protected static final String ORIGINAL_PREFIX = "a";
	protected static final String DOUBLED_PREFIX = "d";
	protected static final String CLASSICAL_NEGATION_PREFIX = "n";

	private OntologyLabel ontologyLabel;
	private CollectionsManager collectionsManager;

	public PredicateCodifier(OntologyLabel ontologyLabel, CollectionsManager collectionsManager) {
		this.ontologyLabel = ontologyLabel;
		this.collectionsManager = collectionsManager;
	}

	public TermModel getPredicate(OWLClass cls, boolean doubled) {
		String pred = (doubled ? DOUBLED_PREFIX : ORIGINAL_PREFIX) +
				ontologyLabel.getLabel(cls, 1);
		collectionsManager.addTabledPredicateOntology(pred + "/1");
		return new TermModel(pred);
	}

	public TermModel getPredicate(OWLObjectProperty prop, boolean doubled) {
		String pred = (doubled ? DOUBLED_PREFIX : ORIGINAL_PREFIX)
		+ ontologyLabel.getLabel(prop, 1);
		collectionsManager.addTabledPredicateOntology(pred + "/2");
		collectionsManager.addHilogPredicates(pred);
		return new TermModel(pred);
	}

	public TermModel getNegativePredicate(OWLClass cls) {
		return new TermModel(CLASSICAL_NEGATION_PREFIX
				+ ontologyLabel.getLabel(cls, 1));
	}

	public TermModel getNegativePredicate(OWLObjectProperty prop) {
		return new TermModel(CLASSICAL_NEGATION_PREFIX
				+ ontologyLabel.getLabel(prop, 1));
	}
}

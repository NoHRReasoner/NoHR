package local.translate.ql;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.declarativa.interprolog.TermModel;

public class TermCodifier {

	protected static final String ORIGINAL_PREFIX = "a";
	protected static final String DOUBLED_PREFIX = "d";
	protected static final String CLASSICAL_NEGATION_PREFIX = "n";
	private static final String CONSTANT_PREFIX = "c";

	private OntologyLabel ontologyLabel;
	private CollectionsManager collectionsManager;

	public TermCodifier(OntologyLabel ontologyLabel, CollectionsManager collectionsManager) {
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
		String pred = CLASSICAL_NEGATION_PREFIX
				+ ontologyLabel.getLabel(cls, 1);
		collectionsManager.addTabledPredicateOntology(pred + "/1");
		collectionsManager.addPrediactesAppearedUnderNunderscore(pred);
		return new TermModel(pred);
	}

	public TermModel getNegativePredicate(OWLObjectProperty prop) {
		String pred = CLASSICAL_NEGATION_PREFIX
				+ ontologyLabel.getLabel(prop, 1);
		collectionsManager.addTabledPredicateOntology(pred + "/2");
		collectionsManager.addPrediactesAppearedUnderNunderscore(pred);
		return new TermModel(pred);
	}

	public Object getConstant(OWLIndividual c) {
		return new TermModel(CONSTANT_PREFIX + ontologyLabel.getLabel(c, 1));
	}
}

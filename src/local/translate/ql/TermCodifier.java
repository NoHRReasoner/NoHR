package local.translate.ql;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import com.declarativa.interprolog.TermModel;

public class TermCodifier {

	protected static final String ORIGINAL_PREFIX = "a";
	protected static final String DOUBLED_PREFIX = "d";
	protected static final String ORIGINAL_DOM_PREFIX = "e";
	protected static final String ORIGINAL_RAN_PREFIX = "f";
	protected static final String DOUBLED_DOM_PREFIX = "g";
	protected static final String DOUBLED_RAN_PREFIX = "h";
	protected static final String CLASSICAL_NEGATION_PREFIX = "n";
	private static final String CONSTANT_PREFIX = "c";

	private OntologyLabel ontologyLabel;
	private CollectionsManager cm;

	public TermCodifier(OntologyLabel ontologyLabel,
			CollectionsManager collectionsManager) {
		this.ontologyLabel = ontologyLabel;
		this.cm = collectionsManager;
	}

	public TermModel getPredicate(OWLClass cls, boolean doubled) {
		String pred = (doubled ? DOUBLED_PREFIX : ORIGINAL_PREFIX)
				+ ontologyLabel.getLabel(cls, 1);
		cm.addTabledPredicateOntology(pred + "/1");
		return new TermModel(pred);
	}

	public TermModel getPredicate(OWLProperty<?, ?> prop, boolean doubled) {
		String pred = (doubled ? DOUBLED_PREFIX : ORIGINAL_PREFIX)
				+ ontologyLabel.getLabel(prop, 1);
		cm.addTabledPredicateOntology(pred + "/2");
		return new TermModel(pred);
	}
	
	public TermModel getNegativePredicate(OWLClass cls) {
		String pred = CLASSICAL_NEGATION_PREFIX
				+ ontologyLabel.getLabel(cls, 1);
		cm.addTabledPredicateOntology(pred + "/1");
		cm.addPrediactesAppearedUnderNunderscore(pred);
		return new TermModel(pred);
	}

	public TermModel getNegativePredicate(OWLProperty<?, ?> prop) {
		String pred = CLASSICAL_NEGATION_PREFIX
				+ ontologyLabel.getLabel(prop, 1);
		cm.addTabledPredicateOntology(pred + "/2");
		cm.addPrediactesAppearedUnderNunderscore(pred);
		return new TermModel(pred);
	}

	public TermModel getConstant(OWLIndividual c) {
		return new TermModel(CONSTANT_PREFIX + ontologyLabel.getLabel(c, 1));
	}

	public TermModel getConstant(OWLLiteral value) {
		return new TermModel(CONSTANT_PREFIX
				+ cm.getHashedLabel(value.getLiteral()));
	}

	public TermModel getExistPredicate(OWLProperty<?, ?> prop,
			boolean inverse, boolean doubled) {	
		String prefix;
		if (!doubled)
			prefix = inverse ? ORIGINAL_DOM_PREFIX : ORIGINAL_RAN_PREFIX;
		else
			prefix = inverse ? DOUBLED_DOM_PREFIX : DOUBLED_RAN_PREFIX;
		String pred = prefix + ontologyLabel.getLabel(prop, 1);
		cm.addTabledPredicateOntology(pred + "/2");
		return new TermModel(pred);
	}

}

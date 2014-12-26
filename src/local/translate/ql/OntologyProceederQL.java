package local.translate.ql;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntology;

import local.translate.CollectionsManager;
import local.translate.OntologyProceeder;
import local.translate.RuleCreator;

public class OntologyProceederQL extends OntologyProceeder{

	public OntologyProceederQL(CollectionsManager _cm, RuleCreator _ruleCreator) {
		super(_cm, _ruleCreator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isOntologyNeedToBeNormalized(OWLOntology ontology) {
		return false;
	}

	@Override
	public void proceed() throws ParserException {
		// TODO implement
	}
	
	

}

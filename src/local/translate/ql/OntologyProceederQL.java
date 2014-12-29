package local.translate.ql;

import org.semanticweb.owlapi.model.OWLOntology;

import local.translate.CollectionsManager;
import local.translate.OntologyProceeder;
import local.translate.RuleCreator;

public class OntologyProceederQL extends OntologyProceeder{

	public OntologyProceederQL(CollectionsManager _cm, RuleCreator _ruleCreator) {
		super(_cm, _ruleCreator);
		// TODO Auto-generated constructor stub
	}

	/** Test if the ontology need to be normalized. 
	 * 
	 * @return false, for OWL QL. */
	@Override
	public boolean isOntologyNeedToBeNormalized(OWLOntology ontology) {
		return false;
	}

	 /** Handle negative axioms*/
	@Override
	protected void fillExistsOntologiesAndRules() {
		//Note:
		//the negative class subsumption axioms supported in the OWL EL profile
		//are a generalization of the ones supported in OWL QL
		super.fillExistsOntologiesAndRules();
		//TODO: handle disjoint property axioms
	}
	
	

}

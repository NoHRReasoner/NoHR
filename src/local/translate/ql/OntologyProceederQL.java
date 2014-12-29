package local.translate.ql;

import java.util.Set;

import local.translate.CollectionsManager;
import local.translate.OntologyProceeder;
import local.translate.RuleCreator;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyProceederQL extends OntologyProceeder{

	public OntologyProceederQL(CollectionsManager _cm, RuleCreator _ruleCreator) {
		super(_cm, _ruleCreator);
	}
	
	private Set<OWLEntity> omega() {
		//TODO implement
		return null;
	}
	
	private Set<OWLObjectProperty> psi() {
		//TODO implement
		return null;
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

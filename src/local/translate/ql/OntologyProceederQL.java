package local.translate.ql;

import java.util.Set;

import local.translate.CollectionsManager;
import local.translate.OntologyProceeder;
import local.translate.RuleCreator;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class OntologyProceederQL extends OntologyProceeder {

	public OntologyProceederQL(CollectionsManager _cm,
			RuleCreator _ruleCreator, OWLOntology ontology) {
		super(_cm, _ruleCreator);
		ontologies.add(ontology);
	}

	/**
	 * Test if the ontology need to be normalized.
	 * 
	 * @return false, for OWL QL.
	 */
	@Override
	public boolean isOntologyNeedToBeNormalized(OWLOntology ontology) {
		return false;
	}

	private Set<OWLEntity> omega() {
		// TODO implement
		return null;
	}

	@Override
	public void proceed() throws ParserException {
		RuleCreatorQL ruleCreatorQL = (RuleCreatorQL) ruleCreator;
		ruleCreatorQL.e();
		for (OWLOntology ontology : ontologies) {
			for (OWLClassAssertionAxiom clsAssertion : ontology
					.getAxioms(AxiomType.CLASS_ASSERTION))
				ruleCreator.writeRuleA1(clsAssertion.getIndividual(),
						(OWLClass) clsAssertion.getClassExpression());
			for (OWLObjectPropertyAssertionAxiom propAssertion : ontology
					.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION))
				ruleCreator.writeRuleA2(propAssertion);
			for (OWLSubClassOfAxiom subClsAxiom : ontology
					.getAxioms(AxiomType.SUBCLASS_OF))
				ruleCreatorQL.s1(subClsAxiom);
			for (OWLSubObjectPropertyOfAxiom subPropAxiom : ontology
					.getAxioms(AxiomType.SUB_OBJECT_PROPERTY))
				ruleCreatorQL.s2(subPropAxiom);
		}
	}

	private Set<OWLObjectProperty> psi() {
		// TODO implement
		return null;
	}
}

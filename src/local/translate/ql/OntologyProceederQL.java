package local.translate.ql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import local.translate.CollectionsManager;
import local.translate.OntoProceeder;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;


public class OntologyProceederQL implements OntoProceeder {//extends OntologyProceeder {
	
	private CollectionsManager cm;
	private RuleCreatorQL ruleCreator;
	private List<OWLOntology> ontologies;


	public OntologyProceederQL(CollectionsManager _cm,
			RuleCreatorQL _ruleCreator, OWLOntology ontology) {
		this.cm = _cm;
		this.ruleCreator = _ruleCreator;
		ontologies = new ArrayList<OWLOntology>();
		ontologies.add(ontology);
	}

	/**
	 * Test if the ontology need to be normalized.
	 * 
	 * @return false, for OWL QL.
	 */
	public boolean isOntologyNeedToBeNormalized(OWLOntology ontology) {
		return false;
	}

	protected List<OWLClass> omegaA() {
		List<OWLClass> result = new ArrayList<OWLClass>();
		// TODO implement
		return result;
	}

	protected List<OWLObjectProperty> omegaP() {
		List<OWLObjectProperty> result = new ArrayList<OWLObjectProperty>();
		// TODO implement
		return result;
	}

	private boolean hasDisjointStatement(OWLOntology ontology) {
		return ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, true)
				+ ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,
						true) > 0;
	}
	
	public void proceed() throws ParserException {
		RuleCreatorQL ruleCreatorQL = (RuleCreatorQL) ruleCreator;
		ruleCreatorQL.e();
		for (OWLOntology ontology : ontologies) {
			cm.setIsAnyDisjointStatement(hasDisjointStatement(ontology));
			for (OWLClassAssertionAxiom clsAssertion : ontology
					.getAxioms(AxiomType.CLASS_ASSERTION))
				ruleCreatorQL.a1((OWLClass) clsAssertion.getClassExpression(),
					clsAssertion.getIndividual());
			for (OWLObjectPropertyAssertionAxiom propAssertion : ontology
					.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION))
				ruleCreatorQL.a2((OWLObjectProperty) propAssertion.getProperty(),
				propAssertion.getSubject(), propAssertion.getObject());
			for (OWLSubClassOfAxiom subClsAxiom : ontology
					.getAxioms(AxiomType.SUBCLASS_OF))
				ruleCreatorQL.s1(subClsAxiom);
			for (OWLSubObjectPropertyOfAxiom subPropAxiom : ontology
					.getAxioms(AxiomType.SUB_OBJECT_PROPERTY))
				ruleCreatorQL.s2(subPropAxiom);
			for (OWLDisjointClassesAxiom disjClsAxiom : ontology
					.getAxioms(AxiomType.DISJOINT_CLASSES)) {
				for (OWLDisjointClassesAxiom disjWithAxiom : disjClsAxiom
						.asPairwiseAxioms()) {
					List<OWLClassExpression> cls = disjWithAxiom
							.getClassExpressionsAsList();
					ruleCreatorQL.n1(cls.get(0), cls.get(1));
				}
			}
			for (OWLDisjointObjectPropertiesAxiom disjPropsAxiom : ontology
					.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
				Set<OWLObjectPropertyExpression> props = disjPropsAxiom
						.getProperties();
				Iterator<OWLObjectPropertyExpression> propsIt1 = props
						.iterator();
				Iterator<OWLObjectPropertyExpression> propsIt2 = props
						.iterator();
				while (propsIt1.hasNext()) {
					OWLObjectPropertyExpression q1 = propsIt1.next();
					while (propsIt2.hasNext()) {
						OWLObjectPropertyExpression q2 = propsIt2.next();
						if (!q1.equals(q2))
							ruleCreatorQL.n2(q1, q2);
					}
				}
			}
			for (OWLClass a : omegaA())
				ruleCreatorQL.i1(a);
			for (OWLObjectProperty p : omegaP())
				ruleCreatorQL.i2(p);
			for (OWLObjectProperty p : psi())
				ruleCreatorQL.ir(p);
		}
	}

	protected List<OWLObjectProperty> psi() {
		List<OWLObjectProperty> result = new ArrayList<OWLObjectProperty>();
		// TODO implement
		return result;
	}

	@Override
	public void setOntologiesToProceed(List<OWLOntology> ontologies) {
		this.ontologies = ontologies;
	}

	@Override
	public OWLOntology normalizeOntology(OWLOntology ontology,
			OWLOntologyManager owlOntologyManager)
			throws OWLOntologyCreationException, OWLOntologyStorageException {
		return ontology;
	}
}

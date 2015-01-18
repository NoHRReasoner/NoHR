package test;

import hybrid.query.views.Rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

class KB {

	private Map<String, OWLClass> concepts;

	private OWLDataFactory df;

	private Map<String, OWLIndividual> individuals;

	private OntologyLabel ol;

	private OWLOntologyManager om;

	private OWLOntology ont;

	private Map<String, OWLObjectProperty> roles;

	KB() throws OWLOntologyCreationException {
		om = OWLManager.createOWLOntologyManager();
		df = om.getOWLDataFactory();
		ont = om.createOntology(IRI.generateDocumentIRI());
		OWLAnnotationProperty lblAnnotProp = om.getOWLDataFactory()
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		ol = new OntologyLabel(om.createOntology(), lblAnnotProp,
				new CollectionsManager());
		concepts = new HashMap<String, OWLClass>();
		roles = new HashMap<String, OWLObjectProperty>();
		individuals = new HashMap<String, OWLIndividual>();
	}

	public void add(OWLAxiom... axioms) {
		for (OWLAxiom a : axioms)
			om.addAxiom(ont, a);
	}

	public void addAssertion(OWLClass concept, OWLIndividual individual) {
		om.addAxiom(ont, df.getOWLClassAssertionAxiom(concept, individual));
	}

	public void addAssertion(OWLObjectProperty role, OWLIndividual ind1,
			OWLIndividual ind2) {
		om.addAxiom(ont,
				df.getOWLObjectPropertyAssertionAxiom(role, ind1, ind2));
	}

	public void addDisjunction(OWLClassExpression b1, OWLClassExpression b2) {
		om.addAxiom(ont, df.getOWLDisjointClassesAxiom(b1, b2));
	}

	public void addDisjunction(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		om.addAxiom(ont, df.getOWLDisjointObjectPropertiesAxiom(q1, q2));
	}

	public void addRule(String rule) {
		Rules.addRule(rule);
	}

	public void addSubsumption(OWLClassExpression b1, OWLClassExpression b2) {
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(b1, b2));
	}

	public void addSubsumption(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		om.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(q1, q2));
	}

	public void clear() throws OWLOntologyCreationException {
		Set<OWLDeclarationAxiom> declarationAxioms = ont
				.getAxioms(AxiomType.DECLARATION);
		om.removeAxioms(ont, ont.getAxioms());
		om.addAxioms(ont, declarationAxioms);
		Rules.resetRules();
	}

	public OWLClass getConcept() {
		return getConcept("Anew" + concepts.size());
	}

	public OWLClass getConcept(String name) {
		OWLClass concept = concepts.get(name);
		if (concept == null) {
			concept = df.getOWLClass(getEntityIRI(name));
			om.addAxiom(ont, df.getOWLDeclarationAxiom(concept));
			concepts.put(name, concept);
		}
		return concept;
	}

	public OWLClass[] getConcepts(int n) {
		OWLClass[] result = new OWLClass[n];
		for (int i = 0; i < n; i++)
			result[i] = getConcept();
		return result;
	}

	public OWLDataFactory getDataFactory() {
		return df;
	}

	private IRI getEntityIRI(String name) {
		IRI ontIRI = ont.getOntologyID().getOntologyIRI();
		return IRI.create(ontIRI + "#" + name);
	}

	public OWLObjectSomeValuesFrom getExistential(
			OWLObjectPropertyExpression owlObjectPropertyExpression) {
		return df.getOWLObjectSomeValuesFrom(owlObjectPropertyExpression,
				df.getOWLThing());
	}

	public OWLObjectSomeValuesFrom getExistential(String roleName) {
		OWLObjectProperty role = getRole(roleName);
		return df.getOWLObjectSomeValuesFrom(role, df.getOWLThing());
	}

	public OWLIndividual getIndividual() {
		return getIndividual("anew" + individuals.size());
	}

	public OWLIndividual getIndividual(String name) {
		OWLIndividual individual = individuals.get(name);
		if (individual == null) {
			individual = df.getOWLNamedIndividual(getEntityIRI(name));
			individuals.put(name, individual);
		}
		return individual;
	}

	public OWLObjectPropertyExpression getInverse(OWLObjectProperty role) {
		return df.getOWLObjectInverseOf(role);
	}

	public OWLObjectPropertyExpression getInverse(String roleName) {
		OWLObjectProperty role = getRole(roleName);
		return df.getOWLObjectInverseOf(role);
	}

	String getLabel(Object obj) {
		if (obj instanceof OWLEntity)
			return getLabel((OWLEntity) obj);
		else if (obj instanceof OWLIndividual)
			return getLabel((OWLIndividual) obj);
		else
			return null;
	}

	String getLabel(OWLEntity e) {
		return ol.getLabel(e, 1);
	}

	String getLabel(OWLIndividual i) {
		return ol.getLabel(i, 1);
	}

	public OWLOntology getOntology() {
		return ont;
	}

	public OWLObjectProperty getRole() {
		return getRole("Pnew" + roles.size());
	}

	public OWLObjectProperty getRole(String name) {
		OWLObjectProperty role = roles.get(name);
		if (role == null) {
			role = df.getOWLObjectProperty(getEntityIRI(name));
			om.addAxiom(ont, df.getOWLDeclarationAxiom(role));
			roles.put(name, role);
		}
		return role;
	}

	public OWLObjectProperty[] getRoles(int n) {
		OWLObjectProperty[] result = new OWLObjectProperty[n];
		for (int i = 0; i < n; i++)
			result[i] = getRole();
		return result;
	}

	public String getRule(Rule rule) {
		return getRule(rule.format, rule.args);
	}

	String getRule(String ruleFormat, Object... cls) {
		Object[] args = new String[cls.length];
		for (int i = 0; i < cls.length; i++)
			args[i] = getLabel(cls[i]);
		return String.format(ruleFormat, args);
	}

	public OntologyLabel getOntologyLabel() {
		return ol;
	}

}

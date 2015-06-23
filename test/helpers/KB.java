package helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.google.common.base.Optional;

import other.Utils;
import pt.unl.fct.di.centria.nohr.plugin.Rules;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.INormalizedOntology;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.Normalizer;

public class KB {

    private final Map<String, OWLClass> concepts;

    private final Map<String, OWLDataProperty> dataRoles;

    private final OWLDataFactory df;

    private final Map<String, OWLIndividual> individuals;

    private final OntologyLabeler ol;

    private final OWLOntologyManager om;

    private final OWLOntology ont;

    private final Map<String, OWLObjectProperty> roles;

    public KB() throws OWLOntologyCreationException {
	om = OWLManager.createOWLOntologyManager();
	df = om.getOWLDataFactory();
	ont = om.createOntology(IRI.generateDocumentIRI());
	final OWLAnnotationProperty lblAnnotProp = om.getOWLDataFactory()
		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	ol = new OntologyLabeler(om.createOntology(), lblAnnotProp);
	concepts = new HashMap<String, OWLClass>();
	roles = new HashMap<String, OWLObjectProperty>();
	dataRoles = new HashMap<String, OWLDataProperty>();
	individuals = new HashMap<String, OWLIndividual>();
    }

    public void add(OWLAxiom... axioms) {
	for (final OWLAxiom a : axioms)
	    om.addAxiom(ont, a);
    }

    public void add(Set<OWLAxiom> axioms) {
	om.addAxioms(ont, axioms);
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
	final Set<OWLDeclarationAxiom> declarationAxioms = ont
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
	final OWLClass[] result = new OWLClass[n];
	for (int i = 0; i < n; i++)
	    result[i] = getConcept();
	return result;
    }

    public OWLDataFactory getDataFactory() {
	return df;
    }

    private OWLDataProperty getDataRole() {
	return getDataRole("Dnew" + dataRoles.size());
    }

    public OWLDataProperty getDataRole(String name) {
	OWLDataProperty dataRole = dataRoles.get(name);
	if (dataRole == null) {
	    dataRole = df.getOWLDataProperty(getEntityIRI(name));
	    om.addAxiom(ont, df.getOWLDeclarationAxiom(dataRole));
	    dataRoles.put(name, dataRole);
	}
	return dataRole;
    }

    public OWLDataProperty[] getDataRoles(int n) {
	final OWLDataProperty[] result = new OWLDataProperty[n];
	for (int i = 0; i < n; i++)
	    result[i] = getDataRole();
	return result;
    }

    private IRI getEntityIRI(String name) {
	final IRI ontIRI = ont.getOntologyID().getOntologyIRI();
	return IRI.create(ontIRI + "#" + name);
    }

    public OWLObjectSomeValuesFrom getExistential(
	    OWLObjectPropertyExpression owlObjectPropertyExpression) {
	return df.getOWLObjectSomeValuesFrom(owlObjectPropertyExpression,
		df.getOWLThing());
    }

    public OWLObjectSomeValuesFrom getExistential(String roleName) {
	final OWLObjectProperty role = getRole(roleName);
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
	final OWLObjectProperty role = getRole(roleName);
	return df.getOWLObjectInverseOf(role);
    }

    String getLabel(Object obj) {
	if (obj instanceof OWLEntity)
	    return getLabel((OWLEntity) obj);
	else if (obj instanceof OWLIndividual)
	    return getLabel((OWLIndividual) obj);
	else if (obj instanceof OWLLiteral)
	    return Utils.getHash(((OWLLiteral) obj).getLiteral());
	else
	    return null;
    }

    String getLabel(OWLEntity e) {
	return ol.getLabel(e, 1);
    }

    String getLabel(OWLIndividual i) {
	return ol.getLabel(i, 1);
    }

    public INormalizedOntology getNormalizedOntology() {
	return new Normalizer(ont);
    }

    public OWLOntology getOntology() {
	return ont;
    }

    public OntologyLabeler getOntologyLabel() {
	return ol;
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
	final OWLObjectProperty[] result = new OWLObjectProperty[n];
	for (int i = 0; i < n; i++)
	    result[i] = getRole();
	return result;
    }

    public String getRule(Rule rule) {
	return getRule(rule.format, rule.args);
    }

    public String getRule(String ruleFormat, Object... cls) {
	final Object[] args = new String[cls.length];
	for (int i = 0; i < cls.length; i++)
	    args[i] = getLabel(cls[i]);
	return String.format(ruleFormat, args);
    }

}

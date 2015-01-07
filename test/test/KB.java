package test;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;
import hybrid.query.views.Rules;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
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
	private OWLDataFactory df;

	private OWLOntologyManager om;

	private OWLOntology ont;

	private OntologyLabel ol;

	KB() throws OWLOntologyCreationException {
		om = OWLManager.createOWLOntologyManager();
		df = om.getOWLDataFactory();
		ont = om.createOntology(IRI.generateDocumentIRI());
		OWLAnnotationProperty lblAnnotProp = om.getOWLDataFactory()
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		ol = new OntologyLabel(om.createOntology(), lblAnnotProp, new CollectionsManager());
	}
	
	String getLabel(OWLEntity e) {
		return ol.getLabel(e, 1);
	}
	
	void addRule(String rule) {
		Rules.addRule(rule);
	}

	void addAssertion(OWLClass concept, OWLIndividual individual) {
		om.addAxiom(ont, df.getOWLClassAssertionAxiom(concept, individual));
	}

	void addAssertion(OWLObjectProperty role, OWLIndividual ind1,
			OWLIndividual ind2) {
		om.addAxiom(ont,
				df.getOWLObjectPropertyAssertionAxiom(role, ind1, ind2));
	}

	void addDisjunction(OWLClassExpression b1, OWLClassExpression b2) {
		om.addAxiom(ont, df.getOWLDisjointClassesAxiom(b1, b2));
	}

	void addDisjunction(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		om.addAxiom(ont, df.getOWLDisjointObjectPropertiesAxiom(q1, q2));
	}

	void addSubsumption(OWLClassExpression b1, OWLClassExpression b2) {
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(b1, b2));
	}

	void addSubsumption(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		om.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(q1, q2));
	}

	OWLClass getConcept(String name) {
		OWLClass concept = df.getOWLClass(getEntityIRI(name));
		om.addAxiom(ont, df.getOWLDeclarationAxiom(concept));
		return concept;
	}

	private IRI getEntityIRI(String name) {
		IRI ontIRI = ont.getOntologyID().getOntologyIRI();
		return IRI.create(ontIRI + "#" + name);
	}

	OWLObjectSomeValuesFrom getExistential(OWLObjectPropertyExpression owlObjectPropertyExpression) {
		return df.getOWLObjectSomeValuesFrom(owlObjectPropertyExpression, df.getOWLThing());
	}

	OWLIndividual getIndividual(String name) {
		OWLIndividual individual = df.getOWLNamedIndividual(getEntityIRI(name));
		return individual;
	}

	OWLObjectPropertyExpression getInverse(OWLObjectProperty role) {
		return df.getOWLObjectInverseOf(role);
	}
    
	OWLOntology getOntology() {
		return ont;
	}

	OWLObjectProperty getRole(String name) {
		OWLObjectProperty role = df.getOWLObjectProperty(getEntityIRI(name));
		om.addAxiom(ont, df.getOWLDeclarationAxiom(role));
		return role;
	}

	void clear() throws OWLOntologyCreationException {
		ont = om.createOntology(IRI.generateDocumentIRI());
		Rules.resetRules();
	}

	String getLabel(OWLIndividual c1) {
		return ol.getLabel(c1, 1);
	}

	String getLabel(String rule) {
		return ol.getLabel(rule, 1);
	}
}

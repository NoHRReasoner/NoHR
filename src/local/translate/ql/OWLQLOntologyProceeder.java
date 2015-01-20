package local.translate.ql;

import java.util.Iterator;
import java.util.List;

import local.translate.CollectionsManager;
import local.translate.OWLOntologyProceeder;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class OWLQLOntologyProceeder implements OWLOntologyProceeder {

	private CollectionsManager cm;

	private TBoxGraph graph;

	private INormalizedOntology normalizedOntology;

	private RuleCreatorQL ruleCreatorQL;

	public OWLQLOntologyProceeder(CollectionsManager _cm,
			RuleCreatorQL _ruleCreator, OWLOntology ontology,
			OWLDataFactory dataFactory, OWLOntologyManager ontologyManager) {
		this.cm = _cm;
		this.ruleCreatorQL = _ruleCreator;
		this.normalizedOntology = new NormalizedOntology(ontology);
		this.graph = new BasicTBoxGraph(normalizedOntology, dataFactory);
	}

	public void proceed() {
		cm.setIsAnyDisjointStatement(normalizedOntology.hasDisjointStatement());
		ruleCreatorQL.e();
		translate();
		for (OWLEntity e : graph.getUnsatisfiableEntities())
			if (e instanceof OWLClass)
				ruleCreatorQL.i1((OWLClass) e);
			else if (e instanceof OWLObjectProperty)
				ruleCreatorQL.i2((OWLObjectProperty) e);
		for (OWLObjectProperty p : graph.getIrreflexiveRoles())
			ruleCreatorQL.ir(p);
	}

	private void translate() {
		for (OWLClassAssertionAxiom f : normalizedOntology
				.getConceptAssertions())
			translate(f);
		for (OWLObjectPropertyAssertionAxiom f : normalizedOntology
				.getRoleAssertions())
			translate(f);
		for (OWLDataPropertyAssertionAxiom f : normalizedOntology
				.getDataAssertions())
			translate(f);
		for (OWLSubClassOfAxiom s : normalizedOntology.getConceptSubsumptions())
			translate(s);
		for (OWLDisjointClassesAxiom d : normalizedOntology
				.getConceptDisjunctions())
			translate(d);
		for (OWLSubObjectPropertyOfAxiom s : normalizedOntology
				.getRoleSubsumptions())
			translate(s);
		for (OWLDisjointObjectPropertiesAxiom d : normalizedOntology
				.getRoleDisjunctions())
			translate(d);
	}

	private void translate(OWLDataPropertyAssertionAxiom f) {
		OWLDataProperty dataProperty = (OWLDataProperty) f.getProperty();
		OWLIndividual individual = f.getSubject();
		OWLLiteral value = f.getObject();
		ruleCreatorQL.translateDataPropertyAssertion(dataProperty, individual,
				value);
	}

	private void translate(OWLClassAssertionAxiom f) {
		OWLClass a = f.getClassExpression().asOWLClass();
		if (a.isOWLThing() || a.isOWLNothing())
			return;
		OWLIndividual i = f.getIndividual();
		ruleCreatorQL.a1(a, i);
	}

	private void translate(OWLDisjointClassesAxiom alpha) {
		List<OWLClassExpression> cls = alpha.getClassExpressionsAsList();
		ruleCreatorQL.n1(cls.get(0), cls.get(1));
	}

	private void translate(OWLDisjointObjectPropertiesAxiom alpha) {
		Iterator<OWLObjectPropertyExpression> props = alpha.getProperties()
				.iterator();
		OWLObjectPropertyExpression q1 = props.next();
		OWLObjectPropertyExpression q2 = props.next();
		ruleCreatorQL.n2(q1, q2);
	}

	private void translate(OWLObjectPropertyAssertionAxiom f) {
		OWLObjectPropertyExpression q = f.getProperty();
		if (q.isOWLBottomObjectProperty() || q.isOWLTopObjectProperty())
			return;
		ruleCreatorQL.a2(f);
	}

	private void translate(OWLSubClassOfAxiom alpha) {
		OWLClassExpression b = alpha.getSubClass();
		OWLClassExpression c = alpha.getSuperClass();
		ruleCreatorQL.s1(b, c);
	}

	private void translate(OWLSubObjectPropertyOfAxiom alpha) {
		OWLObjectPropertyExpression q1 = alpha.getSubProperty();
		OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
		ruleCreatorQL.s2(q1, q2);
	}
}

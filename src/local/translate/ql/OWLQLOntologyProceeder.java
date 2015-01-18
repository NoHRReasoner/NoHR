package local.translate.ql;

import java.util.Iterator;
import java.util.Set;

import local.translate.CollectionsManager;
import local.translate.OWLOntologyProceeder;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

public class OWLQLOntologyProceeder implements OWLOntologyProceeder {

	private CollectionsManager cm;

	private OWLDataFactory df;

	private TBoxGraph graph;

	private OWLOntologyManager om;

	private OWLOntology ontology;

	private RuleCreatorQL ruleCreatorQL;

	public OWLQLOntologyProceeder(CollectionsManager _cm,
			RuleCreatorQL _ruleCreator, OWLOntology ontology,
			OWLDataFactory dataFactory, OWLOntologyManager ontologyManager) {
		this.cm = _cm;
		this.ruleCreatorQL = _ruleCreator;
		this.ontology = ontology;
		this.om = ontologyManager;
		this.df = om.getOWLDataFactory();
		this.graph = new BasicTBoxGraph(ontology, dataFactory);
	}

	//TODO ensure univocal
	private OWLObjectProperty getNewRole(int hashCode) {
		IRI ontologyIri = ontology.getOntologyID().getOntologyIRI();
		String fragment = String.valueOf(hashCode);
		IRI ruleIri = IRI.create(ontologyIri + "#" + fragment);
		return df.getOWLObjectProperty(ontologyIri);
	}

	private boolean hasDisjointStatement(OWLOntology ontology) {
		int dc = 0;
		for (OWLSubClassOfAxiom s : ontology.getAxioms(AxiomType.SUBCLASS_OF))
			if (s.getSuperClass() instanceof OWLObjectComplementOf)
				dc++;
		dc += ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES);
		dc += ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES);
		dc += ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
		dc += ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY);
		return dc > 0;
	}

	public void proceed() {
		cm.setIsAnyDisjointStatement(hasDisjointStatement(ontology));
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

	private OWLClassExpression some(OWLObjectPropertyExpression q) {
		return df.getOWLObjectSomeValuesFrom(q.getSimplified(),
				df.getOWLThing());
	}

	private void translate() {
		for (OWLClassAssertionAxiom a : ontology
				.getAxioms(AxiomType.CLASS_ASSERTION))
			translate(a);
		for (OWLObjectPropertyAssertionAxiom a : ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION))
			translate(a);
		for (OWLSubClassOfAxiom a : ontology.getAxioms(AxiomType.SUBCLASS_OF))
			translate(a);
		for (OWLSubObjectPropertyOfAxiom a : ontology
				.getAxioms(AxiomType.SUB_OBJECT_PROPERTY))
			translate(a);
		for (OWLEquivalentClassesAxiom a : ontology
				.getAxioms(AxiomType.EQUIVALENT_CLASSES))
			for (OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms())
				translate(s);
		for (OWLDisjointClassesAxiom a : ontology
				.getAxioms(AxiomType.DISJOINT_CLASSES))
			for (OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms())
				translate(s);
		for (OWLInverseObjectPropertiesAxiom a : ontology
				.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES))
			for (OWLSubObjectPropertyOfAxiom s : a
					.asSubObjectPropertyOfAxioms())
				translate(s);
		for (OWLDisjointObjectPropertiesAxiom a : ontology
				.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES))
			translate(a);
		for (OWLEquivalentObjectPropertiesAxiom a : ontology
				.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES))
			for (OWLSubObjectPropertyOfAxiom s : a
					.asSubObjectPropertyOfAxioms())
				translate(s);
		for (OWLObjectPropertyDomainAxiom a : ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN))
			translate(a.asOWLSubClassOfAxiom());
		for (OWLObjectPropertyRangeAxiom a : ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE))
			translate(a);
		for (OWLSymmetricObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY))
			for (OWLSubObjectPropertyOfAxiom s : a.asSubPropertyAxioms())
				translate(s);
		for (OWLReflexiveObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.REFLEXIVE_OBJECT_PROPERTY))
			translate(a);
		for (OWLIrreflexiveObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY))
			translate(a);
		for (OWLAsymmetricObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.ASYMMETRIC_OBJECT_PROPERTY))
			translate(a);
	}

	private void translate(OWLAsymmetricObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		translate(df.getOWLDisjointObjectPropertiesAxiom(q, q
				.getInverseProperty().getSimplified()));
	}

	private void translate(OWLClassAssertionAxiom f) {
		OWLClass a = f.getClassExpression().asOWLClass();
		if (a.isOWLThing() || a.isOWLNothing())
			return;
		OWLIndividual i = f.getIndividual();
		ruleCreatorQL.a1(a, i);
	}

	private void translate(OWLDisjointObjectPropertiesAxiom alpha) {
		Set<OWLObjectPropertyExpression> props = alpha.getProperties();
		Iterator<OWLObjectPropertyExpression> propsIt1 = props.iterator();
		while (propsIt1.hasNext()) {
			OWLObjectPropertyExpression q1 = propsIt1.next();
			if (q1.isOWLBottomObjectProperty())
				continue;
			Iterator<OWLObjectPropertyExpression> propsIt2 = props.iterator();
			while (propsIt2.hasNext()) {
				OWLObjectPropertyExpression q2 = propsIt2.next();
				if (q2.isOWLBottomObjectProperty())
					continue;
				if (!q1.equals(q2))
					if (q1.isOWLTopObjectProperty())
						ruleCreatorQL.n2(q2, q2);
					else if (q2.isOWLTopObjectProperty())
						ruleCreatorQL.n2(q1, q1);
					else
						ruleCreatorQL.n2(q1, q2);
			}
		}
	}

	private void translate(OWLIrreflexiveObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		translate(df.getOWLSubClassOfAxiom(some(q),
				some(q.getInverseProperty()).getObjectComplementOf()));

	}

	private void translate(OWLObjectPropertyAssertionAxiom f) {
		OWLObjectPropertyExpression q = f.getProperty();
		if (q.isOWLBottomObjectProperty() || q.isOWLTopObjectProperty())
			return;
		OWLIndividual s = f.getSubject();
		OWLIndividual t = f.getObject();
		ruleCreatorQL.a2(f);
	}

	private void translate(OWLObjectPropertyRangeAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		OWLClassExpression c = alpha.getRange();
		translate(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()), c));
	}

	private void translate(OWLReflexiveObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		translate(df.getOWLSubClassOfAxiom(some(q),
				some(q.getInverseProperty())));
		translate(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()),
				some(q)));
	}

	private void translate(OWLSubClassOfAxiom alpha) {
		OWLClassExpression b = alpha.getSubClass();
		OWLClassExpression c = alpha.getSuperClass();
		if (b.isOWLNothing() || c.isOWLThing())
			return;
		if (c.isOWLNothing()) // BASE CASE
			ruleCreatorQL.n1(b, b);
		else if (c instanceof OWLClass) // BASE CASE
			ruleCreatorQL.s1(b, c);
		else if (c instanceof OWLObjectIntersectionOf) {
			OWLObjectIntersectionOf c0 = (OWLObjectIntersectionOf) c;
			Set<OWLClassExpression> ops = c0.getOperands();
			for (OWLClassExpression ci : ops)
				translate(df.getOWLSubClassOfAxiom(b, ci));
		} else if (c instanceof OWLObjectComplementOf) { // BASE CASE
			OWLObjectComplementOf c0 = (OWLObjectComplementOf) c;
			OWLClassExpression b1 = c0.getOperand();
			if (b1.isOWLNothing())
				return;
			if (b1.isOWLThing())
				ruleCreatorQL.n1(b, b);
			else if (b.isOWLThing())
				ruleCreatorQL.n1(b1, b1);
			else
				ruleCreatorQL.n1(b, b1);
		} else if (c instanceof OWLObjectSomeValuesFrom) {
			OWLObjectSomeValuesFrom b0 = (OWLObjectSomeValuesFrom) c;
			OWLObjectPropertyExpression q = b0.getProperty();
			OWLClassExpression a = b0.getFiller();
			if (a.isOWLThing())
				ruleCreatorQL.s1(b, c);
			else {
				OWLObjectProperty pnew = getNewRole(alpha.hashCode());
				translate(df.getOWLSubObjectPropertyOfAxiom(pnew, q));
				translate(df.getOWLSubClassOfAxiom(
						some(pnew.getInverseProperty()), a));
				translate(df.getOWLSubClassOfAxiom(b, some(pnew)));
			}
		}
	}

	private void translate(OWLSubObjectPropertyOfAxiom alpha) {
		OWLObjectPropertyExpression q1 = alpha.getSubProperty();
		OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
		if (q1.isOWLBottomObjectProperty() || q2.isOWLTopObjectProperty())
			return;
		if (q2.isOWLBottomObjectProperty())
			ruleCreatorQL.n2(q1, q1);
		else
			ruleCreatorQL.s2(q1, q2);
	}
}

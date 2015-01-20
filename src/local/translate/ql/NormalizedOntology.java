package local.translate.ql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
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
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

public class NormalizedOntology implements INormalizedOntology {

	private Set<OWLClassAssertionAxiom> conceptAssertions;
	
	private Set<OWLDataPropertyAssertionAxiom> dataAssertions;

	private Set<OWLDisjointClassesAxiom> conceptDisjunctions;

	private Set<OWLSubClassOfAxiom> conceptSubsumptions;

	private OWLDataFactory df;

	private boolean hasDisjunction;

	private IRI ontologyIRI;

	private Set<OWLObjectPropertyAssertionAxiom> roleAssertions;

	private Set<OWLDisjointObjectPropertiesAxiom> roleDisjunctions;

	private Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions;

	private Set<OWLClassExpression> unsatisfiableConcepts;

	private Set<OWLObjectPropertyExpression> unsatisfiableRoles;

	public NormalizedOntology(OWLOntology ontology) {
		this.ontologyIRI = ontology.getOntologyID().getOntologyIRI();
		this.df = ontology.getOWLOntologyManager().getOWLDataFactory();
		this.conceptAssertions = new HashSet<OWLClassAssertionAxiom>();
		this.roleAssertions = new HashSet<OWLObjectPropertyAssertionAxiom>();
		this.dataAssertions = new HashSet<OWLDataPropertyAssertionAxiom>();
		this.conceptSubsumptions = new HashSet<OWLSubClassOfAxiom>();
		this.roleSubsumptions = new HashSet<OWLSubObjectPropertyOfAxiom>();
		this.conceptDisjunctions = new HashSet<OWLDisjointClassesAxiom>();
		this.roleDisjunctions = new HashSet<OWLDisjointObjectPropertiesAxiom>();
		this.unsatisfiableConcepts = new HashSet<OWLClassExpression>();
		this.unsatisfiableRoles = new HashSet<OWLObjectPropertyExpression>();
		normalize(ontology);
	}

	@Override
	public Set<OWLClassAssertionAxiom> getConceptAssertions() {
		return conceptAssertions;
	}

	@Override
	public Set<OWLDisjointClassesAxiom> getConceptDisjunctions() {
		return conceptDisjunctions;
	}

	@Override
	public Set<OWLSubClassOfAxiom> getConceptSubsumptions() {
		return conceptSubsumptions;
	}

	// TODO ensure univocal
	private OWLObjectProperty getNewRole(int hashCode) {
		String fragment = String.valueOf(hashCode);
		IRI ruleIri = IRI.create(ontologyIRI + "#" + fragment);
		return df.getOWLObjectProperty(ontologyIRI);
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions() {
		return roleAssertions;
	}

	@Override
	public Set<OWLDisjointObjectPropertiesAxiom> getRoleDisjunctions() {
		return roleDisjunctions;
	}

	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getRoleSubsumptions() {
		return roleSubsumptions;
	}

	@Override
	public Set<OWLClassExpression> getUnsatisfiableConcepts() {
		return unsatisfiableConcepts;
	}

	@Override
	public Set<OWLObjectPropertyExpression> getUnsatisfiableRoles() {
		return unsatisfiableRoles;
	}

	@Override
	public boolean hasDisjointStatement() {
		return hasDisjunction;
	}

	private void normalize(OWLAsymmetricObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		normalize(df.getOWLDisjointObjectPropertiesAxiom(q, q
				.getInverseProperty().getSimplified()));
	}

	private void normalize(OWLDisjointObjectPropertiesAxiom alpha) {
		hasDisjunction = true;
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
				if (!q1.equals(q2)) {
					if (q1.isOWLTopObjectProperty())
						unsatisfiableRoles.add(q2);
					else if (q2.isOWLTopObjectProperty())
						unsatisfiableRoles.add(q2);
					else
						roleDisjunctions.add(df
								.getOWLDisjointObjectPropertiesAxiom(q1, q2));
				}
			}
		}
	}

	private void normalize(OWLIrreflexiveObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		normalize(df.getOWLSubClassOfAxiom(some(q),
				some(q.getInverseProperty()).getObjectComplementOf()));

	}

	private void normalize(OWLObjectPropertyRangeAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		OWLClassExpression c = alpha.getRange();
		normalize(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()), c));
	}

	private void normalize(OWLOntology ontology) {
		conceptAssertions.addAll(ontology.getAxioms(AxiomType.CLASS_ASSERTION));
		roleAssertions.addAll(ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION));
		dataAssertions.addAll(ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION));
		for (OWLSubClassOfAxiom a : ontology.getAxioms(AxiomType.SUBCLASS_OF))
			normalize(a);
		for (OWLSubObjectPropertyOfAxiom a : ontology
				.getAxioms(AxiomType.SUB_OBJECT_PROPERTY))
			normalize(a);
		for (OWLDisjointObjectPropertiesAxiom a : ontology
				.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES))
			normalize(a);
		for (OWLEquivalentClassesAxiom a : ontology
				.getAxioms(AxiomType.EQUIVALENT_CLASSES))
			for (OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms())
				normalize(s);
		for (OWLDisjointClassesAxiom a : ontology
				.getAxioms(AxiomType.DISJOINT_CLASSES))
			for (OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms())
				normalize(s);
		for (OWLInverseObjectPropertiesAxiom a : ontology
				.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES))
			for (OWLSubObjectPropertyOfAxiom s : a
					.asSubObjectPropertyOfAxioms())
				normalize(s);
		for (OWLEquivalentObjectPropertiesAxiom a : ontology
				.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES))
			for (OWLSubObjectPropertyOfAxiom s : a
					.asSubObjectPropertyOfAxioms())
				normalize(s);
		for (OWLObjectPropertyDomainAxiom a : ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN))
			normalize(a.asOWLSubClassOfAxiom());
		for (OWLObjectPropertyRangeAxiom a : ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE))
			normalize(a);
		for (OWLSymmetricObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY))
			for (OWLSubObjectPropertyOfAxiom s : a.asSubPropertyAxioms())
				normalize(s);
		for (OWLReflexiveObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.REFLEXIVE_OBJECT_PROPERTY))
			normalize(a);
		for (OWLIrreflexiveObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY))
			normalize(a);
		for (OWLAsymmetricObjectPropertyAxiom a : ontology
				.getAxioms(AxiomType.ASYMMETRIC_OBJECT_PROPERTY))
			normalize(a);
	}

	private void normalize(OWLReflexiveObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		normalize(df.getOWLSubClassOfAxiom(some(q),
				some(q.getInverseProperty())));
		normalize(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()),
				some(q)));
	}

	private void normalize(OWLSubClassOfAxiom alpha) {
		OWLClassExpression b = alpha.getSubClass();
		OWLClassExpression c = alpha.getSuperClass();
		if (b.isOWLNothing() || c.isOWLThing())
			return;
		if (c.isOWLNothing()) // BASE CASE
			unsatisfiableConcepts.add(b);
		else if (c instanceof OWLClass) // BASE CASE
			conceptSubsumptions.add(alpha);
		else if (c instanceof OWLObjectIntersectionOf) {
			OWLObjectIntersectionOf c0 = (OWLObjectIntersectionOf) c;
			Set<OWLClassExpression> ops = c0.getOperands();
			for (OWLClassExpression ci : ops)
				normalize(df.getOWLSubClassOfAxiom(b, ci));
		} else if (c instanceof OWLObjectComplementOf) { // BASE CASE
			hasDisjunction = true;
			OWLObjectComplementOf c0 = (OWLObjectComplementOf) c;
			OWLClassExpression b1 = c0.getOperand();
			if (b1.isOWLNothing())
				return;
			if (b1.isOWLThing())
				unsatisfiableConcepts.add(b);
			else if (b.isOWLThing())
				unsatisfiableConcepts.add(b1);
			else
				conceptDisjunctions.add(df.getOWLDisjointClassesAxiom(b, b1));
		} else if (c instanceof OWLObjectSomeValuesFrom) {
			OWLObjectSomeValuesFrom b0 = (OWLObjectSomeValuesFrom) c;
			OWLObjectPropertyExpression q = b0.getProperty();
			OWLClassExpression a = b0.getFiller();
			if (a.isOWLThing()) // BASE CASE
				conceptSubsumptions.add(df.getOWLSubClassOfAxiom(b, c));
			else {
				OWLObjectProperty pnew = getNewRole(alpha.hashCode());
				normalize(df.getOWLSubObjectPropertyOfAxiom(pnew, q));
				normalize(df.getOWLSubClassOfAxiom(
						some(pnew.getInverseProperty()), a));
				normalize(df.getOWLSubClassOfAxiom(b, some(pnew)));
			}
		}
	}

	private void normalize(OWLSubObjectPropertyOfAxiom alpha) {
		OWLObjectPropertyExpression q1 = alpha.getSubProperty();
		OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
		if (q1.isOWLBottomObjectProperty() || q2.isOWLTopObjectProperty())
			return;
		if (q2.isOWLBottomObjectProperty())
			unsatisfiableRoles.add(q1);
		else
			roleSubsumptions.add(df.getOWLSubObjectPropertyOfAxiom(q1, q2));
	}

	private OWLClassExpression some(OWLObjectPropertyExpression q) {
		return df.getOWLObjectSomeValuesFrom(q.getSimplified(),
				df.getOWLThing());
	}

	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataAssertions() {
		return dataAssertions;
	}

}

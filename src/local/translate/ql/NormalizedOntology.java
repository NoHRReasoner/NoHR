package local.translate.ql;

import java.util.Collection;
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
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

public class NormalizedOntology implements INormalizedOntology {

	private Set<OWLObjectProperty> roles;

	private Set<OWLClassAssertionAxiom> conceptAssertions;

	private Set<OWLDisjointClassesAxiom> conceptDisjunctions;

	private Set<OWLSubClassOfAxiom> conceptSubsumptions;

	private Set<OWLDataPropertyAssertionAxiom> dataAssertions;

	private OWLDataFactory df;

	private boolean hasDisjunction;

	private IRI ontologyIRI;

	private Set<OWLObjectPropertyAssertionAxiom> roleAssertions;

	private Set<OWLNaryPropertyAxiom<?>> roleDisjunctions;

	private Set<OWLSubPropertyAxiom<?>> roleSubsumptions;

	private Set<OWLClassExpression> unsatisfiableConcepts;

	private Set<OWLPropertyExpression<?, ?>> unsatisfiableRoles;

	private Set<OWLClassExpression> subConcepts;

	private Set<OWLClassExpression> superConcepts;

	private Set<OWLClassExpression> disjointConcepts;

	private Set<OWLPropertyExpression> subRules;

	private Set<OWLPropertyExpression> superRules;

	private Set<OWLPropertyExpression> disjointRules;

	public NormalizedOntology(OWLOntology ontology) {
		this.ontologyIRI = ontology.getOntologyID().getOntologyIRI();
		this.df = ontology.getOWLOntologyManager().getOWLDataFactory();
		this.roles = ontology.getObjectPropertiesInSignature();
		this.conceptAssertions = new HashSet<OWLClassAssertionAxiom>();
		this.roleAssertions = new HashSet<OWLObjectPropertyAssertionAxiom>();
		this.dataAssertions = new HashSet<OWLDataPropertyAssertionAxiom>();
		this.conceptSubsumptions = new HashSet<OWLSubClassOfAxiom>();
		this.roleSubsumptions = new HashSet<OWLSubPropertyAxiom<?>>();
		this.conceptDisjunctions = new HashSet<OWLDisjointClassesAxiom>();
		this.roleDisjunctions = new HashSet<OWLNaryPropertyAxiom<?>>();
		this.unsatisfiableConcepts = new HashSet<OWLClassExpression>();
		this.unsatisfiableRoles = new HashSet<OWLPropertyExpression<?, ?>>();
		this.subConcepts = new HashSet<OWLClassExpression>();
		this.superConcepts = new HashSet<OWLClassExpression>();
		this.disjointConcepts = new HashSet<OWLClassExpression>();
		this.subRules = new HashSet<OWLPropertyExpression>();
		this.superRules = new HashSet<OWLPropertyExpression>();
		this.disjointRules = new HashSet<OWLPropertyExpression>();
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

	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataAssertions() {
		return dataAssertions;
	}

	private OWLObjectProperty getNewRole(int hashCode) {
		String fragment = String.valueOf(hashCode);
		IRI ruleIri = IRI.create(ontologyIRI + "#" + fragment);
		return df.getOWLObjectProperty(ruleIri);
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions() {
		return roleAssertions;
	}

	@Override
	public Set<OWLNaryPropertyAxiom<?>> getRoleDisjunctions() {
		return roleDisjunctions;
	}

	@Override
	public Set<OWLSubPropertyAxiom<?>> getRoleSubsumptions() {
		return roleSubsumptions;
	}

	@Override
	public Set<OWLClassExpression> getUnsatisfiableConcepts() {
		return unsatisfiableConcepts;
	}

	@Override
	public Set<OWLPropertyExpression<?, ?>> getUnsatisfiableRoles() {
		return unsatisfiableRoles;
	}

	@Override
	public boolean hasDisjointStatement() {
		return hasDisjunction;
	}

	@Override
	public Set<OWLClassExpression> getSubConcepts() {
		return subConcepts;
	}

	@Override
	public Set<OWLClassExpression> getSuperConcepts() {
		return superConcepts;
	}

	@Override
	public Set<OWLClassExpression> getDisjointConcepts() {
		return disjointConcepts;
	}

	@Override
	public Set<OWLPropertyExpression> getSubRules() {
		return subRules;
	}

	@Override
	public Set<OWLPropertyExpression> getSuperRoles() {
		return superRules;
	}

	@Override
	public Set<OWLPropertyExpression> getDisjointRules() {
		return disjointRules;
	}

	private void normalize(OWLAsymmetricObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		normalize(df.getOWLDisjointObjectPropertiesAxiom(q, q
				.getInverseProperty().getSimplified()));
	}

	private void normalize(OWLIrreflexiveObjectPropertyAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		normalize(df.getOWLSubClassOfAxiom(some(q),
				some(q.getInverseProperty()).getObjectComplementOf()));

	}

	private void normalize(OWLNaryPropertyAxiom alpha) {
		hasDisjunction = true;
		Set<OWLPropertyExpression> props = alpha.getProperties();
		Iterator<OWLPropertyExpression> propsIt1 = props.iterator();
		while (propsIt1.hasNext()) {
			OWLPropertyExpression q1 = propsIt1.next();
			disjointRules.add(q1);
			if (q1.isBottomEntity())
				continue;
			Iterator<OWLPropertyExpression> propsIt2 = props.iterator();
			while (propsIt2.hasNext()) {
				OWLPropertyExpression q2 = propsIt2.next();
				if (q2.isBottomEntity())
					continue;
				if (!q1.equals(q2)) {
					if (q1.isTopEntity())
						unsatisfiableRoles.add(q2);
					else if (q2.isTopEntity())
						unsatisfiableRoles.add(q2);
					else if (q1 instanceof OWLObjectPropertyExpression
							&& q2 instanceof OWLObjectPropertyExpression)
						roleDisjunctions.add(df
								.getOWLDisjointObjectPropertiesAxiom(
										(OWLObjectPropertyExpression) q1,
										(OWLObjectPropertyExpression) q2));
					else if (q1 instanceof OWLDataPropertyExpression
							&& q2 instanceof OWLDataPropertyExpression)
						roleDisjunctions.add(df
								.getOWLDisjointDataPropertiesAxiom(
										(OWLDataPropertyExpression) q1,
										(OWLDataPropertyExpression) q2));
				}
			}
		}

	}

	private void normalize(OWLObjectPropertyRangeAxiom alpha) {
		OWLObjectPropertyExpression q = alpha.getProperty();
		OWLClassExpression c = alpha.getRange();
		normalize(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()), c));
	}

	private void normalize(OWLOntology ontology) {
		conceptAssertions.addAll(ontology.getAxioms(AxiomType.CLASS_ASSERTION));
		roleAssertions.addAll(ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION));
		dataAssertions.addAll(ontology
				.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION));
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
		for (OWLSubDataPropertyOfAxiom a : ontology
				.getAxioms(AxiomType.SUB_DATA_PROPERTY))
			normalize(a);
		for (OWLDisjointDataPropertiesAxiom a : ontology
				.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES))
			normalize(a);
		for (OWLEquivalentDataPropertiesAxiom a : ontology
				.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES))
			for (OWLSubDataPropertyOfAxiom s : a.asSubDataPropertyOfAxioms())
				normalize(s);
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
		if (b.isOWLNothing() || c.isOWLThing()
				|| (b.isOWLThing() && !(c instanceof OWLObjectComplementOf)))
			return;
		if (b instanceof OWLDataSomeValuesFrom
				|| c instanceof OWLDataSomeValuesFrom)
			return;
		if (c.isOWLNothing()) // BASE CASE
			unsatisfiableConcepts.add(b);
		else if (c instanceof OWLClass) { // BASE CASE
			subConcepts.add(b);
			superConcepts.add(c);
			conceptSubsumptions.add(alpha);
		} else if (c instanceof OWLObjectIntersectionOf) {
			OWLObjectIntersectionOf c0 = (OWLObjectIntersectionOf) c;
			Set<OWLClassExpression> ops = c0.getOperands();
			for (OWLClassExpression ci : ops)
				normalize(df.getOWLSubClassOfAxiom(b, ci));
		} else if (c instanceof OWLObjectComplementOf) { // BASE CASE
			hasDisjunction = true;
			OWLObjectComplementOf c0 = (OWLObjectComplementOf) c;
			OWLClassExpression b1 = c0.getOperand();
			disjointConcepts.add(b);
			disjointConcepts.add(b1);
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
			if (a.isOWLThing()) {// BASE CASE
				subConcepts.add(b);
				superConcepts.add(c);
				conceptSubsumptions.add(df.getOWLSubClassOfAxiom(b, c));
			} else {
				OWLObjectProperty pnew = getNewRole(alpha.hashCode());
				normalize(df.getOWLSubObjectPropertyOfAxiom(pnew, q));
				normalize(df.getOWLSubClassOfAxiom(
						some(pnew.getInverseProperty()), a));
				normalize(df.getOWLSubClassOfAxiom(b, some(pnew)));
			}
		}
	}

	private void normalize(OWLSubPropertyAxiom alpha) {
		OWLPropertyExpression q1 = alpha.getSubProperty();
		OWLPropertyExpression q2 = alpha.getSuperProperty();
		subRules.add(q1);
		superRules.add(q2);
		if (q1.isBottomEntity() || q2.isTopEntity() || q1.isTopEntity())
			return;
		if (q2.isBottomEntity())
			unsatisfiableRoles.add(q1);
		else if (q1 instanceof OWLObjectPropertyExpression
				&& q2 instanceof OWLObjectPropertyExpression)
			roleSubsumptions.add(df.getOWLSubObjectPropertyOfAxiom(
					(OWLObjectPropertyExpression) q1,
					(OWLObjectPropertyExpression) q2));
		else if (q1 instanceof OWLDataPropertyExpression
				&& q2 instanceof OWLDataPropertyExpression)
			roleSubsumptions.add(df.getOWLSubDataPropertyOfAxiom(
					(OWLDataPropertyExpression) q1,
					(OWLDataPropertyExpression) q2));
	}

	private OWLClassExpression some(OWLObjectPropertyExpression q) {
		return df.getOWLObjectSomeValuesFrom(q.getSimplified(),
				df.getOWLThing());
	}

	@Override
	public Set<OWLObjectProperty> getRoles() {
		return roles;
	}

}

package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

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

import com.google.common.base.Optional;

import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class QLNormalizedOntologyImpl implements QLNormalizedOntology {

    private final Set<OWLDisjointClassesAxiom> conceptDisjunctions;

    private final Set<OWLSubClassOfAxiom> conceptSubsumptions;

    private final OWLDataFactory df;

    private final Set<OWLClassExpression> disjointConcepts;

    private final Set<OWLProperty> disjointRoles;

    private boolean hasDisjunction;

    private final OWLOntology ontology;

    private final Optional<IRI> ontologyIRI;

    private final Set<OWLNaryPropertyAxiom<?>> roleDisjunctions;

    private final Set<OWLSubPropertyAxiom<?>> roleSubsumptions;

    private final Set<OWLClassExpression> subConcepts;

    private final Set<OWLProperty> subRoles;

    private final Set<OWLClassExpression> superConcepts;

    private final Set<OWLProperty> superRoles;

    private final Set<OWLClassExpression> unsatisfiableConcepts;

    private final Set<OWLPropertyExpression> unsatisfiableRoles;

    public QLNormalizedOntologyImpl(OWLOntology ontology) {
	this.ontology = ontology;
	ontologyIRI = ontology.getOntologyID().getOntologyIRI();
	df = ontology.getOWLOntologyManager().getOWLDataFactory();
	conceptSubsumptions = new HashSet<OWLSubClassOfAxiom>();
	roleSubsumptions = new HashSet<OWLSubPropertyAxiom<?>>();
	conceptDisjunctions = new HashSet<OWLDisjointClassesAxiom>();
	roleDisjunctions = new HashSet<OWLNaryPropertyAxiom<?>>();
	unsatisfiableConcepts = new HashSet<OWLClassExpression>();
	unsatisfiableRoles = new HashSet<OWLPropertyExpression>();
	subConcepts = new HashSet<OWLClassExpression>();
	superConcepts = new HashSet<OWLClassExpression>();
	disjointConcepts = new HashSet<OWLClassExpression>();
	subRoles = new HashSet<OWLProperty>();
	superRoles = new HashSet<OWLProperty>();
	disjointRoles = new HashSet<OWLProperty>();
	normalize(ontology);
    }

    @Override
    public Set<OWLClassAssertionAxiom> getConceptAssertions() {
	return ontology.getAxioms(AxiomType.CLASS_ASSERTION);
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
	return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLClassExpression> getDisjointConcepts() {
	return disjointConcepts;
    }

    @Override
    public Set<OWLProperty> getDisjointRoles() {
	return disjointRoles;
    }

    private OWLObjectProperty getNewRole(int hashCode) {
	final String fragment = String.valueOf(hashCode);
	final IRI ruleIri = IRI.create(ontologyIRI + "#" + fragment);
	final OWLDataFactory df = ontology.getOWLOntologyManager()
		.getOWLDataFactory();
	return df.getOWLObjectProperty(ruleIri);
    }

    @Override
    public OWLOntology getOriginalOntology() {
	return ontology;
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions() {
	return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLNaryPropertyAxiom<?>> getRoleDisjunctions() {
	return roleDisjunctions;
    }

    @Override
    public Set<OWLObjectProperty> getRoles() {
	return ontology.getObjectPropertiesInSignature();
    }

    @Override
    public Set<OWLSubPropertyAxiom<?>> getRoleSubsumptions() {
	return roleSubsumptions;
    }

    @Override
    public Set<OWLClassExpression> getSubConcepts() {
	return subConcepts;
    }

    @Override
    public Set<OWLProperty> getSubRoles() {
	return subRoles;
    }

    @Override
    public Set<OWLClassExpression> getSuperConcepts() {
	return superConcepts;
    }

    @Override
    public Set<OWLProperty> getSuperRoles() {
	return superRoles;
    }

    @Override
    public Set<OWLClassExpression> getUnsatisfiableConcepts() {
	return unsatisfiableConcepts;
    }

    @Override
    public Set<OWLPropertyExpression> getUnsatisfiableRoles() {
	return unsatisfiableRoles;
    }

    @Override
    public boolean hasDisjointStatement() {
	return hasDisjunction;
    }

    @Override
    public boolean isSub(OWLClassExpression ce) {
	return subConcepts.contains(ce);
    }

    @Override
    public boolean isSub(OWLPropertyExpression pe) {
	return subRoles.contains(pe);
    }

    @Override
    public boolean isSuper(OWLClassExpression ce) {
	return superConcepts.contains(ce);
    }

    @Override
    public boolean isSuper(OWLPropertyExpression pe) {
	return superRoles.contains(pe);
    }

    private void normalize(OWLAsymmetricObjectPropertyAxiom alpha) {
	final OWLObjectPropertyExpression q = alpha.getProperty();
	normalize(df.getOWLDisjointObjectPropertiesAxiom(q, q
		.getInverseProperty().getSimplified()));
    }

    private void normalize(OWLIrreflexiveObjectPropertyAxiom alpha) {
	final OWLObjectPropertyExpression q = alpha.getProperty();
	normalize(df.getOWLSubClassOfAxiom(some(q),
		some(q.getInverseProperty()).getObjectComplementOf()));

    }

    private void normalize(OWLNaryPropertyAxiom alpha) {
	hasDisjunction = true;
	final Set<OWLPropertyExpression> props = alpha.getProperties();
	final Iterator<OWLPropertyExpression> propsIt1 = props.iterator();
	while (propsIt1.hasNext()) {
	    final OWLPropertyExpression q1 = propsIt1.next();
	    final OWLProperty p = DLUtils.getRoleName(q1);
	    disjointRoles.add(p);
	    subRoles.add(p);
	    if (q1.isBottomEntity())
		continue;
	    final Iterator<OWLPropertyExpression> propsIt2 = props.iterator();
	    while (propsIt2.hasNext()) {
		final OWLPropertyExpression q2 = propsIt2.next();
		if (q2.isBottomEntity())
		    continue;
		if (!q1.equals(q2))
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

    private void normalize(OWLObjectPropertyRangeAxiom alpha) {
	final OWLObjectPropertyExpression q = alpha.getProperty();
	final OWLClassExpression c = alpha.getRange();
	normalize(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()), c));
    }

    private void normalize(OWLOntology ontology) {
	RuntimesLogger.start("ontology normalization");
	for (final OWLSubClassOfAxiom a : ontology
		.getAxioms(AxiomType.SUBCLASS_OF))
	    normalize(a);
	for (final OWLSubObjectPropertyOfAxiom a : ontology
		.getAxioms(AxiomType.SUB_OBJECT_PROPERTY))
	    normalize(a);
	for (final OWLDisjointObjectPropertiesAxiom a : ontology
		.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES))
	    normalize(a);
	for (final OWLEquivalentClassesAxiom a : ontology
		.getAxioms(AxiomType.EQUIVALENT_CLASSES))
	    for (final OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms())
		normalize(s);
	for (final OWLDisjointClassesAxiom a : ontology
		.getAxioms(AxiomType.DISJOINT_CLASSES))
	    for (final OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms())
		normalize(s);
	for (final OWLInverseObjectPropertiesAxiom a : ontology
		.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES))
	    for (final OWLSubObjectPropertyOfAxiom s : a
		    .asSubObjectPropertyOfAxioms())
		normalize(s);
	for (final OWLEquivalentObjectPropertiesAxiom a : ontology
		.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES))
	    for (final OWLSubObjectPropertyOfAxiom s : a
		    .asSubObjectPropertyOfAxioms())
		normalize(s);
	for (final OWLObjectPropertyDomainAxiom a : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN))
	    normalize(a.asOWLSubClassOfAxiom());
	for (final OWLObjectPropertyRangeAxiom a : ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE))
	    normalize(a);
	for (final OWLSymmetricObjectPropertyAxiom a : ontology
		.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY))
	    for (final OWLSubObjectPropertyOfAxiom s : a.asSubPropertyAxioms())
		normalize(s);
	for (final OWLReflexiveObjectPropertyAxiom a : ontology
		.getAxioms(AxiomType.REFLEXIVE_OBJECT_PROPERTY))
	    normalize(a);
	for (final OWLIrreflexiveObjectPropertyAxiom a : ontology
		.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY))
	    normalize(a);
	for (final OWLAsymmetricObjectPropertyAxiom a : ontology
		.getAxioms(AxiomType.ASYMMETRIC_OBJECT_PROPERTY))
	    normalize(a);
	for (final OWLSubDataPropertyOfAxiom a : ontology
		.getAxioms(AxiomType.SUB_DATA_PROPERTY))
	    normalize(a);
	for (final OWLDisjointDataPropertiesAxiom a : ontology
		.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES))
	    normalize(a);
	for (final OWLEquivalentDataPropertiesAxiom a : ontology
		.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES))
	    for (final OWLSubDataPropertyOfAxiom s : a
		    .asSubDataPropertyOfAxioms())
		normalize(s);
	RuntimesLogger.stop("ontology normalization", "loading");
    }

    private void normalize(OWLReflexiveObjectPropertyAxiom alpha) {
	final OWLObjectPropertyExpression q = alpha.getProperty();
	normalize(df.getOWLSubClassOfAxiom(some(q),
		some(q.getInverseProperty())));
	normalize(df.getOWLSubClassOfAxiom(some(q.getInverseProperty()),
		some(q)));
    }

    private void normalize(OWLSubClassOfAxiom alpha) {
	final OWLClassExpression b = alpha.getSubClass();
	final OWLClassExpression c = alpha.getSuperClass();
	if (b.isOWLNothing() || c.isOWLThing() || b.isOWLThing()
		&& !(c instanceof OWLObjectComplementOf))
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
	    final OWLObjectIntersectionOf c0 = (OWLObjectIntersectionOf) c;
	    final Set<OWLClassExpression> ops = c0.getOperands();
	    for (final OWLClassExpression ci : ops)
		normalize(df.getOWLSubClassOfAxiom(b, ci));
	} else if (c instanceof OWLObjectComplementOf) { // BASE CASE
	    hasDisjunction = true;
	    final OWLObjectComplementOf c0 = (OWLObjectComplementOf) c;
	    final OWLClassExpression b1 = c0.getOperand();
	    disjointConcepts.add(b);
	    disjointConcepts.add(b1);
	    subConcepts.add(b);
	    subConcepts.add(b1);
	    if (b1.isOWLNothing())
		return;
	    if (b1.isOWLThing())
		unsatisfiableConcepts.add(b);
	    else if (b.isOWLThing())
		unsatisfiableConcepts.add(b1);
	    else
		conceptDisjunctions.add(df.getOWLDisjointClassesAxiom(b, b1));
	} else if (c instanceof OWLObjectSomeValuesFrom) {
	    final OWLObjectSomeValuesFrom b0 = (OWLObjectSomeValuesFrom) c;
	    final OWLObjectPropertyExpression q = b0.getProperty();
	    final OWLClassExpression a = b0.getFiller();
	    if (a.isOWLThing()) {// BASE CASE
		subConcepts.add(b);
		superConcepts.add(c);
		conceptSubsumptions.add(df.getOWLSubClassOfAxiom(b, c));
	    } else {
		final OWLObjectProperty pnew = getNewRole(alpha.hashCode());
		normalize(df.getOWLSubObjectPropertyOfAxiom(pnew, q));
		normalize(df.getOWLSubClassOfAxiom(
			some(pnew.getInverseProperty()), a));
		normalize(df.getOWLSubClassOfAxiom(b, some(pnew)));
	    }
	}
    }

    private void normalize(OWLSubPropertyAxiom alpha) {
	final OWLPropertyExpression q1 = alpha.getSubProperty();
	final OWLPropertyExpression q2 = alpha.getSuperProperty();
	subRoles.add(DLUtils.getRoleName(q1));
	superRoles.add(DLUtils.getRoleName(q2));
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

}

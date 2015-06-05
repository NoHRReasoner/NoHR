package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.Iterator;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.CollectionsManager;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator;

public class QLOntologyTranslator implements OntologyTranslator {

    private CollectionsManager cm;

    private TBoxGraph graph;

    private OWLOntologyManager om;

    private INormalizedOntology normalizedOntology;

    private QLAxiomsTranslator ruleCreatorQL;

    private static final String X = "X";
    private static final String Y = "Y";

    public QLOntologyTranslator(CollectionsManager _cm,
	    QLAxiomsTranslator _ruleCreator,
	    INormalizedOntology normalizedOntology, OWLDataFactory dataFactory,
	    OWLOntologyManager ontologyManager) {
	cm = _cm;
	om = ontologyManager;
	ruleCreatorQL = _ruleCreator;
	this.normalizedOntology = normalizedOntology;
	graph = new BasicTBoxGraph(normalizedOntology, dataFactory);
    }

    private void computeNegHeads() {
	for (OWLClassExpression b : normalizedOntology.getSubConcepts())
	    cm.addNegHead(ruleCreatorQL.trNeg(b, X));
	for (OWLClassExpression b : normalizedOntology.getDisjointConcepts())
	    cm.addNegHead(ruleCreatorQL.trNeg(b, X));
	for (OWLPropertyExpression q : normalizedOntology.getSubRoles())
	    cm.addNegHead(ruleCreatorQL.trNeg(q, X, Y));
	for (OWLPropertyExpression q : normalizedOntology.getDisjointRoles())
	    cm.addNegHead(ruleCreatorQL.trNeg(q, X, Y));
	for (OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		cm.addNegHead(ruleCreatorQL.trNeg((OWLClass) e, X));
	    else if (e instanceof OWLProperty)
		cm.addNegHead(ruleCreatorQL.trNeg((OWLProperty) e, X, Y));
	for (OWLObjectProperty p : graph.getIrreflexiveRoles())
	    cm.addNegHead(ruleCreatorQL.trNeg(p, X, Y));
    }

    @Override
    public void proceed() {
	cm.setIsAnyDisjointStatement(normalizedOntology.hasDisjointStatement());
	utils.Tracer.start("ontology translation");
	computeNegHeads();
	utils.Tracer.info("translate");
	translate();
	utils.Tracer.stop("ontology translation", "loading");
	utils.Tracer.start("ontology classification");
	for (OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		ruleCreatorQL.i1((OWLClass) e);
	    else if (e instanceof OWLProperty)
		ruleCreatorQL.i2((OWLProperty) e);
	for (OWLObjectProperty p : graph.getIrreflexiveRoles())
	    ruleCreatorQL.ir(p);
	utils.Tracer.stop("ontology classification", "loading");
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
	for (OWLSubPropertyAxiom<?> s : normalizedOntology
		.getRoleSubsumptions())
	    if (s instanceof OWLSubObjectPropertyOfAxiom)
		translate((OWLSubObjectPropertyOfAxiom) s);
	    else if (s instanceof OWLSubDataPropertyOfAxiom)
		translate((OWLSubDataPropertyOfAxiom) s);
	for (OWLNaryPropertyAxiom<?> d : normalizedOntology
		.getRoleDisjunctions())
	    translate(d);
	for (OWLPropertyExpression p : normalizedOntology.getRoles())
	    if (p instanceof OWLObjectPropertyExpression)
		ruleCreatorQL.e(((OWLObjectPropertyExpression) p)
			.getNamedProperty());
    }

    private void translate(OWLClassAssertionAxiom f) {
	OWLClass a = f.getClassExpression().asOWLClass();
	if (a.isOWLThing() || a.isOWLNothing())
	    return;
	OWLIndividual i = f.getIndividual();
	ruleCreatorQL.a1(a, i);
    }

    private void translate(OWLDataPropertyAssertionAxiom f) {
	OWLDataProperty dataProperty = (OWLDataProperty) f.getProperty();
	if (dataProperty.isOWLTopDataProperty()
		|| dataProperty.isOWLBottomDataProperty())
	    return;
	OWLIndividual individual = f.getSubject();
	OWLLiteral value = f.getObject();
	ruleCreatorQL.translateDataPropertyAssertion(dataProperty, individual,
		value);
    }

    private void translate(OWLDisjointClassesAxiom alpha) {
	List<OWLClassExpression> cls = alpha.getClassExpressionsAsList();
	ruleCreatorQL.n1(cls.get(0), cls.get(1));
    }

    private void translate(OWLNaryPropertyAxiom d) {
	Iterator<OWLPropertyExpression> dIt = d.getProperties().iterator();
	OWLPropertyExpression<?, ?> p1 = dIt.next();
	OWLPropertyExpression<?, ?> p2 = dIt.next();
	ruleCreatorQL.n2(p1, p2);
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

    private void translate(OWLSubDataPropertyOfAxiom alpha) {
	OWLDataPropertyExpression q1 = alpha.getSubProperty();
	OWLDataPropertyExpression q2 = alpha.getSuperProperty();
	ruleCreatorQL.s2(q1, q2);
    }

    private void translate(OWLSubObjectPropertyOfAxiom alpha) {
	OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	ruleCreatorQL.s2(q1, q2);
    }
}

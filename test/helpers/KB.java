package helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.OntologyIndexImpl;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLNormalizedOntology;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLNormalizedOntologyImpl;

public class KB {

    private final Map<String, OWLClass> concepts;

    private final Map<String, OWLDataProperty> dataRoles;

    private final OWLDataFactory df;

    private HybridKB hybridKB;

    private Parser parser;

    private final Map<String, OWLIndividual> individuals;

    private final OntologyLabeler ol;

    private final OWLOntologyManager om;

    private final OWLOntology ontology;

    private final Map<String, OWLObjectProperty> roles;

    public KB() throws OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
	    CloneNotSupportedException, UnsupportedAxiomTypeException {
	om = OWLManager.createOWLOntologyManager();
	df = om.getOWLDataFactory();
	ontology = om.createOntology(IRI.generateDocumentIRI());
	final OWLAnnotationProperty lblAnnotProp = om.getOWLDataFactory()
		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	ol = new OntologyLabeler(om.createOntology(), lblAnnotProp);
	concepts = new HashMap<String, OWLClass>();
	roles = new HashMap<String, OWLObjectProperty>();
	dataRoles = new HashMap<String, OWLDataProperty>();
	individuals = new HashMap<String, OWLIndividual>();
	hybridKB = new HybridKB(ontology);
	parser = new Parser(new OntologyIndexImpl(ontology));
    }

    private void addAxiom(OWLAxiom axiom) {
	om.addAxiom(ontology, axiom);
    }

    public void assertFalse(String query) {
	Assert.assertFalse("shouldn't have answers", hasAnswer(query, true, true, true));

    }

    public void assertInconsistent(String query) {
	Assert.assertTrue("sould have inconsistent answers", hasAnswer(query, false, false, true));
	Assert.assertFalse("should't have non inconsistent answers", hasAnswer(query, true, true, false));
    }

    public void assertNegative(String query) {
	try {
	    final Parser parser = new Parser(new OntologyIndexImpl(ontology));
	    if (!query.endsWith("."))
		query += ".";
	    final Query q = parser.parseQuery(query);
	    for (final Literal l : q.getLiterals()) {
		if (l.isNegative())
		    throw new IllegalArgumentException("literals must be positive");
		if (!l.isGrounded())
		    throw new IllegalArgumentException("literals must be grounded");
		rule(l.getAtom().toString());
	    }
	    Assert.assertTrue("should have inconsistent answers", hasAnswer(query, false, false, true));
	    Assert.assertFalse("shouldn't have non inconsistent answers", hasAnswer(query, true, true, false));
	} catch (final PrologParserException | IOException e) {
	    fail(e);
	}
    }

    public void assertTrue(String query) {
	Assert.assertTrue("should have true answers", hasAnswer(query, true, false, false));
	Assert.assertFalse("shouldn't have non true answers", hasAnswer(query, false, true, true));
    }

    public void assertUndefined(String query) {
	Assert.assertTrue("should have undefined answers", hasAnswer(query, false, true, false));
	Assert.assertFalse("shouldn't have non undefined answers", hasAnswer(query, true, false, true));
    }

    public void asymmetric(String role) {
	addAxiom(df.getOWLAsymmetricObjectPropertyAxiom(role(role)));
    }

    /**
     * @return
     */
    public OWLClassExpression bottom() {
	return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing();
    }

    public void clear() {
	final Set<OWLDeclarationAxiom> declarationAxioms = ontology.getAxioms(AxiomType.DECLARATION);
	om.removeAxioms(ontology, ontology.getAxioms());
	om.addAxioms(ontology, declarationAxioms);
	try {
	    hybridKB = new HybridKB(ontology);
	    parser = new Parser(new OntologyIndexImpl(ontology));
	} catch (OWLOntologyCreationException | OWLOntologyStorageException | UnsupportedOWLProfile | IOException
		| CloneNotSupportedException | UnsupportedAxiomTypeException e) {
	    e.printStackTrace();
	}
    }

    public OWLClassExpression complement(OWLClassExpression concept) {
	return df.getOWLObjectComplementOf(concept);
    }

    public OWLClassExpression complement(String conceptName) {
	return df.getOWLObjectComplementOf(conc(conceptName));
    }

    public OWLClass concept() {
	return conc("a" + concepts.size());
    }

    public OWLClass conc(String name) {
	OWLClass concept = concepts.get(name);
	if (concept == null) {
	    concept = df.getOWLClass(getEntityIRI(name));
	    om.addAxiom(ontology, df.getOWLDeclarationAxiom(concept));
	    concepts.put(name, concept);
	}
	return concept;
    }

    private Set<OWLClass> concepts(String... conceptNames) {
	final Set<OWLClass> concepts = new HashSet<OWLClass>(conceptNames.length);
	for (final String name : conceptNames)
	    concepts.add(conc(name));
	return concepts;
    }

    public OWLObjectIntersectionOf conj(OWLClassExpression... concepts) {
	return getDataFactory().getOWLObjectIntersectionOf(concepts);
    }

    public OWLObjectIntersectionOf conj(String... conceptNames) {
	return df.getOWLObjectIntersectionOf(concepts(conceptNames));
    }

    public OWLDataProperty data(String name) {
	OWLDataProperty dataRole = dataRoles.get(name);
	if (dataRole == null) {
	    dataRole = df.getOWLDataProperty(getEntityIRI(name));
	    om.addAxiom(ontology, df.getOWLDeclarationAxiom(dataRole));
	    dataRoles.put(name, dataRole);
	}
	return dataRole;
    }

    private Set<OWLDataProperty> dataRoles(String... roleNames) {
	final Set<OWLDataProperty> result = new HashSet<>();
	for (final String name : roleNames)
	    result.add(data(name));
	return result;
    }

    public void disjointConcepts(OWLClassExpression... concepts) {
	om.addAxiom(ontology, df.getOWLDisjointClassesAxiom(concepts));
    }

    public void disjointConcepts(String... conceptNames) {
	addAxiom(df.getOWLDisjointClassesAxiom(concepts(conceptNames)));
    }

    public void disjointRoles(OWLObjectPropertyExpression... ope) {
	om.addAxiom(ontology, df.getOWLDisjointObjectPropertiesAxiom(ope));
    }

    public void disjointRoles(String... roleNames) {
	addAxiom(df.getOWLDisjointObjectPropertiesAxiom(roles(roleNames)));
    }

    public void domain(OWLObjectProperty ope, OWLClassExpression ce) {
	addAxiom(getDataFactory().getOWLObjectPropertyDomainAxiom(ope, ce));
    }

    public void domain(String roleName, String fillerName) {
	addAxiom(df.getOWLObjectPropertyDomainAxiom(role(roleName), conc(fillerName)));
    }

    public void equivalentConcepts(OWLClassExpression... ope) {
	addAxiom(df.getOWLEquivalentClassesAxiom(ope));
    }

    public void equivalentConcepts(String... conceptNames) {
	addAxiom(df.getOWLEquivalentClassesAxiom(concepts(conceptNames)));
    }

    public void equivalentData(OWLDataPropertyExpression sub, OWLDataPropertyExpression sup) {
	addAxiom(getDataFactory().getOWLEquivalentDataPropertiesAxiom(sub, sup));
    }

    public void equivalentData(String... dataRoleNames) {
	addAxiom(df.getOWLEquivalentDataPropertiesAxiom(dataRoles(dataRoleNames)));
    }

    public void equivalentRoles(OWLObjectPropertyExpression... ope) {
	addAxiom(getDataFactory().getOWLEquivalentObjectPropertiesAxiom(ope));
    }

    public void equivalentRoles(String... roleNames) {
	addAxiom(df.getOWLEquivalentObjectPropertiesAxiom(roles(roleNames)));
    }

    private void fail(Exception e) {
	if (e instanceof IOException)
	    Assert.fail(e.getMessage());
	if (e instanceof PrologParserException)
	    Assert.fail(e.getMessage());
	if (e instanceof UnsupportedOWLProfile)
	    Assert.fail("ontology is not QL nor EL!\n" + e.getMessage());
	if (e instanceof InconsistentOntologyException)
	    Assert.fail("inconsistent ontology");
	if (e instanceof OWLOntologyCreationException)
	    Assert.fail(e.getMessage());
	if (e instanceof OWLOntologyStorageException)
	    Assert.fail(e.getMessage());
	if (e instanceof CloneNotSupportedException)
	    Assert.fail(e.getMessage());
	if (e instanceof UnsupportedAxiomTypeException)
	    Assert.fail(e.getMessage());
    }

    public OWLClass[] getConcepts(int n) {
	final OWLClass[] result = new OWLClass[n];
	for (int i = 0; i < n; i++)
	    result[i] = concept();
	return result;
    }

    private OWLDataFactory getDataFactory() {
	return df;
    }

    private OWLDataProperty getDataRole() {
	return data("Dnew" + dataRoles.size());
    }

    public OWLDataProperty[] getDataRoles(int n) {
	final OWLDataProperty[] result = new OWLDataProperty[n];
	for (int i = 0; i < n; i++)
	    result[i] = getDataRole();
	return result;
    }

    private IRI getEntityIRI(String name) {
	final IRI ontIRI = ontology.getOntologyID().getOntologyIRI();
	return IRI.create(ontIRI + "#" + name);
    }

    String getLabel(Object obj) {
	if (obj instanceof OWLEntity)
	    return getLabel((OWLEntity) obj);
	else if (obj instanceof OWLIndividual)
	    return getLabel((OWLIndividual) obj);
	else if (obj instanceof OWLLiteral)
	    return OntologyLabeler.escapeAtom(((OWLLiteral) obj).getLiteral());
	else
	    return null;
    }

    String getLabel(OWLEntity e) {
	return ol.getLabel(e, 1);
    }

    String getLabel(OWLIndividual i) {
	return ol.getLabel(i, 1);
    }

    public QLNormalizedOntology getQLNormalizedOntology() {
	return new QLNormalizedOntologyImpl(ontology);
    }

    public OWLObjectProperty[] getRoles(int n) {
	final OWLObjectProperty[] result = new OWLObjectProperty[n];
	for (int i = 0; i < n; i++)
	    result[i] = role();
	return result;
    }

    private boolean hasAnswer(String query, boolean trueAnswers, boolean undefinedAnswers,
	    boolean inconsistentAnswers) {
	try {
	    if (!query.endsWith("."))
		query += ".";
	    final Query q = parser.parseQuery(query);
	    return hybridKB.hasAnswer(q, trueAnswers, undefinedAnswers, inconsistentAnswers);
	} catch (final Exception e) {
	    fail(e);
	    return false;
	}
    }

    public OWLIndividual individual() {
	return individual("anew" + individuals.size());
    }

    public OWLIndividual individual(String name) {
	OWLIndividual individual = individuals.get(name);
	if (individual == null) {
	    individual = df.getOWLNamedIndividual(getEntityIRI(name));
	    individuals.put(name, individual);
	}
	return individual;
    }

    public OWLObjectPropertyExpression inv(OWLObjectProperty role) {
	return df.getOWLObjectInverseOf(role);
    }

    public OWLObjectPropertyExpression inv(String roleName) {
	final OWLObjectProperty role = role(roleName);
	return df.getOWLObjectInverseOf(role);
    }

    public void inverse(String subRole, String superRole) {
	addAxiom(df.getOWLInverseObjectPropertiesAxiom(role(subRole), role(superRole)));
    }

    public void irreflexive(String role) {
	addAxiom(df.getOWLIrreflexiveObjectPropertyAxiom(role(role)));
    }

    public void object(OWLObjectProperty role, OWLIndividual ind1, OWLIndividual ind2) {
	om.addAxiom(ontology, df.getOWLObjectPropertyAssertionAxiom(role, ind1, ind2));
    }

    public void object(String roleName, String subjectIndividualName, String individualName) {
	addAxiom(df.getOWLObjectPropertyAssertionAxiom(role(roleName), individual(subjectIndividualName),
		individual(individualName)));
    }

    public void range(String roleName, String fillerName) {
	addAxiom(df.getOWLObjectPropertyRangeAxiom(role(roleName), conc(fillerName)));
    }

    public void reflexive(String role) {
	addAxiom(df.getOWLReflexiveObjectPropertyAxiom(role(role)));
    }

    public OWLObjectProperty role() {
	return role("p" + roles.size());
    }

    public OWLObjectProperty role(String name) {
	OWLObjectProperty role = roles.get(name);
	if (role == null) {
	    role = df.getOWLObjectProperty(getEntityIRI(name));
	    om.addAxiom(ontology, df.getOWLDeclarationAxiom(role));
	    roles.put(name, role);
	}
	return role;
    }

    private Set<OWLObjectProperty> roles(String... roleNames) {
	final Set<OWLObjectProperty> result = new HashSet<>();
	for (final String name : roleNames)
	    result.add(role(name));
	return result;
    }

    public void rule(String rule) {

	try {
	    if (!rule.endsWith("."))
		rule += ".";
	    pt.unl.fct.di.centria.nohr.model.Rule r;
	    r = parser.parseRule(rule);

	    hybridKB.getRuleBase().add(r);
	} catch (IOException | PrologParserException e) {
	    fail(e);
	}
    }

    public OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression owlObjectPropertyExpression) {
	return df.getOWLObjectSomeValuesFrom(owlObjectPropertyExpression, df.getOWLThing());
    }

    public OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression role, OWLClassExpression filler) {
	return df.getOWLObjectSomeValuesFrom(role, filler);
    }

    public OWLObjectSomeValuesFrom some(String roleName) {
	final OWLObjectProperty role = role(roleName);
	return df.getOWLObjectSomeValuesFrom(role, df.getOWLThing());
    }

    public OWLObjectSomeValuesFrom some(String roleName, String fillerName) {
	return df.getOWLObjectSomeValuesFrom(role(roleName), conc(fillerName));
    }

    public void subConcept(OWLClassExpression b1, OWLClassExpression b2) {
	om.addAxiom(ontology, df.getOWLSubClassOfAxiom(b1, b2));
    }

    public void subConcept(String subName, String superName) {
	addAxiom(df.getOWLSubClassOfAxiom(conc(subName), conc(superName)));
    }

    /**
     * @param d1
     * @param d2
     */
    public void subData(OWLDataProperty d1, OWLDataProperty d2) {
	addAxiom(getDataFactory().getOWLSubDataPropertyOfAxiom(d1, d2));
    }

    public void subData(String subName, String superName) {
	addAxiom(getDataFactory().getOWLSubDataPropertyOfAxiom(data(subName), data(superName)));
    }

    public void subRole(List<? extends OWLObjectPropertyExpression> chain, OWLObjectPropertyExpression q2) {
	om.addAxiom(ontology, df.getOWLSubPropertyChainOfAxiom(chain, q2));
    }

    public void subRole(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2) {
	om.addAxiom(ontology, df.getOWLSubObjectPropertyOfAxiom(q1, q2));
    }

    public void subRole(String subName, String superName) {
	addAxiom(df.getOWLSubObjectPropertyOfAxiom(role(subName), role(superName)));
    }

    public void subRolesChain(String... roleNames) {
	final List<OWLObjectPropertyExpression> chain = new ArrayList<>(roleNames.length - 1);
	for (int i = 0; i < roleNames.length - 1; i++)
	    chain.add(role(roleNames[i]));
	addAxiom(df.getOWLSubPropertyChainOfAxiom(chain, role(roleNames[roleNames.length - 1])));
    }

    public void symmetric(String role) {
	addAxiom(df.getOWLSymmetricObjectPropertyAxiom(role(role)));
    }

    /**
     * @return
     */
    public OWLClassExpression top() {
	return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
    }

    public OWLDataProperty topData() {
	return df.getOWLTopDataProperty();
    }

    public OWLObjectProperty topRole() {
	return df.getOWLTopObjectProperty();
    }

    public void transitive(OWLObjectPropertyExpression ope) {
	addAxiom(getDataFactory().getOWLTransitiveObjectPropertyAxiom(ope));
    }

    public void transitive(String roleName) {
	addAxiom(df.getOWLTransitiveObjectPropertyAxiom(role(roleName)));
    }

    public void typeOf(OWLClassExpression concept, OWLIndividual individual) {
	om.addAxiom(ontology, df.getOWLClassAssertionAxiom(concept, individual));
    }

    public void typeOf(String conceptName, String individualName) {
	addAxiom(df.getOWLClassAssertionAxiom(conc(conceptName), individual(individualName)));
    }

    public void value(OWLDataProperty role, OWLIndividual ind1, String litral) {
	om.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(role, ind1, df.getOWLLiteral(litral)));
    }

    public void value(String dataRoleName, String subjectIndividualName, String literal) {
	addAxiom(df.getOWLDataPropertyAssertionAxiom(data(dataRoleName), individual(subjectIndividualName),
		df.getOWLLiteral(literal)));
    }

}

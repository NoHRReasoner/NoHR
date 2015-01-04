/**
 * 
 */
package test;

import hybrid.query.model.Query;
import hybrid.query.views.Rules;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author nunocosta
 *
 */
public class QueryTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private OWLOntology ont;

	private Query query;

	private OWLOntologyManager om;

	private OWLDataFactory df;

	private void addAssertion(OWLClass concept, OWLIndividual individual) {
		om.addAxiom(ont, df.getOWLClassAssertionAxiom(concept, individual));
	}

	private void addAssertion(OWLObjectProperty role, OWLIndividual ind1,
			OWLIndividual ind2) {
		om.addAxiom(ont,
				df.getOWLObjectPropertyAssertionAxiom(role, ind1, ind2));
	}

	private void addDisjunction(OWLClassExpression b1, OWLClassExpression b2) {
		om.addAxiom(ont, df.getOWLDisjointClassesAxiom(b1, b2));
	}

	private void addDisjunction(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		om.addAxiom(ont, df.getOWLDisjointObjectPropertiesAxiom(q1, q2));
	}

	private void addSubsumption(OWLClassExpression b1, OWLClassExpression b2) {
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(b1, b2));
	}

	private void addSubsumption(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		om.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(q1, q2));
	}

	private void checkInconcistency(String query) {
		ArrayList<ArrayList<String>> result = this.query.query(query);
		Assert.assertEquals("Should be inconsistent", "inconsistent", result
				.get(1).get(0));
	}

	private void checkQuery(String query, String[] expectedAns) {
		ArrayList<ArrayList<String>> result = this.query.query(query);
		Assert.assertNotEquals("Should have anwswers", "no answers found",
				result.get(1).get(0));
		for (int i = 0; i < expectedAns.length; i++)
			Assert.assertEquals("Should have the", expectedAns[i], result
					.get(1).get(i + 1));
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void disjunctionWithAtomicConcepts()
			throws OWLOntologyCreationException {
		reset();
		OWLClass A1 = getConcept("A1");
		OWLClass A2 = getConcept("A2");
		OWLIndividual a = getIndividual("a");
		addAssertion(A1, a);
		addDisjunction(A1, A2);
		Rules.addRule("A2(X):-A1(X)");
		checkInconcistency("A2(X)");
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void disjunctionWithAtomicRoles()
			throws OWLOntologyCreationException {
		reset();
		OWLObjectProperty P1 = getRole("P1");
		OWLObjectProperty P2 = getRole("P2");
		OWLIndividual a = getIndividual("a");
		OWLIndividual b = getIndividual("b");
		addAssertion(P1, a, b);
		addDisjunction(P1, P2);
		Rules.addRule("P2(X,Y):-P1(X,Y).");
		checkInconcistency("P2(X,Y)");
	}
	
	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void disjunctionWithInverseRoles()
			throws OWLOntologyCreationException {
		reset();
		OWLObjectProperty P1 = getRole("P1");
		OWLObjectProperty P2 = getRole("P2");
		OWLIndividual a = getIndividual("a");
		OWLIndividual b = getIndividual("b");
		addAssertion(P1, a, b);
		addDisjunction(P1, getInverse(P2));
		Rules.addRule("P2(Y,X):-P1(X,Y).");
		checkInconcistency("P2(Y,X)");
	}
	
	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void disjunctionWithExistentialsOnTheLeft()
			throws OWLOntologyCreationException {
		reset();
		OWLObjectProperty P1 = getRole("P1");
		OWLClass A2 = getConcept("A2");
		OWLIndividual a = getIndividual("a");
		OWLIndividual b = getIndividual("b");
		addAssertion(P1, a, b);
		addDisjunction(getExistential(P1), A2);
		Rules.addRule("A2(X):-P1(X,_)");
		checkInconcistency("A2(X)");
	}
	
	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void disjunctionWithExistentialsOnTheRight()
			throws OWLOntologyCreationException {
		reset();
		OWLClass A1 = getConcept("A1");
		OWLObjectProperty P2 = getRole("P2");
		OWLClass A3 = getConcept("A3");
		OWLIndividual a = getIndividual("a");
		addAssertion(A1, a);
		addDisjunction(A1, getExistential(P2));
		addDisjunction(getExistential(P2), A3);
		Rules.addRule("A3(X):-A1(X)");
		checkInconcistency("A3(X)");
	}

	private OWLClass getConcept(String name) {
		OWLClass concept = df.getOWLClass(getEntityIRI(name));
		om.addAxiom(ont, df.getOWLDeclarationAxiom(concept));
		return concept;
	}

	private IRI getEntityIRI(String name) {
		IRI ontIRI = ont.getOntologyID().getOntologyIRI();
		return IRI.create(ontIRI + "#" + name);
	}

	private OWLObjectSomeValuesFrom getExistential(OWLObjectProperty role) {
		return df.getOWLObjectSomeValuesFrom(role, df.getOWLThing());
	}

	private OWLIndividual getIndividual(String name) {
		OWLIndividual individual = df.getOWLNamedIndividual(getEntityIRI(name));
		return individual;
	}

	private OWLObjectPropertyExpression getInverse(OWLObjectProperty role) {
		return df.getOWLObjectInverseOf(role);
	}

	private OWLObjectProperty getRole(String name) {
		OWLObjectProperty role = df.getOWLObjectProperty(getEntityIRI(name));
		om.addAxiom(ont, df.getOWLDeclarationAxiom(role));
		return role;
	}

	private void reset() throws OWLOntologyCreationException {
		ont = om.createOntology(IRI.generateDocumentIRI());
		query = new Query(ont);
		Rules.resetRules();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		om = OWLManager.createOWLOntologyManager();
		df = om.getOWLDataFactory();
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void subsumptionWithAtomicConcepts()
			throws OWLOntologyCreationException {
		reset();
		OWLClass A1 = getConcept("A1");
		OWLClass A2 = getConcept("A2");
		OWLIndividual a = getIndividual("a");
		addAssertion(A1, a);
		addSubsumption(A1, A2);
		checkQuery("A2(X)", new String[] { "a" });
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void subsumptionWithAtomicRoles()
			throws OWLOntologyCreationException {
		reset();
		OWLObjectProperty P1 = getRole("P1");
		OWLObjectProperty P2 = getRole("P2");
		OWLIndividual a = getIndividual("a");
		OWLIndividual b = getIndividual("b");
		addAssertion(P1, a, b);
		addSubsumption(P1, P2);
		checkQuery("P2(X,Y)", new String[] { "a", "b" });
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void subsumptionWithExistentialsOnTheLeft()
			throws OWLOntologyCreationException {
		reset();
		OWLObjectProperty P1 = getRole("P1");
		OWLClass A2 = getConcept("A2");
		OWLIndividual a = getIndividual("a");
		OWLIndividual b = getIndividual("b");
		addAssertion(P1, a, b);
		addSubsumption(getExistential(P1), A2);
		checkQuery("A2(X)", new String[] { "a" });
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void subsumptionWithExistentialsOnTheRight()
			throws OWLOntologyCreationException {
		reset();
		OWLClass A1 = getConcept("A1");
		OWLObjectProperty P2 = getRole("P2");
		OWLClass A3 = getConcept("A3");
		OWLIndividual a = getIndividual("a");
		addAssertion(A1, a);
		addSubsumption(A1, getExistential(P2));
		addSubsumption(getExistential(P2), A3);
		checkQuery("A3(X)", new String[] { "a" });
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void subsumptionWithInverseRoles()
			throws OWLOntologyCreationException {
		reset();
		OWLObjectProperty P1 = getRole("P1");
		OWLObjectProperty P2 = getRole("P2");
		OWLIndividual a = getIndividual("a");
		OWLIndividual b = getIndividual("b");
		addAssertion(P1, a, b);
		addSubsumption(P1, getInverse(P2));
		checkQuery("P2(X,Y)", new String[] { "b", "a" });
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}

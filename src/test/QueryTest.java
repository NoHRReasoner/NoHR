/**
 * 
 */
package test;

import hybrid.query.model.Query;

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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author nunocosta
 *
 */
public class QueryTest {

	private static Query query;

	private static OWLClass addAtomicConcept(OWLOntology ont, String name) {
		OWLOntologyManager om = ont.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		OWLClass cls = df.getOWLClass(getEntityIRI(ont, name));
		OWLDeclarationAxiom decl = df.getOWLDeclarationAxiom(cls);
		om.addAxiom(ont, decl);
		return cls;
	}

	private static OWLObjectProperty addAtomicRole(OWLOntology ont, String name) {
		OWLOntologyManager om = ont.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		OWLObjectProperty prop = df
				.getOWLObjectProperty(getEntityIRI(ont, name));
		OWLDeclarationAxiom decl = df.getOWLDeclarationAxiom(prop);
		om.addAxiom(ont, decl);
		return prop;
	}

	private static IRI getEntityIRI(OWLOntology ont, String name) {
		IRI ontIRI = ont.getOntologyID().getOntologyIRI();
		return IRI.create(ontIRI + "#" + name);
	}

	private static OWLObjectSomeValuesFrom getExistential(OWLDataFactory df,
			OWLObjectProperty prop) {
		return df.getOWLObjectSomeValuesFrom(prop, df.getOWLThing());
	}

	private static OWLIndividual getIndividual(OWLOntology ont, String name) {
		OWLDataFactory df = ont.getOWLOntologyManager().getOWLDataFactory();
		return df.getOWLNamedIndividual(getEntityIRI(ont, name));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		OWLOntology ont = om.createOntology(IRI.generateDocumentIRI());
		OWLIndividual a = getIndividual(ont, "a");
		OWLIndividual b = getIndividual(ont, "b");
		OWLClass[] A = new OWLClass[5];
		OWLObjectProperty[] P = new OWLObjectProperty[6];
		for (int i = 0; i <= 4; i++)
			A[i] = addAtomicConcept(ont, "A" + i);
		for (int i = 0; i <= 5; i++)
			P[i] = addAtomicRole(ont, "P" + i);
		OWLObjectPropertyExpression invP5 = df.getOWLObjectInverseOf(P[5]);
		OWLObjectSomeValuesFrom someP1 = getExistential(df, P[1]);
		OWLObjectSomeValuesFrom someP3 = getExistential(df, P[3]);
		om.addAxiom(ont, df.getOWLClassAssertionAxiom(A[1], a));
		om.addAxiom(ont, df.getOWLObjectPropertyAssertionAxiom(P[1], a, b));
		om.addAxiom(ont, df.getOWLObjectPropertyAssertionAxiom(P[4], a, b));
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(A[1], A[2]));
		om.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(P[1], P[2]));
		om.addAxiom(ont, df.getOWLSubObjectPropertyOfAxiom(P[4], invP5));
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(someP1, A[3]));
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(A[1], someP3));
		om.addAxiom(ont, df.getOWLSubClassOfAxiom(someP3, A[4]));
		query = new Query(ont);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 */
	@Test
	public final void testQueryWithAtomicConcepts() {
		ArrayList<ArrayList<String>> result = query.query("A2(X)");
		Assert.assertNotEquals("Should have anwswers", "no answers found",
				result.get(1).get(0));
		Assert.assertEquals("Should have the", "a", result.get(1).get(1));

	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 */
	@Test
	public final void testQueryWithAtomicRoles() {
		ArrayList<ArrayList<String>> result = query.query("P2(X,Y)");
		Assert.assertNotEquals("Should have anwswers", "no answers found",
				result.get(1).get(0));
		Assert.assertEquals("Should have the", "a", result.get(1).get(1));
		Assert.assertEquals("Should have the", "b", result.get(1).get(2));
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 */
	@Test
	public final void testQueryWithExistentials1Axioms() {
		ArrayList<ArrayList<String>> result = query.query("A3(X)");
		Assert.assertNotEquals("Should have anwswers", "no answers found",
				result.get(1).get(0));
		Assert.assertEquals("Should have the", "a", result.get(1).get(1));
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 */
	@Test
	public final void testQueryWithExistentials2Axioms() {
		ArrayList<ArrayList<String>> result = query.query("A4(X)");
		Assert.assertNotEquals("Should have anwswers", "no answers found",
				result.get(1).get(0));
		Assert.assertEquals("Should have the", "a", result.get(1).get(1));
	}

	/**
	 * Test method for {@link hybrid.query.model.Query#query(java.lang.String)}.
	 */
	@Test
	public final void testQueryWithInverseRoles() {
		ArrayList<ArrayList<String>> result = query.query("P5(X,Y)");
		Assert.assertNotEquals("Should have anwswers", "no answers found",
				result.get(1).get(0));
		Assert.assertEquals("Should have the", "b", result.get(1).get(1));
		Assert.assertEquals("Should have the", "a", result.get(1).get(2));
	}

}

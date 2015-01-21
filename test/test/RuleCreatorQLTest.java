/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.List;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;
import local.translate.ql.RuleCreatorQL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import xsb.Rule;

/**
 * @author nunocosta
 *
 */
public class RuleCreatorQLTest extends RuleCreatorQL {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		OWLAnnotationProperty lblAnnotProp = om.getOWLDataFactory()
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		cm = new CollectionsManager();
		ol = new OntologyLabel(om.createOntology(), lblAnnotProp, cm);
		OWLDataFactory df = om.getOWLDataFactory();
		IRI iri1 = IRI.generateDocumentIRI();
		IRI iri2 = IRI.generateDocumentIRI();
		A1 = df.getOWLClass(iri1);
		A2 = df.getOWLClass(iri2);
		A1Lbl = ol.getLabel(A1, 1);
		A2Lbl = ol.getLabel(A2, 1);
		P1 = df.getOWLObjectProperty(iri1);
		P2 = df.getOWLObjectProperty(iri2);
		P1Lbl = ol.getLabel(P1, 1);
		P2Lbl = ol.getLabel(P2, 1);
		c1 = df.getOWLNamedIndividual(IRI.generateDocumentIRI());
		c2 = df.getOWLNamedIndividual(IRI.generateDocumentIRI());
		c1Lbl = ol.getLabel(c1, 1);
		c2Lbl = ol.getLabel(c2, 1);
		Q1 = df.getOWLObjectInverseOf(P1);
		Q2 = df.getOWLObjectInverseOf(P2);
		B1 = df.getOWLObjectSomeValuesFrom(P1, A1);
		B2 = df.getOWLObjectSomeValuesFrom(Q2, A2);
		
		IRI D1IRI = IRI.generateDocumentIRI();
		D1 = df.getOWLDataProperty(D1IRI);
		L1 = df.getOWLLiteral(100);
		D1Lbl = ol.getLabel(D1, 1);
		L1Lbl = cm.getHashedLabel(L1.getLiteral());
		
		cm.setIsAnyDisjointStatement(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private static CollectionsManager cm;
	private static OntologyLabel ol;
	private static OWLClass A1;
	private static OWLClass A2;
	private static OWLObjectSomeValuesFrom B1;
	private static OWLObjectSomeValuesFrom B2;
	private static OWLObjectProperty P1;
	private static OWLObjectProperty P2;
	private static OWLObjectInverseOf Q1;
	private static OWLObjectInverseOf Q2;
	private static OWLDataProperty D1;
	private static OWLLiteral L1;
	private static String A1Lbl;
	private static String A2Lbl;
	private static String P1Lbl;
	private static String P2Lbl;
	private static String D1Lbl;
	private static String L1Lbl;
	private static OWLIndividual c1;
	private static OWLIndividual c2;
	private static String c1Lbl;
	private static String c2Lbl;


	public RuleCreatorQLTest() {
		super(cm, ol);
	}

	private void checkTranslation(String expectedRule,
			String expectedDoubledRule, String expectedNegativeRule,
			List<Rule> actualRules) {
		Assert.assertEquals("Should return the translated rule", expectedRule,
				actualRules.get(0).toString());
		Assert.assertEquals("Should return the translated doubled rule",
				expectedDoubledRule, actualRules.get(1).toString());
		Assert.assertEquals("Should return the translated negative rule",
				expectedNegativeRule, actualRules.get(2).toString());
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
	 * Test method for {@link local.translate.ql.RuleCreatorQL#e()}.
	 */
	@Test
	public final void testE() {
		List<Rule> rules = e();
		String domRule = "dom(P)(X):-P(X,_).";
		String ranRule = "ran(P)(X):-P(_,X).";
		Assert.assertEquals("Should return the dom rule", domRule, rules.get(0)
				.toString());
		Assert.assertEquals("Should return the ran rule", ranRule, rules.get(1)
				.toString());
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#i1(org.semanticweb.owlapi.model.OWLClass)}
	 * .
	 */
	@Test
	public final void testI1() {
		List<Rule> rules = i1(A1);
		String rule = String.format("n%s(X).", A1Lbl);
		Assert.assertEquals("Should return the rule", rule, rules.get(0)
				.toString());
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#i2(org.semanticweb.owlapi.model.OWLProperty)}
	 * .
	 */
	@Test
	public final void testI2() {
		List<Rule> rules = i2(P1);
		String rule = String.format("n%s(X,Y).", P1Lbl);
		Assert.assertEquals("Should return the rule", rule, rules.get(0)
				.toString());
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#i2(org.semanticweb.owlapi.model.OWLProperty)}
	 * .
	 */
	@Test
	public final void testIR() {
		List<Rule> rules = ir(P1);
		String rule = String.format("n%s(X,X).", P1Lbl);
		Assert.assertEquals("Should return the rule", rule, rules.get(0)
				.toString());
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#n1(org.semanticweb.owlapi.model.OWLClassExpression, org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 */
	@Test
	public final void testN1() {
		List<Rule> rules = n1(A1, A2);
		String rule1 = String.format("n%s(X):-a%s(X).", A1Lbl, A2Lbl);
		String rule2 = String.format("n%s(X):-a%s(X).", A2Lbl, A1Lbl);
		Assert.assertEquals("Should return the first rule", rule1, rules.get(0)
				.toString());
		Assert.assertEquals("Should return the first rule", rule2, rules.get(1)
				.toString());
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#n2(org.semanticweb.owlapi.model.OWLPropertyExpression, org.semanticweb.owlapi.model.OWLPropertyExpression)}
	 * .
	 */
	@Test
	public final void testN2() {
		List<Rule> rules = n2(P1, P2);
		String rule1 = String.format("n%s(X,Y):-a%s(X,Y).", P1Lbl, P2Lbl);
		String rule2 = String.format("n%s(X,Y):-a%s(X,Y).", P2Lbl, P1Lbl);
		Assert.assertEquals("Should return the first rule", rule1, rules.get(0)
				.toString());
		Assert.assertEquals("Should return the first rule", rule2, rules.get(1)
				.toString());

		rules = n2(P1, Q2);
		rule1 = String.format("n%s(X,Y):-a%s(Y,X).", P1Lbl, P2Lbl);
		rule2 = String.format("n%s(Y,X):-a%s(X,Y).", P2Lbl, P1Lbl);
		Assert.assertEquals("Should return the first rule", rule1, rules.get(0)
				.toString());
		Assert.assertEquals("Should return the first rule", rule2, rules.get(1)
				.toString());
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#s1(org.semanticweb.owlapi.model.OWLClassExpression, org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 */
	@Test
	public void testS1withAtomics() {
		List<Rule> rules = s1(A1, A2);
		String rule = String.format("a%s(X):-a%s(X).", A2Lbl, A1Lbl);
		String doubledRule = String.format("d%s(X):-d%s(X),tnot(n%s(X)).",
				A2Lbl, A1Lbl, A2Lbl);
		String negativeRule = String.format("n%s(X):-n%s(X).", A1Lbl, A2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#a1(org.semanticweb.owlapi.model.OWLClass org.semanticweb.owlapi.model.OWLIndividual)}
	 * .
	 */
	@Test
	public void testA1() {
		List<Rule> rules = a1(A1, c1);
		String rule = String.format("a%s(c%s).", A1Lbl, c1Lbl);
		String doubledRule = String.format("d%s(c%s):-tnot(n%s(c%s)).", A1Lbl,
				c1Lbl, A1Lbl, c1Lbl);
		checkTranslation(rule, doubledRule, rules);
	}
	
	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#a2(org.semanticweb.owlapi.model.OWLObjectProperty, org.semanticweb.owlapi.model.OWLIndividual, org.semanticweb.owlapi.model.OWLIndividual)}
	 * .
	 */
	@Test
	public void testA2() {
		List<Rule> rules = a2(P1, c1, c2);
		String rule = String.format("a%s(c%s,c%s).", P1Lbl, c1Lbl, c2Lbl);
		String doubledRule = String.format("d%s(c%s,c%s):-tnot(n%s(c%s,c%s)).",
				P1Lbl, c1Lbl, c2Lbl, P1Lbl, c1Lbl, c2Lbl);
		checkTranslation(rule, doubledRule, rules);
	}
	
	private void checkTranslation(String expectedRule, String expectedDoubledRule,
			List<Rule> actualRules) {
		Assert.assertEquals("Should return the translated rule", expectedRule,
				actualRules.get(0).toString());
		Assert.assertEquals("Should return the translated doubled rule",
				expectedDoubledRule, actualRules.get(1).toString());
		
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#s1(org.semanticweb.owlapi.model.OWLClassExpression, org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 */
	@Test
	public void testS1withExistentials() {
		List<Rule> rules = s1(A1, B2);
		String rule = String.format("ran(a%s)(X):-a%s(X).", P2Lbl, A1Lbl);
		String doubledRule = String.format(
				"ran(d%s)(X):-d%s(X),tnot(n%s(_,X)).", P2Lbl, A1Lbl, P2Lbl);
		String negativeRule = String.format("n%s(X):-n%s(_,X).", A1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);

		rules = s1(B1, A2);
		rule = String.format("a%s(X):-dom(a%s)(X).", A2Lbl, P1Lbl);
		doubledRule = String.format("d%s(X):-dom(d%s)(X),tnot(n%s(X)).", A2Lbl,
				P1Lbl, A2Lbl);
		negativeRule = String.format("n%s(X,_):-n%s(X).", A1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);

		rules = s1(B1, B2);
		rule = String.format("ran(a%s)(X):-dom(a%s)(X).", P2Lbl, P1Lbl);
		doubledRule = String.format("ran(d%s)(X):-dom(d%s)(X),tnot(n%s(_,X)).",
				P2Lbl, P1Lbl, P2Lbl);
		negativeRule = String.format("n%s(X,_):-n%s(_,X).", P1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#s2(org.semanticweb.owlapi.model.OWLPropertyExpression, org.semanticweb.owlapi.model.OWLPropertyExpression)}
	 * .
	 */
	@Test
	public void testS2withAtomics() {
		List<Rule> rules = s2(P1, P2);
		List<String> rulesStr = new ArrayList<String>(rules.size());
		for (Rule rule : rules)
			rulesStr.add(rule.toString());
		String rule = String.format("a%s(X,Y):-a%s(X,Y).", P2Lbl, P1Lbl);
		String doubledRule = String.format(
				"d%s(X,Y):-d%s(X,Y),tnot(n%s(X,Y)).", P2Lbl, P1Lbl, P2Lbl);
		String negativeRule = String
				.format("n%s(X,Y):-n%s(X,Y).", P1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#s2(org.semanticweb.owlapi.model.OWLPropertyExpression, org.semanticweb.owlapi.model.OWLPropertyExpression)}
	 * .
	 */
	@Test
	public void testS2withInverses() {
		List<Rule> rules = s2(P1, Q2);
		String rule = String.format("a%s(Y,X):-a%s(X,Y).", P2Lbl, P1Lbl);
		String doubledRule = String.format(
				"d%s(Y,X):-d%s(X,Y),tnot(n%s(Y,X)).", P2Lbl, P1Lbl, P2Lbl);
		String negativeRule = String
				.format("n%s(X,Y):-n%s(Y,X).", P1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);

		rules = s2(Q1, P2);
		rule = String.format("a%s(X,Y):-a%s(Y,X).", P2Lbl, P1Lbl);
		doubledRule = String.format("d%s(X,Y):-d%s(Y,X),tnot(n%s(X,Y)).",
				P2Lbl, P1Lbl, P2Lbl);
		negativeRule = String.format("n%s(Y,X):-n%s(X,Y).", P1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);

		rules = s2(Q1, Q2);
		rule = String.format("a%s(Y,X):-a%s(Y,X).", P2Lbl, P1Lbl);
		doubledRule = String.format("d%s(Y,X):-d%s(Y,X),tnot(n%s(Y,X)).",
				P2Lbl, P1Lbl, P2Lbl);
		negativeRule = String.format("n%s(Y,X):-n%s(Y,X).", P1Lbl, P2Lbl);
		checkTranslation(rule, doubledRule, negativeRule, rules);
	}
	
	/**
	 * Test method for
	 * {@link local.translate.ql.RuleCreatorQL#DataProprietyAssertion(org.semanticweb.owlapi.model.OWLPropertyExpression, org.semanticweb.owlapi.model.OWLPropertyExpression)}
	 * .
	 */
	@Test
	public void testTranslateDataPropertyAssertionWithInverses() {
		List<Rule> rules = translateDataPropertyAssertion(D1, c1, L1);
		String rule = String.format("a%s(c%s,c%s).", D1Lbl, c1Lbl, L1Lbl);
		String doubledRule = String.format("d%s(c%s,c%s):-tnot(n%s(c%s,c%s)).", D1Lbl,
				c1Lbl, L1Lbl, D1Lbl, c1Lbl, L1Lbl);
		checkTranslation(rule, doubledRule, rules);
	}
}

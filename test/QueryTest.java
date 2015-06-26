import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import helpers.KB;

import java.io.IOException;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;

import other.Config;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.TranslationAlgorithm;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

/**
 *
 */

/**
 * @author nunocosta
 *
 */
public class QueryTest extends Object {

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

    private KB kb;
    private final Parser parser = new Parser();

    private void assertAnswer(String query, boolean ql, String... expectedAns) {
	if (ql)
	    Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
	else
	    Config.translationAlgorithm = TranslationAlgorithm.EL;
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = new HybridKB(kb.getOntology())
	    .queryAll(parser.parseQuery(query));
	    assertNotNull("should be consistent", result);
	    assertEquals("should have exactly one answer", 1, result.size());
	    final Answer ans = result.iterator().next();
	    int i = 0;
	    for (final Term val : ans.getValues())
		assertEquals("sould be the expected constant", val,
			cons(expectedAns[i++]));
	} catch (final IOException e) {
	    fail(e.getMessage());
	} catch (final PrologParserException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedOWLProfile e) {
	    fail("ontology is not QL nor EL!\n" + e.getMessage());
	} catch (final InconsistentOntologyException e) {
	    fail("inconsistent ontology");
	} catch (final Exception e) {
	    e.printStackTrace();
	    fail(e.toString());
	} finally {
	    Config.translationAlgorithm = null;
	}
    }

    private void assertAnswer(String query, String... expectedAns) {
	assertAnswer(query, true, expectedAns);
	assertAnswer(query, false, expectedAns);
    }

    private void assertConsistent(String query) {
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = new HybridKB(kb.getOntology())
	    .queryAll(parser.parseQuery(query));
	    assertNotNull("should't be null", result);
	    assertEquals("should have exactly one answer", 1, result.size());
	    final Answer ans = result.iterator().next();
	    assertFalse("sould be consistent",
		    ans.getValuation() == TruthValue.INCONSISTENT);
	} catch (final IOException e) {
	    fail(e.getMessage());
	} catch (final PrologParserException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedOWLProfile e) {
	    fail("ontology is not QL nor EL!\n" + e.getMessage());
	} catch (final InconsistentOntologyException e) {
	    fail("inconsistent ontology");
	} catch (final Exception e) {
	    e.printStackTrace();
	    fail(e.toString());
	}

    }

    private void assertInconsistent(String query) {
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = new HybridKB(kb.getOntology())
	    .queryAll(parser.parseQuery(query));
	    assertNotNull("should be consistent", result);
	    assertEquals("should have exactly one answer", 1, result.size());
	    final Answer ans = result.iterator().next();
	    assertEquals("sould be inconsistent", TruthValue.INCONSISTENT,
		    ans.getValuation());
	} catch (final IOException e) {
	    fail(e.getMessage());
	} catch (final PrologParserException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedOWLProfile e) {
	    fail("ontology is not QL nor EL!\n" + e.getMessage());
	} catch (final InconsistentOntologyException e) {
	    fail("inconsistent ontology");
	} catch (final Exception e) {
	    e.printStackTrace();
	    fail(e.toString());
	}
    }

    private void assertNoneAnswers(String query, boolean ql) {
	if (ql)
	    Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
	else
	    Config.translationAlgorithm = TranslationAlgorithm.EL;
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = new HybridKB(kb.getOntology())
	    .queryAll(parser.parseQuery(query));
	    assertNotNull("should be consistent", result);
	    assertEquals("should have no answer", 0, result.size());
	} catch (final IOException e) {
	    fail(e.getMessage());
	} catch (final PrologParserException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedOWLProfile e) {
	    fail("ontology is not QL nor EL!\n" + e.getMessage());
	} catch (final InconsistentOntologyException e) {
	    fail("inconsistent ontology");
	} catch (final Exception e) {
	    e.printStackTrace();
	    fail(e.toString());
	}
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void cln2() throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a3(X):-a1(X)");
	assertConsistent("a3(X)");
	kb.addSubsumption(a1, a2);
	assertConsistent("a3(X)");
	kb.addDisjunction(a2, a3);
	assertInconsistent("a3(X)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void cln3() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLClass A = kb.getConcept("A");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(A, a);
	kb.addAssertion(p1, a, b);
	kb.addRule("p2(X,Y):-p1(X,Y)");
	assertConsistent("p2(X,Y)");
	kb.addSubsumption(p1, p2);
	assertConsistent("p2(X,Y)");
	kb.addDisjunction(A, kb.getExistential(p2));
	assertInconsistent("p2(X,Y)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void cln4() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLClass A = kb.getConcept("A");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(A, b);
	kb.addAssertion(p1, a, b);
	kb.addRule("p2(X,Y):-p1(X,Y)");
	assertConsistent("p2(X,Y)");
	kb.addSubsumption(p1, p2);
	assertConsistent("p2(X,Y)");
	kb.addDisjunction(A, kb.getExistential(kb.getInverse(p2)));
	assertInconsistent("p2(X,Y)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void cln5() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addRule("p3(X,Y):-p1(X,Y)");
	assertConsistent("p3(X,Y)");
	kb.addSubsumption(p1, p2);
	assertConsistent("p3(X,Y)");
	kb.addDisjunction(p2, p3);
	assertInconsistent("p3(X,Y)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void disjointWithAtomicConcepts()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a2(X):-a1(X)");
	assertConsistent("a2(X)");
	kb.addDisjunction(a1, a2);
	assertInconsistent("a2(X)");
    }

    @Test
    public final void disjointWithAtomicConceptsToExistRoleExist()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty p4 = kb.getRole("p4");
	final OWLClass a5 = kb.getConcept("a5");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a2(X):-a1(X)");
	assertConsistent("a2(X)");
	kb.addDisjunction(a1, a2);
	kb.addSubsumption(a2, kb.getExistential(p3));
	kb.addSubsumption(p3, p4);
	kb.addSubsumption(kb.getExistential(p4), a5);
	assertInconsistent("a5(X)");
    }

    @Test
    public final void disjointWithAtomicConceptsToExistRoleExist2()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty P4 = kb.getRole("p4");
	final OWLClass A5 = kb.getConcept("a5");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a2(X):-a1(X)");
	assertConsistent("a2(X)");
	kb.addDisjunction(a1, a2);
	kb.addSubsumption(a2, kb.getExistential(kb.getInverse(p3)));
	kb.addSubsumption(kb.getInverse(p3), P4);
	kb.addSubsumption(kb.getExistential(P4), A5);
	assertInconsistent("a5(X)");
    }

    @Test
    public final void disjointWithAtomicConceptsToExistRoleExist3()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty P4 = kb.getRole("p4");
	final OWLClass A5 = kb.getConcept("a5");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a2(X):-a1(X)");
	assertConsistent("a2(X)");
	kb.addDisjunction(a1, a2);
	kb.addSubsumption(a2, kb.getExistential(p3));
	kb.addSubsumption(p3, kb.getInverse(P4));
	kb.addSubsumption(kb.getExistential(kb.getInverse(P4)), A5);
	assertInconsistent("a5(X)");
    }

    @Test
    public final void disjointWithAtomicConceptsToExistRoleExist4()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty P4 = kb.getRole("p4");
	final OWLClass A5 = kb.getConcept("a5");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a2(X):-a1(X)");
	assertConsistent("a2(X)");
	kb.addDisjunction(a1, a2);
	kb.addSubsumption(a2, kb.getExistential(kb.getInverse(p3)));
	kb.addSubsumption(kb.getInverse(p3), kb.getInverse(P4));
	kb.addSubsumption(kb.getExistential(kb.getInverse(P4)), A5);
	assertInconsistent("a5(X)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void disjointWithAtomicRoles()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addRule("p2(X,Y):-p1(X,Y).");
	assertConsistent("p2(X,Y)");
	kb.addDisjunction(p1, p2);
	assertInconsistent("p2(X,Y)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void disjointWithExistentialsOnTheLeft()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addRule("a2(X):-p1(X,_)");
	assertConsistent("a2(X)");
	kb.addDisjunction(kb.getExistential(p1), a2);
	assertInconsistent("a2(X)");
    }

    @Test
    public final void disjunctionExistRoleExist1()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass A4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));
	kb.addSubsumption(p2, p3);
	kb.addRule("a4(X):-a1(X)");
	assertConsistent("a4(X)");
	kb.addDisjunction(kb.getExistential(p3), A4);
	assertInconsistent("a4(X)");
    }

    @Test
    public final void disjunctionExistRoleExist2()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass A4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(kb.getInverse(p2)));
	kb.addSubsumption(kb.getInverse(p2), p3);
	kb.addRule("a4(X):-a1(X)");
	assertConsistent("a4(X)");
	kb.addDisjunction(kb.getExistential(p3), A4);
	assertInconsistent("a4(X)");
    }

    @Test
    public final void flounderingInS1() throws OWLOntologyCreationException {
	kb.clear();
	// originate the doubled rules
	final OWLClass ad1 = kb.getConcept("ad1");
	final OWLClass ad2 = kb.getConcept("ad2");
	kb.addDisjunction(ad1, ad2);

	// originate (s1) case
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));

	// to check an answer
	final OWLClass a3 = kb.getConcept("a3");
	kb.addSubsumption(kb.getExistential(p2), a3);
	assertAnswer("a3(X).", "a");
    }

    @Test
    public final void inconsistentRule() throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addDisjunction(a2, a1);
	kb.addRule("a2(X):-a1(X)");
	kb.addRule("a3(X):-a2(X)");
	kb.addRule("a1(X):-a3(X)");
	assertInconsistent("a3(a)");
	assertInconsistent("a2(a)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void irreflexiveRolesFromConceptDisjunction()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(p1, a, a);
	kb.addSubsumption(kb.getExistential(p1), a1);
	kb.addSubsumption(kb.getExistential(kb.getInverse(p1)), a2);
	kb.addDisjunction(a1, a2);
	assertInconsistent("p1(X,X)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void irreflexiveRolesFromRoleDisjunction()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(p1, a, a);
	kb.addSubsumption(p1, p2);
	kb.addSubsumption(kb.getInverse(p1), p3);
	kb.addDisjunction(p2, p3);
	assertInconsistent("p1(X,X)");
    }

    @Test
    public void normalizations() throws OWLOntologyCreationException {
	kb.clear();
	final OWLDataFactory df = kb.getDataFactory();
	// Intersection on right side
	final OWLClass[] A = kb.getConcepts(11);
	final OWLObjectProperty[] P = kb.getRoles(11);
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(A[1], a);
	kb.addSubsumption(A[1], df.getOWLObjectIntersectionOf(A[2], A[3]));
	assertAnswer("a2(X)", true, "a");
	assertAnswer("a3(X)", true, "a");
	// Qualified existential on right side
	kb.addSubsumption(A[3], df.getOWLObjectSomeValuesFrom(P[1], A[4]));
	kb.addSubsumption(kb.getExistential(P[1]), A[6]);
	assertAnswer("a6(X)", true, "a");
	// Concept equivalence
	kb.addAxiom(df.getOWLEquivalentClassesAxiom(A[6], A[7], A[8]));
	assertAnswer("a8(X)", "a");
	// Concept disjunction
	kb.addAxiom(df.getOWLDisjointClassesAxiom(A[7], A[9], A[10]));
	kb.addRule("a10(X):-a7(X)");
	assertInconsistent("a10(X)");
	// Inverse roles
	kb.addAssertion(P[2], a, b);
	kb.addAxiom(df.getOWLInverseObjectPropertiesAxiom(P[2], P[3]));
	assertAnswer("p3(X,Y)", true, "b", "a");

    }

    public final void roleCycle() throws OWLOntologyCreationException {
	kb.clear();
	kb.getConcept("A");
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	// kb.addSubsumption(kb.getExistential(p1), A);
	// kb.addSubsumption(kb.getExistential(kb.getInverse(p2)), A);
	kb.addSubsumption(p1, kb.getInverse(p2));
	kb.addSubsumption(p2, kb.getInverse(p1));
	try {
	    new HybridKB(kb.getOntology()).queryAll(parser
		    .parseQuery("p1(X,Y)."));
	} catch (final IOException e) {
	    fail(e.getMessage());
	} catch (final PrologParserException e) {
	    fail(e.getMessage());
	} catch (final Exception e) {
	    fail(e.getMessage());
	}
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
	kb = new KB();
    }

    @Test
    public final void subsumptionsExistRoleExist1()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass A4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));
	kb.addSubsumption(p2, p3);
	kb.addSubsumption(kb.getExistential(p3), A4);
	assertAnswer("a4(X)", new String[] { "a" });
    }

    @Test
    public final void subsumptionsExistRoleExist2()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass A4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));
	kb.addSubsumption(p2, kb.getInverse(p3));
	kb.addSubsumption(kb.getExistential(kb.getInverse(p3)), A4);
	assertAnswer("a4(X)", true, "a");
    }

    @Test
    public final void subsumptionsExistRoleExist3()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass A4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(kb.getInverse(p2)));
	kb.addSubsumption(kb.getInverse(p2), p3);
	kb.addSubsumption(kb.getExistential(p3), A4);
	assertAnswer("a4(X)", true, "a");
    }

    @Test
    public final void subsumptionsExistRoleExist4()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass A4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(kb.getInverse(p2)));
	kb.addSubsumption(kb.getInverse(p2), kb.getInverse(p3));
	kb.addSubsumption(kb.getExistential(kb.getInverse(p3)), A4);
	assertAnswer("a4(X)", true, "a");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void subsumptionsWithAtomicAndInverseRoles()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, kb.getInverse(p2));
	kb.addSubsumption(p2, p3);
	assertAnswer("p2(X,Y)", true, "b", "a");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void subsumptionWithAtomicConcepts()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, a2);
	assertAnswer("a2(X)", "a");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void subsumptionWithAtomicRoles()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, p2);
	assertAnswer("p2(X,Y)", "a", "b");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void subsumptionWithExistentialsOnTheLeft()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(kb.getExistential(p1), a2);
	assertAnswer("a2(X)", "a");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void subsumptionWithExistentialsOnTheRight()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));
	kb.addSubsumption(kb.getExistential(p2), a3);
	assertAnswer("a3(X)", "a");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void subsumptionWithInverseRoles()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, kb.getInverse(p2));
	assertAnswer("p2(X,Y)", true, "b", "a");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testEL() throws OWLOntologyCreationException {
	Config.translationAlgorithm = null;
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLObjectProperty p4 = kb.getRole("p4");
	final OWLObjectProperty p5 = kb.getRole("p5");
	final OWLClass a6 = kb.getConcept("a6");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLDataFactory df = kb.getDataFactory();
	kb.addAssertion(a1, a);
	kb.addAssertion(a2, a);
	kb.addSubsumption(df.getOWLObjectIntersectionOf(a1, a2), a3);
	kb.addSubsumption(a3, kb.getExistential(p4));
	kb.addSubsumption(p4, p5);
	kb.addSubsumption(kb.getExistential(p5), a6);
	assertAnswer("a6(X)", false, "a");
	Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void unsatisfiableConcepts()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, a2);
	kb.addSubsumption(a2, a1);
	kb.addSubsumption(a3, a2);
	kb.addDisjunction(a1, a2);
	kb.addRule("a3(X):-a1(X).");
	assertInconsistent("a3(X)");
    }

    /**
     * Test method for {@link nohr.reasoner.HybridKB#query(java.lang.String)}.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public final void unsatisfiableRoles() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, p2);
	kb.addSubsumption(p2, p1);
	kb.addSubsumption(p3, p2);
	kb.addDisjunction(p1, p2);
	kb.addRule("p3(X,Y):-p1(X,Y).");
	assertInconsistent("p3(X,Y)");
    }

}

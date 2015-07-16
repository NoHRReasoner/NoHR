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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.profiles.Profiles;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

/**
 *
 */

/**
 * @author nunocosta
 *
 */
public abstract class QueryTest extends Object {

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

    protected KB kb;

    protected void assertAnswer(String query, String... expectedAns) {
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = kb.queryAll(query);
	    assertNotNull("should't have null result", result);
	    assertEquals("should have exactly one answer", 1, result.size());
	    final Answer ans = result.iterator().next();
	    int i = 0;
	    for (final Term val : ans.getValues())
		assertEquals("sould be the expected constant",
			cons(expectedAns[i++]), val);
	} catch (final IOException e) {
	    fail(e.getMessage());
	} catch (final PrologParserException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedOWLProfile e) {
	    fail("ontology is not QL nor EL!\n" + e.getMessage());
	} catch (final InconsistentOntologyException e) {
	    fail("inconsistent ontology");
	} catch (final OWLOntologyCreationException e) {
	    fail(e.getMessage());
	} catch (final OWLOntologyStorageException e) {
	    fail(e.getMessage());
	} catch (final CloneNotSupportedException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedAxiomTypeException e) {
	    fail(e.getMessage());
	}
    }

    protected void assertConsistent(String query) {
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = kb.queryAll(query);
	    assertNotNull("should't hava null result", result);
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
	} catch (final OWLOntologyCreationException e) {
	    fail(e.getMessage());
	} catch (final OWLOntologyStorageException e) {
	    fail(e.getMessage());
	} catch (final CloneNotSupportedException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedAxiomTypeException e) {
	    fail(e.getMessage());
	}

    }

    protected void assertInconsistent(String query) {
	try {
	    if (!query.endsWith("."))
		query = query + ".";
	    final Collection<Answer> result = kb.queryAll(query);
	    assertNotNull("shouldn't have null result", result);
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
	    e.printStackTrace();
	    fail("inconsistent ontology");
	} catch (final OWLOntologyCreationException e) {
	    fail(e.getMessage());
	} catch (final OWLOntologyStorageException e) {
	    fail(e.getMessage());
	} catch (final CloneNotSupportedException e) {
	    fail(e.getMessage());
	} catch (final UnsupportedAxiomTypeException e) {
	    fail(e.getMessage());
	}
    }

    // (a1), (s1), (n1)
    @Test
    public final void cln2() throws OWLOntologyCreationException, IOException,
	    PrologParserException {
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

    // (a1), (a2), (s2), (n1)
    @Test
    public final void cln3() throws OWLOntologyCreationException, IOException,
	    PrologParserException {
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

    // (s1)
    @Test
    public final void conceptCycle() throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	kb.addSubsumption(a1, a2);
	kb.addSubsumption(a2, a1);
	try {
	    new HybridKB(kb.getOntology())
		    .queryAll(Parser.parseQuery("p1(X)."));
	} catch (final Exception e) {
	    fail(e.getMessage());
	}
    }

    // (a1), (n1)
    @Test
    public final void conceptDisjunction() throws OWLOntologyCreationException,
    IOException, PrologParserException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addRule("a2(X):-a1(X)");
	kb.addDisjunction(a1, a2);
	assertInconsistent("a2(X)");
    }

    // (a1), (s1)
    @Test
    public final void conceptSubsumption() throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, a2);
	assertAnswer("a2(X)", "a");
    }

    @Test
    public final void dataPropertyAssertions()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLDataProperty d1 = kb.getDataRole("d1");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(d1, a, "l");
	assertAnswer("d1(X,Y)", "a", "l");
    }

    // (a1), (n1)
    @Test
    public final void existentialDisjunction()
	    throws OWLOntologyCreationException, IOException,
	    PrologParserException {
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

    // (a1), (s1)
    @Test
    public final void existentialSubsumption()
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

    // (a1), (n1), (s1.c), (s2.c),
    @Test
    public final void inconsistencePropagation()
	    throws OWLOntologyCreationException, IOException,
	    PrologParserException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty p4 = kb.getRole("p4");
	final OWLClass a5 = kb.getConcept("a5");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addDisjunction(a1, a2);
	kb.addRule("a2(X):-a1(X)");
	kb.addSubsumption(a2, kb.getExistential(p3));
	kb.addSubsumption(p3, p4);
	kb.addSubsumption(kb.getExistential(p4), a5);
	assertInconsistent("a5(X)");
    }

    // (a1), (n1)
    @Test
    public final void inconsistentRules() throws OWLOntologyCreationException,
	    IOException, PrologParserException {
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

    // (a1), (s1), (s2)
    @Test
    public final void indirectExistentialSubsumption()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass a4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));
	kb.addSubsumption(p2, p3);
	kb.addSubsumption(kb.getExistential(p3), a4);
	assertAnswer("a4(X)", new String[] { "a" });
    }

    @Test
    public final void nonDefinedBodyPredicates()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	kb.addSubsumption(a1, a2);
	test("a2(X).");
    }

    // (s2)
    @Test
    public final void roleCycle() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	kb.addSubsumption(p1, p2);
	kb.addSubsumption(p2, p1);
	test("p1(X,Y).");
    }

    // (s2)
    @Test
    public final void roleSubsumption() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, p2);
	assertAnswer("p2(X,Y)", "a", "b");
    }

    @Before
    public void setUp() throws Exception {
	AbstractOntologyTranslation.profile = Profiles.OWL2_QL;
	kb = new KB();
    }

    @After
    public void tearDown() throws Exception {
    }

    private void test(String query) {
	try {
	    new HybridKB(kb.getOntology()).queryAll(Parser.parseQuery(query));
	} catch (final Exception e) {
	    fail(e.getMessage());
	}
    }

    // (i1) (a1), (s1)
    @Test
    public final void unsatisfiableConcepts()
	    throws OWLOntologyCreationException, IOException,
	    PrologParserException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	kb.addRule("a1(a).");
	kb.addSubsumption(a1, a2);
	kb.addSubsumption(a2, a1);
	kb.addSubsumption(a3, a2);
	kb.addDisjunction(a1, a2);
	kb.addRule("a3(X):-a1(X).");
	assertInconsistent("a3(X)");
    }

}

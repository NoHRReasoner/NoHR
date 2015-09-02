package unittest;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;

import helpers.KB;
import pt.unl.fct.di.centria.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

public class HybridKBTest extends KB {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public HybridKBTest() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IPException, IOException, CloneNotSupportedException,
			UnsupportedAxiomsException, PrologEngineCreationException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test1() throws ParseException, OWLProfilesViolationsException, IPException, IOException,
			UnsupportedAxiomsException, PrologEngineCreationException, OWLOntologyCreationException,
			OWLOntologyStorageException, CloneNotSupportedException {
		rule("A(a)");
		rule("B(?X):-A(?X)");
		subConcept("B", "C");
		assertTrue("C(a)");
		subConcept("D", "E");
		rule("D(?X):-C(?X)");
		assertTrue("E(a)");
		typeOf("F", "a");
		subConcept(conj("E", "F"), conc("G"));
		assertTrue("G(a)");
		rule("P(c, d)");
		rule("R(?X, ?Y) :- Q(?X, ?Y)");
		subRole("P", "Q");
		assertTrue("R(c, d)");
	}

	@Test
	public void test2() throws OWLProfilesViolationsException, IPException, IOException, UnsupportedAxiomsException,
			PrologEngineCreationException {
		clear();
		final OWLAxiom axiom = typeOf(conc("a"), individual("i"));
		assertTrue("a(i)");
		removeAxiom(axiom);
		assertFalse("a(i)");
		typeOf(conc("a"), individual("i"));
		assertTrue("a(i)");
	}

	@Test
	public void test3() throws OWLProfilesViolationsException, IPException, IOException, UnsupportedAxiomsException,
			PrologEngineCreationException, InterruptedException {
		clear();
		final Rule rule = rule("a(i)");
		assertTrue("a(i)");
		final OWLAxiom axiom = typeOf("a", "i");
		assertTrue("a(i)");
		removeAxiom(axiom);
		assertTrue("a(i)");
		remove(rule);
		System.gc();
		Thread.sleep(1100);
		assertFalse("a(i)");
	}

	@Test
	public void test4() throws OWLProfilesViolationsException, IPException, IOException, UnsupportedAxiomsException,
			PrologEngineCreationException, InterruptedException {
		clear();
		final Rule rule = rule("p(a, b)");
		assertTrue("p(a, b)");
		final OWLAxiom axiom = object("p", "a", "b");
		assertTrue("p(a, b)");
		removeAxiom(axiom);
		assertTrue("p(a, b)");
		remove(rule);
		System.gc();
		Thread.sleep(1100);
		assertFalse("p(a, b)");
	}

}

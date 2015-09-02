package unittest;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;

import helpers.KB;
import pt.unl.fct.di.centria.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.terminals.DefaultVocabulary;
import pt.unl.fct.di.centria.nohr.model.terminals.Vocabulary;
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
	public final void test() throws ParseException, OWLProfilesViolationsException, IPException, IOException,
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
	public final void test3() throws OWLOntologyCreationException {
		final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
		final OWLClass concept = dataFactory.getOWLClass(IRI.create("http://test.com/path#A"));
		final OWLIndividual individual = OWLManager.getOWLDataFactory()
				.getOWLNamedIndividual(IRI.create("http://test.com/path#a"));
		final OWLAxiom assertion = dataFactory.getOWLClassAssertionAxiom(concept, individual);
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = ontologyManager.createOntology();
		ontologyManager.addAxiom(ontology, assertion);
		final Vocabulary vocabulary = new DefaultVocabulary(ontology);
		final Constant constant = vocabulary.cons(individual);
		System.out.println(constant.toString());
	}

}

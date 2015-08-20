import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;

import helpers.KB;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;
import pt.unl.fct.di.centria.nohr.prolog.DatabaseCreationException;
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
			UnsupportedAxiomsException, DatabaseCreationException {
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
			UnsupportedAxiomsException, DatabaseCreationException, OWLOntologyCreationException,
			OWLOntologyStorageException, CloneNotSupportedException {
		rule("A(a)");
		rule("B(?X):-A(?X)");
		subConcept("B", "C");
		assertTrue("C(a)");
		rule("D(?X):-C(?X)");
		subConcept("D", "E");
		assertTrue("E(a)");
		rule("F(a)");
		subConcept(conj("E", "F"), conc("G"));
		assertTrue("G(a)");
	}

}

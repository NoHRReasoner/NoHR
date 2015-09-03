package unittest;

import static pt.unl.fct.di.novalincs.nohr.model.Model.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;
import pt.unl.fct.di.centria.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

/**
 *
 */

/**
 * @author nunocosta
 */
public class ParserTest {

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

	@Test
	public void parseFail() {
		final NoHRParser parser = new NoHRRecursiveDescentParser();
		try {
			parser.parseRule("p(?");
			Assert.fail();
		} catch (final ParseException e) {
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testParseQuery() throws Throwable {
		try {
			final Vocabulary v = new DefaultVocabulary(
					OWLManager.createOWLOntologyManager().createOntology(IRI.generateDocumentIRI()));
			final NoHRRecursiveDescentParser parser = new NoHRRecursiveDescentParser(v);
			final Variable X = var("X");
			Assert.assertEquals(query(atom(v, "p", X)), parser.parseQuery("p(?X)"));
			Assert.assertEquals(query(atom(v, "p", v.cons("a"))), parser.parseQuery("p(a)"));
			Assert.assertEquals(query(atom(v, "p", X), atom(v, "q", X), atom(v, "r", X)),
					parser.parseQuery("p(?X),  q(?X), r(?X)"));
			Assert.assertEquals(query(atom(v, "p", v.cons("a"), v.cons("b"), v.cons("c"))),
					parser.parseQuery("p(a, b, c)"));
			Assert.assertEquals(query(atom(v, "p", v.cons(1))), parser.parseQuery("p(1)"));
			Assert.assertEquals(query(atom(v, "p", v.cons(1.1))), parser.parseQuery("p(1.1)"));
		} catch (final ExceptionInInitializerError e) {
			throw e.getCause();
		}
	}

	@Test
	public void testParseRule() throws ParseException, OWLOntologyCreationException {
		final Vocabulary v = new DefaultVocabulary(
				OWLManager.createOWLOntologyManager().createOntology(IRI.generateDocumentIRI()));
		final NoHRRecursiveDescentParser parser = new NoHRRecursiveDescentParser(v);
		final Rule expectedRule = rule(atom(v, "p", var("X"), var("Y"), var("Z")), atom(v, "q", var("X"), var("Y")),
				atom(v, "r", v.cons("a")), atom(v, " a b \\:-() c "), atom(v, " a b \\:-() c "),
				negLiteral(atom(v, "z", var("X"))), negLiteral(v.pred("w", 1), var("Y")));
		final Rule actualRule = parser.parseRule(
				"p(?X, ?Y,  ?Z) :- q(?X, ?Y),r(a), \\ a\\ b\\ \\\\\\:-\\(\\)\\ c\\ , ' a b \\:-() c ' , not z(?X) ,  not w(?Y)");
		Assert.assertEquals(expectedRule, actualRule);
	}

}

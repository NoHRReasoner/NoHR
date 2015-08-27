package unittest;
import static pt.unl.fct.di.centria.nohr.model.Model.*;

import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;
import pt.unl.fct.di.centria.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;

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
			final NoHRRecursiveDescentParser parser = new NoHRRecursiveDescentParser();
			final Variable X = var("X");
			Assert.assertEquals(query(atom("p", X)), parser.parseQuery("p(?X)"));
			Assert.assertEquals(query(atom("p", cons("a"))), parser.parseQuery("p(a)"));
			Assert.assertEquals(query(atom("p", X), atom("q", X), atom("r", X)),
					parser.parseQuery("p(?X),  q(?X), r(?X)"));
			Assert.assertEquals(query(atom("p", cons("a"), cons("b"), cons("c"))), parser.parseQuery("p(a, b ,  c)"));
			Assert.assertEquals(query(atom("p", cons(1))), parser.parseQuery("p(1)"));
			Assert.assertEquals(query(atom("p", cons(1.1))), parser.parseQuery("p(1.1)"));
		} catch (final ExceptionInInitializerError e) {
			throw e.getCause();
		}
	}

	@Test
	public void testParseRule() throws ParseException {
		final NoHRRecursiveDescentParser parser = new NoHRRecursiveDescentParser();
		final Rule expectedRule = rule(atom("p", var("X"), var("Y"), var("Z")), atom("q", var("X"), var("Y")),
				atom("r", cons("a")), negLiteral(atom("z", var("X"))), negLiteral(pred("w", 1), var("Y")));
		final Rule actualRule = parser.parseRule("p(?X, ?Y,  ?Z) :- q(?X, ?Y),r(a), not z(?X),  not w(?Y)");
		Assert.assertEquals(expectedRule, actualRule);
	}

}

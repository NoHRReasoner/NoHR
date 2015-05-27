import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.posLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.query;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.parsing.Parser;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

/**
 *
 */

/**
 * @author nunocosta
 *
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

    private final Parser parser = new Parser();

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
     * Test method for
     * {@link pt.unl.fct.di.centria.nohr.parsing.Parser#parseQuery(java.lang.String)}
     * .
     */
    @Test
    public final void testParseQuery() {
	try {
	    Variable X = var("X");
	    Assert.assertEquals(parser.parseQuery("p(X)."),
		    query(posLiteral("p", X)));
	    Assert.assertEquals(parser.parseQuery("p(a)."),
		    query(posLiteral("p", cons("a"))));
	    Assert.assertEquals(
		    parser.parseQuery("p(X), q(X), r(X)."),
		    query(posLiteral("p", X), posLiteral("q", X),
			    posLiteral("r", X)));
	    Assert.assertEquals(parser.parseQuery("p(a, b, c)."),
		    query(posLiteral("p", cons("a"), cons("b"), cons("c"))));
	    Assert.assertEquals(parser.parseQuery("p(1)."),
		    query(posLiteral("p", cons(1))));
	    Assert.assertEquals(parser.parseQuery("p(1.1)."),
		    query(posLiteral("p", cons(1.1))));

	} catch (PrologParserException e) {
	    Assert.fail(e.getLocalizedMessage());
	    e.printStackTrace();
	} catch (IOException e) {
	    Assert.fail(e.getLocalizedMessage());
	    e.printStackTrace();
	}
    }
}

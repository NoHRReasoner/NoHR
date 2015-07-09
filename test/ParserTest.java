import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.query;
import static pt.unl.fct.di.centria.nohr.model.Model.*;

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
    public final void testParseQuery() {
	try {
	    final Variable X = var("X");
	    Assert.assertEquals(Parser.parseQuery("p(X)."), query(atom("p", X)));
	    Assert.assertEquals(Parser.parseQuery("p(a)."),
		    query(atom("p", cons("a"))));
	    Assert.assertEquals(Parser.parseQuery("p(X), q(X), r(X)."),
		    query(atom("p", X), atom("q", X), atom("r", X)));
	    Assert.assertEquals(Parser.parseQuery("p(a, b, c)."),
		    query(atom("p", cons("a"), cons("b"), cons("c"))));
	    Assert.assertEquals(Parser.parseQuery("p(1)."),
		    query(atom("p", cons(1))));
	    Assert.assertEquals(Parser.parseQuery("p(1.1)."),
		    query(atom("p", cons(1.1))));

	} catch (final PrologParserException e) {
	    Assert.fail(e.getLocalizedMessage());
	    e.printStackTrace();
	} catch (final IOException e) {
	    Assert.fail(e.getLocalizedMessage());
	    e.printStackTrace();
	}
    }

    @Test
    public void testParseRule() {
	// final Rule expectedRule = rule(atom(pred("p", 3), var("X"), var("Y"),
	// var("Z")), atom(pred("q", 2), var("X"), var("Y")), atom(pred("r", 1),
	// cons("a")), negLiteral(pred("z", 1), var("X")), negLiteral(pred("w",
	// 1), var("Y")));
	// final Rule actualRule =
	// Parser.parseRule("p(X,Y,Z):-q(X,Y),r(a),tnot(z(X)),tnot(w(Y))."));
    }
}

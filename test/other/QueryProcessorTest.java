/**
 *
 */
package other;

import static nohr.model.Model.cons;
import static nohr.model.Model.posLiteral;
import static nohr.model.Model.subs;
import static nohr.model.Model.var;
import static org.junit.Assert.fail;

import java.util.Map;

import nohr.model.Model;
import nohr.model.Query;
import nohr.model.Substitution;
import nohr.model.TruthValue;
import nohr.model.Variable;
import nohr.reasoner.QueryProcessor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xsb.XSBDatabase;

/**
 * @author nunocosta
 *
 */
public class QueryProcessorTest extends QueryProcessor {

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
     * @param xsbDatabase
     * @throws Exception
     */
    public QueryProcessorTest() throws Exception {
	super(new XSBDatabase());
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
     * Test method for
     * {@link nohr.reasoner.QueryProcessor#query(nohr.model.Query)}.
     */
    @Test
    public final void testQuery() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link nohr.reasoner.QueryProcessor#queryAll(nohr.model.Query)}.
     */
    @Test
    public final void testQueryAll() {
	xsbDatabase.table("ap/1");
	xsbDatabase.table("dp/1");

	xsbDatabase.add("ap(a)");
	xsbDatabase.add("dp(a)");

	xsbDatabase.add("ap(b)");
	xsbDatabase.add("dp(b):-tnot(dp(b))");

	xsbDatabase.add("ap(c)");

	xsbDatabase.add("ap(d):-tnot(ap(d))");
	xsbDatabase.add("dp(d)");

	xsbDatabase.add("ap(e):-tnot(ap(e))");
	xsbDatabase.add("dp(e):-tnot(ap(e))");

	xsbDatabase.add("ap(f):-tnot(ap(f))");

	xsbDatabase.add("dp(g)");

	xsbDatabase.add("dp(h):-tnot(dp(h))");

	Variable var = var("X");
	Query query = Model.query(posLiteral("p", var));

	Map<Substitution, TruthValue> ans = queryAll(query);

	Assert.assertEquals(TruthValue.TRUE, ans.get(subs(var, cons("a"))));
	Assert.assertEquals(TruthValue.TRUE, ans.get(subs(var, cons("b"))));
	Assert.assertEquals(TruthValue.INCONSITENT,
		ans.get(subs(var, cons("c"))));
	Assert.assertEquals(TruthValue.UNDIFINED, ans.get(subs(var, cons("d"))));
	Assert.assertEquals(TruthValue.UNDIFINED, ans.get(subs(var, cons("e"))));
	Assert.assertNull(ans.get(subs(var, cons("f"))));
	Assert.assertNull(ans.get(subs(var, cons("g"))));
	Assert.assertNull(ans.get(subs(var, cons("h"))));
	Assert.assertNull(ans.get(subs(var, cons("i"))));
    }
}

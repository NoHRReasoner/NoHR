/**
 *
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static pt.unl.fct.di.centria.nohr.model.Model.ans;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.posLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.QueryProcessor;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;

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
	super(new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb")));
    }

    private List<Term> l(Term... elems) {
	List<Term> res = new LinkedList<Term>();
	Collections.addAll(res, elems);
	return res;
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
     * {@link nohr.reasoner.QueryProcessor#query(pt.unl.fct.di.centria.nohr.model.Query)}
     * .
     */
    // @Test
    // public final void testQuery() {
    // fail("Not yet implemented"); // TODO
    // }

    @Test
    public final void testQuery() {
	Variable var = var("X");

	Query q = Model.query(posLiteral("p", var));
	xsbDatabase.add("ap(a)");
	xsbDatabase.add("dp(a)");
	assertEquals(ans(q, TruthValue.TRUE, l(cons("a"))), query(q));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("a"))),
		query(q, TruthValue.TRUE, true));
	assertNull(query(q, TruthValue.UNDEFINED, true));
	assertNull(query(q, TruthValue.INCONSITENT, true));

	assertEquals(ans(q, TruthValue.TRUE, l(cons("a"))), query(q, false));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("a"))),
		query(q, TruthValue.TRUE, false));
	assertNull(query(q, TruthValue.UNDEFINED, false));

	q = Model.query(posLiteral("q", var));
	xsbDatabase.command("assert(aq(b))");
	xsbDatabase.command("table(dq/1),assert((dq(b):-tnot(dq(b))))");
	assertEquals(ans(q, TruthValue.TRUE, l(cons("b"))), query(q));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("b"))),
		query(q, TruthValue.TRUE, true));
	assertNull(query(q, TruthValue.UNDEFINED, true));
	// XSBDatabase.hasAnswers() don't return
	// assertNull(query(q, TruthValue.INCONSITENT, true));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("b"))), query(q, false));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("b"))),
		query(q, TruthValue.TRUE, false));
	assertNull(query(q, TruthValue.UNDEFINED, false));
	// XSBDatabase.hasAnswers() don't return
	// assertNull(query(q, TruthValue.INCONSITENT, true));

	q = Model.query(posLiteral("r", var));
	xsbDatabase.add("ar(c)");
	xsbDatabase.add("dr(o)");
	assertEquals(ans(q, TruthValue.INCONSITENT, l(cons("c"))), query(q));
	// assertEquals(ans(q, TruthValue.INCONSITENT, l(cons("c"))),
	// query(q, TruthValue.INCONSITENT, true));
	assertNull(query(q, TruthValue.TRUE, true));
	assertNull(query(q, TruthValue.UNDEFINED, true));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("c"))), query(q, false));
	assertEquals(ans(q, TruthValue.TRUE, l(cons("c"))),
		query(q, TruthValue.TRUE, false));
	assertNull(query(q, TruthValue.UNDEFINED, false));

	q = Model.query(posLiteral("s", var));
	xsbDatabase.command("table(as/1),assert((as(d):-tnot(as(d))))");
	xsbDatabase.add("ds(d)");
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("d"))), query(q));
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("d"))),
		query(q, TruthValue.UNDEFINED, true));
	assertNull(query(q, TruthValue.TRUE, true));
	assertNull(query(q, TruthValue.INCONSITENT, true));
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("d"))),
		query(q, false));
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("d"))),
		query(q, TruthValue.UNDEFINED, false));
	assertNull(query(q, TruthValue.TRUE, false));

	q = Model.query(posLiteral("t", var));
	xsbDatabase.table("at/1");
	xsbDatabase.table("dt/1");
	xsbDatabase.add("at(e):-tnot(at(e))");
	xsbDatabase.add("dt(e):-tnot(at(e))");
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("e"))), query(q));
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("e"))),
		query(q, TruthValue.UNDEFINED, true));
	assertNull(query(q, TruthValue.TRUE, true));
	assertNull(query(q, TruthValue.INCONSITENT, true));
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("e"))),
		query(q, false));
	assertEquals(ans(q, TruthValue.UNDEFINED, l(cons("e"))),
		query(q, TruthValue.UNDEFINED, false));
	assertNull(query(q, TruthValue.TRUE, false));
    }

    /**
     * Test method for
     * {@link nohr.reasoner.QueryProcessor#queryAll(pt.unl.fct.di.centria.nohr.model.Query)}
     * .
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
	Query q = Model.query(posLiteral("p", var));

	Collection<Answer> ans = queryAll(q, true);

	Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("a")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("b")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSITENT,
		l(cons("c")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED,
		l(cons("d")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED,
		l(cons("e")))));
	Assert.assertTrue(ans.size() == 5);
    }
}

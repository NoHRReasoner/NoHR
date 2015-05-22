/**
 *
 */
package other;

import static nohr.model.Model.ans;
import static nohr.model.Model.cons;
import static nohr.model.Model.posLiteral;
import static nohr.model.Model.var;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nohr.model.Answer;
import nohr.model.Model;
import nohr.model.Query;
import nohr.model.Term;
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
	Query q = Model.query(posLiteral("p", var));

	Collection<Answer> ans = queryAll(q);

	Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("a")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("b")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSITENT,
		l(cons("c")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDIFINED,
		l(cons("d")))));
	Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDIFINED,
		l(cons("e")))));
	Assert.assertTrue(ans.size() == 5);
    }
}

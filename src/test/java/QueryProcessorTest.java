
/**
 *
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pt.unl.fct.di.centria.nohr.model.Model.ans;
import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.QueryProcessor;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabaseCreationException;

/**
 * @author nunocosta
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
		super(new XSBDatabase(FileSystems.getDefault().getPath(System.getenv("XSB_BIN_DIRECTORY"), "xsb").toFile()));
	}

	private List<Term> l(Term... elems) {
		final List<Term> res = new LinkedList<Term>();
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

	@Test
	public final void testHasAnswer() {
		final Variable var = var("X");
		// true true
		Query q = Model.query(atom("p", var));
		xsbDatabase.add("ap(a)");
		xsbDatabase.add("dp(a)");
		assertTrue(hasAnswer(q, true));
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// true undefined
		q = Model.query(atom("q", var));
		xsbDatabase.add("aq(b)");
		xsbDatabase.table("dq/1");
		xsbDatabase.add("dq(b):-tnot(dq(b))");
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// true false
		q = Model.query(atom("r", var));
		xsbDatabase.add("ar(c)");
		xsbDatabase.add("dr(o)");
		assertTrue(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertTrue(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// undefined true
		q = Model.query(atom("s", var));
		xsbDatabase.table("as/1");
		xsbDatabase.add("as(d):-tnot(as(d))");
		xsbDatabase.add("ds(d)");
		assertTrue(hasAnswer(q, true));
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// undefined undefined
		q = Model.query(atom("t", var));
		xsbDatabase.table("at/1");
		xsbDatabase.add("at(e):-tnot(at(e))");
		xsbDatabase.table("dt/1");
		xsbDatabase.add("dt(e):-tnot(at(e))");
		assertTrue(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertTrue(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// undefined false
		q = Model.query(atom("u", var));
		xsbDatabase.table("au/1");
		xsbDatabase.add("au(f):-tnot(au(f))");
		xsbDatabase.add("du(o)");
		assertFalse(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// false false
		q = Model.query(atom("v", var));
		xsbDatabase.add("av(o)");
		xsbDatabase.add("dv(l)");
		assertFalse(hasAnswer(q, true, true, true, false));
	}

	/**
	 * Test method for {@link nohr.reasoner.QueryProcessor#lazilyQuery(pt.unl.fct.di.centria.nohr.model.Query)} .
	 *
	 * @throws IOException
	 * @throws IPException
	 * @throws XSBDatabaseCreationException
	 */
	// TODO fix QueryProcessor.lazilyQuery()
	// wrong when original answer is UNDEFINED and the doubled answer is TRUE
	public final void testLazilyQuery() throws IPException, IOException, XSBDatabaseCreationException {

		xsbDatabase = new XSBDatabase(FileSystems.getDefault().getPath(System.getenv("XSB_BIN_DIRECTORY")).toFile());

		xsbDatabase.table("ap/1");
		xsbDatabase.table("dp/1");

		// xsbDatabase
		// .command("table(az/1),assert((az(j):-tnot(az(j)))), assert(dz(j))");
		final Query qt = Model.query(atom("dz", cons("j")));

		assertTrue(xsbDatabase.hasAnswers(qt));
		assertTrue(xsbDatabase.hasAnswers(qt, true));
		assertFalse(xsbDatabase.hasAnswers(qt, false));

		xsbDatabase.add("dp(h):-tnot(dp(h))");

		xsbDatabase.add("dp(g)");

		xsbDatabase.add("ap(f):-tnot(ap(f))");

		xsbDatabase.add("ap(e):-tnot(ap(e))");
		xsbDatabase.add("dp(e):-tnot(ap(e))");

		xsbDatabase.add("dp(i)");
		xsbDatabase.add("ap(i):-tnot(ap(i))");

		xsbDatabase.add("ap(c)");

		xsbDatabase.add("ap(b)");
		xsbDatabase.add("dp(b):-tnot(dp(b))");

		xsbDatabase.add("ap(a)");
		xsbDatabase.add("dp(a)");

		assertTrue(xsbDatabase.hasAnswers(qt));
		assertFalse(xsbDatabase.hasAnswers(qt, false));
		assertTrue(xsbDatabase.hasAnswers(qt, true));

		final Variable var = var("X");
		final Query q = Model.query(atom("p", var));

		Iterator<Answer> ans = lazilyQuery(q, true, true, true, true).iterator();
		assertTrue(ans.hasNext());
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("a"))));
		assertTrue(ans.hasNext());
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("b"))));
		assertTrue(ans.hasNext());
		assertEquals(ans.next(), ans(q, TruthValue.INCONSISTENT, l(cons("c"))));

		assertTrue(xsbDatabase.hasAnswers(qt));

		// if XSBDatabase.lastSolutionsIterator isn't cancelled then the
		// following happens:
		// XSBDatabase.hasAnswers(q, true) return true iff there is undefined
		// answers and XSBDatabase.hasAnswers(q, false) return true iff there is
		// true answers
		// xsbDatabase.cancelLastIterator();

		assertFalse(xsbDatabase.hasAnswers(qt, false));
		assertTrue(xsbDatabase.hasAnswers(qt, true));

		assertTrue(ans.hasNext());
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("i"))));
		assertTrue(ans.hasNext());
		assertTrue(ans.hasNext());
		assertEquals(ans.next(), ans(q, TruthValue.UNDEFINED, l(cons("e"))));
		assertFalse(ans.hasNext());

		ans = lazilyQuery(q, true, true, true, false).iterator();
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("a"))));
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("b"))));
		assertEquals(ans(q, TruthValue.TRUE, l(cons("d"))), ans.next());
		assertEquals(ans.next(), ans(q, TruthValue.UNDEFINED, l(cons("e"))));
		assertFalse(ans.hasNext());

		ans = lazilyQuery(q, true, true, false, true).iterator();
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("a"))));
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("b"))));
		assertEquals(ans.next(), ans(q, TruthValue.INCONSISTENT, l(cons("c"))));
		// assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("d"))));
		assertFalse(ans.hasNext());

		ans = lazilyQuery(q, true, false, true, true).iterator();
		assertEquals(ans.next(), ans(q, TruthValue.INCONSISTENT, l(cons("c"))));
		ans.next();
		assertEquals(ans.next(), ans(q, TruthValue.UNDEFINED, l(cons("e"))));
		assertFalse(ans.hasNext());

		ans = lazilyQuery(q, true, true, false, false).iterator();
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("a"))));
		assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("b"))));
		// assertEquals(ans.next(), ans(q, TruthValue.TRUE, l(cons("d"))));
		assertFalse(ans.hasNext());

		ans = lazilyQuery(q, true, false, true, false).iterator();
		ans.next();
		assertEquals(ans.next(), ans(q, TruthValue.UNDEFINED, l(cons("e"))));
		assertFalse(ans.hasNext());

		ans = lazilyQuery(q, true, false, false, true).iterator();
		assertEquals(ans.next(), ans(q, TruthValue.INCONSISTENT, l(cons("c"))));
		assertFalse(ans.hasNext());

	}

	@Test
	public final void testQuery() {
		final Variable var = var("X");
		// true true
		Query q = Model.query(atom("p", var));
		xsbDatabase.add("ap(a)");
		xsbDatabase.add("dp(a)");
		Answer ans = ans(q, TruthValue.TRUE, l(cons("a")));
		assertEquals(ans, query(q));
		assertEquals(ans, query(q, true, true, false, false));
		assertNull(query(q, true, false, true, false));
		assertNull(query(q, true, false, false, true));
		assertEquals(ans, query(q, false));
		assertEquals(ans, query(q, false, true, false, false));
		assertNull(query(q, false, false, true, false));
		// true undefined
		q = Model.query(atom("q", var));
		xsbDatabase.add("aq(b)");
		xsbDatabase.table("dq/1");
		xsbDatabase.add("dq(b):-tnot(dq(b))");
		ans = ans(q, TruthValue.TRUE, l(cons("b")));
		assertEquals(ans, query(q));
		assertEquals(ans, query(q, true, true, false, false));
		assertNull(query(q, true, false, true, false));
		assertNull(query(q, true, false, false, true));
		assertEquals(ans, query(q, false));
		assertEquals(ans, query(q, false, true, false, false));
		assertNull(query(q, false, false, true, false));
		// true false
		q = Model.query(atom("r", var));
		xsbDatabase.add("ar(c)");
		xsbDatabase.add("dr(o)");
		ans = ans(q, TruthValue.INCONSISTENT, l(cons("c")));
		assertEquals(ans, query(q));
		assertNull(query(q, true, true, false, false));
		assertNull(query(q, true, false, true, false));
		assertEquals(ans, query(q, true, false, false, true));
		ans = ans(q, TruthValue.TRUE, l(cons("c")));
		assertEquals(ans, query(q, false));
		assertEquals(ans, query(q, false, true, false, false));
		assertNull(query(q, false, false, true, false));
		// undefined true
		q = Model.query(atom("s", var));
		xsbDatabase.table("as/1");
		xsbDatabase.add("as(d):-tnot(as(d))");
		xsbDatabase.add("ds(d)");
		ans = ans(q, TruthValue.TRUE, l(cons("d")));
		assertEquals(ans, query(q));
		assertEquals(ans, query(q, true, true, false, false));
		assertNull(query(q, true, false, true, false));
		assertNull(query(q, true, false, false, true));
		ans = ans(q, TruthValue.UNDEFINED, l(cons("d")));
		assertEquals(ans, query(q, false));
		assertNull(query(q, false, true, false, false));
		assertEquals(ans, query(q, false, false, true, false));
		// undefined undefined
		q = Model.query(atom("t", var));
		xsbDatabase.table("at/1");
		xsbDatabase.add("at(e):-tnot(at(e))");
		xsbDatabase.table("dt/1");
		xsbDatabase.add("dt(e):-tnot(at(e))");
		ans = ans(q, TruthValue.UNDEFINED, l(cons("e")));
		assertEquals(ans, query(q));
		assertNull(query(q, true, true, false, false));
		assertEquals(ans, query(q, true, false, true, false));
		assertNull(query(q, true, false, false, true));
		assertEquals(ans, query(q, false));
		assertNull(query(q, false, true, false, false));
		assertEquals(ans, query(q, false, false, true, false));
		// undefined false
		q = Model.query(atom("u", var));
		xsbDatabase.table("au/1");
		xsbDatabase.add("au(f):-tnot(au(f))");
		xsbDatabase.add("du(o)");
		assertNull(query(q));
		assertNull(query(q, true, true, false, false));
		assertNull(query(q, true, false, true, false));
		assertNull(query(q, true, false, false, true));
		ans = ans(q, TruthValue.UNDEFINED, l(cons("f")));
		assertEquals(ans, query(q, false));
		assertNull(query(q, false, true, false, false));
		assertEquals(ans, query(q, false, false, true, false));
		// false false
		q = Model.query(atom("v", var));
		xsbDatabase.add("av(o)");
		xsbDatabase.add("dv(l)");
		assertNull(query(q, true, true, true, false));
	}

	/**
	 * Test method for {@link nohr.reasoner.QueryProcessor#queryAll(pt.unl.fct.di.centria.nohr.model.Query)} .
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

		final Variable var = var("X");
		final Query q = Model.query(atom("p", var));

		Collection<Answer> ans = queryAll(q, true, true, true, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(cons("c")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("d")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(cons("e")))));
		Assert.assertTrue(ans.size() == 5);

		ans = queryAll(q, true, true, true, false);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("d")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(cons("e")))));
		Assert.assertTrue(ans.size() == 4);

		ans = queryAll(q, true, true, false, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(cons("c")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("d")))));
		Assert.assertTrue(ans.size() == 4);

		ans = queryAll(q, true, false, true, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(cons("c")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(cons("e")))));
		Assert.assertTrue(ans.size() == 2);

		ans = queryAll(q, true, true, false, false);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(cons("d")))));
		Assert.assertTrue(ans.size() == 3);

		ans = queryAll(q, true, false, true, false);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(cons("e")))));
		Assert.assertTrue(ans.size() == 1);

		ans = queryAll(q, true, false, false, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(cons("c")))));
		Assert.assertTrue(ans.size() == 1);

	}
}

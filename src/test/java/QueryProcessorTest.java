
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
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;
import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.prolog.XSBDedutiveDatabase;
import pt.unl.fct.di.centria.nohr.reasoner.QueryProcessor;

/**
 * @author nunocosta
 */
public class QueryProcessorTest extends QueryProcessor {

	class PrologProgram implements Program {

		private final Set<TableDirective> tabled;
		private final Set<Rule> rules;
		private final Parser parser = new NoHRParser();

		public PrologProgram() {
			tabled = new HashSet<>();
			rules = new HashSet<>();
		}

		@Override
		public Program accept(ModelVisitor visitor) {
			return null;
		}

		public void add(String rule) throws ParseException {
			rules.add(parser.parseRule(rule));
			loaded = false;
		}

		public void clear() {
			tabled.clear();
			rules.clear();
		}

		@Override
		public String getID() {
			return null;
		}

		@Override
		public Set<Rule> getRules() {
			return rules;
		}

		@Override
		public Set<TableDirective> getTableDirectives() {
			return tabled;
		}

		public void table(String predicate) {
			final String[] sp = predicate.split("/");
			tabled.add(Model.table(pred(sp[0], Integer.valueOf(sp[1]))));
			loaded = false;
		}

	}

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

	final PrologProgram program;
	boolean loaded;

	/**
	 * @param xsbDatabase
	 * @throws Exception
	 */
	public QueryProcessorTest() throws Exception {
		super(new XSBDedutiveDatabase(
				FileSystems.getDefault().getPath(System.getenv("XSB_BIN_DIRECTORY"), "xsb").toFile()));
		program = new PrologProgram();
	}

	@Override
	public boolean hasAnswer(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (!loaded)
			xsbDatabase.load(program);
		loaded = true;
		return super.hasAnswer(query, hasDoubled, trueAnswers, undefinedAnswers, inconsistentAnswers);
	}

	private List<Term> l(Term... elems) {
		final List<Term> res = new LinkedList<Term>();
		Collections.addAll(res, elems);
		return res;
	}

	@Override
	public Answer query(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (!loaded)
			xsbDatabase.load(program);
		loaded = true;
		return super.query(query, hasDoubled, trueAnswers, undefinedAnswers, inconsistentAnswers);
	}

	@Override
	public List<Answer> queryAll(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (!loaded)
			xsbDatabase.load(program);
		loaded = true;
		return super.queryAll(query, hasDoubled, trueAnswers, undefinedAnswers, inconsistentAnswers);
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
	public final void testHasAnswer() throws IOException, ParseException {

		final Variable var = var("X");
		// true true
		Query q = Model.query(atom("p", var));
		program.add("ap(a)");
		program.add("dp(a)");
		assertTrue(hasAnswer(q, true));
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// true undefined
		q = Model.query(atom("q", var));
		program.add("aq(b)");
		program.table("dq/1");
		program.add("dq(b):-not dq(b)");
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// true false
		q = Model.query(atom("r", var));
		program.add("ar(c)");
		program.add("dr(o)");
		assertTrue(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertTrue(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// undefined true
		q = Model.query(atom("s", var));
		program.table("as/1");
		program.add("as(d):-not as(d)");
		program.add("ds(d)");
		assertTrue(hasAnswer(q, true));
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// undefined undefined
		q = Model.query(atom("t", var));
		program.table("at/1");
		program.add("at(e):-not at(e)");
		program.table("dt/1");
		program.add("dt(e):-not at(e)");
		assertTrue(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertTrue(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// undefined false
		q = Model.query(atom("u", var));
		program.table("au/1");
		program.add("au(f):-not au(f)");
		program.add("du(o)");
		assertFalse(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// false false
		q = Model.query(atom("v", var));
		program.add("av(o)");
		program.add("dv(l)");
		assertFalse(hasAnswer(q, true, true, true, false));
	}

	@Test
	public final void testQuery() throws IOException, ParseException {

		final Variable var = var("X");
		// true true
		Query q = Model.query(atom("p", var));
		program.add("ap(a)");
		program.add("dp(a)");
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
		program.add("aq(b)");
		program.table("dq/1");
		program.add("dq(b):-not dq(b)");
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
		program.add("ar(c)");
		program.add("dr(o)");
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
		program.table("as/1");
		program.add("as(d):-not as(d)");
		program.add("ds(d)");
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
		program.table("at/1");
		program.add("at(e):-not at(e)");
		program.table("dt/1");
		program.add("dt(e):-not at(e)");
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
		program.table("au/1");
		program.add("au(f):-not au(f)");
		program.add("du(o)");
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
		program.add("av(o)");
		program.add("dv(l)");
		assertNull(query(q, true, true, true, false));
	}

	/**
	 * Test method for {@link nohr.reasoner.QueryProcessor#answersValuations(pt.unl.fct.di.centria.nohr.model.Query)} .
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public final void testQueryAll() throws IOException, ParseException {

		program.table("ap/1");
		program.table("dp/1");

		program.add("ap(a)");
		program.add("dp(a)");

		program.add("ap(b)");
		program.add("dp(b):-not dp(b)");

		program.add("ap(c)");

		program.add("ap(d):-not ap(d)");
		program.add("dp(d)");

		program.add("ap(e):-not ap(e)");
		program.add("dp(e):-not ap(e)");

		program.add("ap(f):-not ap(f)");

		program.add("dp(g)");

		program.add("dp(h):-not dp(h)");

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

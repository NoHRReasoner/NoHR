package unittest;
import static org.junit.Assert.assertTrue;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;

/**
 *
 */

/**
 * @author nunocosta
 */
public class ModelTest {

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

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test() {
		final Predicate pred1 = pred("a", 1);
		final Predicate pred2 = pred("a", 1);
		assertTrue("predicates hashCode", pred1.hashCode() == pred2.hashCode());
		assertTrue("predicates equals", pred1.equals(pred2));
		final Term cons1 = cons("a");
		final Term cons2 = cons("a");
		assertTrue("constants hashCode", cons1.hashCode() == cons2.hashCode());
		assertTrue("constants equals", cons1.equals(cons2));
		final Atom atom1 = atom(pred1, cons1);
		final Atom atom2 = atom(pred2, cons2);
		assertTrue("atoms hashCode", atom1.hashCode() == atom2.hashCode());
		assertTrue("atoms equals", atom1.equals(atom2));
		final Rule rule1 = rule(atom1);
		final Rule rule2 = rule(atom2);
		assertTrue("facts hashCode", rule1.hashCode() == rule2.hashCode());
		assertTrue("facts equals", rule1.equals(rule2));
	}
}

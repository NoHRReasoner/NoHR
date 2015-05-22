/**
 *
 */
package other;

import static org.junit.Assert.fail;

import java.util.SortedMap;
import java.util.TreeMap;

import nohr.model.Model;
import nohr.model.Substitution;
import nohr.model.SubstitutionImpl;
import nohr.model.Term;
import nohr.model.Variable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author nunocosta
 *
 */
public class SubstitutionImplTest {

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

    /**
     * Test method for
     * {@link nohr.model.SubstitutionImpl#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsObject() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link nohr.model.SubstitutionImpl#getValue(nohr.model.Variable)}.
     */
    @Test
    public final void testGetValue() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link nohr.model.SubstitutionImpl#getVariables()}.
     */
    @Test
    public final void testGetVariables() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link nohr.model.SubstitutionImpl#hashCode()}.
     */
    @Test
    public final void testHashCode() {
	SortedMap<Variable, Integer> varsIdx1 = new TreeMap<Variable, Integer>();
	Term[] vals1 = new Term[] { Model.cons("a"), Model.cons("b"),
		Model.cons("c") };
	varsIdx1.put(Model.var("X"), 0);
	varsIdx1.put(Model.var("Y"), 1);
	varsIdx1.put(Model.var("Z"), 2);
	SortedMap<Variable, Integer> varsIdx2 = new TreeMap<Variable, Integer>();
	Term[] vals2 = new Term[] { Model.cons("a"), Model.cons("b"),
		Model.cons("c") };
	varsIdx2.put(Model.var("X"), 0);
	varsIdx2.put(Model.var("Y"), 1);
	varsIdx2.put(Model.var("Z"), 2);
	Substitution s1 = Model.subs(varsIdx1, vals1);
	Substitution s2 = Model.subs(varsIdx2, vals2);
	Assert.assertEquals("equals substitutions must be equals", s1, s2);
	Assert.assertEquals(
		"string representations of equal substitutions must be equals",
		s1.toString(), s2.toString());
	Assert.assertEquals("hash codes of equal substitution must be equals",
		s1.hashCode(), s2.hashCode());
    }

    /**
     * Test method for
     * {@link nohr.model.SubstitutionImpl#SubstitutionImpl(java.util.SortedMap, nohr.model.Term[])}
     * .
     */
    @Test
    public final void testSubstitutionImpl() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link nohr.model.SubstitutionImpl#toString()}.
     */
    @Test
    public final void testToString() {
	fail("Not yet implemented"); // TODO
    }

}

package other;

import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nohr.model.Answer;
import nohr.model.AtomImpl;
import nohr.model.Literal;
import nohr.model.PositiveLiteralImpl;
import nohr.model.Query;
import nohr.model.QueryImpl;
import nohr.model.Term;
import nohr.model.TruthValue;
import nohr.model.Variable;
import nohr.model.VariableImpl;
import nohr.model.predicates.Predicate;
import nohr.model.predicates.PredicateImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xsb.XSBDatabase;

public class XSBDatabaseTest extends XSBDatabase {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    public XSBDatabaseTest() throws Exception {
	super(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY")));
    }

    public <E> List<E> list(E... elems) {
	List<E> list = new LinkedList<E>();
	Collections.addAll(list, elems);
	return list;
    }

    public <E> Set<E> set(E... elems) {
	Set<E> set = new HashSet<E>();
	Collections.addAll(set, elems);
	return set;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testQuery() {
	Predicate p = new PredicateImpl("p", 1);
	Variable x = new VariableImpl("X");
	Literal l = new PositiveLiteralImpl(new AtomImpl(p, list((Term) x)));
	Query query = new QueryImpl(list(l), list(x));
	engine.command("dynamic p/1");
	Assert.assertNull("goal should fail", query(query));
	engine.command("assert((p(a)))");
	Answer answer = query(query);
	Assert.assertTrue("incorrect answer", answer.toString().equals("p(a)"));
    }

    @Test
    public final void testQueryAll() {
	Predicate p = new PredicateImpl("p", 1);
	Variable x = new VariableImpl("X");
	Literal l = new PositiveLiteralImpl(new AtomImpl(p, list((Term) x)));
	Query query = new QueryImpl(list(l), list(x));
	engine.command("dynamic p/1");
	Assert.assertTrue("goal should fail", queryAll(query).isEmpty());
	engine.command("assert((p(a)))");
	engine.command("assert((p(b)))");
	engine.command("assert((p(c)))");
	Map<List<Term>, TruthValue> answers = queryAll(query);
	Set<String> result = new HashSet<String>();
	for (List<Term> list : answers.keySet())
	    result.add(query.apply(list).toString());
	Assert.assertEquals("incorrect answers", set("p(a)", "p(b)", "p(c)"),
		result);
    }

}

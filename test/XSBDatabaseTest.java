import static pt.unl.fct.di.centria.nohr.model.Model.posLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.AnswersIterable;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateImpl;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;

public class XSBDatabaseTest extends XSBDatabase {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    public XSBDatabaseTest() throws Exception {
	super(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
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
    public final void testHasAnswers() {
	Predicate p = new PredicateImpl("p", 1);
	Variable x = var("X");
	Literal l = posLiteral(p, x);
	Query q = Model.query(l);
	engine.deterministicGoal("table(p/1)");
	engine.deterministicGoal("assert((p(a)))");
	engine.deterministicGoal("assert((p(b):-tnot(p(b))))");
	Assert.assertTrue(hasAnswers(q));
	Assert.assertTrue(hasAnswers(q, true));
	Assert.assertTrue(hasAnswers(q, false));

	p = new PredicateImpl("q", 1);
	l = posLiteral(p, x);
	q = Model.query(l);
	engine.deterministicGoal("dynamic(q/1)");
	Assert.assertFalse(hasAnswers(q));
	Assert.assertFalse(hasAnswers(q, true));
	Assert.assertFalse(hasAnswers(q, false));

	p = new PredicateImpl("r", 1);
	l = posLiteral(p, x);
	q = Model.query(l);
	engine.deterministicGoal("assert(r(a))");
	Assert.assertTrue(hasAnswers(q));
	Assert.assertTrue(hasAnswers(q, true));
	Assert.assertFalse(hasAnswers(q, false));

	p = new PredicateImpl("s", 1);
	l = posLiteral(p, x);
	q = Model.query(l);
	engine.deterministicGoal("table(s/1),assert((s(b):-tnot(s(b))))");
	Assert.assertTrue(hasAnswers(q));
	Assert.assertFalse(hasAnswers(q, true));
	Assert.assertTrue(hasAnswers(q, false));
    }

    @Test
    public final void testlazlyQueryAll() {
	Predicate p = new PredicateImpl("p", 1);
	Variable x = var("X");
	Literal l = posLiteral(p, x);
	Query query = Model.query(l);
	engine.deterministicGoal("table p/1");
	engine.deterministicGoal("assert((p(a)))");
	engine.deterministicGoal("assert((p(b)))");
	engine.deterministicGoal("assert((p(c)))");
	engine.deterministicGoal("assert((p(d):-tnot(p(d))))");
	engine.deterministicGoal("assert((p(e):-tnot(p(e))))");
	engine.deterministicGoal("assert((p(f):-tnot(p(f))))");
	AnswersIterable answers = lazilyQueryAll(query);
	Iterator<Answer> answersIt = answers.iterator();
	for (String expecteAns : list("p(f)", "p(e)", "p(d)", "p(c)", "p(b)",
		"p(a)")) {
	    Assert.assertTrue(answersIt.hasNext());
	    Answer ans = answersIt.next();
	    Assert.assertEquals(expecteAns, query.apply(ans.getValues())
		    .toString());
	}
	Assert.assertFalse(answersIt.hasNext());
	answers = lazilyQueryAll(query, true);
	answersIt = answers.iterator();
	for (String expecteAns : list("p(c)", "p(b)", "p(a)")) {
	    Assert.assertTrue(answersIt.hasNext());
	    Answer ans = answersIt.next();
	    Assert.assertEquals(expecteAns, query.apply(ans.getValues())
		    .toString());
	}
	Assert.assertFalse(answersIt.hasNext());
	answers = lazilyQueryAll(query, false);
	answersIt = answers.iterator();
	for (String expecteAns : list("p(f)", "p(e)", "p(d)")) {
	    Assert.assertTrue(answersIt.hasNext());
	    Answer ans = answersIt.next();
	    Assert.assertEquals(expecteAns, query.apply(ans.getValues())
		    .toString());
	}
	Assert.assertFalse(answersIt.hasNext());
    }

    @Test
    public final void testQuery() {
	Predicate p = new PredicateImpl("p", 1);
	Variable x = var("X");
	Literal l = posLiteral(p, x);
	Query q = Model.query(l);
	engine.deterministicGoal("table p/1");
	engine.deterministicGoal("assert((p(a))),assert((p(b):-tnot(p(b))))");
	Assert.assertEquals("incorrect answer", "p(b)", query(q).toString());
	Assert.assertEquals("incorrect answer", "p(a)", query(q, true)
		.toString());
	Assert.assertEquals("incorrect answer", "p(b)", query(q, false)
		.toString());
    }

    @Test
    public final void testQueryAll() {
	Predicate p = new PredicateImpl("p", 1);
	Variable x = var("X");
	Literal l = posLiteral(p, x);
	Query query = Model.query(l);
	engine.deterministicGoal("table p/1");
	engine.deterministicGoal("assert((p(a)))");
	engine.deterministicGoal("assert((p(b)))");
	engine.deterministicGoal("assert((p(c)))");
	engine.deterministicGoal("assert((p(d):-tnot(p(d))))");
	engine.deterministicGoal("assert((p(e):-tnot(p(e))))");
	engine.deterministicGoal("assert((p(f):-tnot(p(f))))");
	Map<List<Term>, TruthValue> answers = queryAll(query);
	Set<String> result = new HashSet<String>();
	for (List<Term> list : answers.keySet())
	    result.add(query.apply(list).toString());
	Assert.assertEquals("incorrect answers",
		set("p(a)", "p(b)", "p(c)", "p(d)", "p(e)", "p(f)"), result);
	answers = queryAll(query, true);
	result = new HashSet<String>();
	for (List<Term> list : answers.keySet())
	    result.add(query.apply(list).toString());
	Assert.assertEquals("incorrect answers", set("p(a)", "p(b)", "p(c)"),
		result);
	answers = queryAll(query, false);
	result = new HashSet<String>();
	for (List<Term> list : answers.keySet())
	    result.add(query.apply(list).toString());
	Assert.assertEquals("incorrect answers", set("p(d)", "p(e)", "p(f)"),
		result);
    }

}

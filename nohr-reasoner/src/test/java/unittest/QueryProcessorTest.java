package unittest;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

/**
 *
 */

import java.io.File;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pt.unl.fct.di.novalincs.nohr.model.Model.ans;
import static pt.unl.fct.di.novalincs.nohr.model.Model.atom;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;

import java.io.IOException;
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
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseProgram;
import pt.unl.fct.di.novalincs.nohr.deductivedb.XSBDeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.QueryProcessor;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.TruthValue;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;

/**
 * @author nunocosta
 */
public class QueryProcessorTest extends QueryProcessor {

	public static Vocabulary v;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		v = new DefaultVocabulary(OWLManager.createOWLOntologyManager().createOntology(IRI.generateDocumentIRI()));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public final NoHRParser parser;

	private final DatabaseProgram program;

	/**
	 * @param deductiveDatabase
	 * @throws Exception
	 */
	public QueryProcessorTest() throws Exception {
		super(new XSBDeductiveDatabase(new File(System.getenv("XSB_BIN_DIRECTORY")), v));
		parser = new NoHRRecursiveDescentParser(v);
		program = deductiveDatabase.createProgram();
	}

	public void add(String rule) throws ParseException {
		program.add(parser.parseRule(rule));
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
	public final void testHasAnswer() throws IOException, ParseException {
		final Variable var = var("X");
		// true true
		Query q = Model.query(atom(v, "p", var));
		add("ap(a)");
		add("dp(a)");
		assertTrue(hasAnswer(q, true));
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// true undefined
		q = Model.query(atom(v, "q", var));
		add("aq(b)");

		add("dq(b):-not dq(b)");
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// true false
		q = Model.query(atom(v, "r", var));
		add("ar(c)");
		add("dr(o)");
		assertTrue(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertTrue(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertTrue(hasAnswer(q, false, true, false, false));
		assertFalse(hasAnswer(q, false, false, true, false));
		// undefined true
		q = Model.query(atom(v, "s", var));

		add("as(d):-not as(d)");
		add("ds(d)");
		assertTrue(hasAnswer(q, true));
		assertTrue(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// undefined undefined
		q = Model.query(atom(v, "t", var));

		add("at(e):-not at(e)");

		add("dt(e):-not at(e)");
		assertTrue(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertTrue(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// undefined false
		q = Model.query(atom(v, "u", var));

		add("au(f):-not au(f)");
		add("du(o)");
		assertFalse(hasAnswer(q, true));
		assertFalse(hasAnswer(q, true, true, false, false));
		assertFalse(hasAnswer(q, true, false, true, false));
		assertFalse(hasAnswer(q, true, false, false, true));
		assertTrue(hasAnswer(q, false));
		assertFalse(hasAnswer(q, false, true, false, false));
		assertTrue(hasAnswer(q, false, false, true, false));
		// false false
		q = Model.query(atom(v, "v", var));
		add("av(o)");
		add("dv(l)");
		assertFalse(hasAnswer(q, true, true, true, false));
	}

	@Test
	public final void testQuery() throws IOException, ParseException, OWLOntologyCreationException {
		final Variable var = var("X");
		// true true
		Query q = Model.query(atom(v, "prep"));
		add("aprep");

		assertEquals(ans(q, TruthValue.TRUE, Collections.<Term> emptyList()), oneAnswer(q, false));

		q = Model.query(atom(v, "p", var));
		add("ap(a)");
		add("dp(a)");
		Answer ans = ans(q, TruthValue.TRUE, l(v.cons("a")));
		assertEquals(ans, oneAnswer(q, true));
		assertEquals(ans, oneAnswer(q, true, true, false, false));
		assertNull(oneAnswer(q, true, false, true, false));
		assertNull(oneAnswer(q, true, false, false, true));
		assertEquals(ans, oneAnswer(q, false));
		assertEquals(ans, oneAnswer(q, false, true, false, false));
		assertNull(oneAnswer(q, false, false, true, false));
		// true undefined
		q = Model.query(atom(v, "q", var));
		add("aq(b)");

		add("dq(b):-not dq(b)");
		ans = ans(q, TruthValue.TRUE, l(v.cons("b")));
		assertEquals(ans, oneAnswer(q, true));
		assertEquals(ans, oneAnswer(q, true, true, false, false));
		assertNull(oneAnswer(q, true, false, true, false));
		assertNull(oneAnswer(q, true, false, false, true));
		assertEquals(ans, oneAnswer(q, false));
		assertEquals(ans, oneAnswer(q, false, true, false, false));
		assertNull(oneAnswer(q, false, false, true, false));
		// true false
		q = Model.query(atom(v, "r", var));
		add("ar(c)");
		add("dr(o)");
		ans = ans(q, TruthValue.INCONSISTENT, l(v.cons("c")));
		assertEquals(ans, oneAnswer(q, true));
		assertNull(oneAnswer(q, true, true, false, false));
		assertNull(oneAnswer(q, true, false, true, false));
		assertEquals(ans, oneAnswer(q, true, false, false, true));
		ans = ans(q, TruthValue.TRUE, l(v.cons("c")));
		assertEquals(ans, oneAnswer(q, false));
		assertEquals(ans, oneAnswer(q, false, true, false, false));
		assertNull(oneAnswer(q, false, false, true, false));
		// undefined true
		q = Model.query(atom(v, "s", var));

		add("as(d):-not as(d)");
		add("ds(d)");
		ans = ans(q, TruthValue.TRUE, l(v.cons("d")));
		assertEquals(ans, oneAnswer(q, true));
		assertEquals(ans, oneAnswer(q, true, true, false, false));
		assertNull(oneAnswer(q, true, false, true, false));
		assertNull(oneAnswer(q, true, false, false, true));
		ans = ans(q, TruthValue.UNDEFINED, l(v.cons("d")));
		assertEquals(ans, oneAnswer(q, false));
		assertNull(oneAnswer(q, false, true, false, false));
		assertEquals(ans, oneAnswer(q, false, false, true, false));
		// undefined undefined
		q = Model.query(atom(v, "t", var));

		add("at(e):-not at(e)");

		add("dt(e):-not at(e)");
		ans = ans(q, TruthValue.UNDEFINED, l(v.cons("e")));
		assertEquals(ans, oneAnswer(q, true));
		assertNull(oneAnswer(q, true, true, false, false));
		assertEquals(ans, oneAnswer(q, true, false, true, false));
		assertNull(oneAnswer(q, true, false, false, true));
		assertEquals(ans, oneAnswer(q, false));
		assertNull(oneAnswer(q, false, true, false, false));
		assertEquals(ans, oneAnswer(q, false, false, true, false));
		// undefined false
		q = Model.query(atom(v, "u", var));

		add("au(f):-not au(f)");
		add("du(o)");
		assertNull(oneAnswer(q, true));
		assertNull(oneAnswer(q, true, true, false, false));
		assertNull(oneAnswer(q, true, false, true, false));
		assertNull(oneAnswer(q, true, false, false, true));
		ans = ans(q, TruthValue.UNDEFINED, l(v.cons("f")));
		assertEquals(ans, oneAnswer(q, false));
		assertNull(oneAnswer(q, false, true, false, false));
		assertEquals(ans, oneAnswer(q, false, false, true, false));
		// false false
		q = Model.query(atom(v, "v", var));
		add("av(o)");
		add("dv(l)");
		assertNull(oneAnswer(q, true, true, true, false));
	}

	/**
	 * Test method for {@link nohr.reasoner.QueryProcessor#answersValuations(pt.unl.fct.di.novalincs.nohr.model.Query)} .
	 *
	 * @throws IOException
	 * @throws ParseException
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void testQueryAll() throws IOException, ParseException, OWLOntologyCreationException, Exception {
		add("ap(a)");
		add("dp(a)");

		add("ap(b)");
		add("dp(b):-not dp(b)");

		add("ap(c)");

		add("ap(d):-not ap(d)");
		add("dp(d)");

		add("ap(e):-not ap(e)");
		add("dp(e):-not ap(e)");

		add("ap(f):-not ap(f)");

		add("dp(g)");

		add("dp(h):-not dp(h)");

		final Variable var = var("X");
		final Query q = Model.query(atom(v, "p", var));

		Collection<Answer> ans = allAnswers(q, true, true, true, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(v.cons("c")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("d")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(v.cons("e")))));
		Assert.assertTrue(ans.size() == 5);

		ans = allAnswers(q, true, true, true, false);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("d")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(v.cons("e")))));
		Assert.assertTrue(ans.size() == 4);

		ans = allAnswers(q, true, true, false, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(v.cons("c")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("d")))));
		Assert.assertTrue(ans.size() == 4);

		ans = allAnswers(q, true, false, true, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(v.cons("c")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(v.cons("e")))));
		Assert.assertTrue(ans.size() == 2);

		ans = allAnswers(q, true, true, false, false);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("a")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("b")))));
		Assert.assertTrue(ans.contains(ans(q, TruthValue.TRUE, l(v.cons("d")))));
		Assert.assertTrue(ans.size() == 3);

		ans = allAnswers(q, true, false, true, false);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.UNDEFINED, l(v.cons("e")))));
		Assert.assertTrue(ans.size() == 1);

		ans = allAnswers(q, true, false, false, true);
		Assert.assertTrue(ans.contains(ans(q, TruthValue.INCONSISTENT, l(v.cons("c")))));
		Assert.assertTrue(ans.size() == 1);

	}
}

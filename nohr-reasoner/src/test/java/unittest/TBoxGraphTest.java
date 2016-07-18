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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import com.declarativa.interprolog.util.IPException;

import helpers.KB;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.translation.ql.StaticTBoxDigraph;

/**
 * @author nunocosta
 */
public class TBoxGraphTest {

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
	 * Test method for
	 * {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLClassExpression)} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void conceptsAncestors() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLClassExpression[] a = createAtomicConcepts(kb, 4);
		kb.subConcept(a[1], a[0]);
		kb.subConcept(a[2], a[1]);
		kb.subConcept(a[3], a[2]);
		final Set<OWLClassExpression> expectedAncestors = set(a[1], a[2], a[3]);
		// Test
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		final Set<OWLClassExpression> ancestors = graph.getAncestors(a[0]);
		Assert.assertEquals(expectedAncestors, ancestors);
	}

	/**
	 * Test method for
	 * {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLClassExpression)} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void conceptsAncestorsWithCycles() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLClassExpression[] a = createAtomicConcepts(kb, 4);
		kb.subConcept(a[1], a[0]);
		kb.subConcept(a[2], a[1]);
		kb.subConcept(a[3], a[2]);
		kb.subConcept(a[0], a[3]);
		final Set<OWLClassExpression> expectedAncestors = set(a);
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		Set<OWLClassExpression> ancestors;
		// Tests
		for (int i = 0; i < 4; i++) {
			ancestors = graph.getAncestors(a[i]);
			Assert.assertEquals(expectedAncestors, ancestors);
		}
	}

	/**
	 * Test method for
	 * {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getPredecessors(org.semanticweb.owlapi.model.OWLClassExpression)} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void conceptsPredecessors() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLClassExpression[] a = createAtomicConcepts(kb, 6);
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.subConcept(a[0], a[2]);
		kb.subConcept(a[1], a[2]);
		kb.subConcept(kb.some(p[0]), a[3]);
		kb.subConcept(a[4], kb.some(p[1]));
		kb.subConcept(kb.some(p[2]), kb.some(p[3]));
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		// Test1
		Set<OWLClassExpression> predecessors = graph.getPredecessors(a[2]);
		Assert.assertEquals(set(a[0], a[1]), predecessors);
		// Test2
		predecessors = graph.getPredecessors(a[3]);
		Assert.assertEquals(set(kb.some(p[0])), predecessors);
		// Test3
		predecessors = graph.getPredecessors(kb.some(p[1]));
		Assert.assertEquals(set(a[4]), predecessors);
		// Test4
		predecessors = graph.getPredecessors(kb.some(p[3]));
		Assert.assertEquals(set(kb.some(p[2])), predecessors);
	}

	private OWLClassExpression[] createAtomicConcepts(KB kb, int n) {
		final OWLClassExpression[] a = new OWLClassExpression[n];
		for (int i = 0; i < n; i++)
			a[i] = kb.conc("A" + i);
		return a;
	}

	private OWLObjectPropertyExpression[] createAtomicRoles(KB kb, int n) {
		final OWLObjectPropertyExpression[] p = new OWLObjectPropertyExpression[n];
		for (int i = 0; i < n; i++)
			p[i] = kb.role("P" + i);
		return p;
	}

	/**
	 * Test method for {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getIrreflexiveRoles()} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void irreflexiveRolesFromConceptDisjunction() throws OWLOntologyCreationException,
			OWLOntologyStorageException, OWLProfilesViolationsException, IOException, CloneNotSupportedException,
			UnsupportedAxiomsException, IPException, PrologEngineCreationException {
		final KB kb = new KB();
		final OWLClassExpression[] a = createAtomicConcepts(kb, 2);
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 1);
		kb.subConcept(kb.some(p[0]), a[0]);
		kb.subConcept(kb.some(kb.inv((OWLObjectProperty) p[0])), a[1]);
		kb.disjointConcepts(a[0], a[1]);
		final Set<OWLObjectProperty> expectedIrreflexiveRoles = set((OWLObjectProperty) p[0]);
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		final Set<OWLObjectProperty> irreflexiveRoles = graph.getIrreflexiveRoles();
		// Test
		Assert.assertEquals(expectedIrreflexiveRoles, irreflexiveRoles);
	}

	/**
	 * Test method for {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getIrreflexiveRoles()} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void irreflexiveRolesFromRoleDisjunction() throws OWLOntologyCreationException,
			OWLOntologyStorageException, OWLProfilesViolationsException, IOException, CloneNotSupportedException,
			UnsupportedAxiomsException, IPException, PrologEngineCreationException {
		final KB kb = new KB();
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.subRole(p[0], p[1]);
		kb.subRole(kb.inv((OWLObjectProperty) p[0]), p[2]);
		kb.disjointRoles(p[1], p[2]);
		final Set<OWLObjectProperty> expectedIrreflexiveRoles = set((OWLObjectProperty) p[0]);
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		final Set<OWLObjectProperty> irreflexiveRoles = graph.getIrreflexiveRoles();
		// Test
		Assert.assertEquals(expectedIrreflexiveRoles, irreflexiveRoles);
	}

	/**
	 * Test method for
	 * {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)}
	 * .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void rolesAncestors() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.subRole(p[1], p[0]);
		kb.subRole(p[2], p[1]);
		kb.subRole(p[3], p[2]);
		final Set<OWLObjectPropertyExpression> expectedAncestors = set(p[1], p[2], p[3]);
		// Test
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		final Set<OWLPropertyExpression> ancestors = graph.getAncestors(p[0]);
		Assert.assertEquals(expectedAncestors, ancestors);
	}

	/**
	 * Test method for
	 * {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLClassExpression)} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void rolesAncestorsWithCycles() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.subRole(p[1], p[0]);
		kb.subRole(p[2], p[1]);
		kb.subRole(p[3], p[2]);
		kb.subRole(p[0], p[3]);
		final Set<OWLObjectPropertyExpression> expectedAncestors = set(p);
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		Set<OWLPropertyExpression> ancestors;
		// Tests
		for (int i = 0; i < 4; i++) {
			ancestors = graph.getAncestors(p[i]);
			Assert.assertEquals(expectedAncestors, ancestors);
		}

	}

	/**
	 * Test method for
	 * {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getPredecessors(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)}
	 * .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void rolesPredecessors() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 9);
		kb.subRole(p[0], p[2]);
		kb.subRole(p[1], p[2]);
		kb.subRole(kb.inv((OWLObjectProperty) p[3]), p[4]);
		kb.subRole(p[5], kb.inv((OWLObjectProperty) p[6]));
		kb.subRole(kb.inv((OWLObjectProperty) p[7]), kb.inv((OWLObjectProperty) p[8]));
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		// Test1
		Set<OWLPropertyExpression> predecessors = graph.getPredecessors(p[2]);
		Assert.assertEquals(set(p[0], p[1]), predecessors);
		// Test2
		predecessors = graph.getPredecessors(p[4]);
		Assert.assertEquals(set(kb.inv((OWLObjectProperty) p[3])), predecessors);
		// Test3
		predecessors = graph.getPredecessors(kb.inv((OWLObjectProperty) p[6]));
		Assert.assertEquals(set(p[5]), predecessors);
		// Test4
		predecessors = graph.getPredecessors(kb.inv((OWLObjectProperty) p[8]));
		Assert.assertEquals(set(kb.inv((OWLObjectProperty) p[7])), predecessors);
	}

	private <T> Set<T> set(@SuppressWarnings("unchecked") T... elems) {
		final Set<T> result = new HashSet<T>();
		for (final T e : elems)
			result.add(e);
		return result;

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
	 * Test method for {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getUnsatisfiableEntities()} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void unsatisfiableEntities1() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLClassExpression[] a = createAtomicConcepts(kb, 4);
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.subConcept(a[0], a[1]);
		kb.subConcept(a[0], a[2]);
		kb.disjointConcepts(a[1], a[2]);
		kb.subRole(p[0], p[1]);
		kb.subRole(p[0], p[2]);
		kb.disjointRoles(p[1], p[2]);
		final Set<OWLEntity> expectedUnsatisfiableEntities = set((OWLEntity) a[0], (OWLEntity) p[0]);
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		final Set<OWLEntity> unsatisfiableEntities = graph.getUnsatisfiableEntities();
		// Test
		Assert.assertEquals(expectedUnsatisfiableEntities, unsatisfiableEntities);
	}

	/**
	 * Test method for {@link StaticTBoxDigraph.reasoner.translation.ontology.ql.BasicTBoxGraph#getUnsatisfiableEntities()} .
	 *
	 * @throws OWLOntologyCreationException
	 * @throws UnsupportedAxiomsException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws OWLProfilesViolationsException
	 * @throws OWLOntologyStorageException
	 * @throws PrologEngineCreationException
	 * @throws IPException
	 */
	@Test
	public final void unsatisfiableEntities2() throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
			IPException, PrologEngineCreationException {
		// Data
		final KB kb = new KB();
		final OWLClassExpression[] a = createAtomicConcepts(kb, 3);
		final OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 3);
		kb.subConcept(a[0], a[1]);
		kb.subConcept(a[1], a[0]);
		kb.subConcept(a[2], a[1]);
		kb.disjointConcepts(a[0], a[1]);
		kb.subRole(p[0], p[1]);
		kb.subRole(p[1], p[0]);
		kb.subRole(p[2], p[1]);
		kb.disjointRoles(p[0], p[1]);
		final Set<OWLEntity> expectedUnsatisfiableEntities = set((OWLEntity) a[0], (OWLEntity) a[1], (OWLEntity) a[2],
				(OWLEntity) p[0], (OWLEntity) p[1], (OWLEntity) p[2]);
		// TestInit
		final StaticTBoxDigraph graph = new StaticTBoxDigraph(kb.getQLNormalizedOntology());
		final Set<OWLEntity> unsatisfiableEntities = graph.getUnsatisfiableEntities();
		// Test
		Assert.assertEquals(expectedUnsatisfiableEntities, unsatisfiableEntities);
	}

}

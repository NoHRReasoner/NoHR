/**
 * 
 */
package test;

import java.util.HashSet;
import java.util.Set;

import local.translate.ql.BasicTBoxGraph;

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

/**
 * @author nunocosta
 *
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
	 * {@link local.translate.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void conceptsAncestors() throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLClassExpression[] a = createAtomicConcepts(kb, 4);
		kb.addSubsumption(a[1], a[0]);
		kb.addSubsumption(a[2], a[1]);
		kb.addSubsumption(a[3], a[2]);
		Set<OWLClassExpression> expectedAncestors = set(a[1], a[2], a[3]);
		// Test
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLClassExpression> ancestors = graph.getAncestors(a[0]);
		Assert.assertEquals(expectedAncestors, ancestors);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void conceptsAncestorsWithCycles()
			throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLClassExpression[] a = createAtomicConcepts(kb, 4);
		kb.addSubsumption(a[1], a[0]);
		kb.addSubsumption(a[2], a[1]);
		kb.addSubsumption(a[3], a[2]);
		kb.addSubsumption(a[0], a[3]);
		Set<OWLClassExpression> expectedAncestors = set(a);
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLClassExpression> ancestors;
		// Tests
		for (int i = 0; i < 4; i++) {
			ancestors = graph.getAncestors(a[i]);
			Assert.assertEquals(expectedAncestors, ancestors);
		}
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getPredecessors(org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void conceptsPredecessors()
			throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLClassExpression[] a = createAtomicConcepts(kb, 6);
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.addSubsumption(a[0], a[2]);
		kb.addSubsumption(a[1], a[2]);
		kb.addSubsumption(kb.getExistential(p[0]), a[3]);
		kb.addSubsumption(a[4], kb.getExistential(p[1]));
		kb.addSubsumption(kb.getExistential(p[2]), kb.getExistential(p[3]));
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		// Test1
		Set<OWLClassExpression> predecessors = graph.getPredecessors(a[2]);
		Assert.assertEquals(set(a[0], a[1]), predecessors);
		// Test2
		predecessors = graph.getPredecessors(a[3]);
		Assert.assertEquals(set(kb.getExistential(p[0])), predecessors);
		// Test3
		predecessors = graph.getPredecessors(kb.getExistential(p[1]));
		Assert.assertEquals(set(a[4]), predecessors);
		// Test4
		predecessors = graph.getPredecessors(kb.getExistential(p[3]));
		Assert.assertEquals(set(kb.getExistential(p[2])), predecessors);
	}

	private OWLClassExpression[] createAtomicConcepts(KB kb, int n) {
		OWLClassExpression[] a = new OWLClassExpression[n];
		for (int i = 0; i < n; i++)
			a[i] = kb.getConcept("A" + i);
		return a;
	}

	private OWLObjectPropertyExpression[] createAtomicRoles(KB kb, int n) {
		OWLObjectPropertyExpression[] p = new OWLObjectPropertyExpression[n];
		for (int i = 0; i < n; i++)
			p[i] = kb.getRole("P" + i);
		return p;
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)}
	 * .
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void rolesAncestors() throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.addSubsumption(p[1], p[0]);
		kb.addSubsumption(p[2], p[1]);
		kb.addSubsumption(p[3], p[2]);
		Set<OWLObjectPropertyExpression> expectedAncestors = set(p[1], p[2],
				p[3]);
		// Test
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLObjectPropertyExpression> ancestors = graph.getAncestors(p[0]);
		Assert.assertEquals(expectedAncestors, ancestors);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getAncestors(org.semanticweb.owlapi.model.OWLClassExpression)}
	 * .
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void rolesAncestorsWithCycles()
			throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.addSubsumption(p[1], p[0]);
		kb.addSubsumption(p[2], p[1]);
		kb.addSubsumption(p[3], p[2]);
		kb.addSubsumption(p[0], p[3]);
		Set<OWLObjectPropertyExpression> expectedAncestors = set(p);
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLObjectPropertyExpression> ancestors;
		// Tests
		for (int i = 0; i < 4; i++) {
			ancestors = graph.getAncestors(p[i]);
			Assert.assertEquals(expectedAncestors, ancestors);
		}

	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getPredecessors(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)}
	 * .
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void rolesPredecessors() throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 9);
		kb.addSubsumption(p[0], p[2]);
		kb.addSubsumption(p[1], p[2]);
		kb.addSubsumption(kb.getInverse((OWLObjectProperty) p[3]), p[4]);
		kb.addSubsumption(p[5], kb.getInverse((OWLObjectProperty) p[6]));
		kb.addSubsumption(kb.getInverse((OWLObjectProperty) p[7]),
				kb.getInverse((OWLObjectProperty) p[8]));
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		// Test1
		Set<OWLObjectPropertyExpression> predecessors = graph
				.getPredecessors(p[2]);
		Assert.assertEquals(set(p[0], p[1]), predecessors);
		// Test2
		predecessors = graph.getPredecessors(p[4]);
		Assert.assertEquals(set(kb.getInverse((OWLObjectProperty) p[3])),
				predecessors);
		// Test3
		predecessors = graph.getPredecessors(kb
				.getInverse((OWLObjectProperty) p[6]));
		Assert.assertEquals(set(p[5]), predecessors);
		// Test4
		predecessors = graph.getPredecessors(kb
				.getInverse((OWLObjectProperty) p[8]));
		Assert.assertEquals(set(kb.getInverse((OWLObjectProperty) p[7])),
				predecessors);
	}

	private <T> Set<T> set(T... elems) {
		Set<T> result = new HashSet<T>();
		for (T e : elems)
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
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getIrreflexiveRoles()}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void irreflexiveRolesFromRoleDisjunction()
			throws OWLOntologyCreationException {
		KB kb = new KB();
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.addSubsumption(p[0], p[1]);
		kb.addSubsumption(kb.getInverse((OWLObjectProperty) p[0]), p[2]);
		kb.addDisjunction(p[1], p[2]);
		Set<OWLObjectProperty> expectedIrreflexiveRoles = set((OWLObjectProperty) p[0]);
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLObjectProperty> irreflexiveRoles = graph.getIrreflexiveRoles();
		// Test
		Assert.assertEquals(expectedIrreflexiveRoles, irreflexiveRoles);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getIrreflexiveRoles()}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void irreflexiveRolesFromConceptDisjunction()
			throws OWLOntologyCreationException {
		KB kb = new KB();
		OWLClassExpression[] a = createAtomicConcepts(kb, 2);
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 1);
		kb.addSubsumption(kb.getExistential(p[0]), a[0]);
		kb.addSubsumption(
				kb.getExistential(kb.getInverse((OWLObjectProperty) p[0])),
				a[1]);
		kb.addDisjunction(a[0], a[1]);
		Set<OWLObjectProperty> expectedIrreflexiveRoles = set((OWLObjectProperty) p[0]);
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLObjectProperty> irreflexiveRoles = graph.getIrreflexiveRoles();
		// Test
		Assert.assertEquals(expectedIrreflexiveRoles, irreflexiveRoles);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getUnsatisfiableEntities()}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void unsatisfiableEntities1()
			throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLClassExpression[] a = createAtomicConcepts(kb, 4);
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 4);
		kb.addSubsumption(a[0], a[1]);
		kb.addSubsumption(a[0], a[2]);
		kb.addDisjunction(a[1], a[2]);
		kb.addSubsumption(p[0], p[1]);
		kb.addSubsumption(p[0], p[2]);
		kb.addDisjunction(p[1], p[2]);
		Set<OWLEntity> expectedUnsatisfiableEntities = set((OWLEntity) a[0],
				(OWLEntity) p[0]);
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLEntity> unsatisfiableEntities = graph.getUnsatisfiableEntities();
		// Test
		Assert.assertEquals(expectedUnsatisfiableEntities,
				unsatisfiableEntities);
	}

	/**
	 * Test method for
	 * {@link local.translate.ql.BasicTBoxGraph#getUnsatisfiableEntities()}.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public final void unsatisfiableEntities2()
			throws OWLOntologyCreationException {
		// Data
		KB kb = new KB();
		OWLClassExpression[] a = createAtomicConcepts(kb, 3);
		OWLObjectPropertyExpression[] p = createAtomicRoles(kb, 3);
		kb.addSubsumption(a[0], a[1]);
		kb.addSubsumption(a[1], a[0]);
		kb.addSubsumption(a[2], a[1]);
		kb.addDisjunction(a[0], a[1]);
		kb.addSubsumption(p[0], p[1]);
		kb.addSubsumption(p[1], p[0]);
		kb.addSubsumption(p[2], p[1]);
		kb.addDisjunction(p[0], p[1]);
		Set<OWLEntity> expectedUnsatisfiableEntities = set((OWLEntity) a[0],
				(OWLEntity) a[1], (OWLEntity) a[2], (OWLEntity) p[0],
				(OWLEntity) p[1], (OWLEntity) p[2]);
		// TestInit
		BasicTBoxGraph graph = new BasicTBoxGraph(kb.getNormalizedOntology(),
				kb.getDataFactory());
		Set<OWLEntity> unsatisfiableEntities = graph.getUnsatisfiableEntities();
		// Test
		Assert.assertEquals(expectedUnsatisfiableEntities,
				unsatisfiableEntities);
	}

}

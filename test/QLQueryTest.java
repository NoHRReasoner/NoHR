import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.profiles.Profiles;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;

/**
 *
 */

/**
 * @author nunocosta
 *
 */
public class QLQueryTest extends QueryTest {

    public QLQueryTest() {
    }

    // (a1), (a2), (s2), (n1)
    @Test
    public final void cln4() throws OWLOntologyCreationException, IOException,
	    PrologParserException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLClass A = kb.getConcept("A");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(A, b);
	kb.addAssertion(p1, a, b);
	kb.addRule("p2(X,Y):-p1(X,Y)");
	assertConsistent("p2(X,Y)");
	kb.addSubsumption(p1, p2);
	assertConsistent("p2(X,Y)");
	kb.addDisjunction(A, kb.getExistential(kb.getInverse(p2)));
	assertInconsistent("p2(X,Y)");
    }

    // (a2), (s2), (n2)
    @Test
    public final void cln5() throws OWLOntologyCreationException, IOException,
	    PrologParserException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addRule("p3(X,Y):-p1(X,Y)");
	assertConsistent("p3(X,Y)");
	kb.addSubsumption(p1, p2);
	assertConsistent("p3(X,Y)");
	kb.addDisjunction(p2, p3);
	assertInconsistent("p3(X,Y)");
    }

    // (s1)
    @Test
    public final void flounderingS1() throws OWLOntologyCreationException {
	kb.clear();
	// originate the doubled rules
	final OWLClass ad1 = kb.getConcept("ad1");
	final OWLClass ad2 = kb.getConcept("ad2");
	kb.addDisjunction(ad1, ad2);

	// originate (s1) case
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));

	// to check an answer
	final OWLClass a3 = kb.getConcept("a3");
	kb.addSubsumption(kb.getExistential(p2), a3);
	assertAnswer("a3(X).", "a");
    }

    // (a1), (s1), (s2)
    // @Test
    public void inverseIndirectExistentialSubsumption()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLClass a4 = kb.getConcept("a4");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(a1, a);
	kb.addSubsumption(a1, kb.getExistential(p2));
	kb.addSubsumption(kb.getInverse(p2), p3);
	kb.addSubsumption(kb.getExistential(kb.getInverse(p3)), a4);
	assertAnswer("a4(X)", "a");
    }

    // (a2), (s2)
    @Test
    public final void inverseSubsumption() throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, kb.getInverse(p2));
	assertAnswer("p2(X,Y)", "b", "a");
    }

    // (ir), (a2), (s1), (n1)
    @Test
    public final void irreflexiveFromConceptDisjunction()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(p1, a, a);
	kb.addSubsumption(kb.getExistential(p1), a1);
	kb.addSubsumption(kb.getExistential(kb.getInverse(p1)), a2);
	kb.addDisjunction(a1, a2);
	assertInconsistent("p1(X,X)");
    }

    // (ir), (a2), (s2), (n2)
    @Test
    public final void irreflexiveFromRoleDisjunction()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	kb.addAssertion(p1, a, a);
	kb.addSubsumption(p1, p2);
	kb.addSubsumption(kb.getInverse(p1), p3);
	kb.addDisjunction(p2, p3);
	assertInconsistent("p1(X,X)");
    }

    // (a2), (e), (s1)
    @Test
    public final void leftExistentialSubsumption()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(kb.getExistential(p1), a2);
	assertAnswer("a2(X)", "a");
	kb.addSubsumption(kb.getExistential(kb.getInverse(p1)), a3);
	assertAnswer("a3(X)", "b");
    }

    @Test
    public void normalizations() throws OWLOntologyCreationException,
	    IOException, PrologParserException {
	kb.clear();
	final OWLDataFactory df = kb.getDataFactory();
	// Intersection on right side
	final OWLClass[] A = kb.getConcepts(11);
	final OWLObjectProperty[] P = kb.getRoles(11);
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(A[1], a);
	kb.addSubsumption(A[1], df.getOWLObjectIntersectionOf(A[2], A[3]));
	assertAnswer("a2(X)", "a");
	assertAnswer("a3(X)", "a");
	// Qualified existential on right side
	kb.addSubsumption(A[3], df.getOWLObjectSomeValuesFrom(P[1], A[4]));
	kb.addSubsumption(kb.getExistential(P[1]), A[6]);
	assertAnswer("a6(X)", "a");
	// Concept equivalence
	kb.addAxiom(df.getOWLEquivalentClassesAxiom(A[6], A[7], A[8]));
	assertAnswer("a8(X)", "a");
	// Concept disjunction
	kb.addAxiom(df.getOWLDisjointClassesAxiom(A[7], A[9], A[10]));
	kb.addRule("a10(X):-a7(X)");
	assertInconsistent("a10(X)");
	// Inverse roles
	kb.addAssertion(P[2], a, b);
	kb.addAxiom(df.getOWLInverseObjectPropertiesAxiom(P[2], P[3]));
	assertAnswer("p3(X,Y)", "b", "a");
    }

    // (s2)
    public final void roleCycleByInverseRole()
	    throws OWLOntologyCreationException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	kb.addSubsumption(p1, kb.getInverse(p2));
	kb.addSubsumption(p2, kb.getInverse(p1));
	try {
	    new HybridKB(kb.getOntology()).queryAll(Parser
		    .parseQuery("p1(X,Y)."));
	} catch (final Exception e) {
	    Assert.fail(e.getMessage());
	}
    }

    // (a2), (n2)
    @Test
    public final void roleDisjunction() throws OWLOntologyCreationException,
	    IOException, PrologParserException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addRule("p2(X,Y):-p1(X,Y).");
	assertConsistent("p2(X,Y)");
	kb.addDisjunction(p1, p2);
	assertInconsistent("p2(X,Y)");
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
	AbstractOntologyTranslation.profile = Profiles.OWL2_QL;
    }

    @Override
    public void tearDown() throws Exception {
	super.tearDown();
	AbstractOntologyTranslation.profile = null;
    }

    // (i2) (a2), (s2)
    @Test
    public final void unsatisfiableRoles() throws OWLOntologyCreationException,
	    IOException, PrologParserException {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(p1, p2);
	kb.addSubsumption(p2, p1);
	kb.addSubsumption(p3, p2);
	kb.addDisjunction(p1, p2);
	kb.addRule("p3(X,Y):-p1(X,Y).");
	assertInconsistent("p3(X,Y)");
    }

}

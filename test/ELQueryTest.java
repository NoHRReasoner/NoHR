import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.Profiles;

/**
 *
 */

/**
 * @author nunocosta
 *
 */
public class ELQueryTest extends QueryTest {

    public ELQueryTest() {
    }

    @Test
    public void chainInconsistencePropagation() throws IOException, PrologParserException {
	kb.clear();
	final OWLClass a0 = kb.getConcept("a0");
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty p4 = kb.getRole("p4");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	final OWLIndividual i3 = kb.getIndividual("i3");
	final OWLIndividual i4 = kb.getIndividual("i4");
	kb.addAssertion(p1, i1, i2);
	kb.addAssertion(p2, i2, i3);
	kb.addAssertion(p3, i3, i4);
	final List<OWLObjectProperty> chain = new ArrayList<OWLObjectProperty>(3);
	chain.add(p1);
	chain.add(p2);
	chain.add(p3);
	kb.addSubsumption(chain, p4);
	kb.addDisjunction(a0, kb.getExistential(p2));
	assertConsistent("p4(i1, i4)");
	kb.addRule("a0(i2)");
	assertInconsistent("p4(i1, i4)");
    }

    @Test
    public void combinedNormalizations() {
	kb.clear();
	final OWLClass[] a = kb.getConcepts(11);
	final OWLObjectProperty[] r = kb.getRoles(6);
	final OWLClassExpression c = kb.getConjunction(a[1], kb.getExistential(r[1], a[2]),
		kb.getExistential(r[2], kb.getConjunction(a[3], a[4])),
		kb.getExistential(r[3], kb.getConjunction(a[5], kb.getExistential(r[4], a[6]))));
	final OWLClassExpression d = kb.getConjunction(a[7], a[8], kb.getExistential(r[5], a[9]));
	final OWLIndividual i = kb.getIndividual("i");
	kb.addAssertion(c, i);
	kb.addSubsumption(c, d);
	kb.addSubsumption(d, a[10]);
	assertHasAnswer("a7(i), a8(i), a10(i)", true, false, false);
    }

    @Test
    public void complexSidesNormalization1() {
	kb.clear();
	final OWLIndividual a = kb.getIndividual("a");
	final OWLIndividual b = kb.getIndividual("b");
	final OWLIndividual c = kb.getIndividual("c");
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	kb.addAssertion(p1, a, b);
	kb.addSubsumption(kb.getExistential(p1), kb.getExistential(p2));
	kb.addSubsumption(kb.getExistential(p2), a3);
	assertHasAnswer("a3(a)", true, false, false);
	kb.addAssertion(a1, c);
	kb.addAssertion(a2, c);
	kb.addSubsumption(kb.getConjunction(a1, a2), kb.getExistential(p2));
	assertHasAnswer("a3(c)", true, false, false);
    }

    // example 16
    @Test
    public void complexSidesNormalization2() {
	kb.clear();
	final OWLClass a = kb.getConcept("a");
	final OWLClass b = kb.getConcept("b");
	final OWLClass c = kb.getConcept("c");
	final OWLClass d = kb.getConcept("d");
	final OWLObjectProperty r = kb.getRole("r");
	final OWLIndividual i = kb.getIndividual("i");
	kb.addSubsumption(kb.getConjunction(a, b), kb.getExistential(r, c));
	kb.addSubsumption(kb.getExistential(r, c), d);
	kb.addAssertion(a, i);
	kb.addAssertion(b, i);
	assertHasAnswer("d(i)", true, false, false);
    }

    @Test
    public void conceptAssertionsNormalization() {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLIndividual i = kb.getIndividual("i");
	kb.addAssertion(kb.getConjunction(a1, kb.getTop(), a2, kb.getTop(), a3), i);
	assertHasAnswer("a1(i), a2(i), a3(i)", true, false, false);
    }

    @Test
    public void dataEquivalence() {
	kb.clear();
	final OWLDataProperty d1 = kb.getDataRole("d1");
	final OWLDataProperty d2 = kb.getDataRole("d2");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	kb.addAssertion(d1, i1, "l1");
	kb.addAssertion(d2, i2, "l2");
	kb.addEquivalence(d1, d2);
	assertHasAnswer("d1(i2,l2), d2(i1, l1)", true, false, false);
    }

    @Test
    public void dataSubsumption() {
	kb.clear();
	final OWLDataProperty d1 = kb.getDataRole("d1");
	final OWLDataProperty d2 = kb.getDataRole("d2");
	final OWLIndividual i = kb.getIndividual("i");
	kb.addAssertion(d1, i, "l");
	kb.addSubsumption(d1, d2);
	assertHasAnswer("d2(i,l)", true, false, false);
    }

    @Test
    public void disjointConcepts() throws IOException, PrologParserException {
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	kb.addDisjunction(a1, a2, a3, kb.getTop());
	kb.addRule("a1(i)");
	kb.addRule("a2(i)");
	kb.addRule("a3(i)");
	assertInconsistent("a1(i)");
	assertInconsistent("a2(i)");
	assertInconsistent("a2(i)");
    }

    @Test
    public void equivalentConcepts() {
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	kb.addAssertion(a1, i1);
	kb.addAssertion(a2, i2);
	kb.addAxiom(kb.getDataFactory().getOWLEquivalentClassesAxiom(a1, a2));
	assertHasAnswer("a1(i2), a2(i1)", true, false, false);
    }

    @Test
    public void example19() throws IOException, PrologParserException {
	kb.clear();
	final OWLClass a = kb.getConcept("a");
	final OWLClass b = kb.getConcept("b");
	kb.addSubsumption(a, kb.getBottom());
	kb.addSubsumption(b, a);
	kb.addRule("b(a) :- tnot(c(a))");
	kb.addRule("c(a) :- tnot(b(a))");
	assertHasAnswer("c(a)", true, false, false);
	assertHasNoAnswer("b(a)", true, true, true);
    }

    @Test
    public void existentialAssertion() {
	final OWLClass c = kb.getConcept("c");
	final OWLClass d = kb.getConcept("d");
	final OWLObjectProperty r = kb.getRole("p");
	final OWLIndividual i = kb.getIndividual("i");
	kb.addAssertion(kb.getExistential(r, c), i);
	kb.addSubsumption(kb.getExistential(r, c), d);
	assertHasAnswer("d(i)", true, false, false);
    }

    // example17
    @Test
    public void leftConjunctionNormalization() throws IOException, PrologParserException {
	kb.clear();
	final OWLClass a = kb.getConcept("a");
	final OWLClass b = kb.getConcept("b");
	final OWLClass c = kb.getConcept("c");
	final OWLObjectProperty r = kb.getRole("r");
	kb.addSubsumption(a, kb.getExistential(r, c));
	kb.addSubsumption(kb.getConjunction(kb.getExistential(r, c), b), kb.getBottom());
	kb.addRule("a(o)");
	kb.addRule("b(o)");
	assertInconsistent("b(o)");
    }

    // example18
    @Test
    public void leftExistentialNormalization() throws IOException, PrologParserException {
	kb.clear();
	final OWLClass a = kb.getConcept("a");
	final OWLClass c = kb.getConcept("c");
	final OWLClass d = kb.getConcept("d");
	final OWLObjectProperty r = kb.getRole("r");
	final OWLObjectProperty s = kb.getRole("s_");
	kb.addSubsumption(a, kb.getExistential(r, c));
	kb.addSubsumption(kb.getExistential(s, kb.getExistential(r, c)), d);
	kb.addRule("s_(a,b)");
	kb.addRule("a(b)");
	assertHasAnswer("d(a)", true, false, false);
    }

    @Test
    public void leftTop() {
	kb.clear();
	kb.addSubsumption(kb.getTop(), kb.getConcept("a"));
	assertHasAnswer("a(a1)");
    }

    @Test
    public void rightBottomConjunct() throws IOException, PrologParserException {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	kb.addSubsumption(a1, kb.getConjunction(a2, kb.getBottom(), a3));
	kb.addRule("a1(i)");
	assertInconsistent("a1(i)");
    }

    @Test
    public void rightConjunctionNormalization() {
	kb.clear();
	final OWLClass a1 = kb.getConcept("a1");
	final OWLClass a2 = kb.getConcept("a2");
	final OWLClass a3 = kb.getConcept("a3");
	final OWLClass a4 = kb.getConcept("a4");
	final OWLIndividual i = kb.getIndividual("i");
	kb.addAssertion(a1, i);
	kb.addSubsumption(a1, kb.getConjunction(a2, a3, a4));
	assertHasAnswer("a2(i), a3(i), a4(i)", true, false, false);
    }

    // (a1), (r2)
    @Test
    public void roleChainSubsumption() {
	kb.clear();
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty p4 = kb.getRole("p4");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	final OWLIndividual i3 = kb.getIndividual("i3");
	final OWLIndividual i4 = kb.getIndividual("i4");
	kb.addAssertion(p1, i1, i2);
	kb.addAssertion(p2, i2, i3);
	kb.addAssertion(p3, i3, i4);
	final List<OWLObjectProperty> chain = new ArrayList<OWLObjectProperty>(3);
	chain.add(p1);
	chain.add(p2);
	chain.add(p3);
	kb.addSubsumption(chain, p4);
	assertHasAnswer("p4(i1, i4)");
    }

    @Test
    public void roleChainSubsumptionContrapositive() throws IOException, PrologParserException {
	kb.clear();
	final OWLClass a0 = kb.getConcept("a0");
	final OWLObjectProperty p1 = kb.getRole("p1");
	final OWLObjectProperty p2 = kb.getRole("p2");
	final OWLObjectProperty p3 = kb.getRole("p3");
	final OWLObjectProperty p4 = kb.getRole("p4");
	final List<OWLObjectProperty> chain = new ArrayList<OWLObjectProperty>(3);
	chain.add(p1);
	chain.add(p2);
	chain.add(p3);
	kb.addRule("u1(a, b):-tnot(u1(a, b))");
	kb.addRule("u2(b, c):-tnot(u2(b, c))");
	kb.addRule("u3(c, d):-tnot(u3(c, d))");
	kb.addRule("p1(X, Y):-u1(X, Y)");
	kb.addRule("p2(X, Y):-u2(X, Y)");
	kb.addRule("p3(X, Y):-u3(X, Y)");
	kb.addRule("a0(a)");
	kb.addSubsumption(chain, p4);
	assertHasAnswer("p4(a, d)", false, true, false);
	assertHasNoAnswer("p4(a, d)", true, false, true);
	kb.addDisjunction(a0, kb.getExistential(p4));
    }

    @Test
    public void roleDomain() {
	kb.clear();
	final OWLClass a = kb.getConcept("a");
	final OWLObjectProperty r = kb.getRole("r");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	kb.addAssertion(r, i1, i2);
	kb.addDomain(r, a);
	assertHasAnswer("a(i1)", true, false, false);
    }

    @Test
    public void roleEquivalence() {
	kb.clear();
	final OWLObjectProperty r1 = kb.getRole("r1");
	final OWLObjectProperty r2 = kb.getRole("r2");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	kb.addAssertion(r1, i1, i1);
	kb.addAssertion(r2, i2, i2);
	kb.addEquivalence(r1, r2);
	assertHasAnswer("r1(i2,i2), r2(i1, i1)", true, false, false);
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
	AbstractOntologyTranslation.profile = Profiles.OWL2_EL;
    }

    @Override
    public void tearDown() throws Exception {
	super.tearDown();
	AbstractOntologyTranslation.profile = null;
    }

    @Test
    public void transitiveRole() {
	kb.clear();
	final OWLObjectProperty r = kb.getRole("r");
	final OWLIndividual i1 = kb.getIndividual("i1");
	final OWLIndividual i2 = kb.getIndividual("i2");
	final OWLIndividual i3 = kb.getIndividual("i3");
	kb.addAssertion(r, i1, i2);
	kb.addAssertion(r, i2, i3);
	kb.addTransitive(r);
	assertHasAnswer("r(i1, i3)", true, false, false);
    }

}

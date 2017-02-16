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
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;
import org.semanticweb.owlapi.model.OWLClassExpression;

import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;

/**
 *
 */
/**
 * @author nunocosta
 */
public abstract class AbstractRLQueryTest extends QueryTest {

    public AbstractRLQueryTest() throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        super(Profile.OWL2_RL);
    }

    public AbstractRLQueryTest(NoHRHybridKBConfiguration config) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        super(Profile.OWL2_RL, config);
    }

    @Test
    public final void asymmetric() {
        clear();
        object("p1", "a", "b");
        asymmetric("p1");
        //
        assertNegative("p1(b,a)");
    }

    // (a1.1), (s1.1), (n1.1)
    @Test
    public final void cln2() {
        clear();
        typeOf("a1", "a");
        typeOf("a3", "a");
        subConcept("a1", "a2");
        disjointConcepts("a2", "a3");
        //
        assertInconsistent("a1(a),a3(a)");
    }

    // (a1.1), (a2.1), (s2.3), (n1.1)
    @Test
    public final void cln3() {
        clear();
        object("p1", "a", "b");
        typeOf("a3", "a");
        subRole("p1", "p2");
        disjointConcepts(some("p2"), conc("a3"));
        //
        assertInconsistent("p1(a,b),a3(a)");
    }

    // (a1.1), (a2.1), (s2.3), (n1.1)
    @Test
    public final void cln4() {
        clear();
        object("p1", "a", "b");
        typeOf("a1", "b");
        subRole("p1", "p2");
        disjointConcepts(some(inv("p2")), conc("a1"));
        //
        assertInconsistent("p1(a,b),a1(b)");
    }

    // (a2.1), (s2.3), (n2.1), (n2.2)
    @Test
    public final void cln5() {
        clear();
        object("p1", "a", "b");
        object("p3", "a", "b");
        subRole("p1", "p2");
        disjointRoles("p2", "p3");
        //
        assertInconsistent("p1(a,b), p3(a,b)");
    }

    @Test
    public final void inverseRoles() {
        clear();
        object("p1", "a", "b");
        inverse("p1", "p2");
        //
        assertTrue("p2(b,a)");
    }

    // (a2.1), (s2.1)
    @Test
    public final void inverseSubsumption() {
        clear();
        object("p1", "a", "b");
        subRole(role("p1"), inv("p2"));
        //
        assertTrue("p2(b,a)");
    }

    // (ir)
    @Test
    public final void irreflexive1() {
        clear();
        subConcept(some("p1"), conc("a1"));
        subConcept(some(inv("p1")), conc("a2"));
        disjointConcepts("a1", "a2");
        //
        assertNegative("p1(a,a)");
    }

    // (ir)
    @Test
    public final void irreflexive2() {
        clear();
        subRole("p1", "p2");
        subRole(inv("p1"), role("p3"));
        disjointRoles("p2", "p3");
        //
        assertNegative("p1(a,a)");
    }

    @Test
    public final void irreflexive3() {
        clear();
        role("p");
        range("p", "c");
        domain("p", "d");
        disjointConcepts("c", "d");
        rule("p(a,a)");
        //
        assertInconsistent("p(a,a)");
    }

    @Test
    public final void irreflexive4() {
        clear();
        role("p");
        conc("a");
        irreflexive("p");
        rule("p(a,a)");
        //
        assertInconsistent("p(a,a)");
    }

    // (a2.1), (e.1), (s1.1)
    @Test
    public final void leftExistentialSubsumption() {
        clear();
        object("p1", "a", "b");
        subConcept(some("p1"), conc("a2"));
        subConcept(some(inv("p1")), conc("a3"));
        //
        assertTrue("a2(a),a3(b)");
    }

    @Test
    public void leftTopRole() {
        clear();
        subRole(topRole(), role("p1"));
        assertTrue("p1(a,b)");
    }

    @Test
    public void negativeSubsumption() throws IOException {
        subConcept(top(), neg("a1"));
        rule("a1(i)");
        assertInconsistent("a1(i)");
    }

    @Test
    public final void range() {
        clear();
        object("p1", "a", "b");
        range("p1", "a1");
        //
        assertTrue("a1(b)");
    }

    // (a1.1), (s1.1)
    @Test
    public final void rightComplementNormalization() {
        clear();
        typeOf("a1", "a");
        subConcept(conc("a1"), complement("a2"));
        //
        assertNegative("a2(a)");
    }

    // inverse role predicates tabling
    public final void roleCycle2() {
        clear();
        subRole(role("p1"), inv("p2"));
        subRole(role("p2"), inv("p1"));
        //
        assertFalse("p1(X,Y)");
    }

    // (a2.1), (n2.1)
    @Test
    public final void roleDisjunction() {
        clear();
        object("p1", "a", "b");
        disjointRoles("p1", "p2");
        //
        assertNegative("p2(a,b)");
    }

    @Test
    public final void symmetric() {
        clear();
        object("p1", "a", "b");
        symmetric("p1");
        //
        assertTrue("p1(b,a)");
    }

    // (i2)
    @Test
    public final void unsatisfiableRoles() {
        clear();
        subRole("p1", "p2");
        subRole("p2", "p1");
        subRole("p3", "p2");
        disjointRoles("p1", "p2");
        //
        assertNegative("p3(a,b)");
    }

    @Test
    public final void rightConjunction() {
        clear();
        final OWLClassExpression expression = conj("B", "C");

        subConcept(conc("A"), expression);
        typeOf("A", "a");

        assertTrue("B(a),C(a)");
    }

    @Test
    public final void leftUnion() {
        clear();
        final OWLClassExpression expression = union("B", "C");

        subConcept(expression, conc("A"));

        typeOf("B", "b1");
        typeOf("C", "c1");

        rule("B(b2)");

        assertTrue("A(b1),A(b2),A(c1)");
        assertFalse("A(b3)");
    }

    @Test
    public final void rightAllValuesFrom() {
        clear();
        final OWLClassExpression expression = all(role("P"), conc("C"));

        subConcept(conc("A"), expression);

        object("P", "a1", "c1");
        typeOf("A", "a1");

        assertTrue("C(c1)");

        clear();
        subConcept(conc("A"), expression);
        rule("P(a1,c1)");
        typeOf("A", "a1");

        assertTrue("C(c1)");
    }
}

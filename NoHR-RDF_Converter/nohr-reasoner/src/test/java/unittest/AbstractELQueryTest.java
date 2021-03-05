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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;

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
public abstract class AbstractELQueryTest extends QueryTest {

    public AbstractELQueryTest() throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        super(Profile.OWL2_EL);
    }

    public AbstractELQueryTest(NoHRHybridKBConfiguration config) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        super(Profile.OWL2_EL, config);
    }

    // (a2), (r2.2), (n2.1)
    @Test
    public void chainInconsistencePropagation() throws IOException {
        clear();
        object("p1", "a", "b");
        object("p2", "b", "c");
        object("p3", "c", "d");
        subRolesChain("p1", "p2", "p3", "p4");
        disjointConcepts(conc("a0"), some("p2"));
        //
        rule("a0(b)");
        //
        assertInconsistent("p4(a, d)");
    }

    @Test
    public void combinedNormalizations() {
        clear();
        final OWLClassExpression c = conj(conc("a1"), some("r1", "a2"), some(role("r2"), conj("a3", "a4")),
                some(role("r3"), conj(conc("a5"), some("r4", "r6"))));
        final OWLClassExpression d = conj(conc("a7"), conc("a8"), some("r5", "r9"));
        typeOf(c, individual("i"));
        subConcept(c, d);
        subConcept(d, conc("a10"));
        assertTrue("a7(i), a8(i), a10(i)");
    }

    @Test
    public void complexSidesNormalization1() {
        clear();
        object("p1", "a", "b");
        subConcept(some("p1"), some("p2"));
        subConcept(some("p2"), conc("a3"));
        //
        assertTrue("a3(a)");
    }

    @Test
    public void complexSidesNormalization2() {
        clear();
        typeOf("a1", "a");
        typeOf("a2", "a");
        subConcept(conj("a1", "a2"), some("p2"));
        subConcept(some("p2"), conc("a3"));
        //
        assertTrue("a3(a)");
    }

    // example 16
    @Test
    public void complexSidesNormalization3() {
        clear();
        subConcept(conj("a", "b"), some("r", "c"));
        subConcept(some("r", "c"), conc("d"));
        typeOf("a", "i");
        typeOf("b", "i");
        //
        assertTrue("d(i)");
    }

    @Test
    public void conceptAssertionsNormalization() {
        clear();
        typeOf(conj(conc("a1"), top(), conc("a2"), top(), conc("a3")), individual("i"));
        //
        assertTrue("a1(i), a2(i), a3(i)");
    }

    @Test
    public void example19() throws IOException {
        clear();
        subConcept(conc("a"), bottom());
        subConcept("b", "a");
        rule("b(a) :- not c(a)");
        rule("c(a) :- not b(a)");
        //
        assertTrue("c(a)");
        assertFalse("b(a)");
    }

    @Test
    public void existentialAssertion() {
        typeOf(some("r", "c"), individual("i"));
        subConcept(some("r", "c"), conc("d"));
        //
        assertTrue("d(i)");
    }

    // example17
    @Test
    public void leftConjunctionNormalization() throws IOException {
        clear();
        subConcept(conc("a"), some("r", "c"));
        subConcept(conj(some("r", "c"), conc("b")), bottom());
        rule("a(o)");
        rule("b(o)");
        //
        assertInconsistent("b(o)");
    }

    // example18
    @Test
    public void leftExistentialNormalization() throws IOException {
        clear();
        subConcept(conc("a"), some("r", "c"));
        subConcept(some(role("s_"), some("r", "c")), conc("d"));
        rule("s_(a,b)");
        rule("a(b)");
        //
        assertTrue("d(a)");
    }

    @Test
    public void rightBottomConjunct() throws IOException {
        clear();
        subConcept(conc("a1"), conj(conc("a2"), bottom(), conc("a3")));
        rule("a1(i)");
        //
        assertInconsistent("a1(i)");
    }

    // (a1.1), (r2.1)
    @Test
    public void roleChainSubsumption() {
        clear();
        object("p1", "i1", "i2");
        object("p2", "i2", "i3");
        object("p3", "i3", "i4");
        subRolesChain("p1", "p2", "p3", "p4");
        //
        assertTrue("p4(i1, i4)");
    }

    // (r2.3)
    @Test
    public void roleChainSubsumptionContrapositive() {
        clear();
        object("p2", "b", "c");
        object("p3", "c", "d");
        subRolesChain("p1", "p2", "p3", "p4");
        disjointConcepts(conc("a0"), some("p4"));
        //
        rule("a0(a)");
        //
        assertNegative("p1(a, b)");
    }

    @Test
    public void transitiveRole() {
        clear();
        object("r", "a", "b");
        object("r", "b", "c");
        transitive("r");
        //
        assertTrue("r(a, c)");
    }

    @Test
    public void domainDataRole() {
        clear();
        dataDomain("r", "a");
        rule("r(i,1)");
        //
        assertTrue("a(i)");
    }
}

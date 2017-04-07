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

import helpers.KB;
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
public abstract class QueryTest extends KB {

    public QueryTest(Profile profile) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        super(profile);
    }

    public QueryTest(Profile profile, NoHRHybridKBConfiguration config) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        super(profile, config);
    }

    // (s1.1), (s1.3)
    // (a1), (c1), (c1'), (i2.1)
    @Test
    public final void conceptContrapositive() {
        clear();
        //
        subConcept("a1", "a2");
        disjointConcepts("a3", "a2");
        //
        rule("a3(a)");
        //
        assertNegative("a1(a)");
    }

    // concepts tabling
    @Test
    public final void conceptCycle() {
        clear();
        subConcept("a1", "a2");
        subConcept("a2", "a1");
        //
        assertFalse("a1(X)");
    }

    // (a1.1), (n1.1)
    // (a1), (i2.1)
    @Test
    public final void conceptDisjunction() {
        clear();
        typeOf("a1", "a");
        disjointConcepts("a1", "a2");
        //
        assertNegative("a2(a)");
    }

    // (a1.1), (s1.1)
    @Test
    public final void conceptSubsumption() {
        clear();
        typeOf("a1", "a");
        subConcept("a1", "a2");
        //
        assertTrue("a2(a)");
    }

    @Test
    public final void dataAssertions() {
        clear();
        value("d1", "a", "l");
        //
        assertTrue("d1(a,l)");
    }

    @Test
    public void dataEquivalence() {
        clear();
        value("d1", "i1", "l1");
        value("d2", "i2", "l2");
        equivalentData("d1", "d2");
        assertTrue("d1(i2,l2), d2(i1, l1)");
    }

    @Test
    public void dataSubsumption() {
        clear();
        value("d1", "i", "l");
        subData("d1", "d2");
        assertTrue("d2(i,l)");
    }

    @Test
    public void disjointConcepts() throws IOException {
        disjointConcepts(conc("a1"), conc("a2"), conc("a3"), top());
        rule("a1(i)");
        rule("a2(i)");
        rule("a3(i)");
        assertInconsistent("a1(i)");
        assertInconsistent("a2(i)");
        assertInconsistent("a3(i)");
    }

    @Test
    public void equivalentConcepts() {
        typeOf("a1", "i1");
        typeOf("a2", "i2");
        equivalentConcepts("a1", "a2");
        assertTrue("a1(i2), a2(i1)");
    }

    // (a2.1), (n1.1)
    // (a2), (i2.2)
    @Test
    public final void existentialDisjunction() {
        clear();
        object("p1", "a", "b");
        disjointConcepts(some("p1"), conc("a2"));
        //
        assertNegative("a2(a)");
    }

    // (a1.1), (n1.2)
    // rules, rule duplication
    @Test
    public final void inconsistentRules() {
        clear();
        typeOf("a1", "a");
        disjointConcepts("a2", "a1");
        //
        rule("a2(?X):-a1(?X)");
        rule("a3(?X):-a2(?X)");
        rule("a1(?X):-a3(?X)");
        //
        assertInconsistent("a2(a), a3(a)");
    }

    // (a1.1), (a1.2), (s1.1), (s1.2), (s2.4.1), (s2.5.1)
    // (a1), (c1), (r1), [cls]
    @Test
    public void leftTop() {
        clear();
        subConcept(top(), conc("a1"));
        assertTrue("a1(a)");
    }

    @Test
    public final void nonDefinedBodyPredicates() {
        clear();
        subConcept("a1", "a2");
        //
        assertFalse("a2(?X)");
    }

    @Test
    public final void rightBottom() {
        clear();
        subConcept(conc("a1"), bottom());
        //
        assertNegative("a1(a)");
    }

    @Test
    public void rightConjunctionNormalization() {
        clear();
        typeOf("a1", "i");
        subConcept(conc("a1"), conj("a2", "a3", "a4"));
        assertTrue("a2(i), a3(i), a4(i)");
    }

    // (s2.3), (n1.2),
    // (a1), (r1), (r1'), (i2)
    @Test
    public final void roleContrapositive() {
        clear();
        subRole("p1", "p2");
        disjointConcepts(some("p2"), conc("a3"));
        //
        rule("a3(a)");
        //
        assertNegative("p1(a, b)");
    }

    // roles tabling
    @Test
    public final void roleCycle() {
        clear();
        subConcept("p1", "p2");
        subConcept("p2", "p1");
        //
        assertFalse("p1(?X,?Y)");
    }

    @Test
    public void roleDomain() {
        clear();
        object("r", "i1", "i2");
        domain("r", "a");
        assertTrue("a(i1)");
    }

    @Test
    public void roleEquivalence() {
        clear();
        object("r1", "i1", "i1");
        object("r2", "i2", "i2");
        equivalentRoles("r1", "r2");
        assertTrue("r1(i2, i2), r2(i1, i1)");
    }

    // (a1.1) (s2.1)
    @Test
    public final void roleSubsumption() {
        clear();
        object("p1", "a", "b");
        subRole("p1", "p2");
        //
        assertTrue("p2(a,b)");
    }

    public void tearDown() {
    }

    // (a1.1) (i1)
    // (a1) (c1) (i2.1)
    @Test
    public final void unsatisfiableConcepts() {
        clear();
        rule("a1(a)");
        subConcept("a1", "a2");
        subConcept("a2", "a1");
        subConcept("a3", "a2");
        disjointConcepts("a1", "a2");
        //
        assertNegative("a3(a)");
    }

}

package helpers;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;

import com.declarativa.interprolog.util.IPException;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;

import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.NegativeLiteral;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.nohr.translation.ql.QLOntologyNormalization;
import pt.unl.fct.di.novalincs.nohr.translation.ql.StaticQLOntologyNormalization;

public class KB {

    private final Map<String, OWLClass> concepts;

    private final Map<String, OWLDataProperty> dataRoles;

    protected final OWLDataFactory dataFactory;

    private final Profile profile;

    protected HybridKB hybridKB;

    private NoHRRecursiveDescentParser parser;

    private final Map<String, OWLIndividual> individuals;

    protected OWLOntology ontology;

    private final Map<String, OWLObjectProperty> roles;

    protected final OWLOntologyManager ontologyManager;

    private boolean clear;

    protected final NoHRHybridKBConfiguration config;

    public KB() throws OWLOntologyCreationException, OWLOntologyStorageException, OWLProfilesViolationsException,
            IPException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            PrologEngineCreationException {
        this(null);
    }

    public KB(Profile profile) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException,
            IPException, PrologEngineCreationException {
        this.profile = profile;
        ontologyManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
        concepts = new HashMap<String, OWLClass>();
        roles = new HashMap<String, OWLObjectProperty>();
        dataRoles = new HashMap<String, OWLDataProperty>();
        individuals = new HashMap<String, OWLIndividual>();
        this.config = new NoHRHybridKBConfiguration();
        setup();
        clear = true;
    }

    public KB(Profile profile, NoHRHybridKBConfiguration config) {
        this.profile = profile;
        ontologyManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
        concepts = new HashMap<String, OWLClass>();
        roles = new HashMap<String, OWLObjectProperty>();
        dataRoles = new HashMap<String, OWLDataProperty>();
        individuals = new HashMap<String, OWLIndividual>();
        this.config = config;
        setup();
        clear = true;
    }

    protected void addAxiom(OWLAxiom axiom) {
        ontologyManager.addAxiom(ontology, axiom);
        clear = false;
    }

    public void assertFalse(String query) {
        Assert.assertFalse("shouldn't have answers", hasAnswer(query, true, true, true));
        clear = false;
    }

    public void assertInconsistent(String query) {
        try {
            boolean assertInconsistent = hasAnswer(query, false, false, true);

            Assert.assertTrue("sould have inconsistent answers", assertInconsistent);
        } catch (InconsistentOntologyException e) {
            Assert.assertTrue("should have inconsistent answers", true);
            return;
        }

        boolean assertNotInconsistent = hasAnswer(query, true, true, false);

        Assert.assertFalse("should't have non inconsistent answers", assertNotInconsistent);
    }

    public void assertNegative(String query) {
        try {
            final NoHRRecursiveDescentParser parser = new NoHRRecursiveDescentParser(new DefaultVocabulary(ontology));
            if (!query.endsWith(".")) {
                query += ".";
            }
            final Query q = parser.parseQuery(query);
            for (final Literal l : q.getLiterals()) {
                if (l instanceof NegativeLiteral) {
                    throw new IllegalArgumentException("literals must be positive");
                }
                if (!l.isGrounded()) {
                    throw new IllegalArgumentException("literals must be grounded");
                }
                hybridKB.getProgram().add(Model.rule(l.getAtom()));
            }
            Assert.assertTrue("should have inconsistent answers", hasAnswer(query, false, false, true));
            Assert.assertFalse("shouldn't have non inconsistent answers", hasAnswer(query, true, true, false));
        } catch (final ParseException e) {
            fail(e);
        }
    }

    public void assertTrue(String query) {
        Assert.assertTrue("should have true answers", hasAnswer(query, true, false, false));
        Assert.assertFalse("shouldn't have non true answers", hasAnswer(query, false, true, true));
    }

    public void assertUndefined(String query) {
        Assert.assertTrue("should have undefined answers", hasAnswer(query, false, true, false));
        Assert.assertFalse("shouldn't have non undefined answers", hasAnswer(query, true, false, true));
    }

    public OWLAxiom asymmetric(String role) {
        final OWLAxiom axiom = dataFactory.getOWLAsymmetricObjectPropertyAxiom(role(role));
        addAxiom(axiom);
        return axiom;
    }

    public OWLObjectAllValuesFrom all(OWLObjectPropertyExpression r, OWLClassExpression c) {
        return dataFactory.getOWLObjectAllValuesFrom(r, c);
    }

    /**
     * @return
     */
    public OWLClassExpression bottom() {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing();
    }

    public void clear() {
        if (clear) {
            return;
        }
        concepts.clear();
        roles.clear();
        dataRoles.clear();
        individuals.clear();
        hybridKB.dispose();
        ontologyManager.removeOntology(ontology);
        setup();
        clear = true;
    }

    public OWLClassExpression complement(OWLClassExpression concept) {
        return dataFactory.getOWLObjectComplementOf(concept);
    }

    public OWLClassExpression complement(String conceptName) {
        return dataFactory.getOWLObjectComplementOf(conc(conceptName));
    }

    public OWLClass conc(String name) {
        OWLClass concept = concepts.get(name);
        if (concept == null) {
            concept = dataFactory.getOWLClass(getEntityIRI(name));
            final OWLAxiom axiom = dataFactory.getOWLDeclarationAxiom(concept);
            addAxiom(axiom);
            concepts.put(name, concept);
        }
        return concept;
    }

    public OWLClass concept() {
        return conc("a" + concepts.size());
    }

    private Set<OWLClass> concepts(String... conceptNames) {
        final Set<OWLClass> concepts = new HashSet<OWLClass>(conceptNames.length);
        for (final String name : conceptNames) {
            concepts.add(conc(name));
        }
        return concepts;
    }

    public OWLObjectIntersectionOf conj(OWLClassExpression... concepts) {
        return getDataFactory().getOWLObjectIntersectionOf(concepts);
    }

    public OWLClassExpression union(String... conceptNames) {
        return dataFactory.getOWLObjectUnionOf(concepts(conceptNames));

    }

    public OWLObjectIntersectionOf conj(String... conceptNames) {
        return dataFactory.getOWLObjectIntersectionOf(concepts(conceptNames));
    }

    public OWLDataProperty data(String name) {
        OWLDataProperty dataRole = dataRoles.get(name);
        if (dataRole == null) {
            dataRole = dataFactory.getOWLDataProperty(getEntityIRI(name));
            final OWLAxiom axiom = dataFactory.getOWLDeclarationAxiom(dataRole);
            addAxiom(axiom);
            dataRoles.put(name, dataRole);
        }
        return dataRole;
    }

    public OWLAxiom dataDomain(String roleName, String fillerName) {
        final OWLAxiom axiom = dataFactory.getOWLDataPropertyDomainAxiom(data(roleName), conc(fillerName));
        addAxiom(axiom);
        return axiom;
    }

    private Set<OWLDataProperty> dataRoles(String... roleNames) {
        final Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
        for (final String name : roleNames) {
            result.add(data(name));
        }
        return result;
    }

    public OWLAxiom disjointConcepts(OWLClassExpression... concepts) {
        final OWLAxiom axiom = dataFactory.getOWLDisjointClassesAxiom(concepts);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom disjointConcepts(String... conceptNames) {
        final OWLAxiom axiom = dataFactory.getOWLDisjointClassesAxiom(concepts(conceptNames));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom disjointRoles(OWLObjectPropertyExpression... ope) {
        final OWLAxiom axiom = dataFactory.getOWLDisjointObjectPropertiesAxiom(ope);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom disjointRoles(String... roleNames) {
        final OWLAxiom axiom = dataFactory.getOWLDisjointObjectPropertiesAxiom(roles(roleNames));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom domain(OWLObjectProperty ope, OWLClassExpression ce) {
        final OWLAxiom axiom = getDataFactory().getOWLObjectPropertyDomainAxiom(ope, ce);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom domain(String roleName, String fillerName) {
        final OWLAxiom axiom = dataFactory.getOWLObjectPropertyDomainAxiom(role(roleName), conc(fillerName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom equivalentConcepts(OWLClassExpression... ope) {
        final OWLAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(ope);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom equivalentConcepts(String... conceptNames) {
        final OWLAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(concepts(conceptNames));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom equivalentData(OWLDataPropertyExpression sub, OWLDataPropertyExpression sup) {
        final OWLAxiom axiom = getDataFactory().getOWLEquivalentDataPropertiesAxiom(sub, sup);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom equivalentData(String... dataRoleNames) {
        final OWLAxiom axiom = dataFactory.getOWLEquivalentDataPropertiesAxiom(dataRoles(dataRoleNames));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom equivalentRoles(OWLObjectPropertyExpression... ope) {
        final OWLAxiom axiom = getDataFactory().getOWLEquivalentObjectPropertiesAxiom(ope);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom equivalentRoles(String... roleNames) {
        final OWLAxiom axiom = dataFactory.getOWLEquivalentObjectPropertiesAxiom(roles(roleNames));
        addAxiom(axiom);
        return axiom;
    }

    private void fail(Exception e) {
        if (e instanceof IOException) {
            Assert.fail(e.getMessage());
        }
        if (e instanceof OWLProfilesViolationsException) {
            Assert.fail("ontology is not QL nor EL!\n" + e.getMessage());
        }
        if (e instanceof InconsistentOntologyException) {
            Assert.fail("inconsistent ontology");
        }
        if (e instanceof OWLOntologyCreationException) {
            Assert.fail(e.getMessage());
        }
        if (e instanceof OWLOntologyStorageException) {
            Assert.fail(e.getMessage());
        }
        if (e instanceof CloneNotSupportedException) {
            Assert.fail(e.getMessage());
        }
        if (e instanceof UnsupportedAxiomsException) {
            Assert.fail(e.getMessage());
        }
    }

    public OWLClass[] getConcepts(int n) {
        final OWLClass[] result = new OWLClass[n];
        for (int i = 0; i < n; i++) {
            result[i] = concept();
        }
        return result;
    }

    private OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    private OWLDataProperty getDataRole() {
        return data("Dnew" + dataRoles.size());
    }

    public OWLDataProperty[] getDataRoles(int n) {
        final OWLDataProperty[] result = new OWLDataProperty[n];
        for (int i = 0; i < n; i++) {
            result[i] = getDataRole();
        }
        return result;
    }

    private IRI getEntityIRI(String name) {
        final IRI ontIRI = ontology.getOntologyID().getOntologyIRI().get(); // optional ignored
        return IRI.create(ontIRI + "#" + name);
    }

    public QLOntologyNormalization getQLNormalizedOntology() throws UnsupportedAxiomsException {
        return new StaticQLOntologyNormalization(ontology, hybridKB.getVocabulary());
    }

    public OWLObjectProperty[] getRoles(int n) {
        final OWLObjectProperty[] result = new OWLObjectProperty[n];
        for (int i = 0; i < n; i++) {
            result[i] = role();
        }
        return result;
    }

    private boolean hasAnswer(String query, boolean trueAnswers, boolean undefinedAnswers,
            boolean inconsistentAnswers) {
        try {
            final Query q = parser.parseQuery(query);
            return hybridKB.hasAnswer(q, trueAnswers, undefinedAnswers, inconsistentAnswers);
        } catch (final OWLProfilesViolationsException e) {
            Assert.fail(e.getMessage());
            return false;
        } catch (final UnsupportedAxiomsException e) {
            Assert.fail(e.getMessage());
            return false;
        } catch (final ParseException e) {
            Assert.fail(e.getMessage());
            return false;
        } catch (final IllegalArgumentException e) {
            Assert.fail(e.getMessage());
            return false;
        }
    }

    public OWLIndividual individual() {
        return individual("anew" + individuals.size());
    }

    public OWLIndividual individual(String name) {
        OWLIndividual individual = individuals.get(name);
        if (individual == null) {
            individual = dataFactory.getOWLNamedIndividual(getEntityIRI(name));
            individuals.put(name, individual);
        }
        return individual;
    }

    public OWLObjectPropertyExpression inv(OWLObjectProperty role) {
        return dataFactory.getOWLObjectInverseOf(role);
    }

    public OWLObjectPropertyExpression inv(String roleName) {
        final OWLObjectProperty role = role(roleName);
        return dataFactory.getOWLObjectInverseOf(role);
    }

    public OWLAxiom inverse(String subRole, String superRole) {
        final OWLAxiom axiom = dataFactory.getOWLInverseObjectPropertiesAxiom(role(subRole), role(superRole));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom irreflexive(String role) {
        final OWLAxiom axiom = dataFactory.getOWLIrreflexiveObjectPropertyAxiom(role(role));
        addAxiom(axiom);
        return axiom;
    }

    public OWLObjectComplementOf neg(OWLClassExpression ce) {
        return dataFactory.getOWLObjectComplementOf(ce);
    }

    public OWLObjectComplementOf neg(String conceptName) {
        return dataFactory.getOWLObjectComplementOf(conc(conceptName));
    }

    public OWLAxiom object(OWLObjectProperty role, OWLIndividual ind1, OWLIndividual ind2) {
        final OWLAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(role, ind1, ind2);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom object(String roleName, String subjectIndividualName, String individualName) {
        final OWLObjectPropertyAssertionAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(role(roleName),
                individual(subjectIndividualName), individual(individualName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom range(String roleName, String fillerName) {
        final OWLAxiom axiom = dataFactory.getOWLObjectPropertyRangeAxiom(role(roleName), conc(fillerName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom reflexive(String role) {
        final OWLAxiom axiom = dataFactory.getOWLReflexiveObjectPropertyAxiom(role(role));
        addAxiom(axiom);
        return axiom;
    }

    public boolean remove(Rule rule) {
        return hybridKB.getProgram().remove(rule);
    }

    protected void removeAxiom(OWLAxiom axiom) {
        ontologyManager.removeAxiom(ontology, axiom);
    }

    public OWLObjectProperty role() {
        return role("p" + roles.size());
    }

    public OWLObjectProperty role(String name) {
        OWLObjectProperty role = roles.get(name);
        if (role == null) {
            role = dataFactory.getOWLObjectProperty(getEntityIRI(name));
            final OWLAxiom axiom = dataFactory.getOWLDeclarationAxiom(role);
            addAxiom(axiom);
            roles.put(name, role);
        }
        return role;
    }

    private Set<OWLObjectProperty> roles(String... roleNames) {
        final Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
        for (final String name : roleNames) {
            result.add(role(name));
        }
        return result;
    }

    public Rule rule(String rule) {
        try {
            final Rule r = parser.parseRule(rule);
            hybridKB.getProgram().add(r);
            return r;
        } catch (final ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup() {
        try {
            ontology = ontologyManager.createOntology(IRI.generateDocumentIRI());
            hybridKB = new NoHRHybridKB(config, ontology, profile);
            parser = new NoHRRecursiveDescentParser(hybridKB.getVocabulary());
        } catch (final IPException | OWLOntologyCreationException | UnsupportedAxiomsException | PrologEngineCreationException e) {
            throw new RuntimeException(e);
        }

    }

    public OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return dataFactory.getOWLObjectSomeValuesFrom(owlObjectPropertyExpression, dataFactory.getOWLThing());
    }

    public OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression role, OWLClassExpression filler) {
        return dataFactory.getOWLObjectSomeValuesFrom(role, filler);
    }

    public OWLObjectSomeValuesFrom some(String roleName) {
        final OWLObjectProperty role = role(roleName);
        return dataFactory.getOWLObjectSomeValuesFrom(role, dataFactory.getOWLThing());
    }

    public OWLObjectSomeValuesFrom some(String roleName, String fillerName) {
        return dataFactory.getOWLObjectSomeValuesFrom(role(roleName), conc(fillerName));
    }

    public OWLAxiom subConcept(OWLClassExpression b1, OWLClassExpression b2) {
        final OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(b1, b2);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom subConcept(String subName, String superName) {
        final OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(conc(subName), conc(superName));
        addAxiom(axiom);
        return axiom;
    }

    /**
     * @param d1
     * @param d2
     */
    public OWLAxiom subData(OWLDataProperty d1, OWLDataProperty d2) {
        final OWLAxiom axiom = getDataFactory().getOWLSubDataPropertyOfAxiom(d1, d2);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom subData(String subName, String superName) {
        final OWLAxiom axiom = getDataFactory().getOWLSubDataPropertyOfAxiom(data(subName), data(superName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom subRole(List<? extends OWLObjectPropertyExpression> chain, OWLObjectPropertyExpression q2) {
        final OWLAxiom axiom = dataFactory.getOWLSubPropertyChainOfAxiom(chain, q2);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom subRole(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2) {
        final OWLAxiom axiom = dataFactory.getOWLSubObjectPropertyOfAxiom(q1, q2);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom subRole(String subName, String superName) {
        final OWLAxiom axiom = dataFactory.getOWLSubObjectPropertyOfAxiom(role(subName), role(superName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom subRolesChain(String... roleNames) {
        final List<OWLObjectPropertyExpression> chain = new ArrayList<OWLObjectPropertyExpression>(
                roleNames.length - 1);
        for (int i = 0; i < roleNames.length - 1; i++) {
            chain.add(role(roleNames[i]));
        }
        final OWLAxiom axiom = dataFactory.getOWLSubPropertyChainOfAxiom(chain, role(roleNames[roleNames.length - 1]));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom symmetric(String role) {
        final OWLAxiom axiom = dataFactory.getOWLSymmetricObjectPropertyAxiom(role(role));
        addAxiom(axiom);
        return axiom;
    }

    /**
     * @return
     */
    public OWLClassExpression top() {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
    }

    public OWLDataProperty topData() {
        return dataFactory.getOWLTopDataProperty();
    }

    public OWLObjectProperty topRole() {
        return dataFactory.getOWLTopObjectProperty();
    }

    public OWLAxiom transitive(OWLObjectPropertyExpression ope) {
        final OWLAxiom axiom = getDataFactory().getOWLTransitiveObjectPropertyAxiom(ope);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom transitive(String roleName) {
        final OWLAxiom axiom = dataFactory.getOWLTransitiveObjectPropertyAxiom(role(roleName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom typeOf(OWLClassExpression concept, OWLIndividual individual) {
        final OWLAxiom axiom = dataFactory.getOWLClassAssertionAxiom(concept, individual);
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom typeOf(String conceptName, String individualName) {
        final OWLAxiom axiom = dataFactory.getOWLClassAssertionAxiom(conc(conceptName), individual(individualName));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom value(OWLDataProperty role, OWLIndividual ind1, String litral) {
        final OWLAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(role, ind1,
                dataFactory.getOWLLiteral(litral));
        addAxiom(axiom);
        return axiom;
    }

    public OWLAxiom value(String dataRoleName, String subjectIndividualName, String literal) {
        final OWLDataPropertyAssertionAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(data(dataRoleName),
                individual(subjectIndividualName), dataFactory.getOWLLiteral(literal));
        addAxiom(axiom);
        return axiom;
    }

}

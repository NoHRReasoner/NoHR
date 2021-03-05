/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation.el;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.reasoner.taxonomy.InvalidTaxonomyException;
//import org.semanticweb.elk.reasoner.taxonomy.InvalidTaxonomyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

/**
 * The implementation of {@link ELOntologyReduction}. This
 * {@link ELOntologyReduction} is static in the sense that can represent only
 * the reduction of an ontology in a given moment; if the ontology changes other
 * {@link StaticELOntologyReduction} must be constructed.
 *
 * @author Nuno Costa
 */
public class StaticELOntologyReduction implements ELOntologyReduction {

    /**
     * Given an concept subsumption <i>C&sqsube;D</i>, adds <i>C&sqsube;A'</i>
     * and <i>A'&sqsube;D</i>, where <i>A'</i> is a new atomic concept, if
     * <i>C</i> isn't atomic and <i>D</i> has occurrences of existential; adds
     * nothing otherwise (corresponds to the first normalization presented in
     * the section <b>2.1</b> of {@link <a>A Correct EL Oracle for NoHR
     * (Technical Report)</a>}).
     *
     * @author Nuno Costa
     */
    private class ComplexSidesNormalizer implements Normalizer<OWLSubClassOfAxiom> {

        @Override
        public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
            final OWLClassExpression ce1 = axiom.getSubClass();
            final OWLClassExpression ce2 = axiom.getSuperClass();
            if (ce1.isAnonymous() && hasExistential(ce2)) {
                final OWLClass anew = vocabulary.generateNewConcept();
                newAxioms.add(subsumption(ce1, anew));
                newAxioms.add(subsumption(anew, ce2));
                return true;
            }
            return false;
        }

    }

    /**
     * Given an concept assertion <i>C(a)</i>, adds <i>A<sub>1</sub>(a), ...,
     * A<sub>n</sub>(a)</i> if <i>D = A<sub>1</sub>&sqcap; ... &sqcap; A
     * <sub>n</sub></i>; adds notthing otherwise (see the simplifications
     * presentend above the <b>Definition 12.</b> of {@link <a>A Correct EL
     * Oracle for NoHR (Technical Report)</a>}).
     *
     * @author Nuno Costa
     */
    private class ConceptAssertionsNormalizer implements Normalizer<OWLClassAssertionAxiom> {

        @Override
        public boolean addNormalization(OWLClassAssertionAxiom assertion, Set<OWLClassAssertionAxiom> newAssertions) {
            final Set<OWLClassExpression> ceConj = assertion.getClassExpression().asConjunctSet();
            final OWLIndividual i = assertion.getIndividual();
            if (ceConj.size() > 1) {
                for (final OWLClassExpression ci : ceConj) {
                    if (!ci.isTopEntity()) {
                        newAssertions.add(assertion(ci, i));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Given a concept subsumption <i>C&sqsube;D</i>, indicates whether <i>C</i>
     * is the bottom concept <i>&bot;</i>, in which case <i>C&sqsube;D</i>
     * can be removed.
     *
     * @author Nuno Costa
     */
    private class LeftBottomNormalizer implements Normalizer<OWLSubClassOfAxiom> {

        @Override
        public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
            return axiom.getSubClass().isOWLNothing();
        }

    }

    /**
     * Given an concept subsumption <i>C&sqsube;D</i>, adds
     * <i>C<sub>1</sub>&sqsube;A'<sub>1</sub>, ...,
     * C<sub>n</sub>&sqsube;A'<sub>n</sub></i>, and
     * <i>A<sub>1</sub>&sqcap; ... &sqcap;A<sub>m</sub> &sqcap;
     * A'<sub>1</sub>&sqcap; ... &sqcap;A'<sub>n</sub>&sqsube;D</i>, where each
     * <i>A'
     * <sub>i</sub></i> is a new atomic concept, if <i>C = A<sub>1</sub>&sqcap;
     * ... &sqcap;A<sub>m</sub> &sqcap; &exist;R<sub>1</sub>.C<sub>1</sub>
     * &sqcap; ... &sqcap;&exist;R <sub>n</sub>.C<sub>n</sub></i>, with
     * <i>m&geq;0</i> and <i>n&geq;1</i>; and nothing otherwise (corresponds to
     * the second normalization presented in the section <b>2.1</b> of {@link
     * <a>A Correct EL Oracle for NoHR (Technical Report)</a>}).
     *
     * @author Nuno Costa
     */
    private class LeftConjunctionNormalizer implements Normalizer<OWLSubClassOfAxiom> {

        @Override
        public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
            boolean changed = false;
            final Set<OWLClassExpression> ce1Conj = axiom.getSubClass().asConjunctSet();
            final OWLClassExpression ce2 = axiom.getSuperClass();
            if (ce1Conj.size() > 1) {
                final Set<OWLClassExpression> normCe1Conj = new HashSet<OWLClassExpression>();
                for (final OWLClassExpression ci : ce1Conj) {
                    if (isExistential(ci)) {
                        final OWLClass anew = vocabulary.generateNewConcept();
                        newAxioms.add(subsumption(ci, anew));
                        normCe1Conj.add(anew);
                        changed = true;
                    } else {
                        normCe1Conj.add(ci);
                    }
                }
                if (changed) {
                    newAxioms.add(subsumption(conj(normCe1Conj), ce2));
                }
            }
            return changed;
        }

    }

    /**
     * Given an concept subsumption <i>C&sqsube;D</i>, adds
     * <i>C<sub>1</sub>&sqsube;A'<sub>1</sub>, ...,
     * C<sub>n</sub>&sqsube;A'<sub>n</sub></i> and
     * <i>&exist;R.(A<sub>1</sub>&sqcap; ...
     * &sqcap;A<sub>m</sub>&sqcap;A'<sub>1</sub>&sqcap; ...
     * &sqcap;A'<sub>n</sub>)&sqsube;D </i>, if <i> C =
     * &exist;R.(A<sub>1</sub>&sqcap; ...
     * &sqcap;A<sub>m</sub>&sqcap;&exist;S<sub>1</sub>.C<sub>1</sub>&sqcap;&exist;S<sub>n</sub>.C
     * <sub>n</sub>
     * )</i>, with <i>m&geq;0</i> and <i>n&geq;1</i>; and nothing otherwise
     * (corresponds to the third normalization presented in the section
     * <b>2.1</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>}).
     *
     * @author Nuno Costa
     */
    private class LeftExistentialNormalizer implements Normalizer<OWLSubClassOfAxiom> {

        @Override
        public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
            boolean changed = false;
            final OWLClassExpression ce1 = axiom.getSubClass();
            final OWLClassExpression ce2 = axiom.getSuperClass();
            if (isExistential(ce1)) {
                final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce1;
                final OWLObjectPropertyExpression ope = some.getProperty();
                final Set<OWLClassExpression> fillerConj = some.getFiller().asConjunctSet();
                final Set<OWLClassExpression> normFillerConj = new HashSet<OWLClassExpression>();
                for (final OWLClassExpression ci : fillerConj) {
                    if (isExistential(ci)) {
                        final OWLClass anew = vocabulary.generateNewConcept();
                        newAxioms.add(subsumption(ci, anew));
                        normFillerConj.add(anew);
                        changed = true;
                    } else {
                        normFillerConj.add(ci);
                    }
                }
                if (changed) {
                    newAxioms.add(subsumption(some(ope, conj(normFillerConj)), ce2));
                }
            }
            return changed;
        }
    }

    /**
     * Represents and handles an EL axiom normalization. Given an EL axiom adds
     * the axioms corresponding to the normalization of that axiom to a given
     * set.
     *
     * @author Nuno Costa
     * @param <T> the type of axioms that the concrete {@link Normalizer}
     * handle.
     */
    private interface Normalizer<T extends OWLAxiom> {

        /**
         * Given an EL axiom adds the axioms corresponding to the normalization
         * of that axiom to a given set.
         *
         * @param axiom the axiom to normalize.
         * @param newAxioms the set to wich were the axioms corresponding to the
         * normalization of {@code axiom} will be added.
         * @return true iff some axiom was added to {@code newAxioms}, i.e. the
         * axiom wasn't normalized.
         */
        boolean addNormalization(T axiom, Set<T> newAxioms);

    }

    /**
     * Given a concept subsumption <i>C&sqsube;D</i> adds
     * <i>C&sqsube;D<sub>1</sub>, ..., C&sqsube;D<sub>n</sub></i>, if <i>D =
     * D<sub>1</sub>&sqsube; ... &sqsube;D<sub>n</sub></i>, and nothing
     * otherwise (see the simplifications presentend above the <b>Definition
     * 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>}).
     *
     * @author Nuno Costa
     */
    private class RightConjunctionNormalizer implements Normalizer<OWLSubClassOfAxiom> {

        @Override
        public boolean addNormalization(OWLSubClassOfAxiom axiom, Set<OWLSubClassOfAxiom> newAxioms) {
            boolean changed = false;
            final OWLClassExpression ce1 = axiom.getSubClass();
            final Set<OWLClassExpression> ce2Conj = axiom.getSuperClass().asConjunctSet();
            if (ce2Conj.size() > 1) {
                for (final OWLClassExpression ci : ce2Conj) {
                    newAxioms.add(subsumption(ce1, ci));
                }
                changed = true;
            }
            return changed;
        }
    }

    private static final Logger log = Logger.getLogger(StaticELOntologyReduction.class);

    /**
     * The set of role chain subsumptions <i>R<sub>1</sub>&SmallCircle; ...
     * &SmallCircle;S<sub>n</sub> &sqsube; A</i> in this
     * {@link ELOntologyReduction reduction}.
     */
    private final Set<OWLSubPropertyChainOfAxiom> chainSubsumptions;

    /**
     * The closure of the of all the concept assertions, concept subsumptions
     * and role subsumptions obtained by exhaustively applying all the
     * {@link Normalizer normalizations} specified in this class to the ontology
     * referred by this {@link ELOntologyReduction reduction}, under the
     * inference rules applied by ELK, referred in the <b>Theorem 5.</b> of {@link
     * <a>A Correct EL Oracle for NoHR (Technical Report)</a>}). That closure
     * has (by <b>Theorem 5.</b> and <b>Lemma 7.</b>) the following properties,
     * where <i>O</i> denotes the ontology that this
     * {@link ELOntologyReduction reduction} refer and <b>Closure</b> the
     * closure itself: <br>
     * 1. <i>O &vDash; C&sqsube;&bot;</i> iff <i>C&sqsube;&bot; &in;
     * </i><b>Closure</b>; <br>
     * 2. <i>O &vDash; C&sqsube;A</i> iff <i>C&sqsube;&bot;
     * &in;</i><b>Closure</b> or <i>C&sqsube;A &in; </i><b>Closure</b>;<br>
     * 3. <i>O &vDash; A(a)</i> iff <i> A(a) &in;</i><b>Closure</i>.
     */
    private final OWLOntology closure;

    /**
     * Whether this {@link ELOntologyReduction reduction} has disjunctions.
     */
    private final boolean hasDisjunctions;

    /**
     * The ontology that this {@link ELOntologyReduction reduction} refer.
     */
    private final OWLOntology ontology;

    /**
     * The set of role subsumptions <i>R &sqsube; S</i> in this
     * {@link ELOntologyReduction reduction}.
     */
    private final Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions;

    private final Vocabulary vocabulary;

    /**
     * Constructs the {@code ELOntologyReduction reduction} of a specified
     * ontology.
     *
     * @param ontology an ontology.
     * @throws UnsupportedAxiomsException if {@code ontology} has some concept
     * that can't be reduced.
     */
    public StaticELOntologyReduction(OWLOntology ontology, Vocabulary vocabulary) throws UnsupportedAxiomsException {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(vocabulary);

        this.ontology = ontology;
        this.vocabulary = vocabulary;
        final Set<OWLClassAssertionAxiom> conceptAssertions = ontology.getAxioms(AxiomType.CLASS_ASSERTION);
        final Set<OWLSubClassOfAxiom> conceptSubsumptions = conceptSubsumptions(ontology);
        roleSubsumptions = roleSubsumptions(ontology);
        try {
            closure = closure(conceptAssertions, conceptSubsumptions, roleSubsumptions);
        } catch (final InvalidTaxonomyException e) {
            throw new UnsupportedAxiomsException(null);
        }
        int negAssertions = ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
        negAssertions += ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION);
        hasDisjunctions = hasDisjunctions(closure) || negAssertions > 0;
        chainSubsumptions = chainSubsumptions(ontology);
    }

    /**
     * Creates an assertion from a specified concept and individual.
     *
     * @param c the concept <i>C</i>.
     * @param a the individual <i>a</i>.
     * @return the assertion <i>C(a)</i>.
     */
    private OWLClassAssertionAxiom assertion(OWLClassExpression c, OWLIndividual a) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(c, a);
    }

    /**
     * Returns the bottom concept <i>&bot;</i>.
     *
     * @return <i>&bot;</i>.
     */
    private OWLClass bottom() {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing();
    }

    @Override
    public Iterable<OWLSubPropertyChainOfAxiom> chainSubsumptions() {
        return chainSubsumptions;
    }

    /**
     * Obtains the <i>EL<sub>&bot;</sub><sup>+</sup> role chain subsumptions
     * entailed by a given EL ontology.
     *
     * @param ontology an ontology <i>O</i>.
     * @return <i>{ R<sub>1</sub>&SmallCircle; ...
     * &SmallCircle;R<sub>n</sub>&sqsube;S | R<sub>1</sub>&SmallCircle; ...
     * &SmallCircle;R<sub>n</sub>
     * &sqsube;S &in; O} &cup; {R&SmallCircle;R &sqsube; R |
     * </i><b>transitive</b></i>(R) &in; O} </i>.
     */
    private Set<OWLSubPropertyChainOfAxiom> chainSubsumptions(OWLOntology ontology) {
        final Set<OWLSubPropertyChainOfAxiom> result = new HashSet<OWLSubPropertyChainOfAxiom>(
                ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF));
        for (final OWLTransitiveObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
            result.add(norm(axiom));
        }
        return result;
    }

    /**
     * Classify a given ontology with the ELK.
     *
     * @param ontology an ontology <i>O</i>.
     */
    private void classify(OWLOntology ontology) {
        RuntimesLogger.start("[OWL EL (ELK)] ontology inference");
        Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
        final OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
        final OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        /**
         * Classify the ontology.
         */
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        final List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                3);
        generators.add(new InferredSubClassAxiomGenerator());
        generators.add(new InferredClassAssertionAxiomGenerator());
        final InferredOntologyGenerator inferredOntologyGenerator = new InferredOntologyGenerator(reasoner, generators);
        inferredOntologyGenerator.fillOntology(ontology.getOWLOntologyManager().getOWLDataFactory(), ontology);
        RuntimesLogger.stop("[OWL EL (ELK)] ontology inference", "loading");
        reasoner.dispose();
    }

    /**
     * Creates the closure of all the axioms obtained by exhaustively applying
     * all the {@link Normalizer normalizations} specified in this class to
     * three given sets of concept assertions, concept subsumptions and role
     * subsumptions, respectively, under the inference rules applied by ELK,
     * referred in the <b>Theorem 5.</b> of {@link <a>A Correct EL Oracle for
     * NoHR (Technical Report)</a>}). That closure has (by <b>Theorem 5.</b>
     * and <b>Lemma 7.</b>) the following properties, where <i>O</i> denotes the
     * ontology that this {@link ELOntologyReduction reduction} refer and
     * <b>Closure</b> the closure itself: <br>
     * 1. <i>O &vDash; C&sqsube;&bot;</i> iff <i>C&sqsube;&bot; &in;
     * </i><b>Closure</b>; <br>
     * 2. <i>O &vDash; C&sqsube;A</i> iff <i>C&sqsube;&bot;
     * &in;</i><b>Closure</b> or <i>C&sqsube;A &in; </i><b>Closure</b>;<br>
     * 3. <i>O &vDash; A(a)</i> iff <i> A(a) &in;</i><b>Closure</b></i>.
     *
     * @param conceptAssertions a set of EL concept assertions.
     * @param conceptSubsumptions a set of EL concept subsumptions.
     * @param roleSubsumptions a set of EL role subsumptions.
     * @return an new ontology containing all the axioms of the closure of
     * {@code conceptAssertions}, {@code conceptSubsumptions} and
     * {@code roleSubsumptions}.
     */
    private OWLOntology closure(Set<OWLClassAssertionAxiom> conceptAssertions,
            Set<OWLSubClassOfAxiom> conceptSubsumptions, Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions) {
        final OWLOntology result = norm(conceptAssertions, conceptSubsumptions, roleSubsumptions);
        classify(result);
        return result;
    }

    @Override
    public Iterable<OWLClassAssertionAxiom> conceptAssertions() {
        return closure.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    @Override
    public Iterable<OWLSubClassOfAxiom> conceptSubsumptions() {
        return closure.getAxioms(AxiomType.SUBCLASS_OF);
    }

    /**
     * Obtains the <i>EL<sub>&bot;</sub><sup>+</sup></i> concept subsumptions
     * entailed by a given EL ontology.
     *
     * @param ontology an ontology <i>O</i>
     * @return <i> { C&sqsube;D | C&sqsube;D &in; O } &cup; <br>
     * { C&sqsube;D, D&sqsube;C | C&equiv;D &in; O } &cup; <br>
     * { C&sqcap;D&sqsube;&bot; | C&sqsube;&not;D &in; O} &cup; <br>
     * { &exist;R.&top;&sqsube;D | </i><b>domain</b></i>(R, D)}</i>.
     */
    private Set<OWLSubClassOfAxiom> conceptSubsumptions(OWLOntology ontology) {
        final Set<OWLSubClassOfAxiom> conceptSubsumptions = new HashSet<OWLSubClassOfAxiom>(
                ontology.getAxioms(AxiomType.SUBCLASS_OF));
        for (final OWLEquivalentClassesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
            conceptSubsumptions.addAll(axiom.asOWLSubClassOfAxioms());
        }
        for (final OWLDisjointClassesAxiom axiom : ontology.getAxioms(AxiomType.DISJOINT_CLASSES)) {
            for (final OWLDisjointClassesAxiom ci : axiom.asPairwiseAxioms()) {
                conceptSubsumptions.add(subsumption(conj(ci.getClassExpressions()), bottom()));
            }
        }
        for (final OWLObjectPropertyDomainAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            conceptSubsumptions.add(norm(axiom));
        }

        for (final OWLDataPropertyDomainAxiom axiom : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
            conceptSubsumptions.add(norm(axiom));
        }

        return conceptSubsumptions;
    }

    /**
     * Creates a conjunction from a given set of concepts.
     *
     * @param concepts a set of concepts <i>{C<sub>1</sub>, ...,
     * C<sub>n</sub>}</i>.
     * @return <i>C<sub>1</sub>&sqcap; ... &sqcap;C<sub>n</sub>.
     */
    private OWLObjectIntersectionOf conj(Set<OWLClassExpression> concepts) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectIntersectionOf(concepts);
    }

    @Override
    public Iterable<OWLDataPropertyAssertionAxiom> dataAssertion() {
        return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLSubDataPropertyOfAxiom> dataSubsuptions() {
        final Set<OWLSubDataPropertyOfAxiom> result = new HashSet<OWLSubDataPropertyOfAxiom>(
                ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY));
        for (final OWLEquivalentDataPropertiesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
            result.addAll(axiom.asSubDataPropertyOfAxioms());
        }
        return result;
    }

    @Override
    public boolean hasDisjunctions() {
        return hasDisjunctions;
    }

    /**
     * Check if a given normalized ontology as disjunctions.
     *
     * @param normalizedOntology an normalized ontology (i.e. an ontology where
     * all the {@link Normalizer normalizations} specified in this class where
     * exhaustively applied).
     * @return true iff {@code normalizedOntology} has disjunctions, i.e. if
     * {@code normalizedOntology} has some axiom <i>C<sub>1</sub>&sqcap; ...
     * &sqcap;C<sub>n</sub> &sqsube;&bot;</i>, with <i>n&geq;1</i>.
     */
    private boolean hasDisjunctions(OWLOntology normalizedOntology) {
        for (final OWLSubClassOfAxiom axiom : normalizedOntology.getAxioms(AxiomType.SUBCLASS_OF)) {
            for (final OWLClassExpression ci : axiom.getSuperClass().asConjunctSet()) {
                if (ci.isOWLNothing()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasExistential(OWLClassExpression ce) {
        for (final OWLClassExpression cei : ce.asConjunctSet()) {
            if (isExistential(cei)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a given concept is an existential.
     *
     * @param c a concept.
     * @return true iff {@code c} is an existential <i>&exist;R.D</i>.
     */
    private boolean isExistential(OWLClassExpression c) {
        return c instanceof OWLObjectSomeValuesFrom;
    }

    /**
     * Normalize a given domain axiom.
     *
     * @param axiom a domain axiom <b>domain</b><i>(R, C)</i>.
     * @return <i>&exist;R.&top;&sqsube;C</i>.
     */
    private OWLSubClassOfAxiom norm(OWLObjectPropertyDomainAxiom axiom) {
        final OWLObjectPropertyExpression ope = axiom.getProperty();
        final OWLClassExpression ce = axiom.getDomain();

        return subsumption(some(ope, top()), ce);
    }

    private OWLSubClassOfAxiom norm(OWLDataPropertyDomainAxiom axiom) {
        final OWLDataPropertyExpression ope = axiom.getProperty();
        final OWLClassExpression ce = axiom.getDomain();

        return subsumption(some(ope, topDatatype()), ce);
    }

    /**
     * Normalize a given transitive axiom.
     *
     * @param axiom an transitive axiom <b>transitive</b><i>(R)</i>.
     * @return <i>R&SmallCircle;R&sqsube;R</i>.
     */
    private OWLSubPropertyChainOfAxiom norm(OWLTransitiveObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression ope = axiom.getProperty();
        final List<OWLObjectPropertyExpression> chain = new ArrayList<OWLObjectPropertyExpression>(2);
        chain.add(ope);
        chain.add(ope);
        return subsumption(chain, ope);
    }

    /**
     * Creates an ontology containing exactly the normalizations of three given
     * sets of concept assertions, concept subsumptions and role subsumptions,
     * respectively.
     *
     * @param conceptAssertions a set of concept assertions.
     * @param conceptSubsumptions a set of concept subsumptions.
     * @param roleSubsumptions a set of role subsumptions.
     * @return the ontology containing exactly the axioms obtained by applying
     * exhaustively all the {@link Normalizer normalizations} specified in this
     * class to {@code conceptAssertions}, {@code conceptSubsumptions} and
     * {@code roleSubsumptions}.
     */
    private OWLOntology norm(Set<OWLClassAssertionAxiom> conceptAssertions, Set<OWLSubClassOfAxiom> conceptSubsumptions,
            Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions) {
        final OWLOntologyManager om = OWLManager.createOWLOntologyManager();
        try {
            final OWLOntology result = om.createOntology(ontology.getOntologyID());
            norm(conceptAssertions, new ConceptAssertionsNormalizer());
            norm(conceptSubsumptions);
            om.addAxioms(result, conceptAssertions);
            om.addAxioms(result, conceptSubsumptions);
            om.addAxioms(result, roleSubsumptions);
            return result;
        } catch (final OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Normalize a given set of concept subsumptions, applying exhaustively all
     * the concept subsumptions {@link Normalizer normalization} specified in
     * this class.
     *
     * @param axioms a set of axioms.
     */
    private void norm(Set<OWLSubClassOfAxiom> axioms) {
        boolean changed1;
        boolean changed2 = false;
        boolean first = true;
        do {
            changed1 = norm(axioms, new LeftConjunctionNormalizer());
            if (first || changed1) {
                changed2 = norm(axioms, new LeftExistentialNormalizer());
            } else {
                changed2 = false;
            }
            first = false;
        } while (changed1 || changed2);
        norm(axioms, new LeftBottomNormalizer());
        norm(axioms, new ComplexSidesNormalizer());
        norm(axioms, new RightConjunctionNormalizer());
    }

    /**
     * Applies a given {@link Normalizer normalization} to all the axioms of a
     * specified set of axioms. Iterates the given set, {@code axioms}, calling
     * {@link Normalizer#addNormalization(OWLAxiom, Set) normalizer.addNormalization(axiom, axioms)},
     * where {@code normalizer} is the specified
     * {@link Normalizer normalization}, to each axiom {@code axiom}, and
     * removing it from {@code axioms} if that call returned true.
     *
     * @param <T> the type of the axioms that will be normalized.
     * @param axioms the set of axioms to which the normalization will be
     * applied.
     * @param normalizer the {@link Normalizer normalization} that will be
     * applied to {@code axioms}.
     * @return true iff some axiom was normalized (i.e. replaced by the axioms
     * corresponding to the normalization according to {@code normalizer} of
     * that axiom).
     */
    private <T extends OWLAxiom> boolean norm(Set<T> axioms, Normalizer<T> normalizer) {
        boolean changed = false;
        final Iterator<T> axiomsIt = axioms.iterator();
        final Set<T> newAxioms = new HashSet<T>();
        while (axiomsIt.hasNext()) {
            final T axiom = axiomsIt.next();
            final boolean hasChange = normalizer.addNormalization(axiom, newAxioms);
            if (hasChange) {
                axiomsIt.remove();
                changed = true;
            }
        }
        if (changed) {
            axioms.addAll(newAxioms);
        }
        return changed;
    }

    @Override
    public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions() {
        return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLSubObjectPropertyOfAxiom> roleSubsumptions() {
        return roleSubsumptions;
    }

    /**
     * Obtains all the <i>EL<sub>&bot;</sub><sup>+</sup></i> role subsumptions
     * entailed by a given EL ontology.
     *
     * @param ontology an EL ontology <i>O</i>.
     * @return <i> {R&sqsube;S | R&sqsube;S &in; O} &cup; {R&sqsube;S,
     * S&sqsube;R | R&equiv;S &in; O}</i>.
     */
    private Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions(OWLOntology ontology) {
        final Set<OWLSubObjectPropertyOfAxiom> result = new HashSet<OWLSubObjectPropertyOfAxiom>(
                ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
        for (final OWLEquivalentObjectPropertiesAxiom axiom : ontology
                .getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
            result.addAll(axiom.asSubObjectPropertyOfAxioms());
        }
        return result;
    }

    /**
     * Creates an existential from a specified role and concept.
     *
     * @param r a role <i>R</i>.
     * @param c a concept <i>C</i>.
     * @return <i>&exist;R.C</i>.
     */
    private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression r, OWLClassExpression c) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectSomeValuesFrom(r, c);
    }

    private OWLDataSomeValuesFrom some(OWLDataPropertyExpression r, OWLDataRange c) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDataSomeValuesFrom(r, c);
    }

    /**
     * Creates a role chain subsumption from a given list of roles and a given
     * subsuming role.
     *
     * @param chain a list of roles <i>[R<sub>1</sub>, ..., R<sub>n</sub>]</i>
     * @param superRole ] the subsuming role <i>S</i>
     * @return <i>R<sub>1</sub>&SmallCircle; ...
     * &SmallCircle;R<sub>n</sub>&sqsube;S</i>
     */
    private OWLSubPropertyChainOfAxiom subsumption(List<OWLObjectPropertyExpression> chain,
            OWLObjectPropertyExpression superRole) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubPropertyChainOfAxiom(chain, superRole);
    }

    /**
     * Creates a concept subsumption from a given subsumed and subsuming
     * concept.
     *
     * @param c the subsumed concept <i>C</i>.
     * @param d the subsuming concept <i>D</i>.
     * @return <i>C&sqsube;D</i>.
     */
    private OWLSubClassOfAxiom subsumption(OWLClassExpression c, OWLClassExpression d) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(c, d);
    }

    /**
     * *
     * Returns the top concept <i>&top;</i>.
     *
     * @return <i>&top;</i>
     */
    private OWLClass top() {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
    }

    private OWLDatatype topDatatype() {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getTopDatatype();
    }
}

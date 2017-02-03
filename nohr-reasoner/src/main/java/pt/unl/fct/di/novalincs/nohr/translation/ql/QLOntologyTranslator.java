package pt.unl.fct.di.novalincs.nohr.translation.ql;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link OntologyTranslator} for the
 * {@link Profile#OWL2_QL QL} profile, according to
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
 * Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
public class QLOntologyTranslator extends OntologyTranslatorImpl {

    /**
     * The {@link QLAxiomsTranslator} that obtain the double rules of this
     * {@link OntologyTranslator}.
     */
    private final QLDoubleAxiomsTranslator doubleAxiomsTranslator;

    /**
     * The {@link TBoxDigraph digraph} of the TBox of the ontology of which this
     * {@link OntologyTranslator} is translation.
     */
    private TBoxDigraph graph;

    /**
     * The {@link QLOntologyNormalization normalization} of the ontology of
     * which this {@link OntologyTranslator} is translation.
     */
    private QLOntologyNormalization ontologyNormalization;

    /**
     * The {@link QLAxiomsTranslator} that obtain the original rules of this
     * {@link OntologyTranslator}.
     */
    private final QLOriginalAxiomsTranslator originalAxiomsTranslator;

    /**
     * Constructs an {@link OntologyTranslator} of a given OWL 2 QL ontology.
     *
     * @param ontology an OWL 2 QL ontology.
     * @throws UnsupportedAxiomsException if {@code ontology} contains some
     * axioms of unsupported types.
     */
    public QLOntologyTranslator(OWLOntology ontology, Vocabulary v, DeductiveDatabase dedutiveDatabase)
            throws UnsupportedAxiomsException {
        super(ontology, v, dedutiveDatabase);

        originalAxiomsTranslator = new QLOriginalAxiomsTranslator(v);
        doubleAxiomsTranslator = new QLDoubleAxiomsTranslator(v);

        prepareUpdate();
    }

    /**
     * Translate the negative axioms of the ontology that this ontology refers
     * with a given {@link QLAxiomsTranslator}.
     *
     * @return the translation of the negative axioms.
     */
    private Set<Rule> disjunctionsTranslation() {
        final Set<Rule> result = new HashSet<Rule>();
        for (final OWLDisjointClassesAxiom disjunction : ontologyNormalization.conceptDisjunctions()) {
            final List<OWLClassExpression> concepts = disjunction.getClassExpressionsAsList();
            assert concepts.size() <= 2;
            result.addAll(doubleAxiomsTranslator.disjunctionTranslation(concepts.get(0), concepts.get(1)));
        }
        for (final OWLDisjointObjectPropertiesAxiom disjunction : ontologyNormalization.roleDisjunctions()) {
            final List<OWLObjectPropertyExpression> roles = new LinkedList<OWLObjectPropertyExpression>(
                    disjunction.getProperties());
            assert roles.size() <= 2;
            result.addAll(doubleAxiomsTranslator.disjunctionTranslation(roles.get(0), roles.get(1)));
        }
        for (final OWLDisjointDataPropertiesAxiom disjunction : ontologyNormalization.dataDisjunctions()) {
            final List<OWLDataPropertyExpression> roles = new LinkedList<OWLDataPropertyExpression>(
                    disjunction.getProperties());
            assert roles.size() <= 2;
            result.addAll(doubleAxiomsTranslator.disjunctionTranslation(roles.get(0), roles.get(1)));
        }
        return result;
    }

    @Override
    public Profile getProfile() {
        return Profile.OWL2_QL;
    }

    @Override
    public boolean hasDisjunctions() {
        return ontologyNormalization.hasDisjunctions();
    }

    /**
     * Prepares the translation, creating a new {@link QLOntologyNormalization}
     * and {@link TBoxDigraph} for the current version of the ontology.
     */
    private void prepareUpdate() throws UnsupportedAxiomsException {
        RuntimesLogger.start("[OWL QL] ontology normalization");
        ontologyNormalization = new StaticQLOntologyNormalization(ontology, vocabulary);
        graph = new StaticTBoxDigraph(ontologyNormalization);
        RuntimesLogger.stop("[OWL QL] ontology normalization", "loading");
    }

    private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression ope) {
        final OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
        return df.getOWLObjectSomeValuesFrom(ope, df.getOWLThing());
    }

    /**
     * Translate the positive axioms (including assertions) of the ontology that
     * this ontology refers with a given {@link QLAxiomsTranslator}.
     *
     * @param axiomsTranslator the {@link QLAxiomsTranslator} that will be used.
     * @return the translation.
     */
    // TODO optimize translatin: (e) can be discarded for roles for which there
    // aren't assertions
    private Set<Rule> translation(QLAxiomsTranslator axiomsTranslator) {
        final Set<Rule> result = new HashSet<Rule>();
        for (final OWLClassAssertionAxiom assertion : ontologyNormalization.conceptAssertions()) {
            result.addAll(axiomsTranslator.assertionTranslation(assertion));
        }
        for (final OWLObjectPropertyAssertionAxiom assertion : ontologyNormalization.roleAssertions()) {
            result.addAll(axiomsTranslator.assertionTranslation(assertion));
        }
        for (final OWLDataPropertyAssertionAxiom assertion : ontologyNormalization.dataAssertions()) {
            result.addAll(axiomsTranslator.assertionTranslation(assertion));
        }
        for (final OWLSubClassOfAxiom subsumption : ontologyNormalization.conceptSubsumptions()) {
            result.addAll(
                    axiomsTranslator.subsumptionTranslation(subsumption.getSubClass(), subsumption.getSuperClass()));
        }
        for (final OWLSubPropertyAxiom<?> subsumption : ontologyNormalization.roleSubsumptions()) {
            if (subsumption instanceof OWLSubObjectPropertyOfAxiom) {
                result.addAll(axiomsTranslator.subsumptionTranslation(subsumption.getSubProperty(),
                        subsumption.getSuperProperty()));
                final OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom) subsumption;
                final OWLObjectPropertyExpression ope1 = axiom.getSubProperty();
                final OWLObjectPropertyExpression ope2 = axiom.getSuperProperty();
                final OWLObjectPropertyExpression invOpe1 = ope1.getInverseProperty().getSimplified();
                final OWLObjectPropertyExpression invOpe2 = ope2.getInverseProperty().getSimplified();
                if ((ontologyNormalization.isSuper(some(ope1)) || ontologyNormalization.isSuper(ope1))
                        && (ontologyNormalization.isSub(some(ope2)) || ontologyNormalization.isSub(ope2))) {
                    result.add(axiomsTranslator.domainSubsumptionTranslation(axiom.getSubProperty(),
                            axiom.getSuperProperty()));
                }
                if ((ontologyNormalization.isSuper(some(invOpe1)) || ontologyNormalization.isSuper(invOpe1))
                        && (ontologyNormalization.isSub(some(invOpe2)) || ontologyNormalization.isSub(invOpe2))) {
                    result.add(axiomsTranslator.rangeSubsumptionTranslation(invOpe1, invOpe2));
                }
            } else if (subsumption instanceof OWLSubDataPropertyOfAxiom) {
                result.addAll(axiomsTranslator.subsumptionTranslation(subsumption.getSubProperty(),
                        subsumption.getSuperProperty()));
            }
        }
        for (final OWLPropertyExpression ope : ontologyNormalization.getRoles()) {
            if (ope instanceof OWLObjectPropertyExpression) {
                final OWLObjectProperty p = ((OWLObjectPropertyExpression) ope).getNamedProperty();
                final OWLObjectPropertyExpression invP = p.getInverseProperty();
                if (ontologyNormalization.isSub(some(p)) || ontologyNormalization.isSub(p)) {
                    result.add(axiomsTranslator.domainTranslation(p));
                }
                if (ontologyNormalization.isSub(some(invP)) || ontologyNormalization.isSub(invP)) {
                    result.add(axiomsTranslator.rangeTranslation(p));
                }
            }
        }
        return result;
    }

    @Override
    public void updateTranslation() throws UnsupportedAxiomsException {
        prepareUpdate();
        translation.clear();

        RuntimesLogger.start("[OWL QL] ontology translation");

        final boolean hasDisjunctions = ontologyNormalization.hasDisjunctions();

        translation.addAll(translation(originalAxiomsTranslator));

        if (hasDisjunctions) {
            translation.addAll(translation(doubleAxiomsTranslator));
            translation.addAll(disjunctionsTranslation());

            RuntimesLogger.stop("[OWL QL] ontology translation", "loading");
            RuntimesLogger.start("[OWL QL (TBoxDigraph)] ontology inference");

            for (final OWLEntity e : graph.getUnsatisfiableEntities()) {
                if (e instanceof OWLClass) {
                    translation.addAll(doubleAxiomsTranslator.unsatisfiabilityTranslation((OWLClass) e));
                } else if (e instanceof OWLProperty) {
                    translation.addAll(doubleAxiomsTranslator.unsatisfiabilityTranslation((OWLProperty) e));
                }
            }

            for (final OWLObjectProperty p : graph.getIrreflexiveRoles()) {
                translation.add(doubleAxiomsTranslator.unreflexivityTranslation(p));
            }

            RuntimesLogger.stop("[OWL QL (TBoxDigraph)] ontology inference", "loading");
        } else {
            RuntimesLogger.stop("[OWL QL] ontology translation", "loading");
        }

    }

}

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.nohr.utils.BasicLazyGraphClosure;
import pt.unl.fct.di.novalincs.nohr.utils.GraphClosure;

/**
 * Implementation of {@link TBoxDigraph}. Note that <i>&Psi;(T)</i> and
 * <i>&Omega;(T)</i> are computed only when the corresponding methods are called
 * for the first time. This {@link TBoxDigraph} is static in the sense that can
 * represent only the TBox digraph of an ontology in a given moment; if the
 * ontology changes other {@link StaticTBoxDigraph} must be constructed.
 *
 * @author Nuno Costa
 */
public class StaticTBoxDigraph implements TBoxDigraph {

    /**
     * Returns the atomic concept or role with which a given DL-Lite<sub>R</sub>
     * basic concept is formed.
     *
     * @param b a basic concept <i>B</i>.
     * @return <i>A</i> if <i>B</i> is an atomic concept <i>A</i>; <br>
     * <i>P</i> if <i>B</i> is an existential <i>&exist;P</i> or
     * <i>&exist;P<sup>-</sup></i>.
     * @throws IllegalArgumentException if {@code b} isn't a basic concept.
     */
    private static OWLEntity atomic(OWLClassExpression b) {
        if (b instanceof OWLClass) {
            return (OWLClass) b;
        } else if (b instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom existential = (OWLObjectSomeValuesFrom) b;
            return existential.getProperty().getNamedProperty();
        } else {
            throw new IllegalArgumentException("b: must be an atomic concept or existential");
        }
    }

    /**
     * The {@link GraphClosure closure} of the concepts subgraph (i.e. the
     * subgraph containing only the vertices that represent concepts) of this
     * {@link TBoxDigraph}.
     */
    private final GraphClosure<OWLClassExpression> conceptsClosure;

    /**
     * The mapping between concept vertices and the set of their predecessor
     * {@link GraphClosure closure} in the concepts subgraph (i.e. the subgraph
     * containing only the vertices that represent concepts) of this
     * {@link TBoxDigraph}.
     */
    private final Map<OWLClassExpression, Set<OWLClassExpression>> conceptsPredecessors;

    /**
     * The set of irreflexive roles <i>&Psi;(T)</i> of the the TBox, <i>T</i>,
     * of which this {@link TBoxDigraph} is <i>digraph</i> (see <b>Definition
     * 7.</b> of {@link
     * <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}). May be empty if <i>&Pse;(T)</i> wasn't yet
     * been computed.
     */
    private Set<OWLObjectProperty> irreflexiveRoles;

    /**
     * The ontology of whose TBox this {@link TBoxDigraph} is <i>digraph</i>
     */
    private final QLOntologyNormalization ontology;

    /**
     * the {@link GraphClosure closure} of the roles subgraph (i.e. the subgraph
     * containing only the vertices that represent roles) of this
     * {@link TBoxDigraph}.
     */
    private final GraphClosure<OWLPropertyExpression> rolesClosure;

    /**
     * The mapping between roles vertices and the set of their predecessor
     * {@link GraphClosure closure} in the roles subgraph (i.e. the subgraph
     * containing only the vertices that represent roles) of this
     * {@link TBoxDigraph}.
     */
    private final Map<OWLPropertyExpression, Set<OWLPropertyExpression>> rolesPredecessor;

    /**
     * Computes a the set of irreflexive roles <i>&Omega;(T)</i> of the the
     * TBox, <i>T</i>, of which this {@link TBoxDigraph} is <i>digraph</i> (see
     * <b>Definition 8.</b> of {@link
     * <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}). May be empty, if <i>&Omega;(T)</i> wasn't
     * yet been computed.
     */
    private final Set<OWLEntity> unsatisfiableEntities;

    /**
     * Constructs a {@link TBoxDigraph} of the TBox <i>T</i> of a given
     * DL-Lite<sub>R</sub> ontology. Note that the construction only initialise
     * the needed data structures, and that the computation of <i>&Psi;(T)</i>
     * and <i>&Omega;(T)</i> occur only when the corresponding method are called
     * for the first time.
     *
     * @param ontology an DL-Lite<sub>R</sub> ontology.
     */
    public StaticTBoxDigraph(QLOntologyNormalization ontology) {
        this.ontology = ontology;
        conceptsPredecessors = new HashMap<OWLClassExpression, Set<OWLClassExpression>>();
        rolesPredecessor = new HashMap<OWLPropertyExpression, Set<OWLPropertyExpression>>();
        for (final OWLSubClassOfAxiom axiom : ontology.conceptSubsumptions()) {
            Set<OWLClassExpression> predecessors = conceptsPredecessors.get(axiom.getSuperClass());
            if (predecessors == null) {
                predecessors = new HashSet<OWLClassExpression>();
                conceptsPredecessors.put(axiom.getSuperClass(), predecessors);
            }
            predecessors.add(axiom.getSubClass());
        }
        for (final OWLSubPropertyAxiom<?> axiom : ontology.roleSubsumptions()) {
            Set<OWLPropertyExpression> predecessors = rolesPredecessor.get(axiom.getSuperProperty());
            if (predecessors == null) {
                predecessors = new HashSet<OWLPropertyExpression>();
                rolesPredecessor.put(axiom.getSuperProperty(), predecessors);
            }
            predecessors.add(axiom.getSubProperty());
        }
        conceptsClosure = new BasicLazyGraphClosure<OWLClassExpression>(conceptsPredecessors);
        rolesClosure = new BasicLazyGraphClosure<OWLPropertyExpression>(rolesPredecessor);
        irreflexiveRoles = null;
        unsatisfiableEntities = null;
    }

    private void accumulateUnsatisfiable(final Set<OWLPropertyExpression> unsatisfiableRoles,
            final OWLNaryPropertyAxiom<?> disjPropsAxiom) {
        final Object[] props = disjPropsAxiom.getProperties().toArray();
        for (int i = 0; i < props.length; i++) {
            final OWLPropertyExpression q1 = (OWLPropertyExpression) props[i];
            for (int j = 0; j < props.length; j++) {
                if (i != j) {
                    final OWLPropertyExpression q2 = (OWLPropertyExpression) props[j];
                    Set<OWLPropertyExpression> q1Ancs = getAncestors(q1);
                    Set<OWLPropertyExpression> q2Ancs = getAncestors(q2);
                    if (q1Ancs == null) {
                        q1Ancs = new HashSet<OWLPropertyExpression>();
                    }
                    if (q2Ancs == null) {
                        q2Ancs = new HashSet<OWLPropertyExpression>();
                    }
                    q1Ancs.add(q1);
                    q2Ancs.add(q2);
                    final Set<OWLPropertyExpression> intersection = intersection(q1Ancs, q2Ancs);
                    unsatisfiableRoles.addAll(intersection);
                }
            }
        }
    }

    private void acumulateIrreflexives(final OWLNaryPropertyAxiom<?> disjPropsAxiom) {
        final Object[] props = disjPropsAxiom.getProperties().toArray();
        for (int i = 0; i < props.length; i++) {
            final OWLPropertyExpression q1 = (OWLPropertyExpression) props[i];
            for (int j = 0; j < props.length; j++) {
                if (i != j) {
                    final OWLPropertyExpression q2 = (OWLPropertyExpression) props[j];
                    Set<OWLPropertyExpression> q1Ancs = getAncestors(q1);
                    Set<OWLPropertyExpression> q2Ancs = getAncestors(q2);
                    if (q1Ancs == null) {
                        q1Ancs = new HashSet<OWLPropertyExpression>();
                    }
                    if (q2Ancs == null) {
                        q2Ancs = new HashSet<OWLPropertyExpression>();
                    }
                    q1Ancs.add(q1);
                    q2Ancs.add(q2);
                    final Set<OWLObjectProperty> intersection = inverselyOccurringRoles2(q1Ancs, q2Ancs);
                    irreflexiveRoles.addAll(intersection);
                }
            }
        }
    }

    /**
     * Returns the set of atomic concept and roles obtained by applying
     * {@link StaticTBoxDigraph#atomic(OWLClassExpression)} and
     * {@link DLUtils#atomic(OWLPropertyExpression)} to two given sets of basic
     * concepts and basic roles.
     *
     * @param concepts a set of atomic roles.
     * @param roles a set of basic roles.
     * @return the set of atomic concept and roles obtained by applying
     * {@link StaticTBoxDigraph#atomic(OWLClassExpression)} and
     * {@link DLUtils#atomic(OWLPropertyExpression))} to {@code concepts} and
     * {@code roles}.
     * @throw IllegalArgumentException if some concept of {@code concepts} isn't
     * basic.
     */
    private Set<OWLEntity> atoms(Set<OWLClassExpression> concepts, Set<OWLPropertyExpression> roles) {
        final Set<OWLEntity> result = new HashSet<OWLEntity>();
        for (final OWLClassExpression c : concepts) {
            result.add(StaticTBoxDigraph.atomic(c));
        }
        for (final OWLPropertyExpression r : roles) {
            result.add(DLUtils.atomic(r));
        }
        return result;
    }

    @Override
    public Set<OWLClassExpression> getAncestors(OWLClassExpression v) {
        return conceptsClosure.getAncestors(v);
    }

    @Override
    public Set<OWLPropertyExpression> getAncestors(OWLPropertyExpression v) {
        return rolesClosure.getAncestors(v);
    }

    @Override
    public Set<OWLObjectProperty> getIrreflexiveRoles() {
        if (irreflexiveRoles != null) {
            return irreflexiveRoles;
        }
        
        irreflexiveRoles = new HashSet<OWLObjectProperty>();
        
        for (final OWLDisjointClassesAxiom axiom : ontology.conceptDisjunctions()) {
            for (final OWLDisjointClassesAxiom disjoint : axiom.asPairwiseAxioms()) {
                final List<OWLClassExpression> concepts = disjoint.getClassExpressionsAsList();
                final OWLClassExpression c1 = concepts.get(0);
                final OWLClassExpression c2 = concepts.get(1);
                Set<OWLClassExpression> c1Ancs = getAncestors(c1);
                Set<OWLClassExpression> c2Ancs = getAncestors(c2);
                if (c1Ancs == null) {
                    c1Ancs = new HashSet<OWLClassExpression>();
                }
                if (c2Ancs == null) {
                    c2Ancs = new HashSet<OWLClassExpression>();
                }
                c1Ancs.add(c1);
                c2Ancs.add(c2);
                final Set<OWLObjectProperty> intersection = inverselyOccurringRoles1(c1Ancs, c2Ancs);
                irreflexiveRoles.addAll(intersection);
            }
        }
        
        for (final OWLDisjointObjectPropertiesAxiom disjPropsAxiom : ontology.roleDisjunctions()) {
            acumulateIrreflexives(disjPropsAxiom);
        }
        
        for (final OWLDisjointDataPropertiesAxiom disjPropsAxiom : ontology.dataDisjunctions()) {
            acumulateIrreflexives(disjPropsAxiom);
        }
        
        for (final OWLIrreflexiveObjectPropertyAxiom axiom : ontology.getOntology().getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
            irreflexiveRoles.add(axiom.getProperty().asOWLObjectProperty());
        }
        
        return irreflexiveRoles;
    }

    @Override
    public Set<OWLClassExpression> getPredecessors(OWLClassExpression v) {
        return conceptsPredecessors.get(v);
    }

    @Override
    public Set<OWLPropertyExpression> getPredecessors(OWLObjectPropertyExpression v) {
        return rolesPredecessor.get(v);
    }

    @Override
    public Set<OWLEntity> getUnsatisfiableEntities() {
        if (unsatisfiableEntities != null) {
            return unsatisfiableEntities;
        }
        final Set<OWLClassExpression> unsatisfiableConcepts = new HashSet<OWLClassExpression>();
        for (final OWLDisjointClassesAxiom axiom : ontology.conceptDisjunctions()) {
            for (final OWLDisjointClassesAxiom disjoint : axiom.asPairwiseAxioms()) {
                final List<OWLClassExpression> concepts = disjoint.getClassExpressionsAsList();
                final OWLClassExpression c1 = concepts.get(0);
                final OWLClassExpression c2 = concepts.get(1);
                Set<OWLClassExpression> c1Ancs = getAncestors(c1);
                Set<OWLClassExpression> c2Ancs = getAncestors(c2);
                if (c1Ancs == null) {
                    c1Ancs = new HashSet<OWLClassExpression>();
                }
                if (c2Ancs == null) {
                    c2Ancs = new HashSet<OWLClassExpression>();
                }
                c1Ancs.add(c1);
                c2Ancs.add(c2);
                final Set<OWLClassExpression> intersection = intersection(c1Ancs, c2Ancs);
                unsatisfiableConcepts.addAll(intersection);
            }
        }
        for (final OWLClassExpression c : ontology.getUnsatisfiableConcepts()) {
            Set<OWLClassExpression> cAncs = getAncestors(c);
            if (cAncs == null) {
                cAncs = new HashSet<OWLClassExpression>();
            }
            cAncs.add(c);
            unsatisfiableConcepts.addAll(cAncs);
        }
        final Set<OWLPropertyExpression> unsatisfiableRoles = new HashSet<OWLPropertyExpression>();
        for (final OWLDisjointObjectPropertiesAxiom disjPropsAxiom : ontology.roleDisjunctions()) {
            accumulateUnsatisfiable(unsatisfiableRoles, disjPropsAxiom);
        }
        for (final OWLDisjointDataPropertiesAxiom disjPropsAxiom : ontology.dataDisjunctions()) {
            accumulateUnsatisfiable(unsatisfiableRoles, disjPropsAxiom);
        }
        for (final OWLPropertyExpression q : ontology.getUnsatisfiableRoles()) {
            Set<OWLPropertyExpression> qAncs = getAncestors(q);
            if (qAncs == null) {
                qAncs = new HashSet<OWLPropertyExpression>();
            }
            qAncs.add(q);
            unsatisfiableRoles.addAll(qAncs);
        }
        return atoms(unsatisfiableConcepts, unsatisfiableRoles);
    }

    /**
     * Obtains the intersection of two given sets.
     *
     * @param s1 a set <i>S<sub>1</sub>.
     * @param s2 a set <i>S<sub>2</sub>.
     * @param <E> the type of the elements of {@code s1} and {@code s2}.
     * @return <i>S<sub>1</sub> &cap; S<sub>2</sub></i>.
     */
    private <E> Set<E> intersection(Set<E> s1, Set<E> s2) {
        if (s1.size() >= s2.size()) {
            s1.retainAll(s2);
            return s1;
        } else {
            s2.retainAll(s1);
            return s2;
        }
    }

    /**
     * Obtains the the sets of the atomic roles that occurs in an existential of
     * one given basic concepts set and whose inverse occurs in an existential
     * of another basic concepts set.
     *
     * @param s1 a set of basic concepts <i>S<sub>1</sub></i>.
     * @param s2 a set of basic concepts <i>S<sub>2</sub></i>.
     * @return <i> {P | &exist;P &in; S<sub>1</sub> and &exist;P<sup>-</sup>
     * &in; S<sub>2</sub> } &cup; <i> {P | &exist;P<sup>-</sup> &in; S
     * <sub>1</sub> and &exist;P &in; S<sub>2</sub> } </i>
     */
    private Set<OWLObjectProperty> inverselyOccurringRoles1(Set<OWLClassExpression> s1, Set<OWLClassExpression> s2) {
        final Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
        for (final OWLClassExpression v : s1) {
            if (v instanceof OWLObjectSomeValuesFrom) {
                final OWLObjectSomeValuesFrom exist = (OWLObjectSomeValuesFrom) v;
                final OWLObjectPropertyExpression prop = exist.getProperty();
                final OWLObjectPropertyExpression invProp = prop.getInverseProperty();
                final OWLDataFactory dataFactory = ontology.getOntology().getOWLOntologyManager().getOWLDataFactory();
                final OWLObjectSomeValuesFrom invExist = dataFactory.getOWLObjectSomeValuesFrom(invProp,
                        dataFactory.getOWLThing());
                if (s2.contains(invExist)) {
                    result.add(prop.getNamedProperty());
                }
            }
        }
        return result;
    }

    /**
     * Obtains the the sets of the atomic roles of one given basic roles set
     * whose inverse belongs to another basic roles set.
     *
     * @param s1 a set of basic roles <i>S<sub>1</sub></i>.
     * @param s2 a set of basic roles <i>S<sub>2</sub></i>.
     * @return <i> {P | P &in; S<sub>1</sub> and P<sup>-</sup> &in;
     * S<sub>2</sub> } &cup; <i> {P | P<sup>-</sup> &in; S <sub>1</sub> and P
     * &in; S
     * <sub>2</sub> } </i>
     */
    private Set<OWLObjectProperty> inverselyOccurringRoles2(Set<OWLPropertyExpression> s1,
            Set<OWLPropertyExpression> s2) {
        final Set<OWLObjectProperty> result = new HashSet<>();
        for (final OWLPropertyExpression v : s1) {
            if (v instanceof OWLObjectPropertyExpression) {
                final OWLObjectPropertyExpression vo = (OWLObjectPropertyExpression) v;
                if (s2.contains(vo.getInverseProperty())) {
                    result.add(vo.getNamedProperty());
                }
            }
        }
        return result;
    }

}

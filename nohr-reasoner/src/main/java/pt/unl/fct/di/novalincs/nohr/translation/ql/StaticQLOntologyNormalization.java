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
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;

/**
 * The implementation of {@link QLOntologyNormalization}. This
 * {@link QLOntologyNormalization} is static in the sense that can represent
 * only the normalization of an ontology in a given moment; if the ontology
 * changes other {@link StaticQLOntologyNormalization} must be constructed.
 *
 * @author Nuno Costa
 */
public class StaticQLOntologyNormalization implements QLOntologyNormalization {

    private static final Logger log = Logger.getLogger(StaticQLOntologyNormalization.class);

    /**
     * The set of DL-Lite<sub>R</sub> concept disjunctions in this
     * {@link QLOntologyNormalization}
     */
    private final Set<OWLDisjointClassesAxiom> conceptDisjunctions;

    /**
     * The set of DL-Lite<sub>R</sub> concept subsumptions in this
     * {@link QLOntologyNormalization}
     */
    private final Set<OWLSubClassOfAxiom> conceptSubsumptions;

    /**
     * The ontology of which this {@link QLOntologyTranslator} is normalization.
     */
    private final OWLOntology ontology;

    /**
     * The set of DL-Lite<sub>R</sub> role disjunctions in this
     * {@link QLOntologyNormalization}.
     */
    private final Set<OWLDisjointObjectPropertiesAxiom> roleDisjunctions;

    /**
     * The set of DL-Lite<sub>R</sub> (data) role disjunctions in this
     * {@link QLOntologyNormalization}.
     */
    private final Set<OWLDisjointDataPropertiesAxiom> dataDisjunctions;

    /**
     * The set of DL-Lite<sub>R</sub> role subsumptions in this
     * {@link QLOntologyNormalization}.
     */
    private final Set<OWLSubPropertyAxiom<?>> roleSubsumptions;

    /**
     * The set of DL-Lite<sub>R</sub> concepts occurring as subsumed concept in
     * some subsumption in this {@link QLOntologyNormalization}.
     */
    private final Set<OWLClassExpression> subConcepts;

    /**
     * The set of DL-Lite<sub>R</sub> roles occurring as subsumed roles in some
     * subsumption in this {@link QLOntologyNormalization}.
     */
    private final Set<OWLProperty> subRoles;

    /**
     * The set of DL-Lite<sub>R</sub> concepts occurring as subsuming concept in
     * some subsumption in this {@link QLOntologyNormalization}.
     */
    private final Set<OWLClassExpression> superConcepts;

    /**
     * The set of DL-Lite<sub>R</sub> roles occurring as subsuming role in some
     * subsumption in this {@link QLOntologyNormalization}.
     */
    private final Set<OWLProperty> superRoles;

    /**
     * The set of DL-Lite<sub>R</sub> concepts <i>B</i> that occur in some axiom
     * <i>B&sqsube;&bot;</i>,<i>B&sqsube;&not;&top;</i>,
     * <i>&top;&sqsube;&not;B</i> or </i> B&sqsube;&not;B</i>
     * {@link QLOntologyNormalization}.
     */
    private final Set<OWLClassExpression> unsatisfiableConcepts;

    /**
     * The set of DL-Lite<sub>R</sub> roles <i>Q</i> that occur in some axiom
     * <i>Q&sqsube;&bot;</i>,<i>Q&sqsube;&not;&top;</i>,
     * <i>&top;&sqsube;&not;Q</i> or </i>Q&sqsube;&not;Q</i>
     * {@link QLOntologyNormalization}.
     */
    private final Set<OWLPropertyExpression> unsatisfiableRoles;

    private final Vocabulary vocabulary;

    /**
     * Constructs a {@link QLOntologyNormalization} from a given OWL 2 QL
     * ontology according to <b>Appendix D</b> of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}.
     *
     * @param ontology an OWL 2 QL ontology.
     * @throws UnsupportedAxiomsException if {@code ontology} has some axiom of
     * an unsupported type (i.e. that aren't in {@link #SUPPORTED_AXIOM_TYPES}).
     */
    public StaticQLOntologyNormalization(OWLOntology ontology, Vocabulary vocabulary)
            throws UnsupportedAxiomsException {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(vocabulary);

        this.ontology = ontology;
        this.vocabulary = vocabulary;
        conceptSubsumptions = new HashSet<OWLSubClassOfAxiom>();
        roleSubsumptions = new HashSet<OWLSubPropertyAxiom<?>>();
        conceptDisjunctions = new HashSet<OWLDisjointClassesAxiom>();
        roleDisjunctions = new HashSet<OWLDisjointObjectPropertiesAxiom>();
        dataDisjunctions = new HashSet<OWLDisjointDataPropertiesAxiom>();
        unsatisfiableConcepts = new HashSet<OWLClassExpression>();
        unsatisfiableRoles = new HashSet<OWLPropertyExpression>();
        subConcepts = new HashSet<OWLClassExpression>();
        superConcepts = new HashSet<OWLClassExpression>();
        subRoles = new HashSet<OWLProperty>();
        superRoles = new HashSet<OWLProperty>();
        normalize(ontology);
    }

    @Override
    public Iterable<OWLClassAssertionAxiom> conceptAssertions() {
        return ontology.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointClassesAxiom> conceptDisjunctions() {
        return conceptDisjunctions;
    }

    @Override
    public Iterable<OWLSubClassOfAxiom> conceptSubsumptions() {
        return conceptSubsumptions;
    }

    @Override
    public Iterable<OWLDataPropertyAssertionAxiom> dataAssertions() {
        return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointDataPropertiesAxiom> dataDisjunctions() {
        return dataDisjunctions;
    }

    private OWLDisjointObjectPropertiesAxiom disjunction(OWLObjectPropertyExpression q1,
            OWLObjectPropertyExpression q2) {
        return getDataFactory().getOWLDisjointObjectPropertiesAxiom(q1, q2);
    }

    private OWLDataFactory getDataFactory() {
        return ontology.getOWLOntologyManager().getOWLDataFactory();
    }

    @Override
    public OWLOntology getOntology() {
        return ontology;
    }

    @Override
    public Set<OWLObjectProperty> getRoles() {
        return ontology.getObjectPropertiesInSignature();
    }

    @Override
    public Set<OWLClassExpression> getSubConcepts() {
        return subConcepts;
    }

    @Override
    public Set<OWLProperty> getSubRoles() {
        return subRoles;
    }

    @Override
    public Set<OWLClassExpression> getUnsatisfiableConcepts() {
        return unsatisfiableConcepts;
    }

    @Override
    public Set<OWLPropertyExpression> getUnsatisfiableRoles() {
        return unsatisfiableRoles;
    }

    @Override
    public boolean hasDisjunctions() {
        return !(conceptDisjunctions.isEmpty() && roleDisjunctions.isEmpty() && dataDisjunctions.isEmpty()
                && unsatisfiableConcepts.isEmpty() && unsatisfiableRoles.isEmpty() && ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY) == 0);
    }

    @Override
    public boolean isSub(OWLClassExpression ce) {
        return subConcepts.contains(ce);
    }

    @Override
    public boolean isSub(OWLPropertyExpression pe) {
        return subRoles.contains(pe);
    }

    @Override
    public boolean isSuper(OWLClassExpression ce) {
        return superConcepts.contains(ce);
    }

    @Override
    public boolean isSuper(OWLPropertyExpression pe) {
        return superRoles.contains(pe);
    }

    /**
     * Normalize a given asymmetry axiom according to <b>Appendix D</b> of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}. Given an asymmetry axiom
     * <b>asymmetric</b><i>(P)</i> adds <i>P&sqsube;&not;P<sup>-</sup></i> to
     * {@link #roleDisjunctions}.
     *
     * @param axiom the asymmetry axiom <b>asymmetric</b><i>(P)</i>.
     */
    private void normalize(OWLAsymmetricObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression q = axiom.getProperty();
        roleDisjunctions.add(disjunction(q, q.getInverseProperty()));
    }

    /**
     * Normalize a given range axiom according to <b>Appendix D</b> of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}. Given a range axiom <b>range</b><i>(Q,
     * B)</i> adds <i>&exist;P<sup>-</sup>&sqsube;B</i> to
     * {@link #conceptSubsumptions}.
     *
     * @param axiom the range axiom <b>range</b><i>(Q, B)</i>.
     */
    private void normalize(OWLObjectPropertyRangeAxiom axiom) {
        final OWLObjectPropertyExpression q = axiom.getProperty();
        final OWLClassExpression c = axiom.getRange();
        normalize(getDataFactory().getOWLSubClassOfAxiom(some(q.getInverseProperty()), c));
    }

    /**
     * Normalize a given OWL 2 QL ontology according to <b>Appendix D</b> of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}, adding the obtained axioms to the
     * appropriate fields.
     *
     * @param ontology a OWL 2 QL ontology.
     */
    private void normalize(OWLOntology ontology) {
        for (final OWLSubClassOfAxiom a : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            normalize(a);
        }
        for (final OWLSubObjectPropertyOfAxiom a : ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
            normalize(a);
        }
        for (final OWLDisjointObjectPropertiesAxiom a : ontology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
            normalizeDisjunction(a);
        }
        for (final OWLEquivalentClassesAxiom a : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
            for (final OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms()) {
                normalize(s);
            }
        }
        for (final OWLDisjointClassesAxiom a : ontology.getAxioms(AxiomType.DISJOINT_CLASSES)) {
            for (final OWLSubClassOfAxiom s : a.asOWLSubClassOfAxioms()) {
                normalize(s);
            }
        }
        for (final OWLInverseObjectPropertiesAxiom a : ontology.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
            for (final OWLSubObjectPropertyOfAxiom s : a.asSubObjectPropertyOfAxioms()) {
                normalize(s);
            }
        }
        for (final OWLEquivalentObjectPropertiesAxiom a : ontology.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
            for (final OWLSubObjectPropertyOfAxiom s : a.asSubObjectPropertyOfAxioms()) {
                normalize(s);
            }
        }
        for (final OWLObjectPropertyDomainAxiom a : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            normalize(a.asOWLSubClassOfAxiom());
        }
        for (final OWLObjectPropertyRangeAxiom a : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
            normalize(a);
        }
        for (final OWLSymmetricObjectPropertyAxiom a : ontology.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
            for (final OWLSubObjectPropertyOfAxiom s : a.asSubPropertyAxioms()) {
                normalize(s);
            }
        }
        for (final OWLAsymmetricObjectPropertyAxiom a : ontology.getAxioms(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)) {
            normalize(a);
        }
        for (final OWLSubDataPropertyOfAxiom a : ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY)) {
            normalize(a);
        }
        for (final OWLDisjointDataPropertiesAxiom a : ontology.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES)) {
            normalizeDisjunction(a);
        }
        for (final OWLEquivalentDataPropertiesAxiom a : ontology.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
            for (final OWLSubDataPropertyOfAxiom s : a.asSubDataPropertyOfAxioms()) {
                normalize(s);
            }
        }
    }

    /**
     * Normalize a given concept subsumption axiom according to <b>Appendix
     * D</b> of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}, adding the obtained axioms to the
     * appropriate fields.
     *
     * @param axiom a OWL 2 QL concept subsumption.
     */
    private void normalize(OWLSubClassOfAxiom axiom) {
        final OWLClassExpression b = axiom.getSubClass();
        final OWLClassExpression c = axiom.getSuperClass();
        if (b.isOWLNothing() || c.isOWLThing()) {
            return;
        }
        if (b instanceof OWLDataSomeValuesFrom || c instanceof OWLDataSomeValuesFrom) {
            return;
        }
        if (c.isOWLNothing()) {
            unsatisfiableConcepts.add(b);
        } else if (c instanceof OWLClass) { // BASE CASE
            subConcepts.add(b);
            superConcepts.add(c);
            conceptSubsumptions.add(axiom);
        } else if (c instanceof OWLObjectIntersectionOf) {
            final OWLObjectIntersectionOf c0 = (OWLObjectIntersectionOf) c;
            final Set<OWLClassExpression> ops = c0.getOperands();
            for (final OWLClassExpression ci : ops) {
                normalize(getDataFactory().getOWLSubClassOfAxiom(b, ci));
            }
        } else if (c instanceof OWLObjectComplementOf) { // BASE CASE
            final OWLObjectComplementOf c0 = (OWLObjectComplementOf) c;
            final OWLClassExpression b1 = c0.getOperand();
            subConcepts.add(b);
            subConcepts.add(b1);
            if (b1.isOWLNothing()) {
                return;
            }
            if (b1.isOWLThing()) {
                unsatisfiableConcepts.add(b);
            } else if (b.isOWLThing()) {
                unsatisfiableConcepts.add(b1);
            } else {
                conceptDisjunctions.add(getDataFactory().getOWLDisjointClassesAxiom(b, b1));
            }
        } else if (c instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom b0 = (OWLObjectSomeValuesFrom) c;
            final OWLObjectPropertyExpression q = b0.getProperty();
            final OWLClassExpression a = b0.getFiller();
            if (a.isOWLThing()) {// BASE CASE
                subConcepts.add(b);
                superConcepts.add(c);
                conceptSubsumptions.add(getDataFactory().getOWLSubClassOfAxiom(b, c));
            } else {
                final OWLObjectProperty pnew = vocabulary.generateNewRole();
                normalize(getDataFactory().getOWLSubObjectPropertyOfAxiom(pnew, q));
                normalize(getDataFactory().getOWLSubClassOfAxiom(some(pnew.getInverseProperty()), a));
                normalize(getDataFactory().getOWLSubClassOfAxiom(b, some(pnew)));
            }
        }
    }

    /**
     * Normalize a given role subsumption axiom according to <b>Appendix D</b>
     * of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}, adding the obtained axioms to the
     * appropriate fields.
     *
     * @param alpha a OWL 2 QL role subsumption.
     */
    private void normalize(OWLSubPropertyAxiom<?> alpha) {
        final OWLPropertyExpression q1 = alpha.getSubProperty();
        final OWLPropertyExpression q2 = alpha.getSuperProperty();
        subRoles.add(DLUtils.atomic(q1));
        superRoles.add(DLUtils.atomic(q2));
        if (q1.isBottomEntity() || q2.isTopEntity()) {
            return;
        }
        if (q2.isBottomEntity()) {
            unsatisfiableRoles.add(q1);
        } else if (q1 instanceof OWLObjectPropertyExpression && q2 instanceof OWLObjectPropertyExpression) {
            roleSubsumptions.add(getDataFactory().getOWLSubObjectPropertyOfAxiom((OWLObjectPropertyExpression) q1,
                    (OWLObjectPropertyExpression) q2));
        } else if (q1 instanceof OWLDataPropertyExpression && q2 instanceof OWLDataPropertyExpression) {
            roleSubsumptions.add(getDataFactory().getOWLSubDataPropertyOfAxiom((OWLDataPropertyExpression) q1,
                    (OWLDataPropertyExpression) q2));
        }
    }

    /**
     * Normalize a given role disjunction axiom according to <b>Appendix D</b>
     * of
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}, adding the obtained axioms to the
     * appropriate fields.
     *
     * @param alpha a OWL 2 QL role disjunction.
     */
    private <P extends OWLPropertyExpression> void normalizeDisjunction(OWLNaryPropertyAxiom<P> alpha) {
        final Set<P> props = alpha.getProperties();
        final Iterator<P> propsIt1 = props.iterator();
        while (propsIt1.hasNext()) {
            final OWLPropertyExpression q1 = propsIt1.next();
            final OWLProperty p = DLUtils.atomic(q1);
            subRoles.add(p);
            if (q1.isBottomEntity()) {
                continue;
            }
            final Iterator<P> propsIt2 = props.iterator();
            while (propsIt2.hasNext()) {
                final OWLPropertyExpression q2 = propsIt2.next();
                if (q2.isBottomEntity()) {
                    continue;
                }
                if (!q1.equals(q2)) {
                    if (q1.isTopEntity()) {
                        unsatisfiableRoles.add(q2);
                    } else if (q2.isTopEntity()) {
                        unsatisfiableRoles.add(q2);
                    } else if (q1 instanceof OWLObjectPropertyExpression && q2 instanceof OWLObjectPropertyExpression) {
                        roleDisjunctions.add(getDataFactory().getOWLDisjointObjectPropertiesAxiom(
                                (OWLObjectPropertyExpression) q1, (OWLObjectPropertyExpression) q2));
                    } else if (q1 instanceof OWLDataPropertyExpression && q2 instanceof OWLDataPropertyExpression) {
                        dataDisjunctions.add(getDataFactory().getOWLDisjointDataPropertiesAxiom(
                                (OWLDataPropertyExpression) q1, (OWLDataPropertyExpression) q2));
                    }
                }
            }
        }

    }

    @Override
    public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions() {
        return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointObjectPropertiesAxiom> roleDisjunctions() {
        return roleDisjunctions;
    }

    @Override
    public Iterable<OWLSubPropertyAxiom<?>> roleSubsumptions() {
        return roleSubsumptions;
    }

    /**
     * Returns the unqualified existential quantification of a given role.
     *
     * @param q a role <i>Q</i>
     * @return <i>&exist;Q</i>.
     */
    private OWLClassExpression some(OWLObjectPropertyExpression q) {
        return getDataFactory().getOWLObjectSomeValuesFrom(q, getDataFactory().getOWLThing());
    }

}

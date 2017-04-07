package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLOntologyNormalizationImpl;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ComplexSidesNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ConceptAssertionsNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.GraphNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftBottomNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftConjunctionNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftExistentialNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.Normalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.RightConjunctionNormalizer;

public class RLOntologyNormalizationImpl implements RLOntologyNormalization {

    private static final Logger LOG = Logger.getLogger(DLOntologyNormalizationImpl.class);
    public static final AxiomType<?>[] SUPPORTED_AXIOM_TYPES = new AxiomType<?>[]{
        AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
        AxiomType.CLASS_ASSERTION,
        AxiomType.DATA_PROPERTY_ASSERTION,
        AxiomType.DECLARATION,
        AxiomType.DISJOINT_CLASSES,
        AxiomType.DISJOINT_DATA_PROPERTIES,
        AxiomType.DISJOINT_OBJECT_PROPERTIES,
        AxiomType.EQUIVALENT_CLASSES,
        AxiomType.EQUIVALENT_DATA_PROPERTIES,
        AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
        //        AxiomType.FUNCTIONAL_DATA_PROPERTY,
        //        AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
        AxiomType.INVERSE_OBJECT_PROPERTIES,
        AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
        AxiomType.OBJECT_PROPERTY_ASSERTION,
        AxiomType.OBJECT_PROPERTY_DOMAIN,
        AxiomType.OBJECT_PROPERTY_RANGE,
        AxiomType.SUB_DATA_PROPERTY,
        AxiomType.SUB_OBJECT_PROPERTY,
        AxiomType.SUB_PROPERTY_CHAIN_OF,
        AxiomType.SYMMETRIC_OBJECT_PROPERTY,
        AxiomType.SUBCLASS_OF,
        AxiomType.TRANSITIVE_OBJECT_PROPERTY
    };

    private final OWLOntology normalizedOntology;
    private final boolean hasDisjunctions;
    private final OWLOntology ontology;
    private final Vocabulary vocabulary;

    public RLOntologyNormalizationImpl(OWLOntology ontology, Vocabulary vocabulary) throws UnsupportedAxiomsException {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(vocabulary);

        this.ontology = ontology;
        this.vocabulary = vocabulary;

        final String ignoreUnsupported = System.getenv("IGNORE_UNSUPPORTED");

        if (ignoreUnsupported == null || !ignoreUnsupported.equals("true")) {
            LOG.info("checking axioms support");

            @SuppressWarnings("unchecked")
            final Set<OWLAxiom> unsupportedAxioms = AxiomType.getAxiomsWithoutTypes((Set<OWLAxiom>) (Set<? extends OWLAxiom>) ontology.getLogicalAxioms(), SUPPORTED_AXIOM_TYPES);

            if (unsupportedAxioms.size() > 0) {
                LOG.error("unsupported axioms " + unsupportedAxioms);

                throw new UnsupportedAxiomsException(unsupportedAxioms);
            }
        }

        normalizedOntology = normalize(ontology);

        int negAssertions = ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
        negAssertions += ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION);

        hasDisjunctions = DLUtils.hasDisjunctions(normalizedOntology) || negAssertions > 0;
    }

    @Override
    public Iterable<OWLClassAssertionAxiom> conceptAssertions() {
        return normalizedOntology.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    private Set<OWLClassAssertionAxiom> conceptAssertions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    @Override
    public Iterable<OWLSubClassOfAxiom> conceptSubsumptions() {
        return normalizedOntology.getAxioms(AxiomType.SUBCLASS_OF);
    }

    private Set<OWLSubClassOfAxiom> conceptSubsumptions(OWLOntology ontology) {
        final Set<OWLSubClassOfAxiom> axioms = new HashSet<>();

        for (OWLSubClassOfAxiom i : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            axioms.add(i);
        }

        for (final OWLEquivalentClassesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
            axioms.addAll(axiom.asOWLSubClassOfAxioms());
        }

        for (final OWLDisjointClassesAxiom axiom : ontology.getAxioms(AxiomType.DISJOINT_CLASSES)) {
            for (final OWLDisjointClassesAxiom i : axiom.asPairwiseAxioms()) {
                axioms.add(DLUtils.subsumption(ontology, DLUtils.conjunction(ontology, i.getClassExpressions()), DLUtils.bottom(ontology)));
            }
        }

        for (final OWLObjectPropertyDomainAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            final OWLObjectPropertyExpression p = axiom.getProperty();
            final OWLClassExpression c = axiom.getDomain();

            axioms.add(DLUtils.subsumption(ontology, DLUtils.some(ontology, p, DLUtils.top(ontology)), c));
        }

        for (final OWLObjectPropertyRangeAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
            final OWLObjectPropertyExpression p = axiom.getProperty();
            final OWLClassExpression c = axiom.getRange();

            axioms.add(DLUtils.subsumption(ontology, DLUtils.some(ontology, p.getInverseProperty()), c));
        }

        return axioms;
    }

    private void normalize(Set<OWLSubClassOfAxiom> axioms) {
        normalize(axioms, new GraphNormalizer(ontology));
        boolean changed = normalize(axioms, new LeftConjunctionNormalizer(ontology, vocabulary));

        changed = normalize(axioms, new LeftExistentialNormalizer(ontology, vocabulary)) || changed;

        while (changed) {
            changed = normalize(axioms, new LeftConjunctionNormalizer(ontology, vocabulary));

            if (changed) {
                changed = normalize(axioms, new LeftExistentialNormalizer(ontology, vocabulary)) || changed;
            }
        }

        normalize(axioms, new LeftBottomNormalizer());
        normalize(axioms, new ComplexSidesNormalizer(ontology, vocabulary));
        normalize(axioms, new RightConjunctionNormalizer(ontology));
    }

    private <T extends OWLAxiom> boolean normalize(Set<T> axioms, Normalizer<T> normalizer) {
        boolean changed = false;
        final Iterator<T> axiomsIt = axioms.iterator();
        final Set<T> newAxioms = new HashSet<>();

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

    private OWLOntology normalize(OWLOntology ontology) {
        final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

        try {
            final OWLOntology owlo = ontologyManager.createOntology(ontology.getOntologyID());

            final Set<OWLClassAssertionAxiom> conceptAssertions = conceptAssertions(ontology);
            final Set<OWLSubClassOfAxiom> conceptSubsumptions = conceptSubsumptions(ontology);

            normalize(conceptAssertions, new ConceptAssertionsNormalizer(ontology));
            normalize(conceptSubsumptions);

            ontologyManager.addAxioms(owlo, conceptAssertions);
            ontologyManager.addAxioms(owlo, conceptSubsumptions);

            ontologyManager.addAxioms(owlo, dataAssertions(ontology));
            ontologyManager.addAxioms(owlo, dataDisjunctions(ontology));
            ontologyManager.addAxioms(owlo, dataSubsumptions(ontology));
            ontologyManager.addAxioms(owlo, irreflexiveRoles(ontology));
            ontologyManager.addAxioms(owlo, roleAssertions(ontology));
            ontologyManager.addAxioms(owlo, roleDisjunctions(ontology));
            ontologyManager.addAxioms(owlo, roleSubsumptions(ontology));

            return owlo;
        } catch (final OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<OWLDisjointClassesAxiom> conceptDisjunctions() {
        return normalizedOntology.getAxioms(AxiomType.DISJOINT_CLASSES);
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> dataAssertions() {
        return normalizedOntology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    private Set<OWLDataPropertyAssertionAxiom> dataAssertions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLDisjointDataPropertiesAxiom> dataDisjunctions() {
        return normalizedOntology.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES);
    }

    private Set<OWLDisjointDataPropertiesAxiom> dataDisjunctions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES);
    }

    @Override
    public Iterable<OWLSubDataPropertyOfAxiom> dataSubsumptions() {
        return normalizedOntology.getAxioms(AxiomType.SUB_DATA_PROPERTY);
    }

    private Set<OWLSubDataPropertyOfAxiom> dataSubsumptions(OWLOntology ontology) {
        final Set<OWLSubDataPropertyOfAxiom> ret = new HashSet<>(ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY));

        for (final OWLEquivalentDataPropertiesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
            ret.addAll(axiom.asSubDataPropertyOfAxioms());
        }

        return ret;
    }

    @Override
    public boolean hasDisjunctions() {
        return hasDisjunctions;
    }

    @Override
    public Set<OWLIrreflexiveObjectPropertyAxiom> irreflexiveRoles() {
        return normalizedOntology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
    }

    private Set<OWLIrreflexiveObjectPropertyAxiom> irreflexiveRoles(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> roleAssertions() {
        return normalizedOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    private Set<OWLObjectPropertyAssertionAxiom> roleAssertions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Set<OWLDisjointObjectPropertiesAxiom> roleDisjunctions() {
        return normalizedOntology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES);
    }

    private Set<OWLDisjointObjectPropertiesAxiom> roleDisjunctions(OWLOntology ontology) {
        Set<OWLDisjointObjectPropertiesAxiom> disjointObjectPropertiesAxioms = ontology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES);

        for (final OWLAsymmetricObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)) {
            disjointObjectPropertiesAxioms.add(DLUtils.disjunction(ontology, axiom.getProperty(), axiom.getProperty().getInverseProperty()));
        }

        return disjointObjectPropertiesAxioms;
    }

    @Override
    public Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions() {
        return normalizedOntology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY);

    }

    private Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions(OWLOntology ontology) {
        Set<OWLSubObjectPropertyOfAxiom> ret = new HashSet<>(ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));

        for (final OWLEquivalentObjectPropertiesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
            ret.addAll(axiom.asSubObjectPropertyOfAxioms());
        }

        for (final OWLSymmetricObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
            ret.addAll(axiom.asSubPropertyAxioms());
        }

        for (final OWLInverseObjectPropertiesAxiom axiom : ontology.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
            ret.addAll(axiom.asSubObjectPropertyOfAxioms());
        }

        return ret;
    }

}

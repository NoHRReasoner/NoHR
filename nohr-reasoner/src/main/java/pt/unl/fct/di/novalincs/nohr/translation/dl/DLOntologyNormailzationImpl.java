package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.taxonomy.InvalidTaxonomyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
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
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.nohr.translation.InferenceEngine;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ComplexSidesNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ConceptAssertionsNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftBottomNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftConjunctionNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftExistentialNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.Normalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.RightConjunctionNormalizer;

public class DLOntologyNormailzationImpl implements DLOntologyNormalization {

    private static final Logger LOG = Logger.getLogger(DLOntologyNormailzationImpl.class);
    public static final AxiomType<?>[] SUPPORTED_AXIOM_TYPES = new AxiomType<?>[]{
        AxiomType.CLASS_ASSERTION,
        AxiomType.DATA_PROPERTY_ASSERTION,
        AxiomType.DECLARATION,
        AxiomType.DISJOINT_CLASSES,
        AxiomType.DISJOINT_DATA_PROPERTIES,
        AxiomType.DISJOINT_OBJECT_PROPERTIES,
        AxiomType.EQUIVALENT_CLASSES,
        AxiomType.EQUIVALENT_DATA_PROPERTIES,
        AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
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

    private final OWLOntology closure;
    private final InferenceEngine engine;
    private final boolean hasDisjunctions;
    private final OWLOntology ontology;
    private final Vocabulary vocabulary;

    public DLOntologyNormailzationImpl(OWLOntology ontology, Vocabulary vocabulary, InferenceEngine engine) throws UnsupportedAxiomsException {
        Objects.requireNonNull(ontology);
        Objects.requireNonNull(vocabulary);

        this.engine = engine;
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

        try {
            closure = computeClosure(ontology);
        } catch (final InvalidTaxonomyException e) {
            throw new UnsupportedAxiomsException(null);
        }

        int negAssertions = ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
        negAssertions += ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION);

        hasDisjunctions = DLUtils.hasDisjunctions(closure) || negAssertions > 0;
    }

    @Override
    public Iterable<OWLSubPropertyChainOfAxiom> chainSubsumptions() {
        return chainSubsumptions(ontology);
    }

    private Set<OWLSubPropertyChainOfAxiom> chainSubsumptions(OWLOntology ontology) {
        final Set<OWLSubPropertyChainOfAxiom> ret = new HashSet<>(ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF));
        for (final OWLTransitiveObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
            ret.add(normalize(axiom));
        }
        return ret;
    }

    private OWLOntology computeClosure(OWLOntology ontology) {
        final OWLOntology normalizedOntology = normalize(ontology);

        engine.computeInferences(normalizedOntology);

        return normalizedOntology;
    }

    @Override
    public Iterable<OWLClassAssertionAxiom> conceptAssertions() {
        return closure.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    private Set<OWLClassAssertionAxiom> conceptAssertions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.CLASS_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointClassesAxiom> conceptDisjunctions() {
        return closure.getAxioms(AxiomType.DISJOINT_CLASSES);
    }

    private Set<OWLDisjointClassesAxiom> conceptDisjunctions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.DISJOINT_CLASSES);
    }

    @Override
    public Iterable<OWLSubClassOfAxiom> conceptSubsumptions() {
        return closure.getAxioms(AxiomType.SUBCLASS_OF);
    }

    private Set<OWLSubClassOfAxiom> conceptSubsumptions(OWLOntology ontology) {
        Set<OWLSubClassOfAxiom> ret = new HashSet<>(ontology.getAxioms(AxiomType.SUBCLASS_OF));
        for (final OWLEquivalentClassesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
            ret.addAll(axiom.asOWLSubClassOfAxioms());
        }
        for (final OWLDisjointClassesAxiom axiom : ontology.getAxioms(AxiomType.DISJOINT_CLASSES)) {
            for (final OWLDisjointClassesAxiom ci : axiom.asPairwiseAxioms()) {
                ret.add(DLUtils.subsumption(ontology, DLUtils.conjunction(ontology, ci.getClassExpressions()), DLUtils.bottom(ontology)));
            }
        }
        for (final OWLObjectPropertyDomainAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            ret.add(normalize(axiom));
        }
        for (final OWLObjectPropertyRangeAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
            final OWLObjectPropertyExpression p = axiom.getProperty();
            final OWLClassExpression c = axiom.getRange();
            ret.add(DLUtils.subsumption(ontology, DLUtils.some(ontology, p.getInverseProperty()), c));
        }
        return ret;
    }

    @Override
    public Iterable<OWLDataPropertyAssertionAxiom> dataAssertions() {
        return closure.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    private Set<OWLDataPropertyAssertionAxiom> dataAssertions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointDataPropertiesAxiom> dataDisjunctions() {
        return closure.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES);
    }

    private Set<OWLDisjointDataPropertiesAxiom> dataDisjunctions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES);
    }

    @Override
    public Iterable<OWLSubDataPropertyOfAxiom> dataSubsumptions() {
        return closure.getAxioms(AxiomType.SUB_DATA_PROPERTY);
    }

    public Set<OWLSubDataPropertyOfAxiom> dataSubsumptions(OWLOntology ontology) {
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
    public Iterable<OWLIrreflexiveObjectPropertyAxiom> irreflexiveRoles() {
        return closure.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
    }

    private Set<OWLIrreflexiveObjectPropertyAxiom> irreflexiveRoles(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
    }

    private OWLSubClassOfAxiom normalize(OWLObjectPropertyDomainAxiom axiom) {
        final OWLObjectPropertyExpression ope = axiom.getProperty();
        final OWLClassExpression ce = axiom.getDomain();
        return DLUtils.subsumption(ontology, DLUtils.some(ontology, ope, DLUtils.top(ontology)), ce);
    }

    private OWLSubPropertyChainOfAxiom normalize(OWLTransitiveObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression ope = axiom.getProperty();
        final List<OWLObjectPropertyExpression> chain = new ArrayList<>(2);
        chain.add(ope);
        chain.add(ope);
        return DLUtils.subsumption(ontology, chain, ope);
    }

    private void normalize(Set<OWLSubClassOfAxiom> axioms) {
        boolean changed1;
        boolean changed2;
        boolean first = true;
        do {
            changed1 = normalize(axioms, new LeftConjunctionNormalizer(ontology, vocabulary));
            if (first || changed1) {
                changed2 = normalize(axioms, new LeftExistentialNormalizer(ontology, vocabulary));
            } else {
                changed2 = false;
            }
            first = false;
        } while (changed1 || changed2);
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
        final OWLOntologyManager om = OWLManager.createOWLOntologyManager();
        try {
            final OWLOntology ret = om.createOntology(ontology.getOntologyID());
            //final Set<OWLSubPropertyChainOfAxiom> chainSubsumptions = chainSubsumptions(ontology);
            final Set<OWLClassAssertionAxiom> conceptAssertions = conceptAssertions(ontology);
            //final Set<OWLDisjointClassesAxiom> conceptDisjunctions = conceptDisjunctions(ontology);
            final Set<OWLSubClassOfAxiom> conceptSubsumptions = conceptSubsumptions(ontology);
            final Set<OWLDataPropertyAssertionAxiom> dataAssertions = dataAssertions(ontology);
            final Set<OWLDisjointDataPropertiesAxiom> dataDisjunctions = dataDisjunctions(ontology);
            final Set<OWLSubDataPropertyOfAxiom> dataSubsumptions = dataSubsumptions(ontology);
            final Set<OWLIrreflexiveObjectPropertyAxiom> irreflexiveRoles = irreflexiveRoles(ontology);
            final Set<OWLObjectPropertyAssertionAxiom> roleAssertions = roleAssertions(ontology);
            final Set<OWLDisjointObjectPropertiesAxiom> roleDisjunctions = roleDisjunctions(ontology);
            final Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions = roleSubsumptions(ontology);
            normalize(conceptAssertions, new ConceptAssertionsNormalizer(ontology));
            normalize(conceptSubsumptions);
            //om.addAxioms(ret, chainSubsumptions);
            om.addAxioms(ret, conceptAssertions);
            //om.addAxioms(ret, conceptDisjunctions);
            om.addAxioms(ret, conceptSubsumptions);
            om.addAxioms(ret, dataAssertions);
            om.addAxioms(ret, dataDisjunctions);
            om.addAxioms(ret, dataSubsumptions);
            om.addAxioms(ret, irreflexiveRoles);
            om.addAxioms(ret, roleAssertions);
            om.addAxioms(ret, roleDisjunctions);
            om.addAxioms(ret, roleSubsumptions);
            return ret;
        } catch (final OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions() {
        return closure.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    private Set<OWLObjectPropertyAssertionAxiom> roleAssertions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointObjectPropertiesAxiom> roleDisjunctions() {
        return closure.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES);
    }

    private Set<OWLDisjointObjectPropertiesAxiom> roleDisjunctions(OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES);
    }

    @Override
    public Iterable<OWLSubObjectPropertyOfAxiom> roleSubsumptions() {
        return closure.getAxioms(AxiomType.SUB_OBJECT_PROPERTY);
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

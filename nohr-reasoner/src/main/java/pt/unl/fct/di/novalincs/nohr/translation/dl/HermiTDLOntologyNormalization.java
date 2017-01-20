package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
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
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ComplexSidesNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ConceptAssertionsNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftBottomNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftConjunctionNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftExistentialNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.Normalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.RightConjunctionNormalizer;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public final class HermiTDLOntologyNormalization implements DLOntologyNormalization {

    public static final AxiomType<?>[] SUPPORTED_AXIOM_TYPES = new AxiomType<?>[]{AxiomType.CLASS_ASSERTION,
        AxiomType.DATA_PROPERTY_ASSERTION, AxiomType.DECLARATION, AxiomType.DISJOINT_CLASSES,
        AxiomType.EQUIVALENT_CLASSES, AxiomType.EQUIVALENT_DATA_PROPERTIES, AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
        AxiomType.OBJECT_PROPERTY_ASSERTION, AxiomType.OBJECT_PROPERTY_DOMAIN, AxiomType.SUB_DATA_PROPERTY,
        AxiomType.SUB_DATA_PROPERTY, AxiomType.SUB_OBJECT_PROPERTY, AxiomType.SUB_PROPERTY_CHAIN_OF,
        AxiomType.SUBCLASS_OF, AxiomType.TRANSITIVE_OBJECT_PROPERTY};

    private static final Logger LOG = Logger.getLogger(HermiTDLOntologyNormalization.class);

    private final OWLOntology ontology;
    private final Vocabulary vocabulary;

    private final Set<OWLSubPropertyChainOfAxiom> chainSubsumptions;
    private final Set<OWLClassAssertionAxiom> conceptAssertions;
    private final Set<OWLSubClassOfAxiom> conceptSubsumptions;
    private final Set<OWLSubDataPropertyOfAxiom> dataSubsumptions;
    private final Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions;

    private final OWLOntology closure;
    private final boolean hasDisjunctions;

    public HermiTDLOntologyNormalization(OWLOntology ontology, Vocabulary vocabulary) throws UnsupportedAxiomsException {
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

        this.chainSubsumptions = chainSubsumptions(ontology);
        this.conceptAssertions = conceptAssertions(ontology);
        this.conceptSubsumptions = conceptSubsumptions(ontology);
        this.dataSubsumptions = dataSubsumptions(ontology);
        this.roleSubsumptions = roleSubsumptions(ontology);

        try {
            this.closure = computeClosure(conceptAssertions, conceptSubsumptions, roleSubsumptions);
        } catch (final InvalidTaxonomyException e) {
            throw new UnsupportedAxiomsException(null);
        }

        int negAssertions = ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
        negAssertions += ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION);

        hasDisjunctions = DLUtils.hasDisjunctions(closure) || negAssertions > 0;
    }

    @Override
    public Iterable<OWLSubPropertyChainOfAxiom> chainSubsumptions() {
        return chainSubsumptions;
    }

    private Set<OWLSubPropertyChainOfAxiom> chainSubsumptions(OWLOntology ontology) {
        final Set<OWLSubPropertyChainOfAxiom> ret = new HashSet<>(ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF));

        for (final OWLTransitiveObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
            ret.add(normalize(axiom));
        }

        return ret;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

        return ret;
    }

    @Override
    public Iterable<OWLDataPropertyAssertionAxiom> dataAssertions() {
        return ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointDataPropertiesAxiom> dataDisjunctions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<OWLSubDataPropertyOfAxiom> dataSubsumptions() {
        return dataSubsumptions;
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
    public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions() {
        return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    @Override
    public Iterable<OWLDisjointObjectPropertiesAxiom> roleDisjunctions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<OWLSubObjectPropertyOfAxiom> roleSubsumptions() {
        return roleSubsumptions;
    }

    public Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions(OWLOntology ontology) {
        Set<OWLSubObjectPropertyOfAxiom> ret = new HashSet<>(ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));

        for (final OWLEquivalentObjectPropertiesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
            ret.addAll(axiom.asSubObjectPropertyOfAxioms());
        }

        return ret;
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

    private OWLOntology normalize(Set<OWLClassAssertionAxiom> conceptAssertions, Set<OWLSubClassOfAxiom> conceptSubsumptions, Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions) {
        final OWLOntologyManager om = OWLManager.createOWLOntologyManager();

        try {
            final OWLOntology ret = om.createOntology(ontology.getOntologyID());

            normalize(conceptAssertions, new ConceptAssertionsNormalizer(ontology));
            normalize(conceptSubsumptions);

            om.addAxioms(ret, conceptAssertions);
            om.addAxioms(ret, conceptSubsumptions);
            om.addAxioms(ret, roleSubsumptions);

            return ret;
        } catch (final OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }

    }

    private OWLSubClassOfAxiom normalize(OWLObjectPropertyDomainAxiom axiom) {
        final OWLObjectPropertyExpression ope = axiom.getProperty();
        final OWLClassExpression ce = axiom.getDomain();

        return DLUtils.subsumption(ontology, DLUtils.some(ontology, ope, DLUtils.top(ontology)), ce);
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

    private OWLSubPropertyChainOfAxiom normalize(OWLTransitiveObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression ope = axiom.getProperty();
        final List<OWLObjectPropertyExpression> chain = new ArrayList<>(2);

        chain.add(ope);
        chain.add(ope);

        return DLUtils.subsumption(ontology, chain, ope);
    }

    private OWLOntology computeClosure(Set<OWLClassAssertionAxiom> conceptAssertions, Set<OWLSubClassOfAxiom> conceptSubsumptions, Set<OWLSubObjectPropertyOfAxiom> roleSubsumptions) {
        final OWLOntology normalizedOntology = normalize(conceptAssertions, conceptSubsumptions, roleSubsumptions);

        infer(normalizedOntology);

        return normalizedOntology;
    }

    private void infer(OWLOntology ontology) {
        RuntimesLogger.start("[NoHR DL (HermiT)] ontology inference");
        Logger.getLogger("org.semanticweb.hermit").setLevel(Level.ERROR);

        final OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        final OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

        final List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<>(3);

        generators.add(new InferredSubClassAxiomGenerator());
        generators.add(new InferredClassAssertionAxiomGenerator());

        final InferredOntologyGenerator inferredOntologyGenerator = new InferredOntologyGenerator(reasoner, generators);

        inferredOntologyGenerator.fillOntology(ontology.getOWLOntologyManager().getOWLDataFactory(), ontology);

        RuntimesLogger.stop("[NoHR DL (HermiT)] ontology inference", "loading");
    }

}

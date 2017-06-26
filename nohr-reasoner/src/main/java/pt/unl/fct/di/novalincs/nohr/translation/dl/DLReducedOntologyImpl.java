package pt.unl.fct.di.novalincs.nohr.translation.dl;

import pt.unl.fct.di.novalincs.nohr.translation.OntologyUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
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
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.AnonymousSubClassOfNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.ClassAssertionNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftIntersectionOfNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftNothingNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftSomeValuesFromNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.LeftUnionOfNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.Normalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.Reducer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.RightAllValuesFromNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.RightComplementOfNormalizer;
import pt.unl.fct.di.novalincs.nohr.translation.normalization.RightIntersectionOfNormalizer;

public class DLReducedOntologyImpl implements DLReducedOntology {

    private final OWLOntology ontology;
    private final OntologyUtil util;
    private final Vocabulary vocabulary;

    private final Set<OWLClassAssertionAxiom> classAssertionAxioms;
    private final Set<OWLDataPropertyAssertionAxiom> dataPropertyAssertionAxioms;
    private final Set<OWLDisjointDataPropertiesAxiom> disjointDataPropertiesAxioms;
    private final Set<OWLDisjointObjectPropertiesAxiom> disjointObjectPropertiesAxioms;
    private final Set<OWLIrreflexiveObjectPropertyAxiom> irreflexiveObjectPropertyAxioms;
    private final Set<OWLObjectPropertyAssertionAxiom> objectPropertyAssertionAxioms;
    private final Set<OWLSubClassOfAxiom> subClassOfAxioms;
    private final Set<OWLSubDataPropertyOfAxiom> subDataPropertyOfAxioms;
    private final Set<OWLSubObjectPropertyOfAxiom> subObjectPropertyOfAxioms;
    private final Set<OWLSubPropertyChainOfAxiom> subPropertyChainOfAxioms;

    public DLReducedOntologyImpl(OWLOntology ontology) throws UnsupportedAxiomsException {
        this(ontology, null);
    }

    public DLReducedOntologyImpl(OWLOntology ontology, Vocabulary vocabulary) throws UnsupportedAxiomsException {
        this.ontology = ontology;
        this.util = new OntologyUtil(this.ontology.getOWLOntologyManager().getOWLDataFactory());
        this.vocabulary = (vocabulary == null ? new DefaultVocabulary(ontology) : vocabulary);

        this.classAssertionAxioms = new HashSet<>(ontology.getAxioms(AxiomType.CLASS_ASSERTION));
        this.dataPropertyAssertionAxioms = new HashSet<>(ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION));
        this.disjointDataPropertiesAxioms = new HashSet<>();
        this.disjointObjectPropertiesAxioms = new HashSet<>();
        this.irreflexiveObjectPropertyAxioms = new HashSet<>(ontology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY));
        this.objectPropertyAssertionAxioms = new HashSet<>(ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION));
        this.subClassOfAxioms = new HashSet<>(ontology.getAxioms(AxiomType.SUBCLASS_OF));
        this.subDataPropertyOfAxioms = new HashSet<>(ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY));
        this.subObjectPropertyOfAxioms = new HashSet<>(ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
        this.subPropertyChainOfAxioms = new HashSet<>(ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF));

        simplify();
        reduce();
    }

    private void clear() {
        this.classAssertionAxioms.clear();
        this.dataPropertyAssertionAxioms.clear();
        this.disjointDataPropertiesAxioms.clear();
        this.disjointObjectPropertiesAxioms.clear();
        this.irreflexiveObjectPropertyAxioms.clear();
        this.objectPropertyAssertionAxioms.clear();
        this.subClassOfAxioms.clear();
        this.subDataPropertyOfAxioms.clear();
        this.subObjectPropertyOfAxioms.clear();
        this.subPropertyChainOfAxioms.clear();
    }

    private void fill() {
        classAssertionAxioms.addAll(ontology.getAxioms(AxiomType.CLASS_ASSERTION));
        dataPropertyAssertionAxioms.addAll(ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION));
        irreflexiveObjectPropertyAxioms.addAll(ontology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY));
        objectPropertyAssertionAxioms.addAll(ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION));
        subClassOfAxioms.addAll(ontology.getAxioms(AxiomType.SUBCLASS_OF));
        subDataPropertyOfAxioms.addAll(ontology.getAxioms(AxiomType.SUB_DATA_PROPERTY));
        subObjectPropertyOfAxioms.addAll(ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
        subPropertyChainOfAxioms.addAll(ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF));
    }

    @Override
    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms() {
        return classAssertionAxioms;
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms() {
        return dataPropertyAssertionAxioms;
    }

    @Override
    public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms() {
        return disjointDataPropertiesAxioms;
    }

    @Override
    public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms() {
        return disjointObjectPropertiesAxioms;
    }

    @Override
    public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms() {
        return irreflexiveObjectPropertyAxioms;
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms() {
        return objectPropertyAssertionAxioms;
    }

    @Override
    public OWLOntology getReducedOWLOntology() {
        try {
            final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            final OWLOntology reduced = manager.createOntology(ontology.getOntologyID());

            manager.addAxioms(reduced, classAssertionAxioms);
            manager.addAxioms(reduced, dataPropertyAssertionAxioms);
            manager.addAxioms(reduced, disjointDataPropertiesAxioms);
            manager.addAxioms(reduced, disjointObjectPropertiesAxioms);
            manager.addAxioms(reduced, irreflexiveObjectPropertyAxioms);
            manager.addAxioms(reduced, objectPropertyAssertionAxioms);
            manager.addAxioms(reduced, subClassOfAxioms);
            manager.addAxioms(reduced, subDataPropertyOfAxioms);
            manager.addAxioms(reduced, subObjectPropertyOfAxioms);
            manager.addAxioms(reduced, subPropertyChainOfAxioms);

            return reduced;
        } catch (OWLOntologyCreationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Set<OWLSubClassOfAxiom> getSubClassOfAxioms() {
        return subClassOfAxioms;
    }

    @Override
    public Set<OWLSubDataPropertyOfAxiom> getSubDataPropertyOfAxioms() {
        return subDataPropertyOfAxioms;
    }

    @Override
    public Set<OWLSubObjectPropertyOfAxiom> getSubObjectPropertyOfAxioms() {
        return subObjectPropertyOfAxioms;
    }

    @Override
    public Set<OWLSubPropertyChainOfAxiom> getSubPropertyChainOfAxioms() {
        return subPropertyChainOfAxioms;
    }

    private void reduce() {
        boolean changed = true;

        final List<Normalizer> rightNormalizers = new ArrayList<>(3);

        rightNormalizers.add(new RightAllValuesFromNormalizer(util, vocabulary));
        rightNormalizers.add(new RightComplementOfNormalizer(util));
        rightNormalizers.add(new RightIntersectionOfNormalizer(util, vocabulary));

        while (changed) {
            changed = false;

            for (Normalizer i : rightNormalizers) {
                changed = changed || Reducer.reduce(subClassOfAxioms, i);
            }
        }

        final List<Normalizer> leftNormalizers = new ArrayList<>(4);

        leftNormalizers.add(new LeftIntersectionOfNormalizer(util, vocabulary));
        leftNormalizers.add(new LeftNothingNormalizer(util));
        leftNormalizers.add(new LeftSomeValuesFromNormalizer(util, vocabulary));
        leftNormalizers.add(new LeftUnionOfNormalizer(util));

        changed = true;

        while (changed) {
            changed = false;

            for (Normalizer i : leftNormalizers) {
                changed = changed || Reducer.reduce(subClassOfAxioms, i);
            }
        }

        Reducer.reduce(subClassOfAxioms, new AnonymousSubClassOfNormalizer(util, vocabulary));
        Reducer.reduce(classAssertionAxioms, new ClassAssertionNormalizer(util));
    }

    private void simplify() {
        for (final OWLAsymmetricObjectPropertyAxiom i : ontology.getAxioms(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)) {
            simplify(i);
        }

        for (final OWLDataPropertyDomainAxiom i : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
            simplify(i);
        }

        for (final OWLDisjointClassesAxiom i : ontology.getAxioms(AxiomType.DISJOINT_CLASSES)) {
            simplify(i);
        }

        for (final OWLDisjointDataPropertiesAxiom i : ontology.getAxioms(AxiomType.DISJOINT_DATA_PROPERTIES)) {
            simplify(i);
        }

        for (final OWLDisjointObjectPropertiesAxiom i : ontology.getAxioms(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
            simplify(i);
        }

        for (final OWLEquivalentClassesAxiom i : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
            simplify(i);
        }

        for (final OWLEquivalentDataPropertiesAxiom i : ontology.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
            simplify(i);
        }

        for (final OWLEquivalentObjectPropertiesAxiom i : ontology.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
            simplify(i);
        }

        for (final OWLInverseObjectPropertiesAxiom i : ontology.getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
            simplify(i);
        }

        for (final OWLObjectPropertyDomainAxiom i : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            simplify(i);
        }

        for (final OWLObjectPropertyRangeAxiom i : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
            simplify(i);
        }

        for (final OWLSymmetricObjectPropertyAxiom i : ontology.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
            simplify(i);
        }

        for (final OWLTransitiveObjectPropertyAxiom i : ontology.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
            simplify(i);
        }
    }

    private void simplify(OWLAsymmetricObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression property = axiom.getProperty();

        simplify(util.disjoint(property, util.inverseOf(property)));
    }

    private void simplify(OWLDataPropertyDomainAxiom axiom) {
        final OWLDataPropertyExpression property = axiom.getProperty();
        final OWLClassExpression domain = axiom.getDomain();

        subClassOfAxioms.add(util.subClassOf(util.someValuesFrom(property, util.literal()), domain));
    }

    private void simplify(OWLDisjointClassesAxiom axiom) {
        for (OWLDisjointClassesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLClassExpression> expressions = i.getClassExpressionsAsList();

            subClassOfAxioms.add(util.subClassOf(util.intersectionOf(expressions.get(0), expressions.get(1)), util.nothing()));
        }
    }

    private void simplify(OWLDisjointDataPropertiesAxiom axiom) {
        for (OWLDisjointDataPropertiesAxiom i : axiom.asPairwiseAxioms()) {
            disjointDataPropertiesAxioms.add(i);
        }
    }

    private void simplify(OWLDisjointObjectPropertiesAxiom axiom) {
        for (OWLDisjointObjectPropertiesAxiom i : axiom.asPairwiseAxioms()) {
            disjointObjectPropertiesAxioms.add(i);
        }
    }

    private void simplify(OWLEquivalentClassesAxiom axiom) {
        subClassOfAxioms.addAll(axiom.asOWLSubClassOfAxioms());
    }

    private void simplify(OWLEquivalentDataPropertiesAxiom axiom) {
        subDataPropertyOfAxioms.addAll(axiom.asSubDataPropertyOfAxioms());
    }

    private void simplify(OWLEquivalentObjectPropertiesAxiom axiom) {
        subObjectPropertyOfAxioms.addAll(axiom.asSubObjectPropertyOfAxioms());
    }

    private void simplify(OWLInverseObjectPropertiesAxiom axiom) {
        final OWLObjectPropertyExpression firstObjectProperty = axiom.getFirstProperty();
        final OWLObjectPropertyExpression secondObjectProperty = axiom.getSecondProperty();

        simplify(util.equivalent(firstObjectProperty, util.inverseOf(secondObjectProperty)));
    }

    private void simplify(OWLObjectPropertyDomainAxiom axiom) {
        final OWLObjectPropertyExpression property = axiom.getProperty();
        final OWLClassExpression domain = axiom.getDomain();

        subClassOfAxioms.add(util.subClassOf(util.someValuesFrom(property, util.thing()), domain));
    }

    private void simplify(OWLObjectPropertyRangeAxiom axiom) {
        final OWLObjectPropertyExpression property = axiom.getProperty();
        final OWLClassExpression range = axiom.getRange();

        subClassOfAxioms.add(util.subClassOf(util.someValuesFrom(util.inverseOf(property), util.thing()), range));
    }

    private void simplify(OWLSymmetricObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression property = axiom.getProperty();

        subObjectPropertyOfAxioms.add(util.subObjectPropertyOfAxiom(property, util.inverseOf(property)));
    }

    private void simplify(OWLTransitiveObjectPropertyAxiom axiom) {
        final OWLObjectPropertyExpression property = axiom.getProperty();
        final List<OWLObjectPropertyExpression> chain = new ArrayList<>(2);

        chain.add(property);
        chain.add(property);

        subPropertyChainOfAxioms.add(util.subPropertyChainOf(chain, property));
    }

    public void update() {
        clear();
        fill();
        simplify();
        reduce();
    }
}

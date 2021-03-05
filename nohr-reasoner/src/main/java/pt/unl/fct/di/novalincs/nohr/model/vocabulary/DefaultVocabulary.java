/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.DOUBLE;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.DOUBLED_RANGE;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.DOUBLE_DOMAIN;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.NEGATIVE;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.ORIGINAL;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.ORIGINAL_DOMAIN;
import static pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType.ORIGINAL_RANGE;
import pt.unl.fct.di.novalincs.nohr.translation.DLUtils;
import pt.unl.fct.di.novalincs.nohr.utils.HashMultiset;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * An implementation of {@link Vocabulary} where the concrete representations of
 * concepts and rules are their IRI fragments and label annotation values (see 
 * <a href="http://www.w3.org/TR/owl2-syntax/#Annotation_Properties">Annotation
 * Properties</a>); and the concrete representations of the individuals are
 * their IRI fragments, when they are named, or their node IDs, otherwise.
 *
 * @author Nuno Costa
 */
public class DefaultVocabulary implements Vocabulary {

    /**
     * The character that fills the {@link #newEntitiesFiller} string
     */
    private static final char FILLER_CHAR = '0';

    /**
     * The new entities (concepts and roles) counter which ensures that the
     * generated concepts and roles are different from each other
     */
    private int newEntitiesCounter;

    /**
     * A string with a size greater than all the ontology's concepts and roles
     * names sizes, in order to ensure that when prefixed to a new concept or
     * role name that name will be different from all ontology's concept and
     * role names.
     */
    private final String newEntitiesFiller;

    /**
     * The {@link OWLOntologyChangeListener} that handles concept, role, or
     * individual addition or remotion
     */
    private final OWLOntologyChangeListener ontologyChangeListener;

    /**
     * Maintains a counter of the occurrences, in the ontology, of each concept,
     * role and individual.
     */
    private final HashMultiset<OWLObject> references;

    /**
     * The imports closure of the ontology component of the Hybrid KB of wich
     * this {@link Vocabulary} is vocabulary
     */
    private final Set<OWLOntology> ontologies;

    /**
     * A mapping between symbols and {@link HybridConstant constants} that they
     * represent.
     */
    private final Map<String, HybridConstantWrapper> constants;

    /**
     * A mapping between concepts and {@link HybridPredicate predicates}
     * representing that concepts
     */
    private final Map<OWLClass, ConceptPredicateImpl> conceptPredicates;

    /**
     * A mapping between roles and {@link HybridPredicate predicates}
     * representing that roles
     */
    private final Map<OWLProperty, RolePredicateImpl> rolePredicates;

    /**
     * A mapping between arities/symbols and {@link HybridPredicate predicates}
     * that they represent.
     */
    private final Map<Integer, Map<String, HybridPredicateWrapper>> predicates;

    /**
     * A mapping between individuals and {@link HybridConstant constants}
     * representing that individuals
     */
    private final Map<OWLIndividual, IndividualConstantImpl> individualConstants;

    /**
     * The {@link VocabularyChangeListener listeners} that listen this
     * {@link Vocabulary}
     */
    private final Set<VocabularyChangeListener> listeners;

    /**
     * The ontology component of the Hybrid KB of wich this {@link Vocabulary}
     * is vocabulary
     */
    private final OWLOntology ontology;

    /**
     * The RDFS Label annotation property representation
     */
    private final OWLAnnotationProperty rdfsLabel;

    private final Set<OWL2Datatype> numericDatatypesSet;

    /**
     * Constructs a {@link DefaultVocabulary} for a given set of ontologies;
     *
     * @param ontology the ontology.
     */
    public DefaultVocabulary(OWLOntology ontology) {
        Objects.requireNonNull(ontology);
        this.ontology = ontology;
        ontologies = ontology.getImportsClosure();
        listeners = new HashSet<>();
        references = new HashMultiset<>();
        constants = new HashMap<>();
        predicates = new HashMap<>();
        conceptPredicates = new HashMap<>();
        rolePredicates = new HashMap<>();
        individualConstants = new HashMap<>();
        final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        rdfsLabel = dataFactory.getRDFSLabel();

        final OWL2Datatype[] numericDatatypes = {
            OWL2Datatype.OWL_REAL,
            OWL2Datatype.XSD_BYTE,
            OWL2Datatype.XSD_DECIMAL,
            OWL2Datatype.XSD_DOUBLE,
            OWL2Datatype.XSD_FLOAT,
            OWL2Datatype.XSD_INT,
            OWL2Datatype.XSD_INTEGER,
            OWL2Datatype.XSD_LONG,
            OWL2Datatype.XSD_NEGATIVE_INTEGER,
            OWL2Datatype.XSD_NON_NEGATIVE_INTEGER,
            OWL2Datatype.XSD_NON_POSITIVE_INTEGER,
            OWL2Datatype.XSD_POSITIVE_INTEGER,
            OWL2Datatype.XSD_SHORT,
            OWL2Datatype.XSD_UNSIGNED_BYTE,
            OWL2Datatype.XSD_UNSIGNED_INT,
            OWL2Datatype.XSD_UNSIGNED_LONG,
            OWL2Datatype.XSD_UNSIGNED_SHORT
        };

        numericDatatypesSet = new HashSet<>(Arrays.asList(numericDatatypes));
        
        register(dataFactory.getOWLThing());
        register(dataFactory.getOWLNothing());
        register(dataFactory.getOWLTopObjectProperty());
        register(dataFactory.getOWLBottomObjectProperty());
        register(dataFactory.getOWLTopDataProperty());
        register(dataFactory.getOWLBottomDataProperty());

        for (final OWLOntology ont : ontologies) {
            for (final OWLClass c : ont.getClassesInSignature()) {
                register(c);
            }
            for (final OWLProperty r : ont.getObjectPropertiesInSignature()) {
                register(r);
            }
            for (final OWLProperty d : ont.getDataPropertiesInSignature()) {
                register(d);
            }
            for (final OWLIndividual i : ont.getIndividualsInSignature()) {
                register(i);
            }
        }

        ontologyChangeListener = new OWLOntologyChangeListener() {

            @Override
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                for (final OWLOntologyChange change : changes) {
                    if (ontologies.contains(change.getOntology())) {
                        if (change.isAddAxiom()) {
                            if (change.getAxiom().isOfType(AxiomType.ANNOTATION_ASSERTION)) {
                                final OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) change.getAxiom())
                                        .getSubject();
                                if (subject instanceof IRI) {
                                    for (final OWLEntity entity : change.getOntology()
                                            .getEntitiesInSignature((IRI) subject)) {
                                        if (entity instanceof OWLClass) {
                                            register((OWLClass) entity);
                                        } else if (entity instanceof OWLProperty) {
                                            register((OWLProperty) entity);
                                        }
                                    }
                                }
                            }
                            for (final OWLClass concept : change.getAxiom().getClassesInSignature()) {
                                register(concept);
                            }
                            for (final OWLObjectProperty role : change.getAxiom().getObjectPropertiesInSignature()) {
                                register(role);
                            }
                            for (final OWLDataProperty role : change.getAxiom().getDataPropertiesInSignature()) {
                                register(role);
                            }
                            for (final OWLIndividual individual : change.getAxiom().getIndividualsInSignature()) {
                                register(individual);
                            }
                        } else if (change.isRemoveAxiom()) {
                            if (change.getAxiom().isOfType(AxiomType.ANNOTATION_ASSERTION)) {
                                final OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) change.getAxiom())
                                        .getSubject();
                                if (subject instanceof IRI) {
                                    for (final OWLEntity entity : change.getOntology()
                                            .getEntitiesInSignature((IRI) subject)) {
                                        if (entity instanceof OWLClass) {
                                            unregister((OWLClass) entity);
                                        } else if (entity instanceof OWLProperty) {
                                            unregister((OWLProperty) entity);
                                        }
                                    }
                                }
                            }
                            for (final OWLClass concept : change.getAxiom().getClassesInSignature()) {
                                unregister(concept);
                            }
                            for (final OWLObjectProperty role : change.getAxiom().getObjectPropertiesInSignature()) {
                                unregister(role);
                            }
                            for (final OWLDataProperty role : change.getAxiom().getDataPropertiesInSignature()) {
                                unregister(role);
                            }
                            for (final OWLIndividual individual : change.getAxiom().getIndividualsInSignature()) {
                                unregister(individual);
                            }
                        }
                    }
                }
            }
        };
        for (final OWLOntology ont : ontologies) {
            ont.getOWLOntologyManager().addOntologyChangeListener(ontologyChangeListener);
        }
        // generator
        newEntitiesCounter = 0;
        int maxNameLength = 0;
        for (final OWLOntology ont : ontologies) {
            for (final OWLEntity entity : ont.getSignature()) {
                if (entity.getIRI().toURI().getFragment() != null) {
                    final int len = entity.getIRI().toURI().getFragment().length();
                    if (len > maxNameLength) {
                        maxNameLength = len;
                    }
                }
            }
        }
        final char[] fillerChars = new char[maxNameLength];
        Arrays.fill(fillerChars, FILLER_CHAR);
        newEntitiesFiller = new String(fillerChars);
    }

    @Override
    public void addListener(VocabularyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Returns the concrete representations of a given entity.
     *
     * @param entity the entity.
     * @return the set of concrete representations of {@code entity}
     */
    protected Set<String> concreteRepresentations(OWLEntity entity) {
        final Set<String> result = new HashSet<>();
        final String fragment = entity.getIRI().toURI().getFragment();

        if (fragment != null) {
            result.add(fragment);
        }

        for (final OWLOntology ont : ontologies) {
            for (final OWLAnnotation annotation
                    : EntitySearcher.getAnnotations(entity, ont, rdfsLabel)) {

                final OWLAnnotationValue value = annotation.getValue();

                if (value instanceof OWLLiteral) {
                    result.add(((OWLLiteral) value).getLiteral());
                }
            }
        }

        return result;
    }

    /**
     * Returns the concrete representations of a given individual.
     *
     * @param individual the individual.
     * @return the set of concrete representations of {@code individual}
     */
    protected Set<String> concreteRepresentations(OWLIndividual individual) {
        if (individual.isNamed()) {
            return concreteRepresentations((OWLEntity) individual.asOWLNamedIndividual());
        }

        return Collections.<String>emptySet();
    }

    @Override
    public Constant cons(Number n) {
        return new NumericConstantImpl(n);
    }

    @Override
    public Constant cons(OWLIndividual individual) {
        final Constant cons = individualConstants.get(individual);

        if (cons == null) {
            throw new UndefinedSymbolException();
        }

        return cons;
    }

    @Override
    public Constant cons(OWLLiteral literal) {
        if (numericDatatypesSet.contains(literal.getDatatype().getBuiltInDatatype())) {
            return cons(literal.getLiteral());
        }

        return new LiteralConstantImpl(literal);
    }

    @Override
    public Constant cons(OWLPropertyAssertionObject object) {
        if (object instanceof OWLIndividual) {
            return cons((OWLIndividual) object);
        } else if (object instanceof OWLLiteral) {
            return cons((OWLLiteral) object);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public Constant cons(String symbol) {
        try {
            final Double number = Double.valueOf(symbol);

            return cons(number);
        } catch (final NumberFormatException e) {
            final String sym = StringUtils.simplifySymbol(symbol);

            return setConstant(sym, new RuleConstantImpl(sym), false);
        }
    }

    @Override
    public void dispose() {
        predicates.clear();
        constants.clear();
        conceptPredicates.clear();
        rolePredicates.clear();
        individualConstants.clear();
        references.clear();
        listeners.clear();

        for (final OWLOntology ont : ontologies) {
            ont.getOWLOntologyManager().removeOntologyChangeListener(ontologyChangeListener);
        }
    }

    @Override
    public Predicate domPred(OWLPropertyExpression role, boolean doub) {
        if (doub) {
            return pred(role, DOUBLE_DOMAIN);
        } else {
            return pred(role, ORIGINAL_DOMAIN);
        }
    }

    @Override
    public Predicate doubDomPred(OWLPropertyExpression role) {
        return pred(role, DOUBLE_DOMAIN);
    }

    @Override
    public Predicate doubPred(OWLClass concept) {
        return pred(concept, DOUBLE);
    }

    @Override
    public Predicate doubPred(OWLPropertyExpression role) {
        return pred(role, DOUBLE);
    }

    @Override
    public Predicate doubPred(String symbol, int arity) {
        return pred(symbol, arity, DOUBLE);
    }

    @Override
    public Predicate doubRanPred(OWLPropertyExpression role) {
        return pred(role, DOUBLED_RANGE);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        for (final OWLOntology ont : ontologies) {
            ont.getOWLOntologyManager().removeOntologyChangeListener(ontologyChangeListener);
        }
    }

    /**
     * Generate a new IRI that for {@link #ontology}.
     *
     * @return an IRI {@code <ontologyIRI#newFrament>}, where
     * {@code ontologyIRI} is the {@link #ontology}'s IRI and
     * {@code newFragment} a string that don't occur in none {@link #ontology}
     * entity IRI as fragment.
     */
    private IRI generateIRI() {
        return IRI.create("owlapi:nohr#" + newEntitiesFiller + newEntitiesCounter++);
    }

    /**
     * Generate a new concept that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     *
     * @return a new concept that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     */
    @Override
    public OWLClass generateNewConcept() {
        final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        final OWLClass concept = dataFactory.getOWLClass(generateIRI());
        register(concept);
        return concept;
    }

    /**
     * Generate a new role that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     *
     * @return a new role that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     */
    @Override
    public OWLObjectProperty generateNewRole() {
        final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        final OWLObjectProperty role = dataFactory.getOWLObjectProperty(generateIRI());
        register(role);
        return role;
    }

    @Override
    public OWLOntology getOntology() {
        return ontology;
    }

    @Override
    public Predicate negPred(OWLClass concept) {
        return pred(concept, NEGATIVE);
    }

    @Override
    public Predicate negPred(OWLPropertyExpression role) {
        return pred(role, NEGATIVE);
    }

    @Override
    public Predicate negPred(Predicate predicate) {
        Predicate pred = predicate;
        if (predicate instanceof MetaPredicate) {
            pred = ((MetaPredicate) predicate).getPredicate();
        }
        return new MetaPredicateImpl(pred, NEGATIVE);
    }

    @Override
    public Predicate negPred(String symbol, int arity) {
        return pred(symbol, arity, NEGATIVE);
    }

    @Override
    public Predicate origDomPred(OWLPropertyExpression role) {
        return pred(role, ORIGINAL_DOMAIN);
    }

    @Override
    public Predicate origPred(OWLClass concept) {
        return pred(concept, ORIGINAL);
    }

    @Override
    public Predicate origPred(OWLPropertyExpression role) {
        return pred(role, ORIGINAL);
    }

    @Override
    public Predicate origPred(String symbol, int arity) {
        return pred(symbol, arity, ORIGINAL);
    }

    @Override
    public Predicate origRanPred(OWLPropertyExpression role) {
        return pred(role, ORIGINAL_RANGE);
    }

    @Override
    public Predicate pred(OWLClass concept) {
        final Predicate pred = conceptPredicates.get(concept);

        if (pred == null) {
            throw new UndefinedSymbolException();
        }

        return pred;
    }

    @Override
    public Predicate pred(OWLClass concept, boolean doub) {
        if (doub) {
            return pred(concept, DOUBLE);
        } else {
            return pred(concept, ORIGINAL);
        }
    }

    @Override
    public MetaPredicate pred(OWLClass concept, PredicateType type) {
        return new MetaPredicateImpl(pred(concept), type);
    }

    @Override
    public Predicate pred(OWLPropertyExpression role) {
        final Predicate pred = rolePredicates.get(role);

        if (pred == null) {
            throw new UndefinedSymbolException();
        }

        return pred;
    }

    @Override
    public Predicate pred(OWLPropertyExpression role, boolean doub) {
        if (doub) {
            return pred(role, DOUBLE);
        } else {
            return pred(role, ORIGINAL);
        }
    }

    @Override
    public Predicate pred(OWLPropertyExpression role, PredicateType type) {
        return new MetaPredicateImpl(pred(DLUtils.atomic(role)), type);
    }

    @Override
    public MetaPredicate pred(Predicate predicate, PredicateType type) {
        return new MetaPredicateImpl(predicate, type);
    }

    @Override
    public Predicate pred(String symbol, int arity) {
        final String sym = StringUtils.simplifySymbol(symbol);

        final HybridPredicate pred = new RulePredicateImpl(sym, arity);

        return setPredicate(sym, arity, pred, false);
    }

    @Override
    public Predicate pred(String symbol, int arity, boolean doub) {
        if (doub) {
            return pred(symbol, arity, DOUBLE);
        } else {
            return pred(symbol, arity, ORIGINAL);
        }
    }

    @Override
    public Predicate pred(String symbol, int arity, PredicateType type) {
        return new MetaPredicateImpl(pred(symbol, arity), type);
    }

    @Override
    public Predicate prologPred(String symbol, int arity) {
        return new PrologPredicateImpl(symbol, arity);
    }

    @Override
    public Predicate prologOpPred(String symbol) {
        return new PrologOperatorPredicateImpl(symbol, 2);
    }

    @Override
    public Predicate ranPred(OWLPropertyExpression role, boolean doub) {
        if (doub) {
            return pred(role, DOUBLED_RANGE);
        } else {
            return pred(role, ORIGINAL_RANGE);
        }
    }

    /**
     * Registers an occurrence of a given concept.
     *
     * @param concept the concept.
     */
    private void register(OWLClass concept) {
        ConceptPredicateImpl conceptPred = conceptPredicates.get(concept);

        if (conceptPred == null) {
            conceptPred = new ConceptPredicateImpl(concept);
            conceptPredicates.put(concept, conceptPred);
        }

        setPredicate(conceptPred.asString(), 1, conceptPred, true);

        for (final String symbol : concreteRepresentations(concept)) {
            setPredicate(symbol, 1, conceptPred, true);
        }

        references.add(concept);
    }

    /**
     * Registers an occurrence of a given individual.
     *
     * @param individual
     */
    private void register(OWLIndividual individual) {
        IndividualConstantImpl individualConstant = individualConstants.get(individual);

        if (individualConstant == null) {
            individualConstant = new IndividualConstantImpl(individual);
            individualConstants.put(individual, individualConstant);
        }

        setConstant(individualConstant.asString(), individualConstant, true);

        for (final String symbol : concreteRepresentations(individual)) {
            setConstant(symbol, individualConstant, true);
        }

        references.add(individual);
    }

    /**
     * Registers an occurrence of a given role.
     *
     * @param role a role.
     */
    private void register(OWLProperty role) {
        RolePredicateImpl rolePred = rolePredicates.get(role);

        if (rolePred == null) {
            rolePred = new RolePredicateImpl(role);
            rolePredicates.put(role, rolePred);
        }

        setPredicate(rolePred.asString(), 2, rolePred, true);

        for (final String symbol : concreteRepresentations(role)) {
            setPredicate(symbol, 2, rolePred, true);
        }

        references.add(role);
    }

    @Override
    public void removeListener(VocabularyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sets what {@link HybridConstant constant} is represented by a certain
     * concert representation.
     *
     * @param repr a concrete representation.
     * @param constant the constant that will be associate with {@code repr}.
     * @param change specifies whether the constant associated with {@code repr}
     * will change if a constant is already associated with {@code repr}.
     * @return the new {@link HybridConstantWrapper constant wrapper}.
     */
    private HybridConstantWrapper setConstant(String repr, HybridConstant constant, boolean change) {
        HybridConstantWrapper cons = constants.get(repr);
        if (cons == null) {
            cons = new HybridConstantWrapper(constant);
            constants.put(repr, cons);
        } else if (change) {
            if (cons.changeWrappe(constant)) {
                for (final VocabularyChangeListener listener : listeners) {
                    listener.constantChanged(cons);
                }
            }
        }
        return cons;
    }

    /**
     * Sets what {@link HybridPredicate predicate} is represented by a certain
     * concert representation.
     *
     * @param repr a predicate representation.
     * @param predicate the predicate that will be associate with {@code repr}.
     * @param change specifies whether the constant associated with {@code repr}
     * will change if a predicate is already associated with {@code repr}.
     * @return the new {@link HybridPredicateWrapper predicate wrapper}.
     */
    private HybridPredicateWrapper setPredicate(String repr, int arity, HybridPredicate predicate, boolean change) {
        Map<String, HybridPredicateWrapper> map = predicates.get(arity);

        if (map == null) {
            map = new HashMap<>();
            predicates.put(arity, map);
        }

        HybridPredicateWrapper pred = map.get(repr);

        if (pred == null) {
            pred = new HybridPredicateWrapper(predicate);
            map.put(repr, pred);
        } else if (change) {
            if (pred.changeWrapee(predicate)) {
                for (final VocabularyChangeListener listener : listeners) {
                    listener.predicateChanged(pred);
                }
            }
        }

        return pred;
    }

    /**
     * Unregisters an occurrence of a given concept.
     *
     * @param concept the concept.
     */
    private void unregister(OWLClass concept) {
        references.remove(concept);
        if (!references.contains(concept)) {
            final Predicate pred = conceptPredicates.remove(concept);
            setPredicate(pred.asString(), 1, new RulePredicateImpl(pred.asString(), 1), true);
            for (final String symbol : concreteRepresentations(concept)) {
                setPredicate(symbol, 1, new RulePredicateImpl(symbol, 1), true);
            }
        }
    }

    /**
     * Unregisters an occurrence of a given individual
     *
     * @param individual an individual.
     */
    private void unregister(OWLIndividual individual) {
        references.remove(individual);
        if (!references.contains(individual)) {
            final Constant cons = individualConstants.remove(individual);
            setConstant(cons.asString(), new RuleConstantImpl(cons.asString()), true);
            for (final String symbol : concreteRepresentations(individual)) {
                setConstant(symbol, new RuleConstantImpl(symbol), true);
            }
        }
    }

    /**
     * Unregisters an occurrence of a given role.
     *
     * @param role a role.
     */
    private void unregister(OWLProperty role) {
        references.remove(role);
        if (!references.contains(role)) {
            final Predicate pred = rolePredicates.remove(role);
            setPredicate(pred.asString(), 2, new RulePredicateImpl(pred.asString(), 2), true);
            for (final String symbol : concreteRepresentations(role)) {
                setPredicate(symbol, 2, new RulePredicateImpl(symbol, 2), true);
            }
        }
    }

}

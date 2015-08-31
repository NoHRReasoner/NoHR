/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.terminals;

import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.DOUBLE;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.DOUBLED_RANGE;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.DOUBLE_DOMAIN;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.NEGATIVE;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.ORIGINAL;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.ORIGINAL_DOMAIN;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.ORIGINAL_RANGE;

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

import pt.unl.fct.di.centria.nohr.HashMultiset;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.translation.DLUtils;

/**
 * An implementation of {@link Vocabulary} where the concepts and rules are represented by the fragment of their IRIs and the individuals by their
 * node IDs
 *
 * @author Nuno Costa
 */
public class DefaultVocabulary implements Vocabulary {

	/** The character that fills the {@link #filler} string */
	private static final char FILLER_CHAR = '0';

	/** The entities counter which ensures that the generated concepts and roles are different from each other */
	private int counter;

	/**
	 * A string with a size greater than all the ontology's concepts and roles names sizes, in order to ensure that when prefixed to a new concept or
	 * role name that name will be different from all ontology's concept and role names.
	 */
	private final String filler;

	/** The {@link OWLOntologyChangeListener} that handles concepts, roles, or individual addition or remotion */
	private final OWLOntologyChangeListener ontologyChangeListener;

	/** Maintains a counter of the occurrences, in the ontology, of each concept, role and individual. */
	private final HashMultiset<OWLObject> references;

	/** The ontologies whose concepts, rules, and individuals this {@link Vocabulary} maps. */
	private final Set<OWLOntology> ontologies;

	/** The mapping between symbols and the individuals that they represent. */
	private final Map<String, HybridConstantWrapper> constants;

	private final Map<OWLClass, HybridPredicateWrapper> conceptPredicates;

	private final Map<OWLProperty<?, ?>, HybridPredicateWrapper> rolePredicates;

	private final Map<Integer, Map<String, HybridPredicateWrapper>> predicates;

	private final Map<OWLIndividual, HybridConstantWrapper> individualConstants;

	public DefaultVocabulary(OWLOntology ontology) {
		this(ontology.getImportsClosure());
	}

	/**
	 * Constructs a {@link DefaultVocabulary} for a given set of ontologies;
	 *
	 * @param ontologies
	 *            the set of ontologies.
	 */
	public DefaultVocabulary(final Set<OWLOntology> ontologies) {
		Objects.requireNonNull(ontologies);
		this.ontologies = ontologies;
		references = new HashMultiset<>();
		constants = new HashMap<>();
		predicates = new HashMap<>();
		conceptPredicates = new HashMap<>();
		rolePredicates = new HashMap<>();
		individualConstants = new HashMap<>();
		final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
		register(dataFactory.getOWLThing());
		register(dataFactory.getOWLNothing());
		register(dataFactory.getOWLTopObjectProperty());
		register(dataFactory.getOWLBottomObjectProperty());
		register(dataFactory.getOWLTopDataProperty());
		register(dataFactory.getOWLBottomDataProperty());
		for (final OWLOntology ontology : ontologies) {
			for (final OWLClass c : ontology.getClassesInSignature())
				register(c);
			for (final OWLProperty<?, ?> r : ontology.getObjectPropertiesInSignature())
				register(r);
			for (final OWLProperty<?, ?> d : ontology.getDataPropertiesInSignature())
				register(d);
			for (final OWLIndividual i : ontology.getIndividualsInSignature())
				register(i);
		}
		ontologyChangeListener = new OWLOntologyChangeListener() {

			@Override
			public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
				for (final OWLOntologyChange change : changes)
					if (ontologies.contains(change.getOntology()))
						if (change.isAddAxiom()) {
							if (change.getAxiom().isOfType(AxiomType.ANNOTATION_ASSERTION)) {
								final OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) change.getAxiom())
										.getSubject();
								if (subject instanceof IRI)
									for (final OWLEntity entity : change.getOntology()
											.getEntitiesInSignature((IRI) subject))
										if (entity instanceof OWLClass)
											register((OWLClass) entity);
										else if (entity instanceof OWLProperty)
											register((OWLProperty<?, ?>) entity);
							}
							for (final OWLClass concept : change.getAxiom().getClassesInSignature())
								register(concept);
							for (final OWLObjectProperty role : change.getAxiom().getObjectPropertiesInSignature())
								register(role);
							for (final OWLDataProperty role : change.getAxiom().getDataPropertiesInSignature())
								register(role);
							for (final OWLIndividual individual : change.getAxiom().getIndividualsInSignature())
								register(individual);
						} else if (change.isRemoveAxiom()) {
							if (change.getAxiom().isOfType(AxiomType.ANNOTATION_ASSERTION)) {
								final OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) change.getAxiom())
										.getSubject();
								if (subject instanceof IRI)
									for (final OWLEntity entity : change.getOntology()
											.getEntitiesInSignature((IRI) subject))
										if (entity instanceof OWLClass)
											unregister((OWLClass) entity);
										else if (entity instanceof OWLProperty)
											unregister((OWLProperty<?, ?>) entity);
							}
							for (final OWLClass concept : change.getAxiom().getClassesInSignature())
								unregister(concept);
							for (final OWLObjectProperty role : change.getAxiom().getObjectPropertiesInSignature())
								unregister(role);
							for (final OWLDataProperty role : change.getAxiom().getDataPropertiesInSignature())
								unregister(role);
							for (final OWLIndividual individual : change.getAxiom().getIndividualsInSignature())
								unregiter(individual);
						}
			}
		};
		for (final OWLOntology ontology : ontologies)
			ontology.getOWLOntologyManager().addOntologyChangeListener(ontologyChangeListener);
		// generator
		counter = 0;
		int maxNameLength = 0;
		for (final OWLOntology ontology : ontologies)
			for (final OWLEntity entity : ontology.getSignature()) {
				final int len = entity.getIRI().toURI().getFragment().length();
				if (len > maxNameLength)
					maxNameLength = len;
			}
		final char[] fillerChars = new char[maxNameLength];
		Arrays.fill(fillerChars, FILLER_CHAR);
		filler = new String(fillerChars);
	}

	private HybridConstantWrapper addConstant(String symbol, HybridConstant constant, boolean change) {
		HybridConstantWrapper cons = constants.get(symbol);
		if (cons == null) {
			cons = new HybridConstantWrapper(constant);
			constants.put(symbol, cons);
		} else if (change)
			cons.setWrappe(constant);
		return cons;
	}

	private HybridPredicateWrapper addPredicate(String symbol, int arity, HybridPredicate predicate, boolean change) {
		Map<String, HybridPredicateWrapper> map = predicates.get(arity);
		if (map == null) {
			map = new HashMap<>();
			predicates.put(arity, map);
		}
		HybridPredicateWrapper pred = map.get(symbol);
		if (pred == null) {
			pred = new HybridPredicateWrapper(predicate);
			map.put(symbol, pred);
		} else if (change)
			pred.setWrapee(predicate);
		return pred;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#cons(java.lang.Number)
	 */
	@Override
	public Constant cons(Number n) {
		return new NumericConstantImpl(n);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#cons(java.lang.String)
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#cons(org.semanticweb.owlapi.model.OWLIndividual)
	 */
	@Override
	public Constant cons(OWLIndividual individual) {
		final Constant cons = individualConstants.get(individual);
		if (cons == null)
			throw new UndefinedSymbolException();
		return cons;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#cons(org.semanticweb.owlapi.model.OWLLiteral)
	 */
	@Override
	public Constant cons(OWLLiteral literal) {
		return new LiteralConstantImpl(literal);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#cons(org.semanticweb.owlapi.model.OWLPropertyAssertionObject)
	 */
	@Override
	public Constant cons(OWLPropertyAssertionObject object) {
		if (object instanceof OWLIndividual)
			return cons((OWLIndividual) object);
		else if (object instanceof OWLLiteral)
			return cons((OWLLiteral) object);
		else
			throw new ClassCastException();
	}

	@Override
	public Constant cons(String symbol) {
		try {
			final Double number = Double.valueOf(symbol);
			return cons(number);
		} catch (final NumberFormatException e) {
			return addConstant(symbol, new RuleConstantImpl(symbol), false);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#domPred(org.semanticweb.owlapi.model.OWLPropertyExpression, boolean)
	 */
	@Override
	public Predicate domPred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLE_DOMAIN);
		else
			return pred(role, ORIGINAL_DOMAIN);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#doubDomPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate doubDomPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLE_DOMAIN);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#doubPred(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public Predicate doubPred(OWLClass concept) {
		return pred(concept, DOUBLE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#doubPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate doubPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#doubPred(java.lang.String, int)
	 */
	@Override
	public Predicate doubPred(String symbol, int arity) {
		return pred(symbol, arity, DOUBLE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#doubRanPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate doubRanPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLED_RANGE);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		for (final OWLOntology ontology : ontologies)
			ontology.getOWLOntologyManager().removeOntologyChangeListener(ontologyChangeListener);
	}

	/**
	 * Generate a new IRI that for {@link #ontology}.
	 *
	 * @return an IRI {@code <ontologyIRI#newFrament>}, where {@code ontologyIRI} is the {@link #ontology}'s IRI and {@code newFragment} a string that
	 *         don't occur in none {@link #ontology} entity IRI as fragment.
	 */
	private IRI generateIRI() {
		return IRI.create("#" + filler + counter++);
	}

	/**
	 * Generate a new concept that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 *
	 * @return a new concept that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 */
	@Override
	public OWLClass generateNewConcept() {
		final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
		final OWLClass concept = dataFactory.getOWLClass(generateIRI());
		register(concept);
		return concept;
	}

	/**
	 * Generate a new role that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 *
	 * @return a new role that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 */
	@Override
	public OWLObjectProperty generateNewRole() {
		final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
		final OWLObjectProperty role = dataFactory.getOWLObjectProperty(generateIRI());
		register(role);
		return role;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#getOntologies()
	 */
	@Override
	public Set<OWLOntology> getOntologies() {
		return ontologies;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#negPred(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public Predicate negPred(OWLClass concept) {
		return pred(concept, NEGATIVE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#negPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate negPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, NEGATIVE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#negPred(pt.unl.fct.di.centria.nohr.model.Predicate)
	 */
	@Override
	public Predicate negPred(Predicate predicate) {
		Predicate pred = predicate;
		if (predicate instanceof MetaPredicate)
			pred = ((MetaPredicate) predicate).getPredicate();
		return new MetaPredicateImpl(pred, NEGATIVE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#negPred(java.lang.String, int)
	 */
	@Override
	public Predicate negPred(String symbol, int arity) {
		return pred(symbol, arity, NEGATIVE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#origDomPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate origDomPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL_DOMAIN);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#origPred(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public Predicate origPred(OWLClass concept) {
		return pred(concept, ORIGINAL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#origPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate origPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#origPred(java.lang.String, int)
	 */
	@Override
	public Predicate origPred(String symbol, int arity) {
		return pred(symbol, arity, ORIGINAL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#origRanPred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate origRanPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL_RANGE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public Predicate pred(OWLClass concept) {
		final Predicate pred = conceptPredicates.get(concept);
		if (pred == null)
			throw new UndefinedSymbolException();
		return pred;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(org.semanticweb.owlapi.model.OWLClass, boolean)
	 */
	@Override
	public Predicate pred(OWLClass concept, boolean doub) {
		if (doub)
			return pred(concept, DOUBLE);
		else
			return pred(concept, ORIGINAL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(org.semanticweb.owlapi.model.OWLClass,
	 * pt.unl.fct.di.centria.nohr.model.terminals.PredicateType)
	 */
	@Override
	public MetaPredicate pred(OWLClass concept, PredicateType type) {
		return new MetaPredicateImpl(pred(concept), type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(org.semanticweb.owlapi.model.OWLPropertyExpression)
	 */
	@Override
	public Predicate pred(OWLPropertyExpression<?, ?> role) {
		final Predicate pred = rolePredicates.get(role);
		if (pred == null)
			throw new UndefinedSymbolException();
		return pred;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(org.semanticweb.owlapi.model.OWLPropertyExpression, boolean)
	 */
	@Override
	public Predicate pred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLE);
		else
			return pred(role, ORIGINAL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(org.semanticweb.owlapi.model.OWLPropertyExpression,
	 * pt.unl.fct.di.centria.nohr.model.terminals.PredicateType)
	 */
	@Override
	public Predicate pred(OWLPropertyExpression<?, ?> role, PredicateType type) {
		return new MetaPredicateImpl(pred(DLUtils.atomic(role)), type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(pt.unl.fct.di.centria.nohr.model.Predicate,
	 * pt.unl.fct.di.centria.nohr.model.terminals.PredicateType)
	 */
	@Override
	public MetaPredicate pred(Predicate predicate, PredicateType type) {
		return new MetaPredicateImpl(predicate, type);
	}

	/*
	 * Create a predicate with a specified symbol and arity.
	 *
	 * @param symbol the symbol, <i>S</i>, that represents the predicate.
	 *
	 * @param arity the arity, <i>n</i> of the predicate.
	 *
	 * @return a predicate, <i>S/n</i> with symbol {@symbol} and arity {@code arity}.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(java.lang.String, int)
	 */
	@Override
	public Predicate pred(String symbol, int arity) {
		final HybridPredicate pred = new RulePredicateImpl(symbol, arity);
		return addPredicate(symbol, arity, pred, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(java.lang.String, int, boolean)
	 */
	@Override
	public Predicate pred(String symbol, int arity, boolean doub) {
		if (doub)
			return pred(symbol, arity, DOUBLE);
		else
			return pred(symbol, arity, ORIGINAL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.terminals.Voc#pred(java.lang.String, int, pt.unl.fct.di.centria.nohr.model.terminals.PredicateType)
	 */
	@Override
	public Predicate pred(String symbol, int arity, PredicateType type) {
		return new MetaPredicateImpl(pred(symbol, arity), type);
	}

	@Override
	public Predicate ranPred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLED_RANGE);
		else
			return pred(role, ORIGINAL_RANGE);
	}

	/**
	 * Registers an occurrence of a given concept.
	 *
	 * @param concept
	 *            the concept.
	 */
	private void register(OWLClass concept) {
		final HybridPredicateWrapper pred = conceptPredicates.get(concept);
		final HybridPredicate conceptPred;
		if (pred == null) {
			conceptPred = new ConceptPredicateImpl(concept);
			conceptPredicates.put(concept, new HybridPredicateWrapper(conceptPred));
			addPredicate(conceptPred.getSymbol(), 1, conceptPred, true);
		} else
			conceptPred = pred.getWrapee();
		for (final String symbol : symbols(concept))
			addPredicate(symbol, 1, conceptPred, true);
		references.add(concept);
	}

	/**
	 * Registers an occurrence of a given individual.
	 *
	 * @param individual
	 */
	private void register(OWLIndividual individual) {
		final HybridConstantWrapper cons = individualConstants.get(individual);
		final HybridConstant individualConstant;
		if (cons == null) {
			individualConstant = new IndividualConstantImpl(individual);
			individualConstants.put(individual, new HybridConstantWrapper(individualConstant));
			addConstant(individualConstant.getSymbol(), individualConstant, true);
		} else
			individualConstant = cons.getWrappe();
		for (final String symbol : symbols(individual))
			addConstant(symbol, individualConstant, true);
		references.add(individual);
	}

	/**
	 * Registers an occurrence of a given role.
	 *
	 * @param role
	 *            a role.
	 */
	private void register(OWLProperty<?, ?> role) {
		final HybridPredicateWrapper pred = rolePredicates.get(role);
		final HybridPredicate rolePred;
		if (pred == null) {
			rolePred = new RolePredicateImpl(role);
			rolePredicates.put(role, new HybridPredicateWrapper(rolePred));
			addPredicate(rolePred.getSymbol(), 2, rolePred, true);
		} else
			rolePred = pred.getWrapee();
		for (final String symbol : symbols(role))
			addPredicate(symbol, 2, rolePred, true);
		references.add(role);
	}

	private boolean removePredicate(String symbol, int arity) {
		final Map<String, HybridPredicateWrapper> map = predicates.get(arity);
		if (map == null)
			return false;
		else
			return map.remove(symbol) != null;

	}

	/**
	 * Returns the symbols that represent a given entity.
	 *
	 * @param entity
	 *            the entity.
	 * @return the set of symbols that represent {@code entity}
	 */
	protected Set<String> symbols(OWLEntity entity) {
		final Set<String> result = new HashSet<>();
		final String fragment = entity.getIRI().toURI().getFragment();
		if (fragment != null)
			result.add(fragment);
		for (final OWLOntology ontology : ontologies)
			for (final OWLAnnotation annotation : entity.getAnnotations(ontology,
					OWLManager.getOWLDataFactory().getRDFSLabel())) {
				final OWLAnnotationValue value = annotation.getValue();
				if (value instanceof OWLLiteral)
					result.add(((OWLLiteral) value).getLiteral());
			}
		return result;
	}

	/**
	 * Returns the symbols that represent a given individual.
	 *
	 * @param individual
	 *            the individual.
	 * @return the set of symbols that represent {@code individual}
	 */
	protected Set<String> symbols(OWLIndividual individual) {
		if (individual.isNamed())
			return symbols((OWLEntity) individual.asOWLNamedIndividual());
		return Collections.<String> emptySet();
	}

	/**
	 * Unregisters an occurrence of a given concept.
	 *
	 * @param concept
	 *            the concept.
	 */
	private void unregister(OWLClass concept) {
		references.remove(concept);
		// if (!references.contains(concept))
		// final Predicate pred = conceptPredicates.remove(concept);
		// removePredicate(pred.getSymbol(), 1);
		// for (final String symbol : symbols(concept))
		// removePredicate(symbol, 1);
	}

	/**
	 * Unregisters an occurrence of a given role.
	 *
	 * @param role
	 *            a role.
	 */
	private void unregister(OWLProperty<?, ?> role) {
		references.remove(role);
		// if (!references.contains(role)) {
		// final Predicate pred = rolePredicates.remove(role);
		// removePredicate(pred.getSymbol(), 2);
		// for (final String symbol : symbols(role))
		// removePredicate(symbol, 2);
		// }
	}

	/**
	 * Unregisters an occurrence of a given individual
	 *
	 * @param individual
	 *            an individual.
	 */
	private void unregiter(OWLIndividual individual) {
		references.remove(individual);
		// if (!references.contains(individual)) {
		// final HybridConstant cons = individualConstants.remove(individual);
		// constants.remove(cons);
		// for (final String symbol : symbols(individual))
		// constants.remove(symbol);
		// }
	}

}

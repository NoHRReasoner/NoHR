/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.terminals;

import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

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

	private final OWLAnnotationProperty LABEL_ANNOTATION = OWLManager.getOWLDataFactory()
			.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

	/** The {@link OWLOntologyChangeListener} that handles concepts, roles, or individual addition or remotion */
	private final OWLOntologyChangeListener ontologyChangeListener;

	/** Maintains a counter of the occurrences, in the ontology, of each concept, role and individual. */
	private final HashMultiset<OWLObject> references;

	/** The ontologies whose concepts, rules, and individuals this {@link Vocabulary} maps. */
	private final Set<OWLOntology> ontologies;

	/** The mapping between symbols and the concepts that they represent. */
	private final Map<String, Predicate> concepts;

	/** The mapping between symbols and the roles that they represent. */
	private final Map<String, Predicate> roles;

	/** The mapping between symbols and the individuals that they represent. */
	private final Map<String, Constant> individuals;

	private final Map<OWLEntity, Predicate> predicates;

	private final Map<OWLIndividual, Constant> constants;

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
		this.ontologies = ontologies;
		references = new HashMultiset<>();
		concepts = new HashMap<>();
		roles = new HashMap<>();
		individuals = new HashMap<>();
		predicates = new HashMap<>();
		constants = new HashMap<>();
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
	}

	/**
	 * Creates a constant representing a specified number.
	 *
	 * @param n
	 *            the number
	 * @return the numeric constant representing {@code n}.
	 */
	@Override
	public Constant cons(Number n) {
		return new NumericConstantImpl(n);
	}

	/**
	 * Create a constant representing a specified OWL individual.
	 *
	 * @param individual
	 *            the OWL individual
	 * @return the constant representing {@code individual}.
	 */
	@Override
	public Constant cons(OWLIndividual individual) {
		return new IndividualConstantImpl(individual);
	}

	/**
	 * Create a constant representing a specified OWL literal.
	 *
	 * @param literal
	 *            the OWL literal.
	 * @return the constant representing {@code literal}.
	 */
	@Override
	public Constant cons(OWLLiteral literal) {
		return new LiteralConstantImpl(literal);
	}

	/**
	 * Create a constant representing a OWL individual or OWL literal.
	 *
	 * @param object
	 *            the OWL individual or OWL literal.
	 * @return the constant representing {@code object}.
	 */
	@Override
	public Constant cons(OWLPropertyAssertionObject object) {
		if (object instanceof OWLIndividual)
			return cons((OWLIndividual) object);
		else if (object instanceof OWLLiteral)
			return cons((OWLLiteral) object);
		else
			return null;
	}

	/**
	 * Creates a constant representing a specified symbol. If the symbol is a number, then the created constant is an numeric constant.
	 *
	 * @param symbol
	 *            the symbol.
	 * @return a numeric constant representing {@code symbol} if {@code symbol} is a number; or a rule constant representing {@code symbol},
	 *         otherwise.
	 */

	@Override
	public Constant cons(String symbol) {
		try {
			final Double number = Double.valueOf(symbol);
			return cons(number);
		} catch (final NumberFormatException e) {
			return new RuleConstantImpl(symbol);
		}
	}

	/**
	 * Creates a constant representation of a specified symbol, given a specified {@link Vocabulary}. If the symbol is a number, then the created
	 * constant is an numeric constant.
	 *
	 * @param symbol
	 *            the symbol.
	 * @param vocabularyMapping
	 *            {@link Vocabulary}
	 * @return a numeric constant representing {@code symbol} if {@code symbol} is a number; or a constant representation of {@code symbol}, given
	 *         {@link Vocabulary}, otherwise.
	 */

	@Override
	public Constant cons(String symbol, Vocabulary vocabularyMapping) {
		if (vocabularyMapping != null) {
			final Constant individual = vocabularyMapping.getIndividual(symbol);
			if (individual != null)
				return individual;
		}
		return cons(symbol);
	}

	/**
	 * Create a domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return the domain meta-predicate <i>DP</i>, if {@code doub} is true; the double domain meta-predicate, <i>DP<sup>d</sup></i>, otherwise.
	 */
	@Override
	public Predicate domPred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLE_DOMAIN);
		else
			return pred(role, ORIGINAL_DOMAIN);
	}

	/**
	 * Create a double domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            the role <i>P</i>.
	 * @return the meta-predicate <i>DP<sup>d</sup></i>.
	 */
	@Override
	public Predicate doubDomPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLE_DOMAIN);
	}

	/**
	 * Create a double meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            the concept <i>A</i>.
	 * @return the double meta-predicate <i>A<sup>d</sup></i>.
	 */
	@Override
	public Predicate doubPred(OWLClass concept) {
		return pred(concept, DOUBLE);
	}

	/**
	 * Create a double meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the meta-predicate <i>P<sup>d</sup></i>.
	 */
	@Override
	public Predicate doubPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLE);
	}

	/**
	 * Create a double meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate symbol, <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i>, of the predicate that {@code symbol} represents.
	 * @return the double meta-predicate <i>S<sup>d</sup>/n</i>.
	 */
	@Override
	public Predicate doubPred(String symbol, int arity) {
		return pred(symbol, arity, DOUBLE);
	}

	/**
	 * Create a double range meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role, <i>P</i>.
	 * @return the double range meta-predicate <i>DP<sup>d</sup></i>.
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

	@Override
	public Predicate getConcept(String symbol) {
		return concepts.get(symbol);
	}

	@Override
	public Constant getIndividual(String symbol) {
		return individuals.get(symbol);
	}

	@Override
	public Set<OWLOntology> getOntologies() {
		return ontologies;
	}

	@Override
	public Predicate getRole(String symbol) {
		return roles.get(symbol);
	}

	/**
	 * Create a negative meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A<i>.
	 * @return a negative meta-predicate <i>NA</i>.
	 */
	@Override
	public Predicate negPred(OWLClass concept) {
		return pred(concept, NEGATIVE);
	}

	/**
	 * Create a negative meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the negative meta-predicate <i>NP</i>.
	 */
	@Override
	public Predicate negPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, NEGATIVE);
	}

	/**
	 * Create a negative meta-predicate from a specified predicate.
	 *
	 * @param predicate
	 *            a predicate <i>P</i> or a meta-predicate <i>NP</i>, <i>DP</i>, <i>RP</i>, <i>DP<sup>d</sup></i> or <i>RP<sup>d</sup></i>.
	 * @return the meta-predicate <i>NP</i>.
	 */
	@Override
	public Predicate negPred(Predicate predicate) {
		Predicate pred = predicate;
		if (predicate instanceof MetaPredicate)
			pred = ((MetaPredicate) predicate).getPredicate();
		return new MetaPredicateImpl(pred, NEGATIVE);
	}

	/**
	 * Create a negative meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a symbol, <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i>, of the predicate that {@code symbol} represents.
	 * @return the negative meta-predicate <i>NS/n</i>.
	 */
	@Override
	public Predicate negPred(String symbol, int arity) {
		return pred(symbol, arity, NEGATIVE);
	}

	/**
	 * Create an original domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the original domain meta-predicate <i>DP</i>.
	 */
	@Override
	public Predicate origDomPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL_DOMAIN);
	}

	/**
	 * Create an original meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @return the original meta-predicate <i>A</i>.
	 */
	@Override
	public Predicate origPred(OWLClass concept) {
		return pred(concept, ORIGINAL);
	}

	/**
	 * Create an original meta-predicate from a specifieid role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the original meta-predicate <i>P</i>.
	 */
	@Override
	public Predicate origPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL);
	}

	/**
	 * Create an original meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate symbol <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i> of the predicate that {@code symbol} represents.
	 * @return the original meta-predicate <i>S</i>.
	 */
	@Override
	public Predicate origPred(String symbol, int arity) {
		return pred(symbol, arity, ORIGINAL);
	}

	/**
	 * Create an original range meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the range original meta-predicate <i>RP</i>.
	 */
	@Override
	public Predicate origRanPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL_RANGE);
	}

	/**
	 * Create a predicate representing a specified concept.
	 *
	 * @param concept
	 *            a concept.
	 * @return the predicate representing {@code concept}.
	 */
	@Override
	public Predicate pred(OWLClass concept) {
		return new ConceptPredicateImpl(concept);
	}

	/**
	 * Create a meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return <i>A<sup>d</sup></i> if {@code doub} is true; <i>A</i>, otherwise.
	 */
	@Override
	public Predicate pred(OWLClass concept, boolean doub) {
		if (doub)
			return pred(concept, DOUBLE);
		else
			return pred(concept, ORIGINAL);
	}

	/**
	 * Create a meta-predicate from a specified concept of a specified type.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @param type
	 *            a type. Shoudln't represent a quantification (i.e. {@code type.}{@link PredicateType#isQuantification() isQuantification()} must be
	 *            false).
	 * @return <i>A</i> if {@code type} is {@link PredicateType#ORIGINAL original}; <br>
	 *         <i>A<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLE double}; <br>
	 *         <i>NA</i> if {@code type} is {@link PredicateType#NEGATIVE negative}.
	 * @throws IllegalArgumentException
	 *             if {@code type.}{@link PredicateType#isQuantification() isQuantification()} is true.
	 */
	@Override
	public MetaPredicate pred(OWLClass concept, PredicateType type) {
		return new MetaPredicateImpl(pred(concept), type);
	}

	/**
	 * Create a predicate representing a specified role.
	 *
	 * @param role
	 *            a role.
	 * @return the predicate representing {@code role}.
	 */
	@Override
	public Predicate pred(OWLPropertyExpression<?, ?> role) {
		return new RolePredicateImpl(DLUtils.atomic(role));
	}

	/**
	 * Create a meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>
	 * @param doub
	 *            specified whether the meta-predicate if of a double type.
	 * @return <i>P<sup>d</sup></i> if {@code doub} is true; <i>P</i>, otherwise.
	 */
	@Override
	public Predicate pred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLE);
		else
			return pred(role, ORIGINAL);
	}

	/**
	 * Create a meta-predicate from a specified role of a specified type.
	 *
	 * @param role
	 *            a concept <i>P</i>.
	 * @param type
	 *            a type.
	 * @return <i>P</i> if {@code type} is {@link PredicateType#ORIGINAL original}; <br>
	 *         <i>P<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLE double}; <br>
	 *         <i>NP</i> if {@code type} is {@link PredicateType#NEGATIVE negative}; <br>
	 *         <i>DP</i> if {@code type} is {@link PredicateType#ORIGINAL_DOMAIN original domain}; <br>
	 *         <i>RP</i> if {@code type} is {@link PredicateType#ORIGINAL_RANGE original range} ; <br>
	 *         <i>DP<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLE_DOMAIN double domain}; <br>
	 *         <i>RP<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLED_RANGE double range}.
	 */
	@Override
	public Predicate pred(OWLPropertyExpression<?, ?> role, PredicateType type) {
		return new MetaPredicateImpl(new RolePredicateImpl(DLUtils.atomic(role)), type);
	}

	/**
	 * Create a meta-predicate form a specified predicate with a specified type.
	 *
	 * @param predicate
	 *            the predicate that the meta-predicate refers.
	 * @param type
	 *            the type of the meta-predicate.
	 * @return a meta-predicate referring {@code predicate} with type {@code type}.
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
	@Override
	public Predicate pred(String symbol, int arity) {
		return new RulePredicateImpl(symbol, arity);
	}

	/**
	 * Create a meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate <i>S</i>.
	 * @param arity
	 *            a predicate arity <i>n</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return the double meta-predicate <i>S/n</i> if {@code doub} is true; the original meta-predicate <i>S<sup>d</sup>/n</i>, otherwise.
	 */
	@Override
	public Predicate pred(String symbol, int arity, boolean doub) {
		if (doub)
			return pred(symbol, arity, DOUBLE);
		else
			return pred(symbol, arity, ORIGINAL);
	}

	/**
	 * Create a meta-predicate from a specified predicate symbol and predicate arity with a specified type.
	 *
	 * @param symbol
	 *            a predicate symbol <i>S</i>.
	 * @param arity
	 *            a predicate arity <i>n</i>.
	 * @param type
	 *            the type of the meta-predicate. Shoudn't represent a quantification (i.e. {@code type.} {@link PredicateType#isQuantification()
	 *            isQuantification()} must be false).
	 * @return <i>S/n</i> if {@code type} is {@link PredicateType#ORIGINAL original}; <br>
	 *         <i>S<sup>d</sup>/n</i>, if {@code type} is {@link PredicateType#DOUBLE double}; <br>
	 *         <i>NS/n</i>, if {@code type} is {@link PredicateType#NEGATIVE negative}.
	 * @throw IllegalArgumentException if {@code type.} {@link PredicateType#isQuantification() isQuantification()} is true.
	 */
	@Override
	public Predicate pred(String symbol, int arity, PredicateType type) {
		return new MetaPredicateImpl(pred(symbol, arity), type);
	}

	/**
	 * Create the predicate represented by a specified symbol with a specified arity, given a specified {@link Vocabulary}.
	 *
	 * @param symbol
	 *            a symbol <i>S</i>.
	 * @param arity
	 *            an arity <i>n</i>.
	 * @param vocabularyMapping
	 *            a {@link Vocabulary}.
	 * @return the predicate representing a concept <i>A</i> if {@code arity} is {@literal 1} and {@code vocabularyMapping.}
	 *         {@link Vocabulary#getConcept(String) getConcept(symbol)} returns <i>A</i>; <br>
	 *         the predicate representing a role <i>P</i> if {@code arity} is {@literal 2} and {@code vocabularyMapping.}
	 *         {@link Vocabulary#getRole(String) getRole(symbol)} returns the role <i>P</i>; <br>
	 *         the predicate represented by {@code symbol} with arity {@code arity}, otherwise.
	 */

	@Override
	public Predicate pred(String symbol, int arity, Vocabulary vocabularyMapping) {
		if (vocabularyMapping == null)
			return pred(symbol, arity);
		if (arity == 1) {
			final Predicate concept = vocabularyMapping.getConcept(symbol);
			if (concept != null)
				return concept;
		}
		if (arity == 2) {
			final Predicate role = vocabularyMapping.getRole(symbol);
			if (role != null)
				return role;
		}
		return pred(symbol, arity);
	}

	/**
	 * Create a range meta-predicate from a specified {@code role}.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @param doub
	 *            specifies whether this role is of a double type.
	 * @return <i>RP<sup>d</sup></i> if {@code doub} is true; <i>RP</i>, otherwise.
	 */
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
		final Predicate pred = pred(concept);
		predicates.put(concept, pred);
		concepts.put(pred.getSymbol(), pred);
		for (final String symbol : symbols(concept))
			concepts.put(symbol, pred);
		references.add(concept);
	}

	/**
	 * Registers an occurrence of a given individual.
	 *
	 * @param individual
	 */
	private void register(OWLIndividual individual) {
		final Constant cons = cons(individual);
		constants.put(individual, cons);
		individuals.put(cons.getSymbol(), cons);
		for (final String symbol : symbols(individual))
			individuals.put(symbol, cons);
		references.add(individual);
	}

	/**
	 * Registers an occurrence of a given role.
	 *
	 * @param role
	 *            a role.
	 */
	private void register(OWLProperty<?, ?> role) {
		final Predicate pred = pred(role);
		predicates.put(role, pred);
		roles.put(pred.getSymbol(), pred);
		for (final String symbol : symbols(role))
			roles.put(symbol, pred);
		references.add(role);
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
			for (final OWLAnnotation annotation : entity.getAnnotations(ontology, LABEL_ANNOTATION)) {
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
		if (!references.contains(concept)) {
			final Predicate pred = predicates.remove(concept);
			concepts.remove(pred.getSymbol());
			for (final String symbol : symbols(concept))
				concepts.remove(symbol);
		}
	}

	/**
	 * Unregisters an occurrence of a given role.
	 *
	 * @param role
	 *            a role.
	 */
	private void unregister(OWLProperty<?, ?> role) {
		references.remove(role);
		if (!references.contains(role)) {
			final Predicate pred = predicates.remove(role);
			roles.remove(pred.getSymbol());
			for (final String symbol : symbols(role))
				roles.put(symbol, pred(role));
		}
	}

	/**
	 * Unregisters an occurrence of a given individual
	 *
	 * @param individual
	 *            an individual.
	 */
	private void unregiter(OWLIndividual individual) {
		references.remove(individual);
		if (!references.contains(individual)) {
			final Constant cons = constants.remove(individual);
			individuals.remove(cons);
			for (final String symbol : symbols(individual))
				individuals.remove(symbol);
		}
	}

}

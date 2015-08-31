/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import static pt.unl.fct.di.centria.nohr.model.concrete.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

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
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import pt.unl.fct.di.centria.nohr.HashMultiset;

/**
 * An implementation of {@link VocabularyMapping} where the concepts and rules are represented by the fragment of their IRIs and the individuals by
 * their node IDs
 *
 * @author Nuno Costa
 */
public class DefaultVocabularyMapping implements VocabularyMapping {

	private static final OWLAnnotationProperty LABEL_ANNOTATION = OWLManager.getOWLDataFactory()
			.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

	/** The {@link OWLOntologyChangeListener} that handles concepts, roles, or individual addition or remotion */
	private final OWLOntologyChangeListener ontologyChangeListener;

	/** Maintains a counter of the occurrences, in the ontology, of each concept, role and individual. */
	private final HashMultiset<OWLObject> references;

	/** The ontologies whose concepts, rules, and individuals this {@link VocabularyMapping} maps. */
	private final Set<OWLOntology> ontologies;

	/** The mapping between symbols and the concepts that they represent. */
	private final Map<String, HybridPredicate> concepts;

	/** The mapping between symbols and the roles that they represent. */
	private final Map<String, HybridPredicate> roles;

	/** The mapping between symbols and the individuals that they represent. */
	private final Map<String, Constant> individuals;

	private final Map<OWLEntity, HybridPredicate> predicates;

	private final Map<OWLIndividual, Constant> constants;

	public DefaultVocabularyMapping(OWLOntology ontology) {
		this(ontology.getImportsClosure());
	}

	/**
	 * Constructs a {@link DefaultVocabularyMapping} for a given set of ontologies;
	 *
	 * @param ontologies
	 *            the set of ontologies.
	 */
	public DefaultVocabularyMapping(final Set<OWLOntology> ontologies) {
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

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		for (final OWLOntology ontology : ontologies)
			ontology.getOWLOntologyManager().removeOntologyChangeListener(ontologyChangeListener);
	}

	@Override
	public HybridPredicate getConcept(String symbol) {
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
	public HybridPredicate getRole(String symbol) {
		return roles.get(symbol);
	}

	/**
	 * Registers an occurrence of a given concept.
	 *
	 * @param concept
	 *            the concept.
	 */
	private void register(OWLClass concept) {
		final HybridPredicate pred = pred(concept);
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
		final HybridPredicate pred = pred(role);
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
			final HybridPredicate pred = predicates.remove(concept);
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
			final HybridPredicate pred = predicates.remove(role);
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

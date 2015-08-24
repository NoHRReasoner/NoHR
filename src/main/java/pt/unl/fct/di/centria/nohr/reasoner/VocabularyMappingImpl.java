/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.HashMultiset;

/**
 * An implementation of {@link VocabularyMapping} where the concepts and rules are represented by the fragment of their IRIs and the individuals by
 * their node IDs
 *
 * @author Nuno Costa
 */
public class VocabularyMappingImpl implements VocabularyMapping {

	/** The {@link OWLOntologyChangeListener} that handles concepts, roles, or individual addition or remotion */
	private final OWLOntologyChangeListener ontologyChangeListener;

	/** Maintains a counter of the occurrences, in the ontology, of each concept, role and individual. */
	private final HashMultiset<OWLObject> references;

	/** The ontologies whose concepts, rules, and individuals this {@link VocabularyMapping} maps. */
	private final Set<OWLOntology> ontologies;

	/** The mapping between symbols and the concepts that they represent. */
	private final Map<String, OWLClass> concepts;

	/** The mapping between symbols and the roles that they represent. */
	private final Map<String, OWLProperty<?, ?>> roles;

	/** The mapping between symbols and the individuals that they represent. */
	private final Map<String, OWLIndividual> individuals;

	public VocabularyMappingImpl(OWLOntology ontology) {
		this(Collections.singleton(ontology));
	}

	/**
	 * Constructs a {@link VocabularyMappingImpl} for a given set of ontologies;
	 *
	 * @param ontologies
	 *            the set of ontologies.
	 */
	public VocabularyMappingImpl(final Set<OWLOntology> ontologies) {
		this.ontologies = ontologies;
		references = new HashMultiset<>();
		concepts = new HashMap<>();
		roles = new HashMap<>();
		individuals = new HashMap<>();
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
							for (final OWLClass concept : change.getAxiom().getClassesInSignature())
								register(concept);
							for (final OWLObjectProperty role : change.getAxiom().getObjectPropertiesInSignature())
								register(role);
							for (final OWLDataProperty role : change.getAxiom().getDataPropertiesInSignature())
								register(role);
							for (final OWLIndividual individual : change.getAxiom().getIndividualsInSignature())
								register(individual);
						} else if (change.isRemoveAxiom()) {
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
	public OWLClass getConcept(String symbol) {
		return concepts.get(symbol);
	}

	@Override
	public OWLIndividual getIndividual(String symbol) {
		return individuals.get(symbol);
	}

	@Override
	public Set<OWLOntology> getOntologies() {
		return ontologies;
	}

	@Override
	public OWLProperty<?, ?> getRole(String symbol) {
		return roles.get(symbol);
	}

	/**
	 * Registers an occurrence of a given concept.
	 *
	 * @param concept
	 *            the concept.
	 */
	private void register(OWLClass concept) {
		for (final String symbol : symbols(concept))
			concepts.put(symbol, concept);
		references.add(concept);
	}

	/**
	 * Registers an occurrence of a given individual.
	 *
	 * @param individual
	 */
	private void register(OWLIndividual individual) {
		for (final String symbol : symbols(individual))
			individuals.put(symbol, individual);
		references.add(individual);
	}

	/**
	 * Registers an occurrence of a given role.
	 *
	 * @param role
	 *            a role.
	 */
	private void register(OWLProperty<?, ?> role) {
		for (final String symbol : symbols(role))
			roles.put(symbol, role);
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
		result.add(entity.getIRI().toURI().getFragment());
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
		final Set<String> result = new HashSet<>();
		result.add(individual.toStringID());
		return result;
	}

	/**
	 * Unregisters an occurrence of a given concept.
	 *
	 * @param concept
	 *            the concept.
	 */
	private void unregister(OWLClass concept) {
		references.remove(concept);
		if (!references.contains(concept))
			for (final String symbol : symbols(concept))
				concepts.remove(symbol);
	}

	/**
	 * Unregisters an occurrence of a given role.
	 *
	 * @param role
	 *            a role.
	 */
	private void unregister(OWLProperty<?, ?> role) {
		references.remove(role);
		if (!references.contains(role))
			for (final String symbol : symbols(role))
				roles.put(symbol, role);
	}

	/**
	 * Unregisters an occurrence of a given individual
	 *
	 * @param individual
	 *            an individual.
	 */
	private void unregiter(OWLIndividual individual) {
		references.remove(individual);
		if (!references.contains(individual))
			for (final String symbol : symbols(individual))
				individuals.remove(symbol);
	}

}

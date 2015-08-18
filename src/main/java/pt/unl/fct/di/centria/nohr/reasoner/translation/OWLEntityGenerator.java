/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.Arrays;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * To generate new concepts or roles to a given ontology.
 *
 * @author Nuno Costa
 */
public class OWLEntityGenerator {

	/** The character that fills the {@link #filler} string */
	private static final char FILLER_CHAR = '0';

	/** The entities counter which ensures that the generated concepts and roles are different from each other */
	private int counter;

	/**
	 * A string with a size greater than all the ontology's concepts and roles names sizes, in order to ensure that when prefixed to a new concept or
	 * role name that name will be different from all ontology's concept and role names.
	 */
	private final String filler;

	/**
	 * The ontology for which this {@link OWLEntityGenerator} generate entities.
	 */
	private final OWLOntology ontology;

	/**
	 * Constructs a {@link OWLEntityGenerator} for a given ontology.
	 *
	 * @param ontology
	 *            a ontology.
	 */
	public OWLEntityGenerator(final OWLOntology ontology) {
		this.ontology = ontology;
		counter = 0;
		int maxNameLength = 0;
		for (final OWLEntity entity : ontology.getSignature()) {
			final int len = entity.getIRI().getFragment().length();
			if (len > maxNameLength)
				maxNameLength = len;
		}
		final char[] fillerChars = new char[maxNameLength];
		Arrays.fill(fillerChars, FILLER_CHAR);
		filler = new String(fillerChars);
	}

	/**
	 * Generate a new IRI that for {@link #ontology}.
	 *
	 * @return an IRI {@code <ontologyIRI#newFrament>}, where {@code ontologyIRI} is the {@link #ontology}'s IRI and {@code newFragment} a string that
	 *         don't occur in none {@link #ontology} entity IRI as fragment.
	 */
	private IRI generateIRI() {
		final String ontologyIRI = ontology.getOntologyID().getOntologyIRI().toString();
		final IRI iri = IRI.create(ontologyIRI + "#", filler + counter++);
		assert iri.toURI().getFragment() != null;
		return iri;
	}

	/**
	 * Generate a new concept that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 *
	 * @return a new concept that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 */
	public OWLClass generateNewConcept() {
		final OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		final OWLClass concept = dataFactory.getOWLClass(generateIRI());
		final OWLDeclarationAxiom declaration = dataFactory.getOWLDeclarationAxiom(concept);
		ontology.getOWLOntologyManager().addAxiom(ontology, declaration);
		return concept;
	}

	/**
	 * Generate a new role that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 *
	 * @return a new role that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 */
	public OWLObjectProperty generateNewRole() {
		final OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		final OWLObjectProperty role = dataFactory.getOWLObjectProperty(generateIRI());
		final OWLDeclarationAxiom declaration = dataFactory.getOWLDeclarationAxiom(role);
		ontology.getOWLOntologyManager().addAxiom(ontology, declaration);
		return role;
	}

}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.Arrays;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Nuno Costa
 */
public class ConcetpsGenerator {

	private static final char FILLER_CHAR = '0';

	private int counter;

	private final String filler;

	private final OWLOntology ontology;

	/**
	 *
	 */
	public ConcetpsGenerator(final OWLOntology ontology) {
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

	public OWLClass generateNewConcept() {
		final OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		final String ontologyIRI = ontology.getOntologyID().getOntologyIRI().toString();
		final IRI iri = IRI.create(ontologyIRI + "#", filler + counter++);
		return dataFactory.getOWLClass(iri);
	}

}

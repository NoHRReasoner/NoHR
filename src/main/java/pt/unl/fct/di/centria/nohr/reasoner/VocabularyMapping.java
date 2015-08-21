/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * A mapping between symbols (strings) and the concepts, roles, or individuals, of a specified set of ontologies, that they represent.
 *
 * @author Nuno Costa
 */
public interface VocabularyMapping {

	/**
	 * Get the concept represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the concept represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any concept.
	 */
	public OWLClass getConcept(String symbol);

	/**
	 * Get the individual represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the individual represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any individual.
	 */
	public OWLIndividual getIndividual(String symbol);

	/**
	 * The ontologies whose concepts, roles and individuals this {@link VocabularyMapping} mapps.
	 *
	 * @return
	 */
	public Set<OWLOntology> getOntologies();

	/**
	 * Get the role represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the role represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any role.
	 */
	public OWLProperty<?, ?> getRole(String symbol);
}

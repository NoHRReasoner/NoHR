/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

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
	public HybridPredicate getConcept(String symbol);

	/**
	 * Get the individual represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the individual represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any individual.
	 */
	public Constant getIndividual(String symbol);

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
	public HybridPredicate getRole(String symbol);
}

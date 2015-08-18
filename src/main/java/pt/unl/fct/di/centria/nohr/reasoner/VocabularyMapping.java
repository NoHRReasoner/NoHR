/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * @author nunocosta
 */
public interface VocabularyMapping {

	public void addConcept(OWLClass concept);

	public void addIndividual(OWLIndividual individual);

	public void addRole(OWLProperty<?, ?> role);

	public OWLClass getConcept(String symbol);

	public OWLIndividual getIndividual(String symbol);

	public OWLProperty<?, ?> getRole(String symbol);
}

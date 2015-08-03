/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * @author nunocosta
 *
 */
public interface OntologyIndex {

    public OWLClass getConcept(String symbol);

    public OWLIndividual getIndividual(String symbol);

    public OWLProperty<?, ?> getRole(String symbol);
}

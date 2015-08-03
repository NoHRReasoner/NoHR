/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * @author nunocosta
 *
 */
public class OntologyIndexImpl implements OntologyIndex {

    private final Map<String, OWLClass> concepts;

    private final Map<String, OWLProperty<?, ?>> roles;

    private final Map<String, OWLIndividual> individuals;

    /**
     *
     */
    public OntologyIndexImpl(OWLOntology ontology) {
	concepts = new HashMap<>();
	roles = new HashMap<>();
	individuals = new HashMap<>();
	for (final OWLClass c : ontology.getClassesInSignature())
	    concepts.put(c.getIRI().getFragment(), c);
	for (final OWLProperty<?, ?> r : ontology.getObjectPropertiesInSignature())
	    roles.put(r.getIRI().getFragment(), r);
	for (final OWLProperty<?, ?> d : ontology.getDataPropertiesInSignature())
	    roles.put(d.getIRI().getFragment(), d);
	for (final OWLIndividual i : ontology.getIndividualsInSignature())
	    individuals.put(i.toStringID(), i);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.reasoner.OntologyIndex#getConcept(java.lang.
     * String)
     */
    @Override
    public OWLClass getConcept(String symbol) {
	return concepts.get(symbol);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.reasoner.OntologyIndex#getIndividual(java.lang
     * .String)
     */
    @Override
    public OWLIndividual getIndividual(String symbol) {
	return individuals.get(symbol);
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.reasoner.OntologyIndex#getRole(java.lang.
     * String)
     */
    @Override
    public OWLProperty<?, ?> getRole(String symbol) {
	return roles.get(symbol);
    }

}

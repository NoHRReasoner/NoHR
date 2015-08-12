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
public class VocabularyMappingImpl implements VocabularyMapping {

    private final Map<String, OWLClass> concepts;

    private final Map<String, OWLProperty<?, ?>> roles;

    private final Map<String, OWLIndividual> individuals;

    /**
     *
     */
    public VocabularyMappingImpl(OWLOntology ontology) {
	concepts = new HashMap<>();
	roles = new HashMap<>();
	individuals = new HashMap<>();
	for (final OWLClass c : ontology.getClassesInSignature())
	    addConcept(c);
	for (final OWLProperty<?, ?> r : ontology.getObjectPropertiesInSignature())
	    addRole(r);
	for (final OWLProperty<?, ?> d : ontology.getDataPropertiesInSignature())
	    addRole(d);
	for (final OWLIndividual i : ontology.getIndividualsInSignature())
	    addIndividual(i);
    }

    @Override
    public void addConcept(OWLClass concept) {
	concepts.put(concept.getIRI().getFragment(), concept);
    }

    @Override
    public void addIndividual(OWLIndividual individual) {
	individuals.put(individual.toStringID(), individual);
    }

    @Override
    public void addRole(OWLProperty<?, ?> role) {
	roles.put(role.getIRI().getFragment(), role);
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
    public OWLProperty<?, ?> getRole(String symbol) {
	return roles.get(symbol);
    }

}

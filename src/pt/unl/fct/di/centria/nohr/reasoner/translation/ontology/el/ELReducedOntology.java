/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

/**
 * @author nunocosta
 *
 */
public interface ELReducedOntology {

    public Set<OWLSubPropertyChainOfAxiom> getChainSubsumptions();

    public Set<OWLClassAssertionAxiom> getConceptAssertions();

    public Set<OWLSubClassOfAxiom> getConceptSubsumptions();

    public Set<OWLDataPropertyAssertionAxiom> getDataAssertion();

    public Set<OWLSubDataPropertyOfAxiom> getDataSubsuptions();

    public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions();

    public Set<OWLSubObjectPropertyOfAxiom> getRoleSubsumptions();

    public boolean hasDisjunction();

}

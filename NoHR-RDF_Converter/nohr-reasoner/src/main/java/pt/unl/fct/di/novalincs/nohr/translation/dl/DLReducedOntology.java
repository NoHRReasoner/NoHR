package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

public interface DLReducedOntology {

    Set<OWLClassAssertionAxiom> getClassAssertionAxioms();

    Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms();

    Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms();

    Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms();

    Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms();

    Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms();

    OWLOntology getReducedOWLOntology();

    Set<OWLSubClassOfAxiom> getSubClassOfAxioms();

    Set<OWLSubDataPropertyOfAxiom> getSubDataPropertyOfAxioms();

    Set<OWLSubObjectPropertyOfAxiom> getSubObjectPropertyOfAxioms();

    Set<OWLSubPropertyChainOfAxiom> getSubPropertyChainOfAxioms();

}

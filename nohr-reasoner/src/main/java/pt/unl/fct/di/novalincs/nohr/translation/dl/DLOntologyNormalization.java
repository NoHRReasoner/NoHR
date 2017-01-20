package pt.unl.fct.di.novalincs.nohr.translation.dl;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

public interface DLOntologyNormalization {

    public Iterable<OWLSubPropertyChainOfAxiom> chainSubsumptions();

    public Iterable<OWLClassAssertionAxiom> conceptAssertions();

    public Iterable<OWLDisjointClassesAxiom> conceptDisjunctions();

    public Iterable<OWLSubClassOfAxiom> conceptSubsumptions();

    public Iterable<OWLDataPropertyAssertionAxiom> dataAssertions();

    public Iterable<OWLDisjointDataPropertiesAxiom> dataDisjunctions();

    public Iterable<OWLSubDataPropertyOfAxiom> dataSubsumptions();

    public boolean hasDisjunctions();

    public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions();

    public Iterable<OWLDisjointObjectPropertiesAxiom> roleDisjunctions();

    public Iterable<OWLSubObjectPropertyOfAxiom> roleSubsumptions();

}

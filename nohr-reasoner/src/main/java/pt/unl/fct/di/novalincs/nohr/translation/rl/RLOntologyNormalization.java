package pt.unl.fct.di.novalincs.nohr.translation.rl;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public interface RLOntologyNormalization {

    Iterable<OWLClassAssertionAxiom> conceptAssertions();

    Iterable<OWLSubClassOfAxiom> conceptSubsumptions();

    Iterable<OWLDisjointClassesAxiom> conceptDisjunctions();

    Iterable<OWLDataPropertyAssertionAxiom> dataAssertions();

    Iterable<OWLDisjointDataPropertiesAxiom> dataDisjunctions();

    Iterable<OWLSubDataPropertyOfAxiom> dataSubsumptions();

    boolean hasDisjunctions();

    Iterable<OWLIrreflexiveObjectPropertyAxiom> irreflexiveRoles();

    Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions();

    Iterable<OWLDisjointObjectPropertiesAxiom> roleDisjunctions();

    Iterable<OWLSubObjectPropertyOfAxiom> roleSubsumptions();

}

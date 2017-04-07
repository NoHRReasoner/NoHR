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

    AxiomType<?>[] SUPPORTED_AXIOM_TYPES = new AxiomType<?>[]{
        AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
        AxiomType.CLASS_ASSERTION,
        AxiomType.DATA_PROPERTY_ASSERTION,
        AxiomType.DATA_PROPERTY_DOMAIN,
        AxiomType.DECLARATION,
        AxiomType.DISJOINT_CLASSES,
        AxiomType.DISJOINT_DATA_PROPERTIES,
        AxiomType.DISJOINT_OBJECT_PROPERTIES,
        AxiomType.EQUIVALENT_CLASSES,
        AxiomType.EQUIVALENT_DATA_PROPERTIES,
        AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
        AxiomType.INVERSE_OBJECT_PROPERTIES,
        AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
        AxiomType.OBJECT_PROPERTY_ASSERTION,
        AxiomType.OBJECT_PROPERTY_DOMAIN,
        AxiomType.OBJECT_PROPERTY_RANGE,
        AxiomType.SUB_DATA_PROPERTY,
        AxiomType.SUB_OBJECT_PROPERTY,
        AxiomType.SUB_PROPERTY_CHAIN_OF,
        AxiomType.SYMMETRIC_OBJECT_PROPERTY,
        AxiomType.SUBCLASS_OF,
        AxiomType.TRANSITIVE_OBJECT_PROPERTY
    };

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

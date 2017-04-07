package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.Collection;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.translation.UnsupportedAxiomException;

public interface RLAxiomTranslator {

    // ClassAxiom
    Collection<Rule> translate(OWLSubClassOfAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLEquivalentClassesAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDisjointClassesAxiom axiom) throws UnsupportedAxiomException;

    // ObjectPropertyAxiom
    Collection<Rule> translate(OWLSubObjectPropertyOfAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLEquivalentObjectPropertiesAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDisjointObjectPropertiesAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLInverseObjectPropertiesAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLObjectPropertyDomainAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLObjectPropertyRangeAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLFunctionalObjectPropertyAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLInverseFunctionalObjectPropertyAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLIrreflexiveObjectPropertyAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLSymmetricObjectPropertyAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLAsymmetricObjectPropertyAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLTransitiveObjectPropertyAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLSubPropertyChainOfAxiom axiom) throws UnsupportedAxiomException;

    // DataPropertyAxiom
    Collection<Rule> translate(OWLSubDataPropertyOfAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLEquivalentDataPropertiesAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDisjointDataPropertiesAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDataPropertyDomainAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDataPropertyRangeAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLFunctionalDataPropertyAxiom axiom) throws UnsupportedAxiomException;

    // Assertion
    Collection<Rule> translate(OWLSameIndividualAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDifferentIndividualsAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLClassAssertionAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLObjectPropertyAssertionAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLNegativeObjectPropertyAssertionAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLDataPropertyAssertionAxiom axiom) throws UnsupportedAxiomException;

    Collection<Rule> translate(OWLNegativeDataPropertyAssertionAxiom axiom) throws UnsupportedAxiomException;

}

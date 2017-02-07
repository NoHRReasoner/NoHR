package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.Collection;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Rule;

public interface RLAxiomTranslator {

    Collection<Rule> translate(OWLClassAssertionAxiom axiom);

    Collection<Rule> translate(OWLDisjointClassesAxiom axiom);

    Collection<Rule> translate(OWLDisjointDataPropertiesAxiom axiom);

    Collection<Rule> translate(OWLDisjointObjectPropertiesAxiom axiom);

    Collection<Rule> translate(OWLIrreflexiveObjectPropertyAxiom axiom);

    Collection<Rule> translate(OWLObjectPropertyDomainAxiom axiom);

    Collection<Rule> translate(OWLObjectPropertyRangeAxiom axiom);

    Collection<Rule> translate(OWLPropertyAssertionAxiom axiom);

    Collection<Rule> translate(OWLSubClassOfAxiom axiom);

    Collection<Rule> translate(OWLSubPropertyAxiom axiom);

    Collection<Rule> translate(OWLTransitiveObjectPropertyAxiom axiom);

}

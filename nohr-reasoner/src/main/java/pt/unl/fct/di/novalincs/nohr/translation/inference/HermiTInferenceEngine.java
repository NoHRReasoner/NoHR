package pt.unl.fct.di.novalincs.nohr.translation.inference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class HermiTInferenceEngine implements ELInferenceEngine, QLInferenceEngine {

    private final OWLOntology ontology;
    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory dataFactory;

    private final Set<OWLProperty> irreflexiveRoles;
    private final Set<OWLClass> unsatisfiableConcepts;
    private final Set<OWLProperty> unsatisfiableRoles;

    public HermiTInferenceEngine(OWLOntology ontology) {
        this.ontology = ontology;
        ontologyManager = ontology.getOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();

        irreflexiveRoles = new HashSet<>();
        unsatisfiableConcepts = new HashSet<>();
        unsatisfiableRoles = new HashSet<>();

        computeInferences();
    }

    private void computeInferences() {
        final Map<OWLClass, OWLProperty> pIrreflexive = new HashMap<>();
        final Set<OWLAxiom> tAxioms = new HashSet<>();

        for (final OWLObjectProperty role : ontology.getObjectPropertiesInSignature()) {
            final OWLClass concept = dataFactory.getOWLClass(IRI.create("http://local/nohr/irref/" + role.getIRI().getShortForm()));
            final OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(concept, dataFactory.getOWLObjectHasSelf(role));

            ontologyManager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(concept, dataFactory.getOWLObjectHasSelf(role)));
            tAxioms.add(axiom);

            pIrreflexive.put(concept, role);
        }

        OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
        OWLReasoner reasoner = factory.createReasoner(ontology);

        reasoner.precomputeInferences();

        for (final OWLClass concept : reasoner.getBottomClassNode().getEntities()) {
            OWLProperty role = pIrreflexive.get(concept);

            if (role != null) {
                irreflexiveRoles.add(role);
            } else {
                unsatisfiableConcepts.add(concept);
            }
        }

        for (final OWLObjectPropertyExpression role : reasoner.getBottomObjectPropertyNode().getEntitiesMinusBottom()) {
            unsatisfiableRoles.add(role.asOWLObjectProperty());
        }

        for (final OWLDataProperty role : reasoner.getBottomDataPropertyNode().getEntitiesMinusBottom()) {
            unsatisfiableRoles.add(role.asOWLDataProperty());
        }

        reasoner.dispose();                

        ontologyManager.removeAxioms(ontology, tAxioms);
    }

    @Override
    public Set<OWLClass> getConceptAssertions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public Set<OWLProperty> getIrreflexiveRoles() {
        return irreflexiveRoles;
    }

    @Override
    public Set<OWLSubClassOfAxiom> getSubClassOfAxioms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<OWLClass> getUnsatisfiableConcepts() {
        return unsatisfiableConcepts;
    }

    @Override
    public Set<OWLProperty> getUnsatisfiableRoles() {
        return unsatisfiableRoles;
    }

}

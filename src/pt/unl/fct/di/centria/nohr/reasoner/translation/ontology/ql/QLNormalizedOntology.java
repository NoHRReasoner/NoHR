package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

public interface QLNormalizedOntology {

    public Set<OWLClassAssertionAxiom> getConceptAssertions();

    public Set<OWLDisjointClassesAxiom> getConceptDisjunctions();

    public Set<OWLSubClassOfAxiom> getConceptSubsumptions();

    public Set<OWLDataPropertyAssertionAxiom> getDataAssertions();

    public Set<OWLClassExpression> getDisjointConcepts();

    public Set<OWLProperty<?, ?>> getDisjointRoles();

    public OWLOntology getOriginalOntology();

    public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions();

    public Set<OWLNaryPropertyAxiom<?>> getRoleDisjunctions();

    public Set<OWLObjectProperty> getRoles();

    public Set<OWLSubPropertyAxiom<?>> getRoleSubsumptions();

    public Set<OWLClassExpression> getSubConcepts();

    public Set<OWLProperty<?, ?>> getSubRoles();

    public Set<OWLClassExpression> getSuperConcepts();

    public Set<OWLProperty<?, ?>> getSuperRoles();

    public Set<OWLClassExpression> getUnsatisfiableConcepts();

    public Set<OWLPropertyExpression<?, ?>> getUnsatisfiableRoles();

    public boolean hasDisjointStatement();

    public boolean isSub(OWLClassExpression ce);

    public boolean isSub(OWLPropertyExpression<?, ?> pe);

    public boolean isSuper(OWLClassExpression ce);

    public boolean isSuper(OWLPropertyExpression<?, ?> pe);
}
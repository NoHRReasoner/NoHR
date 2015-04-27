package local.translate.ql;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

public interface INormalizedOntology {

	public Set<OWLClassAssertionAxiom> getConceptAssertions();

	public Set<OWLDisjointClassesAxiom> getConceptDisjunctions();

	public Set<OWLSubClassOfAxiom> getConceptSubsumptions();

	public Set<OWLDataPropertyAssertionAxiom> getDataAssertions();

	public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions();

	public Set<OWLNaryPropertyAxiom<?>> getRoleDisjunctions();

	public Set<OWLSubPropertyAxiom<?>> getRoleSubsumptions();

	public Set<OWLClassExpression> getUnsatisfiableConcepts();

	public Set<OWLPropertyExpression<?, ?>> getUnsatisfiableRoles();

	public boolean hasDisjointStatement();

	public Set<OWLObjectProperty> getRoles();
	
	public Set<OWLClassExpression> getSubConcepts();
	
	public Set<OWLClassExpression> getSuperConcepts();

	public Set<OWLClassExpression> getDisjointConcepts();	

	public Set<OWLPropertyExpression> getSubRules();	

	public Set<OWLPropertyExpression> getSuperRoles();	

	public Set<OWLPropertyExpression> getDisjointRules();
}
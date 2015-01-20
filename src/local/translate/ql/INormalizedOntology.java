package local.translate.ql;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public interface INormalizedOntology {

	public Set<OWLClassAssertionAxiom> getConceptAssertions();
	
	public Set<OWLDisjointClassesAxiom> getConceptDisjunctions();

	public Set<OWLSubClassOfAxiom> getConceptSubsumptions();

	public Set<OWLDataPropertyAssertionAxiom> getDataAssertions();

	public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions();

	public Set<OWLDisjointObjectPropertiesAxiom> getRoleDisjunctions();

	public Set<OWLSubObjectPropertyOfAxiom> getRoleSubsumptions();

	public Set<OWLClassExpression> getUnsatisfiableConcepts();

	public Set<OWLObjectPropertyExpression> getUnsatisfiableRoles();

	public boolean hasDisjointStatement();

}
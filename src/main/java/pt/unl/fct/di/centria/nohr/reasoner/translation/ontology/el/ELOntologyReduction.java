/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

/**
 * Represents an <i>EL<sub>&bot;</sub><sup>+</sup></i> normalized, reduced and simplified ontolology, according to <b>Definition 6.</b>, and
 * respective assumed normalizations, and some of the simplifications assumed in the <b>Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR
 * (Technical Report)</a>}. A reduced ontology of an <i>EL<sub>&bot;</sub><sup>+</sup></i> ontology <i>O</i> is an ontology <i>O'</i> composed by -
 * <i>A</i> represents an atomic concept, <i>C</i> a concept where, in each occurrence of an existential <i>&exist;R.D</i>, <i>D</i> is an atomic
 * concept or a conjunction of atomic concepts, <i>R</i> and <i>S</i> roles, and <i>a</i> and <i>b</i> individuals: <br>
 * -concept assertions <i>A(a)</i> or <i>&exist; R.C(a)</i>; <br>
 * -role assertions <i>R(a, b)</i>; <br>
 * -concept subsumptions <i>C &sqsube; A</i>; <br>
 * -role subsumptions <i>R &sqsube; S</i>; <br>
 * -role chain subsumptions <i>R<sub>1</sub>&SmallCircle; ... &SmallCircle;R<sub>n</sub> &sqsube; S </i><br>
 * that entails exactly the same membership assertions that <i>O</i>, i.e. <i>O&vDash;A(a)</i> iff <i>O'&vDash;A(a)</i> and <i>O&vDash;R(a,b)</i> iff
 * <i>O'&vDash;R(a,b)</i>.
 *
 * @author Nuno Costa
 */
public interface ELOntologyReduction {

	/**
	 * Returns the set of role chain subsumptions <i>R<sub>1</sub>&SmallCircle; ... &SmallCircle;S<sub>n</sub> &sqsube; A</i> in this
	 * {@link ELOntologyReduction reduction}.
	 *
	 * @return Returns the set of role chain subsumptions <i>R<sub>1</sub>&SmallCircle; ... &SmallCircle;S<sub>n</sub> &sqsube; A</i> in this
	 *         {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLSubPropertyChainOfAxiom> getChainSubsumptions();

	/**
	 * Returns the set of concept assertions <i>A(a)</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the set of concept assertions <i>A(a)</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLClassAssertionAxiom> getConceptAssertions();

	/**
	 * Returns the set of concept subsumptions <i>C &sqsube; A</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the set of concept subsumptions <i>C &sqsube; A</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLSubClassOfAxiom> getConceptSubsumptions();

	/**
	 * Returns the set of data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the set of data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLDataPropertyAssertionAxiom> getDataAssertion();

	/**
	 * Returns the set of role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the set of role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLSubDataPropertyOfAxiom> getDataSubsuptions();

	/**
	 * Returns the set of data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the set of data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions();

	/**
	 * Returns the set of role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the set of role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Set<OWLSubObjectPropertyOfAxiom> getRoleSubsumptions();

	/**
	 * Returns true iff this reduction has disjunctions.
	 *
	 * @return rue iff this reduction has disjunctions.
	 */
	public boolean hasDisjunction();

}

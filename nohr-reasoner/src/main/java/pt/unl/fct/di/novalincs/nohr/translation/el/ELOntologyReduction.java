/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation.el;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

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
	 * Returns the role chain subsumptions <i>R<sub>1</sub>&SmallCircle; ... &SmallCircle;S<sub>n</sub> &sqsube; A</i> in this
	 * {@link ELOntologyReduction reduction}.
	 *
	 * @return Returns the role chain subsumptions <i>R<sub>1</sub>&SmallCircle; ... &SmallCircle;S<sub>n</sub> &sqsube; A</i> in this
	 *         {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLSubPropertyChainOfAxiom> chainSubsumptions();

	/**
	 * Returns the concept assertions <i>A(a)</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the concept assertions <i>A(a)</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLClassAssertionAxiom> conceptAssertions();

	/**
	 * Returns the concept subsumptions <i>C &sqsube; A</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the concept subsumptions <i>C &sqsube; A</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLSubClassOfAxiom> conceptSubsumptions();

	/**
	 * Returns the data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLDataPropertyAssertionAxiom> dataAssertion();

	/**
	 * Returns the role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLSubDataPropertyOfAxiom> dataSubsuptions();

	/**
	 * Returns true iff this reduction has disjunctions.
	 *
	 * @return rue iff this reduction has disjunctions.
	 */
	public boolean hasDisjunctions();

	/**
	 * Returns the data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the data role assertions <i>R(a,b)</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions();

	/**
	 * Returns the role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 *
	 * @return the role subsumptions <i>R &sqsube; S</i> in this {@link ELOntologyReduction reduction}.
	 */
	public Iterable<OWLSubObjectPropertyOfAxiom> roleSubsumptions();

}

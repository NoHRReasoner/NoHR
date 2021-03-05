package pt.unl.fct.di.novalincs.nohr.translation.ql;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

/**
 * Represents the <i>digraph</i> <i>&Gscr;<sub>T</sub></i> of a certain QL ontology's TBox <i>T</i> (see <b>Definition 6.</b> of
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
 *
 * @author Nuno Costa
 */
public interface TBoxDigraph {

	/**
	 * Obtains the set of ancestors of a given concept vertex in this {@link TBoxDigraph}.
	 *
	 * @param v
	 *            a concept vertex.
	 * @return the ancestors of {@code v}.
	 */
	public Set<OWLClassExpression> getAncestors(OWLClassExpression v);

	/**
	 * Obtains the set of ancestors of a given role vertex in this {@link TBoxDigraph}.
	 *
	 * @param v
	 *            a role vertex.
	 * @return the ancestors of {@code v}.
	 */
	public Set<OWLPropertyExpression> getAncestors(OWLPropertyExpression v);

	/**
	 * Computes a the set of irreflexive roles <i>&Psi;(T)</i> of the the TBox, <i>T</i>, of which this {@link TBoxDigraph} is <i>digraph</i> (see
	 * <b>Definition 7.</b> of {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2
	 * QL</a>}).
	 *
	 * @return <i>&Psi;(T)</i>.
	 */
	public Set<OWLObjectProperty> getIrreflexiveRoles();

	/**
	 * Returns the set of the predecessors of a given concept vertex in this {@link TBoxDigraph}.
	 *
	 * @param v
	 *            a concept vertex.
	 * @return the predecessors of {@code v}.
	 */
	public Set<OWLClassExpression> getPredecessors(OWLClassExpression v);

	/**
	 * Returns the set of the predecessors of a given role vertex in this {@link TBoxDigraph}.
	 *
	 * @param v
	 *            a role vertex.
	 * @return the predecessor of {@code v}.
	 */
	public Set<OWLPropertyExpression> getPredecessors(OWLObjectPropertyExpression v);

	/**
	 * Computes a the set of irreflexive roles <i>&Omega;(T)</i> of the the TBox, <i>T</i>, of which this {@link TBoxDigraph} is <i>digraph</i> (see
	 * <b>Definition 8.</b> of {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2
	 * QL</a>}).
	 *
	 * @return <i>&Omega;(T)</i>.
	 */
	public Set<OWLEntity> getUnsatisfiableEntities();

}
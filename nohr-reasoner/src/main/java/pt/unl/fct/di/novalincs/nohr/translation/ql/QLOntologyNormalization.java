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

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

/**
 * Represents an OWL 2 QL ontology normalized in order to fit in DL-Lite<sub>R</sub>, i.e. a DL-Lite<sub>R</sub> ontology that entails exactly the
 * same axioms that a certain QL ontology (see <b>Appendix D</b> of
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
 *
 * @author Nuno Costa
 */
public interface QLOntologyNormalization {

	/**
	 * Returns the DL-Lite<sub>R</sub> concept assertions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> concept assertions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLClassAssertionAxiom> conceptAssertions();

	/**
	 * Returns the DL-Lite<sub>R</sub> concept disjunctions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> concept disjunctions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLDisjointClassesAxiom> conceptDisjunctions();

	/**
	 * Returns the DL-Lite<sub>R</sub> concept subsumptions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> concept subsumptions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLSubClassOfAxiom> conceptSubsumptions();

	/**
	 * Returns the DL-Lite<sub>R</sub> (data) role assertions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> (data) role assertions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLDataPropertyAssertionAxiom> dataAssertions();

	/**
	 * Returns the DL-Lite<sub>R</sub> (data) role disjunctions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> (data) role disjunctions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLDisjointDataPropertiesAxiom> dataDisjunctions();

	/**
	 * The ontology of which this {@link QLOntologyTranslator} is normalization.
	 *
	 * @return the ontology of which this {@link QLOntologyTranslator} is normalization.
	 */
	public OWLOntology getOntology();

	/**
	 * Returns the DL-Lite<sub>R</sub> roles occurring in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> roles occurring in this {@link QLOntologyNormalization}.
	 */
	public Set<OWLObjectProperty> getRoles();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts occurring as subsumed concept in some subsumption in this {@link QLOntologyNormalization}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts occurring as subsumed concept in some subsumption in this {@link QLOntologyNormalization}.
	 */
	public Set<OWLClassExpression> getSubConcepts();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> roles occurring as subsumed roles in some subsumption in this {@link QLOntologyNormalization}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> role occurring as subsumed roles in some subsumption in this {@link QLOntologyNormalization}.
	 */
	public Set<OWLProperty> getSubRoles();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts <i>B</i> that occur in some axiom <i>B&sqsube;&bot;</i>,<i>B&sqsube;&not;&top;</i>,
	 * <i>&top;&sqsube;&not;B</i> or </i> B&sqsube;&not;B</i> {@link QLOntologyNormalization}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts <i>B</i> that occur in some axiom <i>B&sqsube;&bot;</i>,<i>B&sqsube;&not;&top;</i>,
	 *         <i>&top;&sqsube;&not;B</i> or </i> B&sqsube;&not;B</i> {@link QLOntologyNormalization}.
	 */
	public Set<OWLClassExpression> getUnsatisfiableConcepts();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> roles <i>Q</i> that occur in some axiom <i>Q&sqsube;&bot;</i>,<i>Q&sqsube;&not;&top;</i>,
	 * <i>&top;&sqsube;&not;Q</i> or </i>Q&sqsube;&not;Q</i> {@link QLOntologyNormalization}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts <i>Q</i> that occur in some axiom <i>Q&sqsube;&bot;</i>,<i>Q&sqsube;&not;&top;</i>,
	 *         <i>&top;&sqsube;&not;Q</i> or </i> Q&sqsube;&not;Q</i> {@link QLOntologyNormalization}.
	 */
	public Set<OWLPropertyExpression> getUnsatisfiableRoles();

	/**
	 * Returns true iff this {@link QLOntologyNormalization} has disjunctions.
	 *
	 * @return true iff this {@link QLOntologyNormalization} has disjunctions.
	 */
	public boolean hasDisjunctions();

	/**
	 * Checks whether a given concept occur as subsumed concept in some subsumption of this {@link QLOntologyNormalization}.
	 *
	 * @param b
	 *            a concept.
	 * @return true iff {@code b} occur as subsumed concept in some subumption of this {@link QLOntologyNormalization}.
	 */
	public boolean isSub(OWLClassExpression b);

	/**
	 * Checks whether a given concept occur as subsumed role in some subsumption of this {@link QLOntologyNormalization}.
	 *
	 * @param q
	 *            a concept.
	 * @return true iff {@code q} occur as subsumed role in some subumption of this {@link QLOntologyNormalization}.
	 */
	public boolean isSub(OWLPropertyExpression q);

	/**
	 * Checks whether a given concept occur as subsuming concept in some subsumption of this {@link QLOntologyNormalization}.
	 *
	 * @param c
	 *            a concept.
	 * @return true iff {@code c} occur as subsuming concept in some subumption of this {@link QLOntologyNormalization}.
	 */
	public boolean isSuper(OWLClassExpression c);

	/**
	 * Checks whether a given concept occur as subsuming role in some subsumption of this {@link QLOntologyNormalization}.
	 *
	 * @param r
	 *            a role.
	 * @return true iff {@code r} occur as subsuming role in some subumption of this {@link QLOntologyNormalization}.
	 */
	public boolean isSuper(OWLPropertyExpression r);

	/**
	 * Returns the DL-Lite<sub>R</sub> role assertions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> role assertions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLObjectPropertyAssertionAxiom> roleAssertions();

	/**
	 * Returns the DL-Lite<sub>R</sub> role disjunctions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> role disjunctions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLDisjointObjectPropertiesAxiom> roleDisjunctions();

	/**
	 * Returns the DL-Lite<sub>R</sub> role subsumptions in this {@link QLOntologyNormalization}.
	 *
	 * @return the DL-Lite<sub>R</sub> role subsumptions in this {@link QLOntologyNormalization}.
	 */
	public Iterable<OWLSubPropertyAxiom<?>> roleSubsumptions();
}
package pt.unl.fct.di.novalincs.nohr.translation;

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
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseProgram;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;

/**
 * Represents a component that can translate a specified {@link OWLOntology}
 * <i>O</i> to a logic program <i>T</i> and load that program in a specified
 * {@link DeductiveDatabase}. The following properties must be satisfied, where
 * <i>A</i> denotes an atomic concept and the corresponding predicate,
 * <i>P</i> an atomic role and the corresponding predicate, <i>a</i> and
 * <i>b</i> individuals and the corresponding constants:<br>
 * -<i>T&vDash;A(a) iff O&vDash;A(a)</i>;<br>
 * -<i>T&vDash;A<sup>d</sup>(a) iff O&vDash;A(a)</i>;<br>
 * -<i>T&vDash;P(a, b) iff O&vDash;P(a, b)</i>;<br>
 * -<i>T&vDash;P<sup>d</sup>(a, b) iff O&vDash;P(a, b)</i>.
 *
 * @author Nuno Costa
 */
public interface OntologyTranslator {

    /**
     * Clear the {@link DatabaseProgram program}(s) that maintains the
     * translation.
     */
    public void clear();

    /**
     * Returns the {@link DeductiveDatabase} where this
     * {@link OntologyTranslator translator} maintains the translation.
     *
     * @return the {@link DeductiveDatabase} where this
     * {@link OntologyTranslator translator} maintains the translation.
     */
    public DeductiveDatabase getDedutiveDatabase();

    public Set<AxiomType<?>> getIgnoredUnsupportedAxioms();

    /**
     * Returns the profile of the {@link OntologyTranslator translator}'s
     * ontology.
     *
     * @return the profile of the {@link OntologyTranslator translator}'s
     * ontology.
     */
    public Profile getProfile();

    /**
     * Returns the {@link OWLOntology} that this
     * {@link OntologyTranslator translator} translates.
     *
     * @return the {@link OWLOntology} that this
     * {@link OntologyTranslator translator} translates.
     */
    public OWLOntology getOntology();

    public AxiomType<?>[] getSupportedAxioms();

    public boolean ignoreAllUnsupportedAxioms();

    /**
     * Returns true iff the {@link OntologyTranslator translator}'s ontology has
     * disjunctions.
     *
     * @return iff the {@link OntologyTranslator translator}'s has disjunctions.
     */
    public boolean requiresDoubling();

    public void setIgnoreAllUnsupportedAxioms(boolean value);

    /**
     * Updates the translation {@link DatabaseProgram program}(s) in the
     * {@link OntologyTranslator translator}'s {@link DeductiveDatabase deductive
     * database} with the translation of the current version of the
     * {@link OntologyTranslator translator}'s ontology.
     *
     * @throws UnsupportedAxiomsException if the
     * {@link OntologyTranslator translator}'s ontology has some axioms of an
     * unsupported type.
     * @throws OWLProfilesViolationsException if the
     * {@link OntologyTranslator translator}'s ontology isn't in any supported
     * OWL profile.
     */
    public void updateTranslation() throws OWLProfilesViolationsException, UnsupportedAxiomsException;

}

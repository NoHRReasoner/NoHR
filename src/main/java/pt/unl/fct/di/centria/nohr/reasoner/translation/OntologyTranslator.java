package pt.unl.fct.di.centria.nohr.reasoner.translation;

import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

/**
 * Represents a component that can translate a specified {@link OWLOntology} <i>O</i> to a {@link Program logic program} <i>T</i> and load that
 * program in a specified {@link DeductiveDatabase}. The following properties must be satisfied, where <i>A</i> denotes an atomic concept and
 * the corresponding predicate, <i>P</i> an atomic role and the corresponding predicate, <i>a</i> and <i>b</i> individuals and the corresponding
 * constants:<br>
 * -<i>T&vDash;A(a) iff O&vDash;A(a)</i>;<br>
 * -<i>T&vDash;A<sup>d</sup>(a) iff O&vDash;A(a)</i>;<br>
 * -<i>T&vDash;P(a, b) iff O&vDash;P(a, b)</i>;<br>
 * -<i>T&vDash;P<sup>d</sup>(a, b) iff O&vDash;P(a, b)</i>;<br>
 * -<i>A</i> and <i>A<sup>d</sup></i> are tabled in <i>T</i> if, for some concept <i>C &ne; A</i>, <i>O&vDash;A&sqsube;C</i> and
 * <i>O&vDash;C&sqsube;A</i>; <br>
 * -<i>P</i> and <i>P<sup>d</sup></i> are tabled in <i>T</i> if, for some role <i>R &ne; P</i>, <i>O&vDash;P&sqsube;R</i> and
 * <i>O&vDash;R&sqsube;P</i>.
 *
 * @author Nuno Costa
 */
public interface OntologyTranslator {

	/**
	 * Returns the {@link DeductiveDatabase} where this {@link OntologyTranslator translator} load the translation.
	 *
	 * @return the {@link DeductiveDatabase} where this {@link OntologyTranslator translator} load the translation.
	 */
	public DeductiveDatabase getDedutiveDatabase();

	/**
	 * Returns the {@link OWLOntology} that this {@link OntologyTranslator translator} translates.
	 *
	 * @return the {@link OWLOntology} that this {@link OntologyTranslator translator} translates.
	 */
	public OWLOntology getOntology();

	/**
	 * Returns the profile of the translated ontology.
	 *
	 * @return the profile of the translated ontology.
	 */
	public Profile getProfile();

	/**
	 * Retruns true iff the translated ontology has disjunctions.
	 *
	 * @return iff the translated ontology has disjunctions.
	 */
	public boolean hasDisjunctions();

	/**
	 * Translates the {@link OntologyTranslator translator}'s ontology and load the translation in the {@link OntologyTranslator translator}'s
	 * {@link DeductiveDatabase}.
	 *
	 * @throws UnsupportedAxiomsException
	 *             if the {@link OntologyTranslator translator}'s ontology has some axioms of an unsupported type.
	 * @throws OWLProfilesViolationsException
	 *             if the {@link OntologyTranslator translator}'s ontology isn't in any supported OWL profile.
	 */
	public void translate() throws OWLProfilesViolationsException, UnsupportedAxiomsException;

}
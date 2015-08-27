/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;

/**
 * Represents an <i> MKNF Hybrid Knowledge Base </i> (see {@link <a href="http://knoesis.wright.edu/pascal/resources/publications/mknftheo.pdf">Local
 * Closed World Reasoning with Description Logics under the Well-Founded Semantics</a>}) that can be queried.
 *
 * @author Nuno Costa
 */
public interface HybridKB {

	/**
	 * Obtains all answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @throws OWLProfilesViolationsException
	 *             if the <i>ontology</i> component isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if the <i>ontology</i> has some axiom of an unsupported type.
	 * @return the list of all answers {@code query}.
	 */
	List<Answer> allAnswers(Query query) throws OWLProfilesViolationsException, UnsupportedAxiomsException;

	/**
	 * Obtains all answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param trueAnswers
	 *            specifies whether to obtain {@link TruthValue#TRUE true} answers.
	 * @param undefinedAnswers
	 *            specifies whether to obtain {@link TruthValue#UNDEFINED undefined} answers.
	 * @param inconsistentAnswers
	 *            specifies whether to obtain {@link TruthValue#INCONSISTENT inconsistent} answers.
	 * @throws OWLProfilesViolationsException
	 *             if the <i>ontology</i> component isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if the <i>ontology</i> has some axiom of an unsupported type.
	 * @return the list of all answers {@code query} valued according to the {@code trueAnswers}, {@code undefinedAnswers} and
	 *         {@code inconsistentAnswers} flags.
	 */
	List<Answer> allAnswers(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException;

	/**
	 * Release all the resources reclaimed by this {@link HybridKB}.
	 */
	void dispose();

	/**
	 * Returns the <i>ontology</i> component of this {@link HybridKB <i>Hybrid MKNF knowledge base</i>}.
	 *
	 * @return the <i>ontology</i> component of this {@link HybridKB <i>Hybrid MKNF knowledge base</i>}.
	 */
	OWLOntology getOntology();

	/**
	 * Returns the <i>program</i> component of this {@link HybridKB <i>Hybrid MKNF knowledge base</i>}.
	 *
	 * @return returns the <i>program</i> component of this {@link HybridKB <i>Hybrid MKNF knowledge base</i>}
	 */
	Program getProgram();

	/**
	 * Returns the {@link VocabularyMapping} that this {@link HybridKB} applies.
	 *
	 * @return the {@link VocabularyMapping} that this {@link HybridKB} applies.
	 */
	VocabularyMapping getVocabularyMapping();

	/**
	 * Checks if there is some answer to a given query.
	 *
	 * @param query
	 *            the query.
	 * @return true iff there is at least one answer to {@code query}.
	 * @throws OWLProfilesViolationsException
	 *             if the <i>ontology</i> component isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if the <i>ontology</i> has some axiom of an unsupported type.
	 */
	boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException;

	/**
	 * Obtains one answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @return one answer to {@code query}.
	 * @throws OWLProfilesViolationsException
	 *             if the <i>ontology</i> component isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if the <i>ontology</i> has some axiom of an unsupported type.
	 */
	Answer oneAnswer(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, UnsupportedAxiomsException;

	/**
	 * Obtains one answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param trueAnswers
	 *            specifies whether to obtain a {@link TruthValue#TRUE true} answers.
	 * @param undefinedAnswers
	 *            specifies whether to obtain a {@link TruthValue#UNDEFINED undefined} answers.
	 * @param inconsistentAnswers
	 *            specifies whether to obtain a {@link TruthValue#INCONSISTENT inconsistent} answers.
	 * @return one answer to {@code query} valued according to the {@code trueAnswers}, {@code undefinedAnswers} and {@code inconsistentAnswers}
	 *         flags.
	 * @throws OWLProfilesViolationsException
	 *             if the <i>ontology</i> component isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if the <i>ontology</i> has some axiom of an unsupported type.
	 */
	Answer oneAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException;

}
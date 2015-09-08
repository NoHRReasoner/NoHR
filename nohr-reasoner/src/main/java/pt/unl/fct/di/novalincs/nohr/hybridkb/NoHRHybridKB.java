/*
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseProgram;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.deductivedb.XSBDeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.ProgramChangeListener;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateTypeVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.VocabularyChangeListener;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link HybridKB} according to {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} and
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
public class NoHRHybridKB implements HybridKB {

	/** The <i>ontology</i> component of this {@link HybridKB} */
	private final OWLOntology ontology;

	/** The <i>program</i> component of this {@link HybridKB} */
	private final Program program;

	/** The {@link Vocabulary} that this {@link HybridKB} applies. */
	private final Vocabulary vocabulary;

	/**
	 * The underlying {@link DeductiveDatabase}, where the <i>ontology</i> translation and the <i>program</i> rules (and double rules, when necessary)
	 * are loaded for querying
	 */
	private final DeductiveDatabase dedutiveDatabase;

	/** The underlying {@link OntologyTranslator}, that translates the <i>ontology</i> component to rules. */
	private final OntologyTranslator ontologyTranslator;

	/** The underlying {@link QueryProcessor} that mediates the queries to the underlying {@link DeductiveDatabase}. */
	private final QueryProcessor queryProcessor;

	/**
	 * The {@link DatabaseProgram} that contains the doubled (or only the original ones, if the ontology doesn't have disjunctions) rules of the
	 * <i>program</i> component.
	 */
	private final DatabaseProgram doubledProgram;

	/** Whether the ontology had disjunctions at last call to {@link #preprocess()}. */
	private boolean hadDisjunctions;

	/** Whether the ontology has changed since the last call to {@link #preprocess()}. */
	private boolean hasOntologyChanges;

	/** Whether the program has changed since the last call to {@link #preprocess()}. */
	private boolean hasProgramChanges;

	/** The {@link OWLOntologyChangeListener} that tracks the {@link OWLOntology ontology} changes. */
	private final OWLOntologyChangeListener ontologyChangeListener;

	/** The {@link ProgramChangeListener} that will track the tracks the {@link Program} changes. */
	private final ProgramChangeListener programChangeListener;

	private final VocabularyChangeListener vocabularyChangeListener;

	/**
	 * Constructs a {@link NoHRHybridKB} from a given {@link OWLOntology ontology} and {@link Program program}.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system to use as underlying Prolog engine is located.
	 * @param ontology
	 *            the <i>ontology</i> component of this {@link HybridKB}.
	 * @param profile
	 *            the {@link Profile OWL profile} that will be considered during the ontology translation. That will determine which the translation -
	 *            {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} or
	 *            {@link <a href= "http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} will
	 *            be applied. If none is specified the preferred one will be applied. Whenever the ontology isn't in the specified profile, if some is
	 *            specified, an {@link OWLProfilesViolationsException} will be thrown.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile != null} and {@code ontology} isn't in the profile {@code profile}; or {@code profile == null} and the
	 *             {@code ontology} isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some profile of an unsupported type.
	 * @throws PrologEngineCreationException
	 *             if there was some problem during the creation of the underlying Prolog engine.
	 */
	public NoHRHybridKB(final File binDirectory, final OWLOntology ontology, Profile profile)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException,
			PrologEngineCreationException {
		this(binDirectory, ontology, Model.program(), null, profile);
	}

	/**
	 * Constructs a {@link NoHRHybridKB} from a given {@link OWLOntology ontology} and {@link Program program}.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system to use as underlying Prolog engine is located.
	 * @param ontology
	 *            the <i>ontology</i> component of this {@link HybridKB}.
	 * @param program
	 *            the <i>program</i> component of this {@link HybridKB}.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile != null} and {@code ontology} isn't in the profile {@code profile}; or {@code profile == null} and the
	 *             {@code ontology} isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some profile of an unsupported type.
	 * @throws PrologEngineCreationException
	 *             if there was some problem during the creation of the underlying Prolog engine.
	 */
	public NoHRHybridKB(final File binDirectory, final OWLOntology ontology, final Program program)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException,
			PrologEngineCreationException {
		this(binDirectory, ontology, program, null, null);
	}

	/**
	 * Constructs a {@link NoHRHybridKB} from a given {@link OWLOntology ontology} and {@link Program program}.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system to use as underlying Prolog engine is located.
	 * @param ontology
	 *            the <i>ontology</i> component of this {@link HybridKB}.
	 * @param program
	 *            the <i>program</i> component of this {@link HybridKB}.
	 * @param profile
	 *            the {@link Profile OWL profile} that will be considered during the ontology translation. That will determine which the translation -
	 *            {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} or
	 *            {@link <a href= "http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} will
	 *            be applied. If none is specified the preferred one will be applied. Whenever the ontology isn't in the specified profile, if some is
	 *            specified, an {@link OWLProfilesViolationsException} will be thrown.
	 * @param vocabulary
	 *            the {@link Vocabulary} that will be used in this {@link HybridKB}.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile != null} and {@code ontology} isn't in the profile {@code profile}; or {@code profile == null} and the
	 *             {@code ontology} isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some profile of an unsupported type.
	 * @throws PrologEngineCreationException
	 *             if there was some problem during the creation of the underlying Prolog engine.
	 * @throws IllegalArgumentException
	 *             if {@code vocabularyMapping} doesn't contains {@code ontology}.
	 */
	public NoHRHybridKB(final File binDirectory, final OWLOntology ontology, final Program program,
			Vocabulary vocabulary, Profile profile)
					throws OWLProfilesViolationsException, UnsupportedAxiomsException, PrologEngineCreationException {
		Objects.requireNonNull(binDirectory);
		this.ontology = ontology;
		this.program = program;
		if (vocabulary != null) {
			if (!vocabulary.getOntology().equals(ontology))
				throw new IllegalArgumentException("vocabularyMapping: must contain the given ontology");
			this.vocabulary = vocabulary;
		} else
			this.vocabulary = new DefaultVocabulary(ontology);
		assert this.vocabulary != null;
		dedutiveDatabase = new XSBDeductiveDatabase(binDirectory, this.vocabulary);
		doubledProgram = dedutiveDatabase.createProgram();
		queryProcessor = new QueryProcessor(dedutiveDatabase);
		ontologyTranslator = new OntologyTranslatorImpl(ontology, this.vocabulary, dedutiveDatabase, profile);
		hasOntologyChanges = true;
		hasProgramChanges = true;
		ontologyChangeListener = new OWLOntologyChangeListener() {

			@Override
			public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
				for (final OWLOntologyChange change : changes)
					if (change.getOntology() == ontology && change.isAxiomChange()
							&& change.getAxiom().isLogicalAxiom())
						hasOntologyChanges = true;
			}
		};
		programChangeListener = new ProgramChangeListener() {

			@Override
			public void added(Rule rule) {
				hasProgramChanges = true;
			}

			@Override
			public void cleared() {
				hasProgramChanges = true;
			}

			@Override
			public void removed(Rule rule) {
				hasProgramChanges = true;
			}

			@Override
			public void updated(Rule oldRule, Rule newRule) {
				hasProgramChanges = true;
			}
		};
		vocabularyChangeListener = new VocabularyChangeListener() {

			@Override
			public void constantChanged(Constant constant) {
				hasProgramChanges = true;
			}

			@Override
			public void predicateChanged(Predicate predicate) {
				hasProgramChanges = true;
			}
		};
		ontology.getOWLOntologyManager().addOntologyChangeListener(ontologyChangeListener);
		program.addListener(programChangeListener);
		this.vocabulary.addListener(vocabularyChangeListener);
		preprocess();
	}

	@Override
	public List<Answer> allAnswers(Query query) throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		return allAnswers(query, true, true, true);
	}

	@Override
	public List<Answer> allAnswers(Query query, boolean trueAnswer, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasProgramChanges)
			preprocess();
		RuntimesLogger.start("query");
		RuntimesLogger.info("querying: " + query);
		final List<Answer> answers = queryProcessor.allAnswers(query, hadDisjunctions, trueAnswer, undefinedAnswers,
				inconsistentAnswers);
		RuntimesLogger.stop("query", "queries");
		final List<Answer> result = new LinkedList<Answer>();
		for (final Answer ans : answers)
			result.add(ans);
		return result;
	}

	@Override
	public void dispose() {
		dedutiveDatabase.dipose();
		ontology.getOWLOntologyManager().removeOntologyChangeListener(ontologyChangeListener);
		program.removeListener(programChangeListener);
		vocabulary.removeListener(vocabularyChangeListener);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		dispose();
	}

	@Override
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * @return the ruleBase
	 */
	@Override
	public Program getProgram() {
		return program;
	}

	@Override
	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	@Override
	public boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasProgramChanges)
			preprocess();
		RuntimesLogger.start("query");
		RuntimesLogger.info("querying: " + query);
		final boolean hasAnswer = queryProcessor.hasAnswer(query, hadDisjunctions, trueAnswer, undefinedAnswers,
				inconsistentAnswers);
		RuntimesLogger.stop("query", "queries");
		return hasAnswer;
	}

	@Override
	public boolean hasDisjunctions() {
		return ontologyTranslator.hasDisjunctions();
	}

	@Override
	public Answer oneAnswer(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, UnsupportedAxiomsException {
		return oneAnswer(query, true, true, true);
	}

	@Override
	public Answer oneAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasProgramChanges)
			preprocess();
		RuntimesLogger.start("query");
		RuntimesLogger.info("querying: " + query);
		final Answer answer = queryProcessor.oneAnswer(query, hadDisjunctions, trueAnswer, undefinedAnswers,
				inconsistentAnswers);
		RuntimesLogger.stop("query", "queries");
		return answer;
	}

	/**
	 * Preprocesses this {@link HybridKB} according to {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} and
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}, depending on
	 * the current ontolgy profile, so that it can be queried. The translation {@link DatabaseProgram}s, loaded in {@link #dedutiveDatabase} are
	 * updated, if the ontology has changed since the last call; {@link #doubledProgram} is updated, if they were introduced disjunctions in the
	 * ontology, or if the program has changed, since the last call.
	 *
	 * @throws UnsupportedAxiomsException
	 *             if the current version of the ontology has some axioms of an unsupported type.
	 * @throws OWLProfilesViolationsException
	 *             if the { ontology isn't in any supported OWL profile.
	 */
	private void preprocess() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges) {
			RuntimesLogger.start("ontology processing");
			ontologyTranslator.updateTranslation();
			RuntimesLogger.stop("ontology processing", "loading");
		}
		if (hasProgramChanges || ontologyTranslator.hasDisjunctions() != hadDisjunctions) {
			RuntimesLogger.start("rules parsing");
			doubledProgram.clear();
			if (ontologyTranslator.hasDisjunctions())

				for (final Rule rule : program)
					doubledProgram.addAll(ProgramDoubling.doubleRule(rule));
			else {
				final ModelVisitor originalPredicates = new PredicateTypeVisitor(PredicateType.ORIGINAL);
				for (final Rule rule : program)
					doubledProgram.add(rule.accept(originalPredicates));
			}

			RuntimesLogger.stop("rules parsing", "loading");
		}
		hasOntologyChanges = false;
		hasProgramChanges = false;
		hadDisjunctions = ontologyTranslator.hasDisjunctions();
	}

}

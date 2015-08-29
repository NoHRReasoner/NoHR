/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

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

import pt.unl.fct.di.centria.nohr.deductivedb.DatabaseProgram;
import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.centria.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.centria.nohr.deductivedb.XSBDeductiveDatabase;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.DefaultVocabularyMapping;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.ProgramChangeListener;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link HybridKB} according to {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} and
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
public class HybridKBImpl implements HybridKB {

	/** The <i>ontology</i> component of this {@link HybridKB} */
	private final OWLOntology ontology;

	/** The <i>program</i> component of this {@link HybridKB} */
	private final Program program;

	/** The {@link VocabularyMapping} that this {@link HybridKB} applies. */
	private final VocabularyMapping vocabularyMapping;

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

	/**
	 * Constructs a {@link HybridKBImpl} from a given {@link OWLOntology ontology} and {@link Program program}.
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
	public HybridKBImpl(final File binDirectory, final OWLOntology ontology, Profile profile)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException,
			PrologEngineCreationException {
		this(binDirectory, ontology, Model.program(), null, profile);
	}

	/**
	 * Constructs a {@link HybridKBImpl} from a given {@link OWLOntology ontology} and {@link Program program}.
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
	public HybridKBImpl(final File binDirectory, final OWLOntology ontology, final Program program)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException,
			PrologEngineCreationException {
		this(binDirectory, ontology, program, null, null);
	}

	/**
	 * Constructs a {@link HybridKBImpl} from a given {@link OWLOntology ontology} and {@link Program program}.
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
	 * @param vocabularyMapping
	 *            the {@link VocabularyMapping} that will be used in this {@link HybridKB}.
	 * @throws OWLProfilesViolationsException
	 *             if {@code profile != null} and {@code ontology} isn't in the profile {@code profile}; or {@code profile == null} and the
	 *             {@code ontology} isn't in any supported profile.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} has some profile of an unsupported type.
	 * @throws PrologEngineCreationException
	 *             if there was some problem during the creation of the underlying Prolog engine.
	 * @throws IllegalArgumentException
	 *             if {@code vocabularyMapping doesn't contains {@code ontology}.
	 */
	public HybridKBImpl(final File binDirectory, final OWLOntology ontology, final Program program,
			VocabularyMapping vocabularyMapping, Profile profile)
					throws OWLProfilesViolationsException, UnsupportedAxiomsException, PrologEngineCreationException {
		Objects.requireNonNull(binDirectory);
		this.ontology = ontology;
		this.program = program;
		if (vocabularyMapping != null) {
			this.vocabularyMapping = vocabularyMapping;
			if (!vocabularyMapping.getOntologies().contains(ontology))
				throw new IllegalArgumentException("vocabularyMapping: must contain the given ontology");
		} else
			this.vocabularyMapping = new DefaultVocabularyMapping(ontology);
		dedutiveDatabase = new XSBDeductiveDatabase(binDirectory, vocabularyMapping);
		doubledProgram = dedutiveDatabase.createProgram();
		queryProcessor = new QueryProcessor(dedutiveDatabase);
		ontologyTranslator = new OntologyTranslatorImpl(ontology, dedutiveDatabase, profile);
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
			public void cleaned() {
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
		ontology.getOWLOntologyManager().addOntologyChangeListener(ontologyChangeListener);
		program.addListener(programChangeListener);
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
				hadDisjunctions ? inconsistentAnswers : false);
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
	public VocabularyMapping getVocabularyMapping() {
		return vocabularyMapping;
	}

	@Override
	public boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasProgramChanges)
			preprocess();
		RuntimesLogger.start("query");
		RuntimesLogger.info("querying: " + query);
		final boolean hasAnswer = queryProcessor.hasAnswer(query, hadDisjunctions, trueAnswer, undefinedAnswers,
				hadDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		return hasAnswer;
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
				hadDisjunctions ? inconsistentAnswers : false);
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

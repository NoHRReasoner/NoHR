package pt.unl.fct.di.centria.nohr.deductivedb;

import java.io.BufferedWriter;
import static pt.unl.fct.di.centria.nohr.model.Model.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.SolutionIterator;
import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.reasoner.VocabularyMapping;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * Partial implementation of {@link DeductiveDatabaseManager} based on the Interprolog API.
 *
 * @author Nuno Costa
 */
public abstract class PrologDeductiveDatabaseManager implements DeductiveDatabaseManager {

	/**
	 * The maximum time, in seconds, in which the construction of a {@link PrologEngine} will be attempted, before giving up and throw a
	 * {@link PrologEngineCreationException}.
	 */
	private static final int CREATION_TIMEOUT = 3;

	/** The name of the file were the programs are written. */
	private static final String PROLOG_FILE_NAME = "nohrtr.P";

	private final TermModelConverter termModelConverter;

	/** The {@link PrologSystemInterface} used to create the Prolog goals. */
	private final PrologSystemInterface prologEngineInterface;

	/**
	 * The {@link FormatVisitor} that formats the {@link Rule rules} and {@link TableDirective table directives} when they are sent to the Prolog
	 * engine.
	 */
	private final FormatVisitor formatVisitor;

	private SolutionIterator lastSolutionsIterator;

	/** The directory where the Prolog engine binary can be found. */
	protected final File binDirectory;

	/** The prolog engine to where the {@link Program programs} will be loaded and that will answer the queries. */
	protected PrologEngine prologEngine;

	/** The mapping between {@link Program programs} identifiers and respective programs. */
	private final Map<Object, Program> programs;

	/** Whether all the loaded programs were loaded in the Prolog Engine. */
	private boolean loaded = false;

	/**
	 * Constructs a {@link DeductiveDatabaseManager} with the Prolog system located in a given directory as underlying Prolog engine.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system that will be used as underlying Prolog engine is located.
	 * @param prologModuleName
	 *            the name of the Prolog module that defines the predicates specified by {@link PrologSystemInterface}.
	 * @throws IPException
	 *             if some exception was thrown by the Interprolog API.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out. That could mean that the Prolog system located at {@code binDirectory}
	 *             isn't an operational Prolog system.
	 */
	public PrologDeductiveDatabaseManager(File binDirectory, String prologModuleName,
			VocabularyMapping vocabularyMapping) throws IPException, PrologEngineCreationException {
		Objects.requireNonNull(binDirectory);
		this.binDirectory = binDirectory;
		formatVisitor = new XSBFormatVisitor();
		prologEngineInterface = new PrologSystemInterface(formatVisitor, prologModuleName);
		termModelConverter = new TermModelConverter(vocabularyMapping);
		programs = new HashMap<>();
		startPrologEngine();
	}

	@Override
	public void add(Object programID, Rule rule) {
		final Program program = programs.get(programID);
		Set<TableDirective> tableDirectives;
		Set<Rule> rules;
		if (program == null) {
			tableDirectives = new HashSet<>();
			rules = new HashSet<>();
		} else {
			tableDirectives = program.getTableDirectives();
			rules = program.getRules();
		}
		rules.add(rule);
		programs.put(programID, prog(programID, tableDirectives, rules));
	}

	@Override
	public void add(Object programID, TableDirective tableDirective) {
		final Program program = programs.get(programID);
		final Set<TableDirective> tableDirectives;
		final Set<Rule> rules;
		if (program == null) {
			tableDirectives = new HashSet<>();
			rules = new HashSet<>();
		} else {
			tableDirectives = program.getTableDirectives();
			rules = program.getRules();
		}
		tableDirectives.add(tableDirective);
		programs.put(programID, prog(programID, tableDirectives, rules));
	}

	/**
	 * Add an answer to the result of an {@link #answersValuations(Query, Boolean)} call.
	 *
	 * @param valuesList
	 *            a list of {@link TermModel}s representing the terms of the key term list in the result map.
	 * @param answers
	 *            the map were the answer will be added.
	 */
	private void addAnswer(TermModel valuesList, Map<List<Term>, TruthValue> answers) {
		final TermModel[] termsList = valuesList.flatList();
		final List<Term> vals = new ArrayList<Term>(termsList.length);
		for (int i = 1; i < termsList.length; i++)
			vals.add(termModelConverter.term(termsList[i]));
		final TruthValue truth = termModelConverter.truthValue(termsList[0]);
		answers.put(vals, truth);

	}

	/**
	 * Create an answer to a given query from a given termList.
	 *
	 * @param query
	 *            a query.
	 * @param valuesList
	 *            the terms to which the query's free variables are mapped in the answer.
	 * @return the answer to {@code query} corresponding to {@code answer}.
	 */
	private Answer ans(Query query, TermModel valuesList) {
		final TermModel[] termsList = valuesList.flatList();
		final TruthValue truth = termModelConverter.truthValue(termsList[0]);
		final List<Term> vals = new ArrayList<Term>(termsList.length);
		for (int i = 1; i <= query.getVariables().size(); i++)
			vals.add(termModelConverter.term(termsList[i]));
		return Model.ans(query, truth, vals);
	}

	@Override
	public Answer answer(Query query) throws IOException {
		return answer(query, null);
	}

	@Override
	public Answer answer(Query query, Boolean trueAnswers) throws IOException {
		if (trueAnswers != null && !trueAnswers && !hasWFS())
			return null;
		load();
		final Object[] bindings = prologEngine
				.deterministicGoal(prologEngineInterface.detGoal(query, trueAnswers, "TM"), "[TM]");
		if (bindings == null)
			return null;
		return ans(query, (TermModel) bindings[0]);
	}

	@Override
	public Iterable<Answer> answers(Query query) throws IOException {
		return answers(query, null);
	}

	@Override
	public Iterable<Answer> answers(final Query query, Boolean trueAnswers) throws IOException {
		if (trueAnswers != null && !trueAnswers && !hasWFS())
			return Collections.<Answer> emptyList();
		load();
		if (lastSolutionsIterator != null) {
			lastSolutionsIterator.cancel();
			lastSolutionsIterator = null;
		}
		final SolutionIterator solutions = prologEngine.goal(prologEngineInterface.detGoal(query, trueAnswers, "TM"),
				"[TM]");
		lastSolutionsIterator = solutions;
		final PrologDeductiveDatabaseManager xsbDatabase = this;
		return new Iterable<Answer>() {

			@Override
			public Iterator<Answer> iterator() {
				return new Iterator<Answer>() {

					private boolean canceled;

					@Override
					public boolean hasNext() {
						if (canceled)
							return false;
						return solutions.hasNext();
					}

					@Override
					public Answer next() {
						final Object[] bindings = solutions.next();
						if (!solutions.hasNext()) {
							solutions.cancel();
							canceled = true;
							xsbDatabase.lastSolutionsIterator = null;
						}
						final TermModel valuesList = (TermModel) bindings[0];
						return ans(query, valuesList);
					}

					@Override
					public void remove() {
						solutions.remove();
					}
				};
			}

		};
	}

	@Override
	public Map<List<Term>, TruthValue> answersValuations(Query query) throws IOException {
		return answersValuations(query, null);
	}

	@Override
	public Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) throws IOException {
		final Map<List<Term>, TruthValue> answers = new HashMap<List<Term>, TruthValue>();
		if (trueAnswers != null && trueAnswers == false && !hasWFS())
			return answers;
		load();
		final Object[] bindings = prologEngine
				.deterministicGoal(prologEngineInterface.nonDetGoal(query, trueAnswers, "TM"), "[TM]");
		if (bindings == null)
			return answers;
		final TermModel ansList = (TermModel) bindings[0];
		for (final TermModel ans : ansList.flatList())
			addAnswer(ans, answers);
		return answers;
	}

	@Override
	public void clear() {
		try {
			programs.clear();
			loaded = false;
			prologEngine.shutdown();
			startPrologEngine();
		} catch (IPException | PrologEngineCreationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create the concrete underlying {@link PrologEngine Prolog engine}.
	 */
	protected abstract PrologEngine createPrologEngine();

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		clear();
	}

	/**
	 * Write a Prolog file with all the {@link TableDirective table directives} and {@link Rule rules} of the loaded {@link Program program} to be
	 * loaded in the underlying {@link PrologEngine prolog engine}.
	 *
	 * @return the generated file.
	 * @throws IOException
	 *             if the generation of the file failed.
	 */
	private File generateFile() throws IOException {
		final File file = FileSystems.getDefault().getPath(PROLOG_FILE_NAME).toAbsolutePath().toFile();
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		final FormatVisitor xsbFormatedVisitor = new XSBFormatVisitor();
		final Set<TableDirective> tabledDirectives = new HashSet<>();
		for (final Program program : programs.values())
			tabledDirectives.addAll(program.getTableDirectives());
		for (final TableDirective predicate : tabledDirectives) {
			writer.write(predicate.accept(xsbFormatedVisitor));
			writer.newLine();
		}
		for (final Program program : programs.values())
			for (final Rule rule : program.getRules()) {
				writer.write(rule.accept(xsbFormatedVisitor));
				writer.newLine();
			}

		writer.close();
		return file;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#hasAnswers(pt.unl.fct.di.centria.nohr.model.Query)
	 */
	@Override
	public boolean hasAnswers(Query query) throws IOException {
		return hasAnswers(query, null);
	}

	@Override
	public boolean hasAnswers(Query query, Boolean trueAnswers) throws IOException {
		if (trueAnswers != null && !trueAnswers && !hasWFS())
			return false;
		load();
		if (trueAnswers == null)
			return prologEngine.deterministicGoal(prologEngineInterface.toString(query));
		else
			return prologEngine.deterministicGoal(prologEngineInterface.hasValue(query, trueAnswers));
	}

	@Override
	public abstract boolean hasWFS();

	/**
	 * Initializes the underlying Prolog engine. All the needed initializations commands must be executed here.
	 */
	abstract protected void initializePrologEngine();

	/**
	 * Load the loaded {@link Program programs} in the underlying {@link PrologEngine}.
	 *
	 * @throws IOException
	 *             if the generation of the Prolog file failed.
	 */
	private void load() throws IOException {
		if (loaded)
			return;
		RuntimesLogger.start("file writing");
		final File file = generateFile();
		RuntimesLogger.stop("file writing", "loading");
		RuntimesLogger.start("xsb loading");
		final boolean loaded = prologEngine.load_dynAbsolute(file);
		RuntimesLogger.stop("xsb loading", "loading");
		if (!loaded)
			throw new IPException("file not loaded");
		this.loaded = true;
	}

	@Override
	public void load(Program program) {
		programs.put(program.getID(), program);
		loaded = false;
	}

	@Override
	public void remove(Object programID, Rule rule) {
		final Program program = programs.get(programID);
		Set<TableDirective> tableDirectives;
		Set<Rule> rules;
		if (program == null) {
			tableDirectives = new HashSet<>();
			rules = new HashSet<>();
		} else {
			tableDirectives = program.getTableDirectives();
			rules = program.getRules();
		}
		rules.remove(rule);
		programs.put(programID, prog(programID, tableDirectives, rules));
	}

	@Override
	public void remove(Object programID, TableDirective tableDirective) {
		final Program program = programs.get(programID);
		Set<TableDirective> tableDirectives;
		Set<Rule> rules;
		if (program == null) {
			tableDirectives = new HashSet<>();
			rules = new HashSet<>();
		} else {
			tableDirectives = program.getTableDirectives();
			rules = program.getRules();
		}
		tableDirectives.remove(tableDirective);
		programs.put(programID, prog(programID, tableDirectives, rules));
	}

	/**
	 * Start the underlying {@link PrologEngine}.
	 *
	 * @throws IPException
	 *             if some exception was thrown by the Interprolog API.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out. That could mean that the Prolog system located at {@code binDirectory}
	 *             isn't an operational Prolog system.
	 */
	private void startPrologEngine() throws PrologEngineCreationException, IPException {
		prologEngine = tryPrologEngineCreation();
		final DeductiveDatabaseManager self = this;
		prologEngine.consultFromPackage(prologEngineInterface.getPrologModuleName(), self);
		initializePrologEngine();
	}

	/**
	 * Try to create a {@link PrologEngine} interrupting the creation and throwing an {@link PrologEngineCreationException} after the time specified
	 * by {@code CREATION_TIMEOUT} runs out.
	 *
	 * @throws IPException
	 *             if some exception was thrown by the Interprolog API.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out. That could mean that the Prolog system located at {@code binDirectory}
	 *             isn't an operational Prolog system.
	 */
	protected PrologEngine tryPrologEngineCreation() throws IPException, PrologEngineCreationException {
		PrologEngine result = null;
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<PrologEngine> future = executor.submit(new Callable<PrologEngine>() {

			@Override
			public PrologEngine call() throws Exception {
				return createPrologEngine();
			}
		});
		try {
			result = future.get(CREATION_TIMEOUT, TimeUnit.SECONDS);
		} catch (final TimeoutException e) {
			// Without the below cancel the thread will continue to live
			// even though the timeout exception thrown.
			future.cancel(false);
			throw new PrologEngineCreationException();
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		} catch (final ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof IPException)
				throw (IPException) cause;
			else
				throw new RuntimeException(e);
		}

		executor.shutdownNow();
		return result;
	}

}

package pt.unl.fct.di.centria.nohr.deductivedb;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.fail;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.table;

import java.io.BufferedWriter;
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

import pt.unl.fct.di.centria.nohr.Multiset;
import pt.unl.fct.di.centria.nohr.MultisetImpl;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.reasoner.VocabularyMapping;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * Partial implementation of {@link DeductiveDatabase} based on the Interprolog API.
 *
 * @author Nuno Costa
 */
public abstract class PrologDeductiveDatabase implements DeductiveDatabase {

	/**
	 * The maximum time, in seconds, in which the construction of a {@link PrologEngine} will be attempted, before giving up and throw a
	 * {@link PrologEngineCreationException}.
	 */
	private static final int CREATION_TIMEOUT = 3;

	/** The name of the file were the table directives and fail rules are written. */
	private static final String TABLES_FILE_NAME = "tables.P";

	private static final String FAILS_FILE_NAME = "fails.P";

	private final TermModelConverter termModelConverter;

	/** The {@link PrologSystemInterface} used to create the Prolog goals. */
	private final PrologSystemInterface prologEngineInterface;

	/**
	 * The {@link FormatVisitor} that formats the {@link Rule rules} and {@link TableDirective table directives} when they are sent to the Prolog
	 * engine.
	 */
	protected final FormatVisitor formatVisitor;

	/**
	 * The {@link SolutionIterator} returned by the last {@link PrologEngine#goal(String, String, Object[], String)} call.
	 */
	private SolutionIterator lastSolutionsIterator;

	/** The directory where the Prolog engine binary can be found. */
	protected final File binDirectory;

	/** The prolog engine to where the {@link Program programs} will be loaded and that will answer the queries. */
	protected PrologEngine prologEngine;

	private final Map<String, Set<Rule>> programs;

	/**
	 * The multiset of {@link TableDirectives} table directives, where the multiplicity represent the number of rules where the tabled predicate
	 * occurs.
	 */
	private final MultisetImpl<TableDirective> tableDirectives;

	/** The set of programs that have changed since the last call to {@link #load()}. Is {@code null} if all the programs where disposed. */
	private final Set<Object> changedProgramsKeys;

	/**
	 * The multiset of negative head predicate functors, where the multiplicity represent the number of rules where that predicates occurs.
	 */
	private final Multiset<Predicate> negativeHeadFunctors;

	private final Map<Predicate, Multiset<String>> definingPrograms;

	private final Map<String, Multiset<Predicate>> definedPredicates;

	/**
	 * Constructs a {@link DeductiveDatabase} with the Prolog system located in a given directory as underlying Prolog engine.
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
	public PrologDeductiveDatabase(File binDirectory, String prologModuleName, VocabularyMapping vocabularyMapping)
			throws IPException, PrologEngineCreationException {
		Objects.requireNonNull(binDirectory);
		this.binDirectory = binDirectory;
		formatVisitor = new XSBFormatVisitor();
		prologEngineInterface = new PrologSystemInterface(formatVisitor, prologModuleName);
		termModelConverter = new TermModelConverter(vocabularyMapping);
		tableDirectives = new MultisetImpl<>();
		negativeHeadFunctors = new MultisetImpl<>();
		definingPrograms = new HashMap<>();
		definedPredicates = new HashMap<>();
		programs = new HashMap<>();
		changedProgramsKeys = new HashSet<>();
		startPrologEngine();
	}

	@Override
	public void add(String programKey, Rule rule) {
		tableDirectives.addAll(tableDirectives(rule));
		final Predicate headFunctor = rule.getHead().getFunctor();
		addToMultiset(headFunctor, programKey, definingPrograms);
		addToMultiset(programKey, headFunctor, definedPredicates);
		addToSet(programKey, rule, programs);
		if (headFunctor.isMetaPredicate() && headFunctor.asMetaPredicate().hasType(PredicateType.NEGATIVE))
			negativeHeadFunctors.add(headFunctor);
		changedProgramsKeys.add(programKey);
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

	private <K, E> void addToMultiset(K key, E element, Map<K, Multiset<E>> map) {
		Multiset<E> collection = map.get(key);
		if (collection == null) {
			collection = new MultisetImpl<E>();
			map.put(key, collection);
		}
		collection.add(element);
	}

	private <K, E> void addToSet(K key, E element, Map<K, Set<E>> map) {
		Set<E> collection = map.get(key);
		if (collection == null) {
			collection = new HashSet<E>();
			map.put(key, collection);
		}
		collection.add(element);
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
		final PrologDeductiveDatabase xsbDatabase = this;
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

	/**
	 * Create the concrete underlying {@link PrologEngine Prolog engine}.
	 */
	protected abstract PrologEngine createPrologEngine();

	@Override
	public void dipose() {
		try {
			tableDirectives.clear();
			programs.clear();
			changedProgramsKeys.clear();
			prologEngine.shutdown();
			startPrologEngine();
		} catch (IPException | PrologEngineCreationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dispose(Object programKey) {
		programs.remove(programKey);
	}

	/**
	 * Computes the set of predicates whose atoms must be failed (i.e. must have a false truth value) and add the corresponding rules.
	 */
	private Set<Rule> failRules() {
		final Set<Rule> result = new HashSet<>();
		for (final TableDirective tableDirective : tableDirectives)
			if (isFailed(tableDirective.getPredicate()))
				result.add(rule(atom(tableDirective.getPredicate()), fail()));
		return result;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		dipose();
	}

	private File generateFailsFile() throws IOException {
		final File file = FileSystems.getDefault().getPath(FAILS_FILE_NAME).toAbsolutePath().toFile();
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		final FormatVisitor xsbFormatedVisitor = new XSBFormatVisitor();
		for (final Rule rule : failRules()) {
			writer.write(rule.accept(xsbFormatedVisitor));
			writer.newLine();
		}
		writer.close();
		return file;
	}

	private File generateFile(String programKey) throws IOException {
		final File file = FileSystems.getDefault().getPath(programKey.toString() + ".P").toAbsolutePath().toFile();
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		final FormatVisitor xsbFormatedVisitor = new XSBFormatVisitor();
		for (final Predicate predicate : definedPredicates.get(programKey))
			if (definingPrograms.get(predicate).size() > 1) {
				writer.write(multifileDirective(predicate));
				writer.newLine();
			}
		for (final Rule rule : programs.get(programKey)) {
			writer.write(rule.accept(xsbFormatedVisitor));
			writer.newLine();
		}
		writer.close();
		return file;
	}

	private File generateTablesFile() throws IOException {
		final File file = FileSystems.getDefault().getPath(TABLES_FILE_NAME).toAbsolutePath().toFile();
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		final FormatVisitor xsbFormatedVisitor = new XSBFormatVisitor();
		for (final TableDirective predicate : tableDirectives) {
			writer.write(predicate.accept(xsbFormatedVisitor));
			writer.newLine();
		}
		writer.close();
		return file;
	}

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

	private boolean isFailed(Predicate predicate) {
		return predicate.isMetaPredicate() && predicate.asMetaPredicate().hasType(PredicateType.NEGATIVE)
				&& !negativeHeadFunctors.contains(predicate);
	}

	/**
	 * Load all the <i>programs</i> in the underlying {@link PrologEngine}.
	 *
	 * @throws IOException
	 *             if the generation of the Prolog file failed.
	 */
	private void load() throws IOException {
		if (changedProgramsKeys == null || changedProgramsKeys.isEmpty())
			return;
		RuntimesLogger.start("file writing");
		final List<File> files = new ArrayList<>(programs.size());
		files.add(generateTablesFile());
		files.add(generateFailsFile());
		for (final String programKey : programs.keySet())
			files.add(generateFile(programKey));
		RuntimesLogger.stop("file writing", "loading");
		RuntimesLogger.start("xsb loading");
		for (final File file : files)
			if (!prologEngine.load_dynAbsolute(file))
				throw new IPException("file not loaded");
		RuntimesLogger.stop("xsb loading", "loading");
		changedProgramsKeys.clear();
	}

	abstract protected String multifileDirective(Predicate predicate);

	@Override
	public void remove(String programKey, Rule rule) {
		final Set<Rule> program = programs.get(programKey);
		if (program == null)
			return;
		tableDirectives.removeAll(tableDirectives(rule));
		final Predicate headFunctor = rule.getHead().getFunctor();
		definedPredicates.get(programKey).remove(headFunctor);
		definingPrograms.get(headFunctor).remove(programKey);
		if (headFunctor.isMetaPredicate() && headFunctor.asMetaPredicate().hasType(PredicateType.NEGATIVE))
			negativeHeadFunctors.remove(headFunctor);
		program.remove(rule);
		changedProgramsKeys.add(programKey);
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
		final DeductiveDatabase self = this;
		prologEngine.consultFromPackage(prologEngineInterface.getPrologModuleName(), self);
		initializePrologEngine();
	}

	abstract protected String tableDirective(Predicate predicate);

	private Set<TableDirective> tableDirectives(Rule rule) {
		final Set<TableDirective> tableDirectives = new HashSet<>();
		final Predicate headPred = rule.getHead().getFunctor();
		tableDirectives.add(table(headPred));
		for (final Literal negLiteral : rule.getNegativeBody())
			tableDirectives.add(table(negLiteral.getFunctor()));
		return tableDirectives;
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

package pt.unl.fct.di.centria.nohr.deductivedb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import pt.unl.fct.di.centria.nohr.HashMultiset;
import pt.unl.fct.di.centria.nohr.Multiset;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.NegativeLiteral;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.terminals.Vocabulary;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * Abstract implementation of {@link DeductiveDatabase} based on the Interprolog API. In order to ensures termination and to support the default
 * negation, each predicate, <i>P</i>, satisfying one of the following conditions is tabled: <br>
 * - <i>P</i> appears in some (non fact) rule head and in some rule body; <br>
 * - <i>P</i> appears in some {@link NegativeLiteral negative literal}. <br>
 *
 * @author Nuno Costa
 */
public abstract class PrologDeductiveDatabase implements DeductiveDatabase {

	private class ProgramImpl implements DatabaseProgram {

		private final Set<Rule> rules;

		private ProgramImpl() {
			rules = new HashSet<>();
		}

		@Override
		public void add(Rule rule) {
			if (rules.add(rule))
				addPredicates(rule);
		}

		@Override
		public void addAll(Collection<Rule> rules) {
			for (final Rule rule : rules)
				add(rule);
		}

		@Override
		public void clear() {
			for (final Rule rule : rules)
				removePredicates(rule);
			rules.clear();
		}

		@Override
		protected void finalize() {
			rules.clear();
			programs.remove(this);
		}

		@Override
		public DeductiveDatabase getDeductiveDatabase() {
			return PrologDeductiveDatabase.this;
		}

		@Override
		public void remove(Rule rule) {
			if (rules.remove(rule))
				removePredicates(rule);
		}

		@Override
		public void removeAll(Collection<Rule> rules) {
			for (final Rule rule : rules)
				remove(rule);
		}

	}

	/**
	 * The extension of the Prolog files.
	 */
	private static final String PROLOG_EXTENSION = ".P";

	/**
	 * The prefix of the {@link PrologDeductiveDatabase}'s files.
	 */
	private static final String FILE_PREFIX = "deductivedb";

	/**
	 * The maximum time, in seconds, in which the construction of a {@link PrologEngine} will be attempted, before giving up and throw a
	 * {@link PrologEngineCreationException}.
	 */
	private static final int CREATION_TIMEOUT = 3;

	/** The {@link TermModelConverter} that converts the {@link TermModel}s, of the results of {@link PrologEngine} calls, to {@link Term terms}. */
	private final TermModelConverter termModelConverter;

	/**
	 * The file where the {@link Rule rules} of the loaded {@link DatabaseProgram programs} are written and from where they are loaded in the
	 * underlying Prolog engine.
	 */
	protected final File file;

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

	/** The Prolog module that defines the predicates described {@link Goals}. */
	protected final String prologModule;

	/** The prolog engine to where the {@link DatabaseProgram programs} will be loaded and that will answer the queries. */
	protected PrologEngine prologEngine;

	/**
	 * Indicates whether the loaded {@link DatabaseProgram programs} have changed since the last call to {@link #commit()}.
	 */
	private boolean hasChanges;

	/** The set of loaded {@link DatabaseProgram programs}. */
	private final Set<ProgramImpl> programs;

	/** The multiset of arities of the predicates of the loaded programs, the multiplicity represents the number of occurrences. */
	private final Multiset<Integer> arities;

	/**
	 * The multiset of predicates that are functor of atoms that occur in heads of non fact (i.e. with non empty body) rules, where the multiplicity
	 * represent the number of {@link Rule rules} of the loaded programs where that predicates occurs in such position.
	 */
	private final Multiset<Predicate> headFunctors;

	/**
	 * The multiset of predicates that are functor of atoms that occur in facts, where the multiplicity represents the number of {@link Rule rules} of
	 * the loaded {@link DatabaseProgram programs} where that predicates occurs in such position.
	 */
	private final Multiset<Predicate> factFunctors;

	/**
	 * The multiset of predicates that are functor of atoms that occur in positive bodies (see {@link Rule#getPositiveBody()}), where the multiplicity
	 * represents the number of {@link Rule rules} of the loaded {@link DatabaseProgram programs} where that predicates occurs in such position.
	 */
	private final Multiset<Predicate> positiveBodyFunctors;

	/**
	 * The multiset of predicates that are functor of atoms that occur in negative bodies (see {@link Rule#getNegativeBody()} , where the multiplicity
	 * represents the number of {@link Rule rules} of the loaded {@link Programs programs} where that predicates occur in such position.
	 */
	private final Multiset<Predicate> negativeBodyFunctors;

	protected final Vocabulary vocabulary;

	/**
	 * Constructs a {@link DeductiveDatabase} with the Prolog system located in a given directory as underlying Prolog engine.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system that will be used as underlying Prolog engine is located.
	 * @param prologModule
	 *            the name of the Prolog module that defines the predicates specified by {@link Goals}.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out. That could mean that the Prolog system located at {@code binDirectory}
	 *             isn't an operational Prolog system. @
	 */
	public PrologDeductiveDatabase(File binDirectory, String prologModule, FormatVisitor formatVisitor,
			Vocabulary vocabulary) throws PrologEngineCreationException {
		Objects.requireNonNull(binDirectory);
		Objects.requireNonNull(prologModule);
		Objects.requireNonNull(formatVisitor);
		Objects.requireNonNull(vocabulary);
		this.binDirectory = binDirectory;
		this.prologModule = prologModule;
		this.formatVisitor = formatVisitor;
		this.vocabulary = vocabulary;
		try {
			file = File.createTempFile(FILE_PREFIX, PROLOG_EXTENSION);
			file.deleteOnExit();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		programs = new HashSet<>();
		arities = new HashMultiset<>();
		factFunctors = new HashMultiset<>();
		headFunctors = new HashMultiset<>();
		positiveBodyFunctors = new HashMultiset<>();
		negativeBodyFunctors = new HashMultiset<>();
		termModelConverter = new TermModelConverter(vocabulary);
		try {
			startPrologEngine();
		} catch (final IPException e) {
			throw new PrologEngineCreationException(e);
		}
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
	 * Add the predicates of a given rule to the appropriate multisets, {@link #factFunctors}, {@link #headFunctors}, {@link #positiveBodyFunctors}
	 * and {@link #negativeBodyFunctors}, and sets {@link #hasChanges} to true.
	 *
	 * @param rule
	 *            a rule.
	 */
	private void addPredicates(Rule rule) {
		final Predicate headFunctor = rule.getHead().getFunctor();
		arities.add(headFunctor.getArity());
		if (rule.isFact())
			factFunctors.add(headFunctor);
		else {
			headFunctors.add(headFunctor);
			for (final Literal literal : rule.getPositiveBody()) {
				final Predicate pred = literal.getFunctor();
				arities.add(pred.getArity());
				positiveBodyFunctors.add(pred);
			}
			for (final Literal literal : rule.getNegativeBody()) {
				final Predicate pred = literal.getFunctor();
				arities.add(pred.getArity());
				negativeBodyFunctors.add(pred);
			}
		}
		hasChanges = true;
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
	public Answer answer(Query query) {
		return answer(query, null);
	}

	@Override
	public Answer answer(Query query, Boolean trueAnswers) {
		if (trueAnswers != null && !trueAnswers && !hasWFS())
			return null;
		commit();
		final Object[] bindings = prologEngine.deterministicGoal(Goals.detGoal(formatVisitor, query, trueAnswers, "TM"),
				"[TM]");
		if (bindings == null)
			return null;
		return ans(query, (TermModel) bindings[0]);
	}

	@Override
	public Iterable<Answer> answers(Query query) {
		return answers(query, null);
	}

	@Override
	public Iterable<Answer> answers(final Query query, Boolean trueAnswers) {
		if (trueAnswers != null && !trueAnswers && !hasWFS())
			return Collections.<Answer> emptyList();
		commit();
		if (lastSolutionsIterator != null) {
			lastSolutionsIterator.cancel();
			lastSolutionsIterator = null;
		}
		final SolutionIterator solutions = prologEngine.goal(Goals.detGoal(formatVisitor, query, trueAnswers, "TM"),
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
	public Map<List<Term>, TruthValue> answersValuations(Query query) {
		return answersValuations(query, null);
	}

	@Override
	public Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) {
		final Map<List<Term>, TruthValue> answers = new HashMap<List<Term>, TruthValue>();
		if (trueAnswers != null && trueAnswers == false && !hasWFS())
			return answers;
		commit();
		final Object[] bindings = prologEngine
				.deterministicGoal(Goals.nonDetGoal(formatVisitor, query, trueAnswers, "TM"), "[TM]");
		if (bindings == null)
			return answers;
		final TermModel ansList = (TermModel) bindings[0];
		for (final TermModel ans : ansList.flatList())
			addAnswer(ans, answers);
		return answers;
	}

	/**
	 * Commits all the loaded {@link DatabaseProgram programs} to the underlying {@link PrologEngine}.
	 *
	 * @throws PrologEngineCreationException
	 */
	protected void commit() {
		if (!hasChanges)
			return;
		restartPrologEngine();
		RuntimesLogger.start("file writing");
		try {
			write();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		RuntimesLogger.stop("file writing", "loading");
		RuntimesLogger.start("xsb loading");
		load();
		RuntimesLogger.stop("xsb loading", "loading");
		hasChanges = false;
	}

	@Override
	public DatabaseProgram createProgram() {
		final ProgramImpl program = new ProgramImpl();
		programs.add(program);
		return program;
	}

	/**
	 * Create the concrete underlying {@link PrologEngine Prolog engine}.
	 */
	protected abstract PrologEngine createPrologEngine();

	@Override
	public void dipose() {
		try {
			headFunctors.clear();
			negativeBodyFunctors.clear();
			programs.clear();
			hasChanges = false;
			prologEngine.shutdown();
		} catch (final IPException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the string representation of a fail rule (i.e. the rule that ensures that all atoms with the given functor predicate have a false truth
	 * value, what is needed for tabled predicates that doesn't occur in any rule body) of a given predicate.
	 *
	 * @param pred
	 *            the predicate.
	 * @return the string representation of the fail rule for {@code pred}.
	 */
	protected abstract String failRule(Predicate pred);

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		dipose();
	}

	@Override
	public boolean hasAnswers(Query query) {
		return hasAnswers(query, null);
	}

	@Override
	public boolean hasAnswers(Query query, Boolean trueAnswers) {
		if (trueAnswers != null && !trueAnswers && !hasWFS())
			return false;
		commit();
		if (trueAnswers == null)
			return prologEngine.deterministicGoal(Goals.toString(formatVisitor, query));
		else
			return prologEngine.deterministicGoal(Goals.hasValue(formatVisitor, query, trueAnswers));
	}

	@Override
	public abstract boolean hasWFS();

	/**
	 * Initializes the underlying Prolog engine. All the needed initializations commands must be executed here.
	 */
	abstract protected void initializePrologEngine();

	/**
	 * Loads {@link #file} in the underlying Prolog engine.
	 */
	protected abstract void load();

	/**
	 * Removes the predicates of a given rule from the appropriate multisets, {@link #factFunctors}, {@link #headFunctors},
	 * {@link #positiveBodyFunctors} and {@link #negativeBodyFunctors}, and sets {@link #hasChanges} to true.
	 *
	 * @param rule
	 *            a rule.
	 */
	private void removePredicates(Rule rule) {
		final Predicate headFunctor = rule.getHead().getFunctor();
		arities.add(headFunctor.getArity());
		if (rule.isFact())
			factFunctors.remove(headFunctor);
		else {
			headFunctors.remove(headFunctor);
			for (final Literal literal : rule.getPositiveBody()) {
				final Predicate pred = literal.getFunctor();
				arities.remove(pred.getArity());
				positiveBodyFunctors.remove(pred);
			}
			for (final Literal literal : rule.getNegativeBody()) {
				final Predicate pred = literal.getFunctor();
				arities.add(pred.getArity());
				negativeBodyFunctors.remove(pred);
			}
		}
		hasChanges = true;
	}

	/**
	 * Restarts the underlying Prolog engine.
	 */
	private void restartPrologEngine() {
		prologEngine.shutdown();
		try {
			startPrologEngine();
		} catch (final PrologEngineCreationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts the underlying Prolog engine.
	 *
	 * @throws PrologEngineCreationException
	 */
	private void startPrologEngine() throws PrologEngineCreationException {
		prologEngine = tryPrologEngineCreation();
		final DeductiveDatabase self = this;
		prologEngine.consultFromPackage(prologModule, self);
		initializePrologEngine();
	}

	/**
	 * Returns the table directive for a given predicate.
	 *
	 * @param predicate
	 *            the predicate.
	 * @return the table directive for {@code predicate}.
	 */
	abstract protected String tableDirective(Predicate predicate);

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
			throw new PrologEngineCreationException(e);
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

	/**
	 * Write the {@link Rule rules} of all the loaded {@link DatabaseProgram programs} in {@link #file}, and the corresponding table directives and
	 * fail rules. @
	 */
	protected void write() throws IOException {
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.newLine();
		for (final Predicate predicate : headFunctors)
			if (positiveBodyFunctors.contains(predicate)) {
				writer.write(tableDirective(predicate));
				writer.newLine();
			}
		for (final Predicate predicate : negativeBodyFunctors) {
			writer.write(tableDirective(predicate));
			writer.newLine();
		}
		for (final Predicate pred : negativeBodyFunctors)
			if (!factFunctors.contains(pred) && !headFunctors.contains(pred)) {
				writer.write(failRule(pred));
				writer.newLine();
			}
		for (final ProgramImpl program : programs)
			for (final Rule rule : program.rules) {
				writer.write(rule.accept(formatVisitor));
				writer.newLine();
			}
		writer.newLine();
		writer.close();
	}

}

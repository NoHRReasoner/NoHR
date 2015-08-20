package pt.unl.fct.di.centria.nohr.prolog;

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

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.FormatVisitable;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public abstract class PrologDedutiveDatabase implements DedutiveDatabaseManager {

	private static final String TRANSLATION_FILE_NAME = "nohrtr.P";

	private final FormatVisitor formatVisitor;

	private SolutionIterator lastSolutionsIterator;

	protected final File binDirectory;

	private PrologEngine prologEngine;

	private final Map<Object, Program> programs;

	private boolean loaded = false;

	public PrologDedutiveDatabase(File binDirectory) throws IPException, DatabaseCreationException {
		Objects.requireNonNull(binDirectory);
		this.binDirectory = binDirectory;
		formatVisitor = new XSBFormatVisitor();
		programs = new HashMap<>();
		prologEngine = createPrologEngine();
	}

	private void addAnswer(TermModel valuesList, Map<List<Term>, TruthValue> answers) {
		final TermModel[] termsList = valuesList.flatList();
		final List<Term> vals = new ArrayList<Term>(termsList.length);
		for (int i = 1; i < termsList.length; i++)
			vals.add(TermModelAdapter.getTerm(termsList[i]));
		final TruthValue truth = TermModelAdapter.getTruthValue(termsList[0]);
		answers.put(vals, truth);

	}

	private Answer ans(Query query, TermModel valuesList) {
		final TermModel[] termsList = valuesList.flatList();
		final TruthValue truth = TermModelAdapter.getTruthValue(termsList[0]);
		final List<Term> vals = new ArrayList<Term>(termsList.length);
		for (int i = 1; i <= query.getVariables().size(); i++)
			vals.add(TermModelAdapter.getTerm(termsList[i]));
		return Model.ans(query, truth, vals);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#query(pt.unl.fct.di.centria.nohr.model.Query)
	 */
	@Override
	public Answer answer(Query query) throws IOException {
		return answer(query, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#query(pt.unl.fct.di.centria.nohr.model.Query, java.lang.Boolean)
	 */
	@Override
	public Answer answer(Query query, Boolean trueAnswers) throws IOException {
		if (trueAnswers != null && !trueAnswers && !isTrivalued())
			return null;
		load();
		final Object[] bindings = prologEngine.deterministicGoal(detGoal(query, trueAnswers, "TM"), "[TM]");
		if (bindings == null)
			return null;
		return ans(query, (TermModel) bindings[0]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#lazilyQuery(pt.unl.fct.di.centria.nohr.model.Query)
	 */
	@Override
	public Iterable<Answer> answers(Query query) throws IOException {
		return answers(query, null);
	}

	/*
	 * (non-Javadoc):w
	 *
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#lazilyQuery(pt.unl.fct.di.centria.nohr.model.Query, java.lang.Boolean)
	 */
	@Override
	public Iterable<Answer> answers(final Query query, Boolean trueAnswers) throws IOException {
		if (trueAnswers != null && !trueAnswers && !isTrivalued())
			return Collections.<Answer> emptyList();
		load();
		if (lastSolutionsIterator != null) {
			lastSolutionsIterator.cancel();
			lastSolutionsIterator = null;
		}
		final SolutionIterator solutions = prologEngine.goal(detGoal(query, trueAnswers, "TM"), "[TM]");
		lastSolutionsIterator = solutions;
		final PrologDedutiveDatabase xsbDatabase = this;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#queryAll(pt.unl.fct.di.centria.nohr.model.Query)
	 */
	@Override
	public Map<List<Term>, TruthValue> answersValuations(Query query) throws IOException {
		return answersValuations(query, null);
	}

	@Override
	public Map<List<Term>, TruthValue> answersValuations(Query query, Boolean trueAnswers) throws IOException {
		final Map<List<Term>, TruthValue> answers = new HashMap<List<Term>, TruthValue>();
		if (trueAnswers != null && trueAnswers == false && !isTrivalued())
			return answers;
		load();
		final Object[] bindings = prologEngine.deterministicGoal(nonDetGoal(query, trueAnswers, "TM"), "[TM]");
		if (bindings == null)
			return answers;
		final TermModel ansList = (TermModel) bindings[0];
		for (final TermModel ans : ansList.flatList())
			addAnswer(ans, answers);
		return answers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#clear()
	 */
	@Override
	public void clear() {
		try {
			programs.clear();
			loaded = false;
			prologEngine.shutdown();
			prologEngine = createPrologEngine();
		} catch (IPException | DatabaseCreationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return
	 * @throws IPException
	 * @throws DatabaseCreationException
	 */
	abstract protected PrologEngine createPrologEngine() throws IPException, DatabaseCreationException;

	private String detGoal(Query query, Boolean trueAnswers, String var) {
		if (trueAnswers == null)
			return String.format("detGoal([%s],(%s),%s)", varsList(query), query.accept(formatVisitor), var);
		else
			return String.format("detGoal([%s],(%s),%s,%s)", varsList(query), query.accept(formatVisitor),
					toString(trueAnswers), var);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		clear();
	}

	private File generateTranslationFile() throws IOException {
		final File file = FileSystems.getDefault().getPath(TRANSLATION_FILE_NAME).toAbsolutePath().toFile();
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

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#hasAnswers(pt.unl.fct.di.centria.nohr.model.Query, java.lang.Boolean)
	 */
	@Override
	public boolean hasAnswers(Query query, Boolean trueAnswers) throws IOException {
		if (trueAnswers != null && !trueAnswers && !isTrivalued())
			return false;
		load();
		if (trueAnswers == null)
			return prologEngine.deterministicGoal(toString(query));
		else
			return prologEngine.deterministicGoal(hasValue(query, trueAnswers));
	}

	private String hasValue(Query query, boolean trueAnswer) {
		return String.format("hasValue((%s), %s)", toString(query), toString(trueAnswer));
	}

	@Override
	public abstract boolean isTrivalued();

	private void load() throws IOException {
		if (loaded)
			return;
		RuntimesLogger.start("file writing");
		final File file = generateTranslationFile();
		RuntimesLogger.stop("file writing", "loading");
		RuntimesLogger.start("xsb loading");
		final boolean loaded = prologEngine.load_dynAbsolute(file);
		RuntimesLogger.stop("xsb loading", "loading");
		if (!loaded)
			throw new IPException("file not loaded");
		this.loaded = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#load(pt.unl.fct.di.centria.nohr.model.Program)
	 */
	@Override
	public void load(Program program) {
		programs.put(program.getID(), program);
		loaded = false;
	}

	private String nonDetGoal(Query query, Boolean trueAnswers, String var) {
		if (trueAnswers == null)
			return String.format("nonDetGoal([%s],(%s),%s)", varsList(query), toString(query), var);
		else
			return String.format("nonDetGoal([%s],(%s),%s,%s)", varsList(query), toString(query), toString(trueAnswers),
					var);
	}

	private String toString(boolean trueValue) {
		return trueValue ? "true" : "undefined";
	}

	private String toString(FormatVisitable element) {
		return element.accept(formatVisitor);
	}

	protected PrologEngine tryPrologEngineCreation(Callable<PrologEngine> prologEngineCreator)
			throws IPException, DatabaseCreationException {
		PrologEngine result = null;
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<PrologEngine> future = executor.submit(prologEngineCreator);
		try {
			result = future.get(3, TimeUnit.SECONDS);
		} catch (final TimeoutException e) {
			// Without the below cancel the thread will continue to live
			// even though the timeout exception thrown.
			future.cancel(false);
			throw new DatabaseCreationException();
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

	private String varsList(Query query) {
		return Model.concat(query.getVariables(), formatVisitor, ",");
	}
}

package pt.unl.fct.di.centria.nohr.reasoner;

import static pt.unl.fct.di.centria.nohr.model.Model.ans;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;

/**
 * Handle queries to a {@link HybridKB}. Given a certain query, executes the corresponding original and double - or original only, if the KB hasn't
 * disjunctions - queries on the underlying {@link DeductiveDatabaseManager} and combines their results to obtain the query's answers according to
 * {@link <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i>Query-driven Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
 *
 * @author Nuno Costa
 */
public class QueryProcessor {

	/**
	 * The underlying {@link DeductiveDatabaseManager}, where the queries will be posed.
	 */
	protected DeductiveDatabaseManager deductiveDatabaseManager;

	/**
	 * Constructs a query processor to a given {@link DeductiveDatabaseManager}.
	 *
	 * @param deductiveDatabaseManager
	 *            the {@link DeductiveDatabaseManager} where the queries will be posed.
	 */
	protected QueryProcessor(DeductiveDatabaseManager deductiveDatabaseManager) {
		this.deductiveDatabaseManager = deductiveDatabaseManager;
	}

	/**
	 * Obtains all answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param isDoubled
	 *            specifies whether the KB is doubled is doubled.
	 * @return the list of all answers {@code query}.
	 * @throws IOException
	 *             if the underlying {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	protected List<Answer> allAnswers(Query query, boolean hasDoubled) throws IOException {
		return allAnswers(query, hasDoubled, true, true, hasDoubled);
	}

	/**
	 * Obtains all answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param isDoubled
	 *            specifies whether the KB is doubled.
	 * @param trueAnswers
	 *            specifies whether to obtain {@link TruthValue#TRUE true} answers.
	 * @param undefinedAnswers
	 *            specifies whether to obtain {@link TruthValue#UNDEFINED undefined} answers.
	 * @param inconsistentAnswers
	 *            specifies whether to obtain {@link TruthValue#INCONSISTENT inconsistent} answers.
	 * @return the list of all answers {@code query} valued according to the {@code trueAnswers}, {@code undefinedAnswers} and
	 *         {@code inconsistentAnswers} flags.
	 * @throws IOException
	 *             if the underlying {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	protected List<Answer> allAnswers(Query query, boolean isDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (inconsistentAnswers && isDoubled == false)
			throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
		if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
			throw new IllegalArgumentException("must have at least one truth value enabled");
		final List<Answer> result = new LinkedList<Answer>();
		Map<List<Term>, TruthValue> origAnss;
		if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
			origAnss = deductiveDatabaseManager.answersValuations(query.getOriginal(), false);
		else if (inconsistentAnswers && !trueAnswers && !undefinedAnswers)
			origAnss = deductiveDatabaseManager.answersValuations(query.getOriginal(), true);
		else
			origAnss = deductiveDatabaseManager.answersValuations(query.getOriginal());
		Map<List<Term>, TruthValue> doubAnss = new HashMap<List<Term>, TruthValue>();
		if (isDoubled)
			if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
				doubAnss = deductiveDatabaseManager.answersValuations(query.getDouble(), false);
			else
				doubAnss = deductiveDatabaseManager.answersValuations(query.getDouble());
		for (final Entry<List<Term>, TruthValue> origEntry : origAnss.entrySet()) {
			final List<Term> vals = origEntry.getKey();
			final TruthValue origTruth = origEntry.getValue();
			TruthValue truth;
			if (isDoubled) {
				TruthValue doubTruth = doubAnss.get(vals);
				if (doubTruth == null)
					doubTruth = TruthValue.FALSE;
				truth = process(origTruth, doubTruth);
			} else
				truth = origTruth;
			if (isRequiredTruth(truth, trueAnswers, undefinedAnswers, inconsistentAnswers))
				result.add(ans(query, truth, vals));
		}
		return result;
	}

	/**
	 * Checks if there is some answer to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param isDoubled
	 *            specifies whether the KB is doubled.
	 * @param trueAnswers
	 *            specifies whether to consider {@link TruthValue#TRUE true} answers.
	 * @param undefinedAnswers
	 *            specifies whether to consider {@link TruthValue#UNDEFINED undefined} answers.
	 * @param inconsistentAnswers
	 *            specifies whether to consider :w {@link TruthValue#INCONSISTENT inconsistent} answers.
	 * @return true iff there is at least one answer to {@code query} valued according to the {@code trueAnswers}, {@code undefinedAnswers} and
	 *         {@code inconsistentAnswers} flags.
	 * @throws IOException
	 *             if the underlying {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	protected boolean hasAnswer(Query query, boolean hasDoubled) throws IOException {
		return hasAnswer(query, hasDoubled, true, true, hasDoubled);
	}

	/**
	 * Checks if there is some answer to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param isDoubled
	 *            specifies whether the KB is doubled.
	 * @param trueAnswers
	 *            specifies whether to consider {@link TruthValue#TRUE true} answers.
	 * @param undefinedAnswers
	 *            specifies whether to consider {@link TruthValue#UNDEFINED undefined} answers.
	 * @param inconsistentAnswers
	 *            specifies whether to consider :w {@link TruthValue#INCONSISTENT inconsistent} answers.
	 * @return true iff there is at least one answer to {@code query} valued according to the {@code trueAnswers}, {@code undefinedAnswers} and
	 *         {@code inconsistentAnswers} flags.
	 * @throws IOException
	 *             if the underlying {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	protected boolean hasAnswer(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (inconsistentAnswers && hasDoubled == false)
			throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
		if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
			throw new IllegalArgumentException("must have at least one truth value enabled");
		final Query origQuery = query.getOriginal();
		// true original answers
		if (inconsistentAnswers || trueAnswers)
			for (final Answer origAns : deductiveDatabaseManager.answers(origQuery, true)) {
				if (trueAnswers && !hasDoubled)
					return true;
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				final boolean hasDoubAns = deductiveDatabaseManager.hasAnswers(doubQuery);
				if (inconsistentAnswers && !hasDoubAns)
					return true;
				if (trueAnswers && hasDoubAns)
					return true;
			}
		// undefined original answers
		if (trueAnswers || undefinedAnswers)
			for (final Answer origAns : deductiveDatabaseManager.answers(origQuery, false)) {
				if (!hasDoubled && undefinedAnswers)
					return true;
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				if (trueAnswers && hasDoubled && deductiveDatabaseManager.hasAnswers(doubQuery, true))
					return true;
				if (undefinedAnswers && deductiveDatabaseManager.hasAnswers(doubQuery, false))
					return true;
			}
		return false;
	}

	/**
	 * Check whether a given {@link TruthValue truth value} match the given truth values flags.
	 *
	 * @param truth
	 *            the truth value.
	 * @param trueFlag
	 *            specifies whether the {@link TruthValue truth value} {@link TruthValue#TRUE true} is enabled.
	 * @param undefinedFlag
	 *            specifies whether the {@link TruthValue truth value} {@link TruthValue#UNDEFINED undefined} is enabled.
	 * @param inconsistentFlag
	 *            specifies whether the {@link TruthValue truth value} {@link TruthValue#INCONSISTENT inconsistent} is enabled.
	 * @return true iff {@code truth} match the given flags.
	 */
	private boolean isRequiredTruth(TruthValue truth, boolean trueFlag, boolean undefinedFlag,
			boolean inconsistentFlag) {
		switch (truth) {
		case TRUE:
			return trueFlag;
		case UNDEFINED:
			return undefinedFlag;
		case INCONSISTENT:
			return inconsistentFlag;
		default:
			return false;
		}
	}

	/**
	 * Obtains one answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param isDoubled
	 *            specifies whether the KB is doubled.
	 * @return one answer to {@code query}.
	 * @throws IOException
	 *             if the underlying {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	protected Answer oneAnswer(Query query, boolean hasDoubled) throws IOException {
		return oneAnswer(query, hasDoubled, true, true, hasDoubled);
	}

	/**
	 * Obtains one answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param isDoubled
	 *            specifies whether the KB is doubled.
	 * @param trueAnswers
	 *            specifies whether to obtain a {@link TruthValue#TRUE true} answers.
	 * @param undefinedAnswers
	 *            specifies whether to obtain a {@link TruthValue#UNDEFINED undefined} answers.
	 * @param inconsistentAnswers
	 *            specifies whether to obtain a {@link TruthValue#INCONSISTENT inconsistent} answers.
	 * @return one answer to {@code query} valued according to the {@code trueAnswers}, {@code undefinedAnswers} and {@code inconsistentAnswers}
	 *         flags.
	 * @throws IOException
	 *             if the underlying {@link DeductiveDatabaseManager} needed to read or write some file and was unsuccessful.
	 */
	protected Answer oneAnswer(Query query, boolean isDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (inconsistentAnswers && isDoubled == false)
			throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
		if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
			throw new IllegalArgumentException("must have at least one truth value enabled");
		final Query origQuery = query.getOriginal();
		// undefined original answers
		if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
			for (final Answer origAns : deductiveDatabaseManager.answers(origQuery, false)) {
				if (!isDoubled)
					return ans(query, TruthValue.UNDEFINED, origAns.getValues());
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				if (deductiveDatabaseManager.hasAnswers(doubQuery, false))
					return ans(query, TruthValue.UNDEFINED, origAns.getValues());
			}
		// true original answers
		if (!undefinedAnswers)
			for (final Answer origAns : deductiveDatabaseManager.answers(origQuery, true)) {
				if (trueAnswers && !isDoubled)
					return ans(query, TruthValue.TRUE, origAns.getValues());
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				final boolean hasDoubAns = deductiveDatabaseManager.hasAnswers(doubQuery);
				if (trueAnswers && hasDoubAns)
					return ans(query, TruthValue.TRUE, origAns.getValues());
				else if (inconsistentAnswers && !hasDoubAns)
					return ans(query, TruthValue.INCONSISTENT, origAns.getValues());
			}

		// all original answers
		for (final Answer origAns : deductiveDatabaseManager.answers(origQuery, null)) {
			final TruthValue origTruth = origAns.getValuation();
			if ((trueAnswers && origTruth == TruthValue.TRUE || undefinedAnswers && origTruth == TruthValue.UNDEFINED)
					&& !isDoubled)
				return ans(query, origTruth, origAns.getValues());
			final Query doubQuery = query.getDouble().apply(origAns.getValues());
			if ((trueAnswers || inconsistentAnswers) && origTruth == TruthValue.TRUE) {
				final boolean hasDoubAns = deductiveDatabaseManager.hasAnswers(doubQuery);
				if (trueAnswers && hasDoubAns)
					return ans(query, TruthValue.TRUE, origAns.getValues());
				else if (inconsistentAnswers && !hasDoubAns)
					return ans(query, TruthValue.INCONSISTENT, origAns.getValues());
			}
			if ((trueAnswers && isDoubled || undefinedAnswers) && origTruth == TruthValue.UNDEFINED) {
				final Answer doubAns = deductiveDatabaseManager.answer(doubQuery);
				if (doubAns != null) {
					final TruthValue doubTruth = doubAns.getValuation();
					if (trueAnswers && doubTruth == TruthValue.TRUE)
						return ans(query, TruthValue.TRUE, origAns.getValues());
					else if (undefinedAnswers && doubTruth == TruthValue.UNDEFINED)
						return ans(query, TruthValue.UNDEFINED, origAns.getValues());
				}
			}
		}
		return null;
	}

	/**
	 * Obtains the final {@link TruthValue truth value} given an original and a doubled truth value.
	 *
	 * @param originalTruth
	 *            the original truth value, i.e. the valuation of an answer to an original query.
	 * @param doubledTruth
	 *            the double truth value, i.e. the valuation of an answer to a double query.
	 * @return the final answer's valuation given {@code originalTruth} and {@code doubleTruth}.
	 */
	private TruthValue process(TruthValue originalTruth, TruthValue doubledTruth) {
		if (originalTruth == TruthValue.FALSE)
			return TruthValue.FALSE;
		else if (originalTruth == TruthValue.UNDEFINED)
			return doubledTruth;
		else if (originalTruth == TruthValue.TRUE)
			if (doubledTruth == TruthValue.FALSE)
				return TruthValue.INCONSISTENT;
			else
				return TruthValue.TRUE;
		else
			return null;
	}

}

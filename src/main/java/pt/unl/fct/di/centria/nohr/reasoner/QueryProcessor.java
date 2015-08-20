package pt.unl.fct.di.centria.nohr.reasoner;

import static pt.unl.fct.di.centria.nohr.model.Model.ans;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabaseManager;

public class QueryProcessor {

	protected DedutiveDatabaseManager xsbDatabase;

	public QueryProcessor(DedutiveDatabaseManager xsbDatabase) {
		this.xsbDatabase = xsbDatabase;
	}

	public boolean hasAnswer(Query query, boolean hasDoubled) throws IOException {
		return hasAnswer(query, hasDoubled, true, true, hasDoubled);
	}

	public boolean hasAnswer(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (inconsistentAnswers && hasDoubled == false)
			throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
		if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
			throw new IllegalArgumentException("must have at least one truth value enabled");
		final Query origQuery = query.getOriginal();
		// true original answers
		if (inconsistentAnswers || trueAnswers)
			for (final Answer origAns : xsbDatabase.answers(origQuery, true)) {
				if (trueAnswers && !hasDoubled)
					return true;
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				final boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
				if (inconsistentAnswers && !hasDoubAns)
					return true;
				if (trueAnswers && hasDoubAns)
					return true;
			}
		// undefined original answers
		if (trueAnswers || undefinedAnswers)
			for (final Answer origAns : xsbDatabase.answers(origQuery, false)) {
				if (!hasDoubled && undefinedAnswers)
					return true;
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				if (trueAnswers && hasDoubled && xsbDatabase.hasAnswers(doubQuery, true))
					return true;
				if (undefinedAnswers && xsbDatabase.hasAnswers(doubQuery, false))
					return true;
			}
		return false;
	}

	private boolean isRequiredTruth(TruthValue truth, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) {
		switch (truth) {
		case TRUE:
			return trueAnswers;
		case UNDEFINED:
			return undefinedAnswers;
		case INCONSISTENT:
			return inconsistentAnswers;
		default:
			return false;
		}
	}

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

	public Answer query(Query query) throws IOException {
		return query(query, true, true, true, true);
	}

	public Answer query(Query query, boolean hasDoubled) throws IOException {
		return query(query, hasDoubled, true, true, hasDoubled);
	}

	public Answer query(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (inconsistentAnswers && hasDoubled == false)
			throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
		if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
			throw new IllegalArgumentException("must have at least one truth value enabled");
		final Query origQuery = query.getOriginal();
		// undefined original answers
		if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
			for (final Answer origAns : xsbDatabase.answers(origQuery, false)) {
				if (!hasDoubled)
					return ans(query, TruthValue.UNDEFINED, origAns.getValues());
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				if (xsbDatabase.hasAnswers(doubQuery, false))
					return ans(query, TruthValue.UNDEFINED, origAns.getValues());
			}
		// true original answers
		if (!undefinedAnswers)
			for (final Answer origAns : xsbDatabase.answers(origQuery, true)) {
				if (trueAnswers && !hasDoubled)
					return ans(query, TruthValue.TRUE, origAns.getValues());
				final Query doubQuery = query.getDouble().apply(origAns.getValues());
				final boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
				if (trueAnswers && hasDoubAns)
					return ans(query, TruthValue.TRUE, origAns.getValues());
				else if (inconsistentAnswers && !hasDoubAns)
					return ans(query, TruthValue.INCONSISTENT, origAns.getValues());
			}

		// all original answers
		for (final Answer origAns : xsbDatabase.answers(origQuery, null)) {
			final TruthValue origTruth = origAns.getValuation();
			if ((trueAnswers && origTruth == TruthValue.TRUE || undefinedAnswers && origTruth == TruthValue.UNDEFINED)
					&& !hasDoubled)
				return ans(query, origTruth, origAns.getValues());
			final Query doubQuery = query.getDouble().apply(origAns.getValues());
			if ((trueAnswers || inconsistentAnswers) && origTruth == TruthValue.TRUE) {
				final boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
				if (trueAnswers && hasDoubAns)
					return ans(query, TruthValue.TRUE, origAns.getValues());
				else if (inconsistentAnswers && !hasDoubAns)
					return ans(query, TruthValue.INCONSISTENT, origAns.getValues());
			}
			if ((trueAnswers && hasDoubled || undefinedAnswers) && origTruth == TruthValue.UNDEFINED) {
				final Answer doubAns = xsbDatabase.answer(doubQuery);
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

	public List<Answer> queryAll(Query query) throws IOException {
		return queryAll(query, true);
	}

	public List<Answer> queryAll(Query query, boolean hasDoubled) throws IOException {
		return queryAll(query, hasDoubled, true, true, hasDoubled);
	}

	public List<Answer> queryAll(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
			boolean inconsistentAnswers) throws IOException {
		if (inconsistentAnswers && hasDoubled == false)
			throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
		if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
			throw new IllegalArgumentException("must have at least one truth value enabled");
		final List<Answer> result = new LinkedList<Answer>();
		Map<List<Term>, TruthValue> origAnss;
		if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
			origAnss = xsbDatabase.answersValuations(query.getOriginal(), false);
		else if (inconsistentAnswers && !trueAnswers && !undefinedAnswers)
			origAnss = xsbDatabase.answersValuations(query.getOriginal(), true);
		else
			origAnss = xsbDatabase.answersValuations(query.getOriginal());
		Map<List<Term>, TruthValue> doubAnss = new HashMap<List<Term>, TruthValue>();
		if (hasDoubled)
			if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
				doubAnss = xsbDatabase.answersValuations(query.getDouble(), false);
			else
				doubAnss = xsbDatabase.answersValuations(query.getDouble());
		for (final Entry<List<Term>, TruthValue> origEntry : origAnss.entrySet()) {
			final List<Term> vals = origEntry.getKey();
			final TruthValue origTruth = origEntry.getValue();
			TruthValue truth;
			if (hasDoubled) {
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

}

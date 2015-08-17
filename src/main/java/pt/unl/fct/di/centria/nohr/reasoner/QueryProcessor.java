package pt.unl.fct.di.centria.nohr.reasoner;

import static pt.unl.fct.di.centria.nohr.model.Model.ans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;

public class QueryProcessor {

    protected XSBDatabase xsbDatabase;

    public QueryProcessor(XSBDatabase xsbDatabase) {
	this.xsbDatabase = xsbDatabase;
    }

    public boolean hasAnswer(Query query, boolean hasDoubled) {
	return hasAnswer(query, hasDoubled, true, true, hasDoubled);
    }

    public boolean hasAnswer(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
	    boolean inconsistentAnswers) {
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException("must have at least one truth value enabled");
	final Query origQuery = query.getOriginal();
	// true original answers
	if (inconsistentAnswers || trueAnswers)
	    for (final Answer origAns : xsbDatabase.lazilyQuery(origQuery, true)) {
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
	    for (final Answer origAns : xsbDatabase.lazilyQuery(origQuery, false)) {
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

    public Iterable<Answer> lazilyQuery(final Query query, final boolean hasDoubled, final boolean trueAnswers,
	    final boolean undefinedAnswers, final boolean inconsistentAnswers) {
	xsbDatabase.cancelLastIterator();
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException("must have at least one truth value enabled");
	final Iterable<Answer> origAnss;
	if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
	    origAnss = xsbDatabase.lazilyQuery(query.getOriginal(), false);
	else if (inconsistentAnswers && !trueAnswers && !undefinedAnswers)
	    origAnss = xsbDatabase.lazilyQuery(query.getOriginal(), true);
	else
	    origAnss = xsbDatabase.lazilyQuery(query.getOriginal());
	return new Iterable<Answer>() {

	    @Override
	    public Iterator<Answer> iterator() {

		final Iterator<Answer> origAnssIt = origAnss.iterator();

		return new Iterator<Answer>() {

		    private Answer next;

		    @Override
		    public boolean hasNext() {
			if (next == null)
			    searchNext();
			return next != null;
		    }

		    @Override
		    public Answer next() {
			Answer result = next;
			if (result == null)
			    result = nextAnswer();
			if (result == null)
			    throw new NoSuchElementException();
			next = null;
			return result;
		    }

		    private Answer nextAnswer() {
			while (origAnssIt.hasNext()) {
			    final Answer origAns = origAnssIt.next();
			    final Query doubQuery = query.getDouble().apply(origAns.getValues());
			    final TruthValue origTruth = origAns.getValuation();
			    if (!hasDoubled
				    && isRequiredTruth(origTruth, trueAnswers, undefinedAnswers, inconsistentAnswers))
				return origAns;
			    if (origTruth == TruthValue.TRUE) {
				final boolean hasDoubAns = xsbDatabase.hasAnswers(doubQuery);
				if (trueAnswers && hasDoubAns)
				    return ans(query, TruthValue.TRUE, origAns.getValues());
				else if (inconsistentAnswers && !hasDoubAns)
				    return ans(query, TruthValue.INCONSISTENT, origAns.getValues());
			    } else if (origTruth == TruthValue.UNDEFINED) {
				if (undefinedAnswers)
				    if (xsbDatabase.hasAnswers(doubQuery, false))
					return ans(query, TruthValue.UNDEFINED, origAns.getValues());
				if (trueAnswers)
				    if (xsbDatabase.hasAnswers(doubQuery, true))
					return ans(query, TruthValue.TRUE, origAns.getValues());
			    }
			}
			return null;
		    }

		    @Override
		    public void remove() {
			throw new UnsupportedOperationException();

		    }

		    private void searchNext() {
			next = nextAnswer();
		    }

		};

	    }
	};
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

    public Answer query(Query query) {
	return query(query, true, true, true, true);
    }

    public Answer query(Query query, boolean hasDoubled) {
	return query(query, hasDoubled, true, true, hasDoubled);
    }

    public Answer query(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
	    boolean inconsistentAnswers) {
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException("must have at least one truth value enabled");
	final Query origQuery = query.getOriginal();
	// undefined original answers
	if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
	    for (final Answer origAns : xsbDatabase.lazilyQuery(origQuery, false)) {
		if (!hasDoubled)
		    return ans(query, TruthValue.UNDEFINED, origAns.getValues());
		final Query doubQuery = query.getDouble().apply(origAns.getValues());
		if (xsbDatabase.hasAnswers(doubQuery, false))
		    return ans(query, TruthValue.UNDEFINED, origAns.getValues());
	    }
	// true original answers
	if (!undefinedAnswers)
	    for (final Answer origAns : xsbDatabase.lazilyQuery(origQuery, true)) {
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
	for (final Answer origAns : xsbDatabase.lazilyQuery(origQuery, null)) {
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
		final Answer doubAns = xsbDatabase.query(doubQuery);
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

    public List<Answer> queryAll(Query query) {
	return queryAll(query, true);
    }

    public List<Answer> queryAll(Query query, boolean hasDoubled) {
	return queryAll(query, hasDoubled, true, true, hasDoubled);
    }

    public List<Answer> queryAll(Query query, boolean hasDoubled, boolean trueAnswers, boolean undefinedAnswers,
	    boolean inconsistentAnswers) {
	if (inconsistentAnswers && hasDoubled == false)
	    throw new IllegalArgumentException("can't be inconsistent if there is no doubled rules");
	if (!trueAnswers && !undefinedAnswers && !inconsistentAnswers)
	    throw new IllegalArgumentException("must have at least one truth value enabled");
	final List<Answer> result = new LinkedList<Answer>();
	Map<List<Term>, TruthValue> origAnss;
	if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
	    origAnss = xsbDatabase.queryAll(query.getOriginal(), false);
	else if (inconsistentAnswers && !trueAnswers && !undefinedAnswers)
	    origAnss = xsbDatabase.queryAll(query.getOriginal(), true);
	else
	    origAnss = xsbDatabase.queryAll(query.getOriginal());
	Map<List<Term>, TruthValue> doubAnss = new HashMap<List<Term>, TruthValue>();
	if (hasDoubled)
	    if (undefinedAnswers && !trueAnswers && !inconsistentAnswers)
		doubAnss = xsbDatabase.queryAll(query.getDouble(), false);
	    else
		doubAnss = xsbDatabase.queryAll(query.getDouble());
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

package benchmark;

import java.util.Collection;

import benchmark.ubt.api.QueryResult;
import pt.unl.fct.di.novalincs.nohr.model.Answer;

public class NoHRQueryResult implements QueryResult {

	Collection<Answer> result;
	int c;

	public NoHRQueryResult(Collection<Answer> result2) {
		result = result2;
		c = 0;
	}

	@Override
	public long getNum() {
		return result.size();
	}

	@Override
	public boolean next() {
		return c++ < result.size();
	}

}

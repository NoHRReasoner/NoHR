import java.util.Collection;

import pt.unl.fct.di.centria.nohr.model.Answer;
import ubt.api.QueryResult;

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

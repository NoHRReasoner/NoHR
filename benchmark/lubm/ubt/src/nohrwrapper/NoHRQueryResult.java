package nohrwrapper;

import java.util.ArrayList;

import edu.lehigh.swat.bench.ubt.api.QueryResult;

public class NoHRQueryResult implements QueryResult {
	
	private int size;
	private int pos;

	public NoHRQueryResult(ArrayList<ArrayList<String>> resultList) {
		this.size = resultList.size();
		this.pos = 0;
	}

	@Override
	public long getNum() {
		return pos;
	}

	@Override
	public boolean next() {
		return pos++ < size;
	}

}

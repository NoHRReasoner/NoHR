package benchmark;

/*
 * #%L
 * nohr-benchmark
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

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

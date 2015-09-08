/**
 * 
 */
package benchmark.data;

/*
 * #%L
 * nohr-benchmark
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.util.Random;

class NormalDiscreteVariable implements DiscreteRandomVariable {

	private final double mean;
	private final double stdDeviation;
	private final Random random;

	public NormalDiscreteVariable(long seed, double mean, double stdDeviation) {
		random = new Random(seed);
		this.mean = mean;
		this.stdDeviation = stdDeviation;
	}

	@Override
	public int next() {
		return (int) Math.round(random.nextGaussian() * stdDeviation + mean);
	}

}
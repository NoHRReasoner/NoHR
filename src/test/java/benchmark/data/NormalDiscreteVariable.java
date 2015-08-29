/**
 * 
 */
package benchmark.data;

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
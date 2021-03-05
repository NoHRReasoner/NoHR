package pt.unl.fct.di.novalincs.runtimeslogger;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class RuntimesTable {

	private final List<String> datasets;

	private final String name;

	private final List<String> phases;

	private int runs;

	// phase, run, data set, time
	private final Map<String, Map<Integer, Map<String, Long>>> table;

	RuntimesTable(String name) {
		this.name = name;
		datasets = new LinkedList<String>();
		phases = new LinkedList<String>();
		table = new HashMap<String, Map<Integer, Map<String, Long>>>();
		runs = 1;
	}

	private long average(String phase, String dataset) {
		final Map<Integer, Map<String, Long>> phaseMap = table.get(phase);
		if (phaseMap == null)
			return -1;
		long ac = 0;
		for (int run = 1; run <= runs; run++) {
			final long time = get(phase, run, dataset);
			if (time == -1)
				return -1;
			ac += time;
		}
		return ac / runs;
	}

	long get(String phase, int run, String dataset) {
		final Map<Integer, Map<String, Long>> phaseMap = table.get(phase);
		if (phaseMap == null)
			return -1;
		final Map<String, Long> runMap = phaseMap.get(run);
		if (runMap == null)
			return -1;
		final Long time = runMap.get(dataset);
		if (time == null)
			return -1;
		return time;
	}

	void put(String phase, int run, String dataset, Long time) {
		Map<Integer, Map<String, Long>> phaseMap = table.get(phase);
		if (phaseMap == null) {
			phaseMap = new HashMap<Integer, Map<String, Long>>();
			table.put(phase, phaseMap);
			phases.add(phase);
		}
		Map<String, Long> runMap = phaseMap.get(run);
		if (runMap == null) {
			runMap = new HashMap<String, Long>();
			phaseMap.put(run, runMap);
		}
		if (run > runs)
			runs = run;
		if (!datasets.contains(dataset))
			datasets.add(dataset);
		runMap.put(dataset, time);
	}

	void save() {
		final Charset charset = Charset.forName("US-ASCII");
		final Path file = FileSystems.getDefault().getPath(name + ".csv");
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(file, charset);
			if (runs > 1)
				writer.write(",");
			for (final String dataset : datasets) {
				writer.write(",");
				writer.write(dataset);
			}
			writer.newLine();
			for (final String phase : phases) {
				for (int run = 1; run <= runs; run++) {
					writer.write(phase);
					if (runs > 1)
						writer.write("," + run);
					for (final String dataset : datasets) {
						final long time = get(phase, run, dataset);
						writer.write(",");
						writer.write(time == -1 ? "-" : String.valueOf(time));
					}
					writer.newLine();
				}
				if (runs > 1) {
					writer.write(phase);
					writer.write(",");
					writer.write("average");
					for (final String dataset : datasets) {
						final long average = average(phase, dataset);
						writer.write(",");
						writer.write(average == -1 ? "-" : String.valueOf(average));
					}
					writer.newLine();
				}
			}
		} catch (final IOException x) {
			System.err.format("IOException: %s%n", x);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (final IOException e) {
				System.err.format("IOException: %s%n", e);
			}
		}
	}
}
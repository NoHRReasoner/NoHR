package utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Tracer {

	private static String dataset;

	private static Map<String, Long> durations;
	private static int level;

	private static final String MARGIN = "\t\t";

	private static final boolean ON = true;

	private static int run;

	private static Map<String, Long> starts;

	private static Map<String, RunTimesTable> tables;

	static {
		level = 0;
		starts = new HashMap<String, Long>();
		durations = new HashMap<String, Long>();
		tables = new HashMap<String, RunTimesTable>();
		run = 1;
	}

	public static void close() {
		if (!ON)
			return;
		for (RunTimesTable table : tables.values())
			table.save();
	}

	public static void end(String phase, String table) {
		Long duration = durations.get(phase);
		if (duration == null)
			return;
		System.out.println(margin() + (duration / 1000.0) + " s");
		level--;
		if(!tables.isEmpty())
		tables.get(table).put(phase, run, dataset, duration);
		durations.remove(phase);
	}

	public static void info(String msg) {
		System.out.println(margin() + msg);
	}

	private static String margin() {
		String result = MARGIN;
		for (int l = 0; l < level; l++)
			result += "\t";
		return result;
	}

	public static void open(String... fileNames) {
		if (!ON)
			return;
		for (String fileName : fileNames)
			tables.put(fileName, new RunTimesTable(fileName));

	}

	public static void pause(String phase) {
		if (!ON)
			return;
		Long duration = System.currentTimeMillis() - starts.get(phase);
		Long comulativeDuration = durations.get(phase);
		if (comulativeDuration == null)
			durations.put(phase, duration);
		else
			durations.put(phase, comulativeDuration + duration);
	}

	public static void setDataset(String name) {
		System.out.println("\tDataset: " + name);
		dataset = name;
	}

	public static void setRun(int i) {
		System.out.println("Run: " + i);
		run = i;
	}

	public static void start(String phase) {
		if (!ON)
			return;
		starts.put(phase, System.currentTimeMillis());
		if (!durations.containsKey(phase)) {
			level++;
			Calendar cal = Calendar.getInstance();
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = Calendar.getInstance().get(Calendar.MINUTE);
			int second = Calendar.getInstance().get(Calendar.SECOND);
			String line = margin();
			if (level == 1)
				line += run + " " + dataset + " ";
			line += phase + " " + hour + ":" + minute + ":" + second;
			System.out.println(line);
		}
	}
	
	public static void interrupt(String phase, String table) {
		if (!ON)
			return;
		System.out.println(margin() + "interrupted!");
		level--;
		if(!tables.isEmpty())
			tables.get(table).put(phase, run, dataset, null);
	}

	public static void stop(String phase, String table) {
		if (!ON)
			return;
		Long duration = System.currentTimeMillis() - starts.get(phase);
		System.out.println(margin() + (duration / 1000.0) + " s");
		level--;
		if (!tables.isEmpty())
			tables.get(table).put(phase, run, dataset, duration);
	}

	public static void err(String string) {
		info(string);
	}

	public static void logBool(String message, boolean bool) {
		String ansStr = bool ? "yes" : "no";
		info(message + ": " + ansStr);
	}

}

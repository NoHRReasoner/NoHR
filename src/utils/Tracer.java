package utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Tracer {

    private static String dataset;

    private static final Map<String, Long> durations;

    private static int level;

    private static final String MARGIN = "\t\t";

    private static final boolean ON = true;

    private static final Map<String, String> phaseIterations;

    private static int run;

    private static final Map<String, Long> starts;

    private static final Map<String, RunTimesTable> tables;

    static {
	level = 0;
	starts = new HashMap<String, Long>();
	durations = new HashMap<String, Long>();
	tables = new HashMap<String, RunTimesTable>();
	phaseIterations = new HashMap<String, String>();
	run = 1;
    }

    public static void close() {
	if (!ON)
	    return;
	for (final RunTimesTable table : tables.values())
	    table.save();
    }

    public static void end(String phase, String table) {
	final Long duration = durations.get(phase);
	if (duration == null)
	    return;
	System.out.println(margin() + duration / 1000.0 + " s");
	level--;
	if (!tables.isEmpty())
	    tables.get(table).put(phase, run, dataset, duration);
	durations.remove(phase);
    }

    public static void err(String string) {
	info(string);
    }

    public static void info(String msg) {
	System.out.println(margin() + msg);
    }

    public static void interrupt(String phase, String table) {
	if (!ON)
	    return;
	System.out.println(margin() + "interrupted!");
	level--;
	if (!tables.isEmpty())
	    tables.get(table).put(phase, run, dataset, null);
    }

    public static void logBool(String message, boolean bool) {
	final String ansStr = bool ? "yes" : "no";
	info(message + ": " + ansStr);
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
	for (final String fileName : fileNames)
	    tables.put(fileName, new RunTimesTable(fileName));

    }

    public static void pause(String phase) {
	if (!ON)
	    return;
	final Long duration = System.currentTimeMillis() - starts.get(phase);
	final Long comulativeDuration = durations.get(phase);
	if (comulativeDuration == null)
	    durations.put(phase, duration);
	else
	    durations.put(phase, comulativeDuration + duration);
    }

    public static void setDataset(String name) {
	System.out.println("\tDataset: " + name);
	dataset = name;
    }

    public static void setIteration(String phase, String iteration) {
	phaseIterations.put(phase, iteration);
    }

    public static void setRun(int i) {
	System.out.println("Run: " + i);
	run = i;
    }

    public static void start(String phase) {
	if (!ON)
	    return;
	final String id = phaseIterations.get(phase);
	if (id != null)
	    phase += id;
	starts.put(phase, System.currentTimeMillis());
	if (!durations.containsKey(phase)) {
	    level++;
	    final Calendar cal = Calendar.getInstance();
	    final int hour = cal.get(Calendar.HOUR_OF_DAY);
	    final int minute = Calendar.getInstance().get(Calendar.MINUTE);
	    final int second = Calendar.getInstance().get(Calendar.SECOND);
	    String line = margin();
	    if (level == 1)
		line += run + " " + dataset + " ";
	    line += phase + " " + hour + ":" + minute + ":" + second;
	    System.out.println(line);
	}
    }

    public static void stop(String phase, String table) {
	if (!ON)
	    return;
	final String id = phaseIterations.get(phase);
	if (id != null)
	    phase += id;
	final Long duration = System.currentTimeMillis() - starts.get(phase);
	System.out.println(margin() + duration / 1000.0 + " s");
	level--;
	if (!tables.isEmpty())
	    tables.get(table).put(phase, run, dataset, duration);
    }

}

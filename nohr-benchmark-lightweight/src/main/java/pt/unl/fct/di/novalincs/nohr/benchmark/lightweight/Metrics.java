package pt.unl.fct.di.novalincs.nohr.benchmark.lightweight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metrics {

    private final Map<String, List<Long>> metrics;
    private final int measureCount;

    public int getMeasureCount() {
        return measureCount;
    }
    
    public Metrics(int measureCount) {
        metrics = new HashMap<>();
        this.measureCount = measureCount;
    }

    public void add(String query) {
        metrics.put(query, new ArrayList<Long>(measureCount));
    }

    public void add(String query, long value) {
        metrics.get(query).add(value);
    }

    @Override
    public String toString() {
        String result = "";

        for (String i : metrics.keySet()) {
            result += i;

            for (Long j : metrics.get(i)) {
                result += "," + j.toString();
            }

            result += "\n";
        }

        return result;
    }
}

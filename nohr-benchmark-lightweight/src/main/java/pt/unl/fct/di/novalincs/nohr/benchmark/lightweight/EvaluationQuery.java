package pt.unl.fct.di.novalincs.nohr.benchmark.lightweight;

public class EvaluationQuery {

    private String name;
    private String query;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public EvaluationQuery(String q) {
        name = q.substring(0, q.indexOf(" "));
        query = q.substring(q.indexOf(" ") + 1);
    }
}

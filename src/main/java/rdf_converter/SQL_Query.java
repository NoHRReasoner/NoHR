package rdf_converter;

public enum SQL_Query {

    COMMA(", "),
    CREATE_TABLE("CREATE TABLE "),
    DROP_TABLE("DROP TABLE "),
    LEFTPARENTHESES("( "),
    RIGHTPARENTHESES(")"),
    CONNECTON_URL("jdbc:mysql://"),
    CONNECTION_URL_NECESSITY("?useTimezone=true&serverTimezone=GMT"),
    VARCHAR(" VARCHAR(1000)"),
    SELECT("Select "),
    WHERE("Where {"),
    RIGHTBRACKETS("}");


    private final String label;

    SQL_Query(String label) {
        this.label = label;
    }

    public static String getLabel(SQL_Query l) {
        return l.label;
    }

}

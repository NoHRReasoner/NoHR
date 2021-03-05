package pt.unl.fct.di.novalincs.nohr.plugin.rdf_converter;

import java.util.HashMap;
import java.util.Map;

enum Commands {
    CONNECTIONURL("jdbc:mysql://"),
    CONNECTIONURLNECESSITY("?useTimezone=true&serverTimezone=GMT&rewriteBatchedStatements=true "),
    INPUTCOMMAND("--input.file "),
    AND("&& "),
    MAVEN("mvn exec:java -Dexec.args=\"convert "),
    START("cmd.exe /C cd "),
    DBCOMMAND("--output.target DB "),
    DBURLCOMMAND("--db.url "),
    DBUSER("--db.user "),
    DBPASSWORD("--db.password ");

    final String label;

    private static final Map<String,Commands> BY_LABEL = new HashMap<>();

    static {
        for (Commands c: values()) {
            BY_LABEL.put(c.label,c);
        }
    }

    Commands(String Label) {
        this.label = Label;
    }

    public static Commands valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }


}




/*
*         String username = "user ";
        String password = "Brunocastelo1995* \"";
        String connectionName = "jdbc:mysql://127.0.0.1:3306/firstdatabase";
        String connectionNameNecessity = "?useTimezone=true&serverTimezone=GMT ";
        String inputFile = "C:\\Users\\Bruno\\Desktop\\card.rdf ";
        String directory = "C:\\Users\\Bruno\\Desktop\\rdf2x-master\\rdf2x-master ";
        String and = "&& ";
        String maven = "mvn exec:java -Dexec.args=\"convert ";
        String inputCommand = "--input.file ";
        String start = "cmd.exe /C cd ";
        String dbcommand = "--output.target DB ";
        String dburlcommand = "--db.url ";
        String dbuser = "--db.user ";
        String dbpassword = "--db.password ";

        String fullcmdLine  = start + directory + and + maven + inputCommand
                + inputFile + dbcommand + dburlcommand +
                connectionName + connectionNameNecessity + dbuser + username + dbpassword + password;
*
*
*  */
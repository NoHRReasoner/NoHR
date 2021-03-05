package rdf_converter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SQLTutorials {


    public static void dropDatabase(String dbName, ODBC database, String tableName){
        Connection connect = null;
        Statement statement = null;
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB

            System.out.println("Connecting ...");
            String connectionName = SQL_Query.getLabel(SQL_Query.CONNECTON_URL) + database.getHost() + ":" + database.getPort() + "/" + dbName + SQL_Query.getLabel(SQL_Query.CONNECTION_URL_NECESSITY);
            connect = DriverManager.getConnection(connectionName, database.getUsername(), database.getPassword());
            System.out.println("Connection successful ...");

            System.out.println("Dropping table ....");
            statement = connect.createStatement();

            String dropQuery = SQL_Query.getLabel(SQL_Query.DROP_TABLE) + tableName;

            System.out.println("Final query is: \n" + dropQuery);

            statement.executeUpdate(dropQuery);

            System.out.println("Table dropped");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();

                if (connect != null)
                    connect.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    public static void createDatabase(String dbName, ODBC database, String tableName, Map<String, List<String>> rdfAnswers) {
        Connection connect = null;
        Statement statement = null;
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB

            System.out.println("Connecting ...");
            String connectionName = SQL_Query.getLabel(SQL_Query.CONNECTON_URL) + database.getHost() + ":" + database.getPort() + "/" + dbName + SQL_Query.getLabel(SQL_Query.CONNECTION_URL_NECESSITY);
            connect = DriverManager.getConnection(connectionName, database.getUsername(), database.getPassword());
            System.out.println("Connection successful ...");


            System.out.println("Creating table ....");
            statement = connect.createStatement();

            String createSQLQuery = SQL_Query.getLabel(SQL_Query.CREATE_TABLE) + tableName + SQL_Query.getLabel(SQL_Query.LEFTPARENTHESES);

            Iterator<String> keys = rdfAnswers.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String line = key + SQL_Query.getLabel(SQL_Query.VARCHAR);
                if (keys.hasNext())
                    line += SQL_Query.getLabel(SQL_Query.COMMA);
                createSQLQuery += line;

            }

            createSQLQuery += SQL_Query.getLabel(SQL_Query.RIGHTPARENTHESES);

            System.out.println("Final query is: \n" + createSQLQuery);

            statement.executeUpdate(createSQLQuery);
            System.out.println("Table created in database");


            String InsertLine = "insert into ";
            String values = "values";

            String line = SQL_Query.getLabel(SQL_Query.LEFTPARENTHESES);

            int number = rdfAnswers.keySet().size();
            for (int i = 0; i < number; i++)
                if (i == number - 1)
                    line += "?" + SQL_Query.getLabel(SQL_Query.RIGHTPARENTHESES);
                else
                    line += "?" + SQL_Query.getLabel(SQL_Query.COMMA);


            String updateQuery = InsertLine + tableName + " " + values + line;
            System.out.println(updateQuery);

            int counter = 0;
            for (String key : rdfAnswers.keySet()) {
                counter += rdfAnswers.get(key).size();
            }

            List<String> keyIndex = new ArrayList<>(number);

            keyIndex.addAll(rdfAnswers.keySet());

            for (int a = 0; a < (counter / number); a++) {

                System.out.println("Inserting values....");
                PreparedStatement preparedStatement = connect.prepareStatement(updateQuery);


                for (int i = 0; i < number; i++) {
                    preparedStatement.setString(i + 1, rdfAnswers.get(keyIndex.get(i)).remove(0));
                }

                System.out.println("Values added....");
                preparedStatement.executeUpdate();

                preparedStatement.close();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();

                if (connect != null)
                    connect.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

}

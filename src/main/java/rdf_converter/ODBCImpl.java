package rdf_converter;

public class ODBCImpl implements ODBC {

    static final long serialVersionUID = 0L;


    private int port;
    private String databaseName;
    private String host;
    private String username;
    private String password;

    public ODBCImpl(String databaseName, String host, int port, String username, String password) {
        this.databaseName = databaseName;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}

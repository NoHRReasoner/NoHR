package rdf_converter;

import java.io.Serializable;

public interface ODBC extends Serializable {

    String getDatabaseName();

    String getHost();

    int getPort();

    String getUsername();

    String getPassword();
}

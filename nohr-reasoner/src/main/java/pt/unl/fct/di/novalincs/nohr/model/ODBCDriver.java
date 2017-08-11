package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;

/**
 * Defining a ODBC driver. A driver has: String odbcID, String conectionName, 
 * String username, String password, String databaseName, DatabaseType databaseType;
 *
 * @author Vedran Kasalica
 */

public interface ODBCDriver   {

	public String getID();
	
	public String getConnName();
	
	public String getUsername();
	
	public String getPass();

	public String getDBName();

	public DatabaseType getDBType(); 


}

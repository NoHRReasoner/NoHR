package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;

/**
 * Defining a ODBC driver. A driver has: String odbcID, String conectionName, 
 * String username, String password, String databaseName, DatabaseType databaseType;
 *
 * @author Vedran Kasalica
 */

public interface ODBCDriver   {

	public String getOdbcID();
	
	public String getConectionName();
	
	public String getUsername();
	
	public String getPassword();

	public String getDatabaseName();

	public DatabaseType getDatabaseType(); 
	
	public void setConectionName(String string);
	
	public void setUsername(String string);
	
	public void setPassword(String string);

	public void setDatabaseName(String string);
	
	public void setDatabaseType(DatabaseType databaseType);



}

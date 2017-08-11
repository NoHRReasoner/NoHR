package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ODBCDriver}.
 *
 * @author Vedran Kasalica
 */

public class ODBCDriverImpl implements ODBCDriver{

	private String odbcID;
	private String conectionName;
	private String username;
	private String password;
	private String databaseName;
	private DatabaseType databaseType;
	
//	ID is auto-increment
	public ODBCDriverImpl(String conectionName, String username, String password, String databaseName,
			DatabaseType databaseType) {
		super();
		this.odbcID="1";
		this.conectionName = conectionName;
		this.username = username;
		this.password = password;
		this.databaseName = databaseName;
		this.databaseType = databaseType;
	}
	
	public ODBCDriverImpl(String odbcID,String conectionName, String username, String password, String databaseName,
			DatabaseType databaseType) {
		super();
		this.odbcID=odbcID;
		this.conectionName = conectionName;
		this.username = username;
		this.password = password;
		this.databaseName = databaseName;
		this.databaseType = databaseType;
	}
	
	
	@Override
	public String getID() {
		return odbcID;
	}
	
	@Override
	public String getConnName() {
		return conectionName;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPass() {
		return password;
	}

	@Override
	public String getDBName() {
		return databaseName;
	}

	@Override
	public DatabaseType getDBType() {
		return databaseType;
	}


	@Override
	public String toString() {
		return odbcID+"_"+conectionName+"_"+username+"_"+password+"_"+databaseName+"_"+databaseType.toString();
	}
	
	

}

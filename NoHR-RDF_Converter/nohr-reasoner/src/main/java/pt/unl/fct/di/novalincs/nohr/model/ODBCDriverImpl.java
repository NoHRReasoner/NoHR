package pt.unl.fct.di.novalincs.nohr.model;


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
		this.odbcID="ODBCDriverImpl";
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
	


	public String getConectionName() {
		return conectionName;
	}

	public void setConectionName(String conectionName) {
		this.conectionName = conectionName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	public String getOdbcID() {
		return odbcID;
	}

	@Override
	public String toString() {
		return conectionName;
	}
	
	public String getInfo() {
		return odbcID+"_"+conectionName+"_"+username+"_"+password+"_"+databaseName+"_"+databaseType.toString();
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) 
            return false;
        if (!(obj instanceof ODBCDriverImpl || obj instanceof String)) 
            return false;

        if (this.conectionName.matches(obj.toString())) 
            return true;

        return false;
    }
	
	

}

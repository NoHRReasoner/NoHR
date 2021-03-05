package pt.unl.fct.di.novalincs.nohr.model;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

public class DatabaseType {

	private final String dbtype;
	
	public DatabaseType(String type){
		dbtype=type;
	}
	
	
	public String toString() {
		return dbtype;
	}

	/**
	 * 
	 * @return the list of supported database types
	 * 
	 */
	public static List<DatabaseType> getDBTypes(){
		List<DatabaseType> list = new ArrayList<DatabaseType>();
		list.add(new DatabaseType("MySQL"));
		list.add(new DatabaseType("Oracle"));
		return list;
			
		}

	
	/**
	 * 
	 * @param  the ODBC connection that is being used
	 * @return the quotation used in SQL (around table names, database name etc.) for this particular database type
	 * @throws exceptiom in the case of an unsupported DatabaseType
	 */
	public static String getQuotation(ODBCDriver odbc) throws InvalidAttributesException{
		
		switch (odbc.getDatabaseType().toString()) {
		case "MySQL":
			return "`";
		case "Oracle":
			return "\"";
		default:
			throw new InvalidAttributesException("The database type is not supported.");
		}
	}
	
	 @Override
	    public boolean equals(Object obj) {
	        if (obj == null) 
	            return false;
	        if (!(obj instanceof DatabaseType)) 
	            return false;

	        if (this.dbtype.matches(obj.toString())) 
	            return true;

	        return false;
	    }
}

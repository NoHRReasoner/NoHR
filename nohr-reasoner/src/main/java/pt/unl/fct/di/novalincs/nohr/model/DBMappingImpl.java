package pt.unl.fct.di.novalincs.nohr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link DBMapping}.
 *
 * @author Vedran Kasalica
 */
public class DBMappingImpl implements DBMapping {

	
	/** ODBC connection that is beeing used */
	private final ODBCDriver odbcDriver;
	
	/** Tables that are being mapped */
	private final String[][]  table;
	
//	private on 
	
	/** Columns from the table */
	private final List<String> columns;

	/** Predicate that the query is being mapped to */
	private final String predicate;
	
	

	public DBMappingImpl(ODBCDriver driver,String[][] table, List<String> columns, String predicate) {
		super();
		this.odbcDriver=driver;
		this.table = table;
		this.columns = new ArrayList<String>();
		for(String s:columns)
			this.columns.add(s);
		this.predicate = predicate;
	}
	

//	public DBMappingImpl(ODBCDriver driver, String[][] table, String cols, String predicate) {
//		super();
//		this.odbcDriver=driver;
//		this.table = table;
//		this.columns = Arrays.asList(cols.split("\\s*,\\s*"));
//		this.predicate = predicate;
//
//	}

	@Override
	public List<String> getColumns() {
		if (columns == null)
			return Collections.<String>emptyList();
		return columns;
	}

	@Override
	public String getColumnsString() {
		if (columns == null)
			return "";

		String tmpCols = new String("");
		for (String s: columns)
			tmpCols = tmpCols.concat(s + ",");

		if (columns.size() > 0)
			tmpCols = tmpCols.substring(0, tmpCols.length() - 1);

		return tmpCols;
	}

	@Override
	public String[][] getTable() {
		return table;
	}

	@Override
	public String getPredicate() {
		return predicate;
	}
	
	@Override
	public ODBCDriver getODBC() {
		return odbcDriver;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + table.hashCode();
		result = prime * result + (columns == null ? 0 : columns.hashCode());
		result = prime * result + predicate.hashCode();
		return result;
	}

	@Override
	public String toString() {
//		System.out.println("DBMappingImpl.toString() called");
		String tmpCols = new String("");
		for (int i = 0; i < columns.size(); i++) { 
			tmpCols = tmpCols.concat(columns.get(i) + ",");
		}
		if (columns.size() > 0) {
			tmpCols = tmpCols.substring(0, tmpCols.length() - 1);
		}
		return predicate + "  <-  "+getTables()+"("+tmpCols+")";
	}
	
	public String getTables(){
		String temp=new String();
		for(int i=0;i<table.length;i++){
			if(i>0)
				temp+=",";
			temp+=table[i][0];
		}
		return temp;
	}
	
	public String toRule() {
		String tmpVar = new String("");
		String tmpCols = new String("");
		for (int i = 0; i < columns.size(); i++) {
			tmpVar = tmpVar.concat("X" + "" + i + ",");
			tmpCols = tmpCols.concat(columns.get(i) + ",");
		}
		if (columns.size() > 0) {
			tmpVar = tmpVar.substring(0, tmpVar.length() - 1);
			tmpCols = tmpCols.substring(0, tmpCols.length() - 1);
		}
		return predicate + "(" + tmpVar + ") :- odbc_sql([],'SELECT " + tmpCols + " FROM "+table+"', [" + tmpVar
				+ "])";
	}

	@Override
	public String getFileSyntax() {
		String tmp = new String("");
		if(odbcDriver==null)
			return null;
		tmp=tmp.concat(odbcDriver.getConectionName()+"<break>");
		tmp=tmp.concat(table+",");
		for (int i = 0; i < columns.size(); i++) {
			tmp = tmp.concat(columns.get(i) + ",");
		}
		if (columns.size() > 0) {
			tmp = tmp.substring(0, tmp.length() - 1);
		}
		tmp=tmp.concat("<break>");
		tmp=tmp.concat(predicate);
		return tmp;
	}

	@Override
	public DBMapping setDBMapping(String stringFromFile, List<ODBCDriver> drivers) {
		String[] map=stringFromFile.split("<break>");
		if(map==null || map.length!=4)
			return null;
		
		return null;
	}

}

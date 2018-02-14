package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;

import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;

/**
 * Object representing a single database mapping. 
 * It is consistent of a odbc driver, table, columns and a predicate.
 *
 * @author Vedran Kasalica
 */

public interface DBMapping   {

	public List<String[]> getColumns();
	
	public String getSQL();

	public List<DBTable> getTables();

	public Predicate getPredicate(); 
	
	public Predicate getOriginalPredicate();
	
	public Predicate getNPredicate();
	
	public ODBCDriver getODBC(); 
	
	public Integer getArity(); 
	
	public boolean isSQL(); 
	
	public void setAliasNumber(int n);
	
	public Integer getAliasNumber();
	
	public String getFileSyntax();
	


}

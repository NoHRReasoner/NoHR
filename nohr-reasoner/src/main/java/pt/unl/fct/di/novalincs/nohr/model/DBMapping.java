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

	public List<String> getColumns();

	public String getTable();

	public String getPredicate(); 

	String getColumnsString();


}

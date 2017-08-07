package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;

import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;

public interface DBMapping   {

	public List<String> getColumns();

	public String getTable();

	public String getPredicate(); 

	String getColumnsString();


}

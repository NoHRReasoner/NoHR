package pt.unl.fct.di.novalincs.nohr.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreatingMappings {

	private final String apostrophe;
	private final String db;
	private final String ontologyDesc;
	
	
	public CreatingMappings(String apostrophe, String db, String ontologyDesc){
		this.apostrophe=apostrophe;
		this.db=db;
		this.ontologyDesc=ontologyDesc;
	}
	
	
	public <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list;
	}
	
	public void createPredicateMapping(String table,
			String[] columns, boolean [] floats, String predicate, Boolean isDL,
			File file, String ontology, String apostrophe) {

		int arrity = columns.length;
		List<Set<String>> nonvarsSet = new ArrayList<Set<String>>();
		List<Set<String>> varsSet = new ArrayList<Set<String>>();
		varGenerator(arrity, nonvarsSet, varsSet);

		String rules = "";

		try {
			FileWriter out = new FileWriter(file, true);
			for (int i = 0; i < nonvarsSet.size(); i++) {
				String currRule="";
				List<String> nonvars = asSortedList(nonvarsSet.get(i));
				List<String> vars = asSortedList(varsSet.get(i));
				
//				writing the first part of the mapping 
				currRule += "(" + varList(arrity) + ")" + " :- " + vars(vars)
						+ nonvars(nonvars) + "findall_odbc_sql(" + nonvars + ",'SELECT " + columns(columns) 
						+ " FROM " + table(table);
				
//				writing where clause of the sql, if there are some constants
				if(nonvars.size()>0){
					String where="";
//					creating list of columns based on the constants' names
					for(int j=0;j<nonvars.size();j++){
						if(j>0)
							where += " AND ";
						int c= Integer.parseInt(nonvars.get(j).substring(1));
						where += column(columns[c]) + "= ?";
					}
					currRule +=" WHERE " + where;
				}
//				list of mapped values
				currRule +=" ', [" + returnVar(floats) + "])";
				
//				add float columns casting to integer
				currRule += setCast(floats);
				
//				ending of the rule
				String ending=". \n";
//				adding original rule
					rules += predicate(false, isDL, predicate) + currRule + ending;
					
//				adding doubled rule
					rules += predicate(true, isDL, predicate) + currRule + ending;
			}
			out.write(rules);
			out.close();
		} catch (IOException e) {
			System.err.println("Mistake with createDataProperties.");
			e.printStackTrace();
		}
	}
	
	public String setCast(boolean [] floats){
		String cast="";
		for(int i=0;i<floats.length;i++){
			if(floats[i]){
				cast += ", " + getVar(floats.length, i) + " is floor(" + getVar(floats.length, i) + "c)" ;
			}
		}
		
		return cast;
	}
	
	public String returnVar(boolean [] floats){
		String returnVar="";
		for(int i=0;i<floats.length;i++){
			if(floats[i]){
				returnVar += getVar(floats.length, i) + "c" +",";
			}else
				returnVar += getVar(floats.length, i) +",";
		}
				
		return returnVar.substring(0, returnVar.length()-1);
	}

	public String table(String table) {
		return apostrophe + db + apostrophe + "." + apostrophe + table + apostrophe;
	}

	public String column(String column) {
		return apostrophe + column + apostrophe;
	}

	public String columns(String[] column) {
		String cols = "";
		for (int i = 0; i < column.length; i++)
			cols += apostrophe + column[i] + apostrophe + ",";
		return cols.substring(0, cols.length() -1);
	}

	private String vars(List<String> list) {
		String vars = "";
		for (String s : list)
			vars = vars + "var(" + s + "),";

		return vars;
	}

	private String nonvars(List<String> list) {
		String nonvars = "";
		for (String s : list)
			nonvars = nonvars + "nonvar(" + s + "),";

		return nonvars;
	}

//	list of variables base on the arrity of the predicate
	private String varList(int n) {
		String vars = "";
		for (int i = 0; i < n; i++)
			vars += getVar(n,i) + ",";
		return vars.substring(0, vars.length() - 1);
	}

//	defining the predicate name for the mapping
	private String predicate(boolean doubled, boolean isDL, String predicate) {
		String fullPred, type = doubled ? "d" : "a";

		if (isDL)
			fullPred = "'" + type + "<" + ontologyDesc + predicate + ">'";
		else
			fullPred = "'" + type + predicate + "'";

		return fullPred;
	}
	
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}
	
	public static String getVar(int size, int i){
		if(size>9){
			if(i>9)
				return "V"+i;
			else
				return "V0"+i;
		}else
			return "V"+i;
	}

//	function used to generate all combinations of var/nonvar arguments
	public void varGenerator(int n, List<Set<String>> nonvarsSet, List<Set<String>> varsSet) {
		Set<String> mySet = new HashSet<String>();
		
			for (int i = 0; i < n; i++)
					mySet.add(getVar(n,i));
		
		
		for (Set<String> s : powerSet(mySet)) {
			Set<String> r = new HashSet<String>();
			r.addAll(mySet);
			r.removeAll(s);
			nonvarsSet.add(s);
			varsSet.add(r);
		}
	}
}

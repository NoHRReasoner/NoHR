package pt.unl.fct.di.novalincs.nohr.deductivedb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.directory.InvalidAttributesException;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBTable;
import pt.unl.fct.di.novalincs.nohr.model.DatabaseType;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.HybridPredicate;

public class MappingGenerator {

	private final String apostrophe;
	private final String predicate;
	private final String nPredicate;
	private final Predicate nPredicateOriginal;
	private final String db;
	private final String sql;
	private final String odbc;
	private final List<DBTable> table;
	private final int arrity;
	private final String[] colTables;
	private final String[] columns;
	private final boolean[] colFloats;

	public MappingGenerator(DBMapping mapping, FormatVisitor formatVisitor) throws InvalidAttributesException {

		if (mapping.getTables() != null)
			this.table = mapping.getTables();
		else
			this.table = null;
		if (mapping.getColumns() != null) {
			this.colTables = new String[mapping.getColumns().size()];
			this.columns = new String[mapping.getColumns().size()];
			this.colFloats = new boolean[mapping.getColumns().size()];

			for (int i = 0; i < mapping.getColumns().size(); i++) {
				this.colTables[i] = mapping.getColumns().get(i)[1];
				this.columns[i] = mapping.getColumns().get(i)[2];
				this.colFloats[i] = mapping.getColumns().get(i)[3].matches("true");
			}
		} else {
			this.colTables = null;
			this.columns = null;
			this.colFloats = null;

		}
		if (formatVisitor != null) {
			final boolean isDL;

	        if (mapping.getOriginalPredicate() instanceof HybridPredicate) {
	            final HybridPredicate hybridHeadFunctor = (HybridPredicate) mapping.getOriginalPredicate();
	            isDL = hybridHeadFunctor.isConcept() || hybridHeadFunctor.isRole();
	        } else {
	            isDL = false;
	        }
			this.predicate = mapping.getPredicate().accept(formatVisitor);
			if (!isDL || mapping.getNPredicate() == null) {
				this.nPredicate = null;
				this.nPredicateOriginal = null;
			} else {
				this.nPredicate = mapping.getNPredicate().accept(formatVisitor);
				this.nPredicateOriginal = mapping.getNPredicate();
			}
		} else {
			this.predicate = null;
			this.nPredicate = null;
			this.nPredicateOriginal = null;
		}
		this.arrity = mapping.getArity();
		this.sql = mapping.getSQL();
		this.odbc = mapping.getODBC().getConectionName();

		this.db = mapping.getODBC().getDatabaseName();
		this.apostrophe = DatabaseType.getQuotation(mapping.getODBC());

	}
	
	public String getNPredicate() {
		return nPredicate;
	}
	
	public Predicate getNPredicateOriginal() {
		return nPredicateOriginal;
	}

	public List<String> asSortedList(Collection<String> c) {
		List<String> list = new ArrayList<String>(c);
		Collections.sort(list);
		return list;
	}

	public List<String> createMappingRule() {
		if (sql == null)
			return createBasicMapping();
		else
			return createSQLMapping();
	}

	public String createSQL() {
		if(sql!=null && sql.length()!=0)
			return sql;
		return "SELECT " + selectColumns(colTables, columns) + " FROM " + table(table);
	}

	public List<String> createBasicMapping() {
		List<Set<String>> nonvarsSet = new ArrayList<Set<String>>();
		List<Set<String>> varsSet = new ArrayList<Set<String>>();
		varGenerator(arrity, nonvarsSet, varsSet);

		List<String> mappingBody = new ArrayList<String>();

		for (int i = 0; i < nonvarsSet.size(); i++) {
			String currRule = "";
			List<String> nonvars = asSortedList(nonvarsSet.get(i));
			List<String> vars = asSortedList(varsSet.get(i));

			// writing the first part of the mapping
			currRule += predicate + "(" + varList(arrity) + ")" + " :- " + vars(vars) + nonvars(nonvars)
					+ "findall_odbc_sql('"+odbc+"'," + nonvars + ",'SELECT " + selectColumns(colTables, columns) + " FROM "
					+ table(table);
			// writing where clause of the sql, if there are some constants
			if (nonvars.size() > 0) {
				String where = "";
				// creating list of columns based on the constants' names
				for (int j = 0; j < nonvars.size(); j++) {
					if (j > 0)
						where += " AND ";
					int c = Integer.parseInt(nonvars.get(j).substring(1));
					where += selectColumn(colTables[c], columns[c]) + "= ?";
				}
				currRule += " WHERE " + where;
			}
			// list of mapped values
			currRule += " ', [" + returnVar(colFloats) + "])";

			// add float columns casting to integer
			currRule += setCast(colFloats);

			// ending of the rule (adding N predicate in case of doubled
			// predicate)
			String ending = "";
			if (nPredicate != null) {
				ending += ",tnot(" + nPredicate + "(" + varList(arrity) + "))";
			}
			ending += ".";

			// adding original rule
			mappingBody.add(currRule + ending);

		}
		return mappingBody;
	}

	public List<String> createSQLMapping() {
		List<String> mappingBody = new ArrayList<String>();

		String currRule = "";
		// writing the first part of the mapping
		currRule += predicate + "(" + varList(arrity) + ")";
		currRule += " :- " + "findall_odbc_sql('"+odbc+"',[],'" + sql + "', [" + returnVar(new boolean[arrity]) + "])";
		if (nPredicate != null) {
			currRule += ",tnot(" + nPredicate + "(" + varList(arrity) + "))";
		} else {
			currRule += ".";
		}
		// adding the rule
		mappingBody.add(currRule);
		return mappingBody;
	}

	public String setCast(boolean[] floats) {
		String cast = "";
		for (int i = 0; i < floats.length; i++) {
			if (floats[i]) {
				cast += ", " + getVar(floats.length, i) + " is floor(" + getVar(floats.length, i) + "c)";
			}
		}

		return cast;
	}

	public String returnVar(boolean[] floats) {
		String returnVar = "";
		for (int i = 0; i < floats.length; i++) {
			if (floats[i]) {
				returnVar += getVar(floats.length, i) + "c" + ",";
			} else {
				returnVar += getVar(floats.length, i) + ",";
			}
		}

		return returnVar.substring(0, returnVar.length() - 1);
	}

	public String table(List<DBTable> table) {
		String tables = apostrophe + db + apostrophe + "." + apostrophe + table.get(0).getNewTableName() + apostrophe + " " + apostrophe + table.get(0).getNewTableAlias() + apostrophe;
		for (int i = 1; i < table.size(); i++) {
			tables += " JOIN " + apostrophe + db + apostrophe + "." + apostrophe + table.get(i).getNewTableName()
					+ apostrophe + " " + apostrophe + table.get(i).getNewTableAlias() + apostrophe + " ON ";

			for (int j = 0; j < table.get(i).getNewTableCol().size(); j++) {
				tables += apostrophe + table.get(i).getNewTableAlias() + apostrophe + "." + apostrophe
						+ table.get(i).getNewTableCol().get(j) + apostrophe + "=" + apostrophe
						+ table.get(i).getOldTableAlias() + apostrophe + "." + apostrophe
						+ table.get(i).getOldTableCol().get(j) + apostrophe;
				if (j < table.get(i).getNewTableCol().size() - 1)
					tables += " AND ";
			}
		}
		return tables;
	}

	public String selectColumn(String table, String column) {
		return apostrophe + table + apostrophe + "." + apostrophe + column + apostrophe;
	}

	public String selectColumns(String[] colTables, String[] column) {
		String cols = "";
		for (int i = 0; i < column.length; i++)
			cols += selectColumn(colTables[i], column[i]) + ",";
		return cols.substring(0, cols.length() - 1);
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

	// list of variables base on the arrity of the predicate
	private String varList(int n) {
		String vars = "";
		for (int i = 0; i < n; i++)
			vars += getVar(n, i) + ",";
		return vars.substring(0, vars.length() - 1);
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

	public static String getVar(int size, int i) {
		if (size > 9) {
			if (i > 9)
				return "V" + i;
			else
				return "V0" + i;
		} else
			return "V" + i;
	}

	// function used to generate all combinations of var/nonvar arguments
	public void varGenerator(int n, List<Set<String>> nonvarsSet, List<Set<String>> varsSet) {
		Set<String> mySet = new HashSet<String>();

		for (int i = 0; i < n; i++)
			mySet.add(getVar(n, i));

		for (Set<String> s : powerSet(mySet)) {
			Set<String> r = new HashSet<String>();
			r.addAll(mySet);
			r.removeAll(s);
			nonvarsSet.add(s);
			varsSet.add(r);
		}
	}
}

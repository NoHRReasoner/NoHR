package pt.unl.fct.di.novalincs.nohr.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.utils.CreatingMappings;

/**
 * Implementation of {@link DBMapping}.
 *
 * @author Vedran Kasalica
 */
public class DBMappingImpl implements DBMapping {

	/** ODBC connection that is beeing used */
	private final ODBCDriver odbcDriver;

	/** Tables that are being mapped */
	private final List<DBTable> tables;

	/** Variable is used to represent if the mapping is given using SQL query */
	private final boolean isSQL;

	/**
	 * Variable is used to represent the arity of a predicate if using SQL query
	 */
	private final Integer arity;

	/** SQL representation of the mapping */
	private final String sql;

	/**
	 * Columns[4] from the table, [table name with alias, table alias, column
	 * name, float]
	 */
	private final List<String[]> columns;

	/** Predicate that the query is being mapped to */
	private final Predicate predicate;

	/** Predicate that the query is being mapped to */
	private final Predicate originalPredicate;

	/** Classically negated predicate - used only with doubled predicate */
	private final Predicate nPredicate;

	/** Number used to create aliases for new tables */
	private int tableAliasNumber;

	// constructor for GUI - mapping
	public DBMappingImpl(ODBCDriver driver, List<DBTable> tables, List<String[]> columns, Predicate predicate,
			int aliasNumber) {
		super();
		this.odbcDriver = driver;
		this.tables = new ArrayList<DBTable>();
		for (DBTable table : tables)
			this.tables.add(table);
		this.columns = new ArrayList<String[]>();
		for (String[] col : columns)
			this.columns.add(col);
		this.predicate = predicate;
		this.originalPredicate = predicate;
		this.nPredicate = null;
		this.isSQL = false;
		this.arity = columns.size();
		this.sql = null;
		this.tableAliasNumber = aliasNumber;
	}

	/**
	 *  Used to create original or doubled mapping according to the encoder. Used during the reasoning process in {@link pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB NoHRHybridKB}.
	 * @param dbMapping
	 * @param encoder
	 * @param encoderTnot
	 */
	public DBMappingImpl(DBMapping dbMapping, ModelVisitor encoder, ModelVisitor encoderTnot) {
		super();
		this.odbcDriver = dbMapping.getODBC();
		this.tables = new ArrayList<DBTable>();
		for (DBTable table : dbMapping.getTables())
			this.tables.add(table);
		this.columns = new ArrayList<String[]>();
		for (String[] col : dbMapping.getColumns())
			this.columns.add(col);
		this.predicate = dbMapping.getPredicate().accept(encoder);
		this.originalPredicate = dbMapping.getPredicate();
		if (encoderTnot == null) {
			this.nPredicate = null;
		} else {
			this.nPredicate = dbMapping.getPredicate().accept(encoderTnot);
		}
		this.isSQL = dbMapping.isSQL();
		this.arity = dbMapping.getArity();
		this.sql = dbMapping.getSQL();
		this.tableAliasNumber = dbMapping.getAliasNumber();
	}

	private void validate(String[] input, int size, int line) throws IOException {
		/**
		 * <mapping> odbcDriver <mapping> sql <mapping>
		 * <table>
		 * <mapping> <column> <mapping> - <mapping>
		 */
		if (input == null || input.length != size) {
			throw new IOException("Line: " + line + ". The file is corrupted.");
		} else {
			if (!input[0].matches("") || !input[input.length -1].matches("")) {
				throw new IOException("Line: " + line + ". The file is corrupted. Mappings are not defined properly.");
			}
		}

	}

	/**
	 * Creating mapping from a string/file
	 * @param stringFromFile
	 * @param drivers
	 * @param line
	 * @param vocabulary
	 * @throws IOException
	 */
	public DBMappingImpl(String stringFromFile, List<ODBCDriver> drivers, int line, Vocabulary vocabulary) throws IOException {
		String[] mapping = stringFromFile.split("<mapping>",-1);

		validate(mapping, 7, line);

		/**
		 * odbc driver scanning
		 */
		String odbc = mapping[1];
		int odbcIndex = -1;
		
		for(int i =0; i<drivers.size();i++){
			if(drivers.get(i).getConectionName().matches(odbc)){
				odbcIndex = i;
				break;
			}
		}

		if (odbcIndex == -1){
			throw new IOException("Line: " + line + ". ODBC: " + odbc + ", doesn not exist");
		}
		this.odbcDriver = drivers.get(odbcIndex);
		
		String tmpSQL = mapping[2];
		if (tmpSQL == null || tmpSQL.isEmpty()) {
			this.isSQL = false;
			this.sql = null;
		} else {
			this.isSQL = true;
			this.sql = tmpSQL;
		}
		/**
		 * Parsing
		 * <table>
		 * section
		 * 
		 * 
		 * <table>
		 * <tableInfo> newTableName <tableInfo> oldTableName <tableInfo>
		 * newTableAlias <tableInfo> oldTableAlias <tableInfo>
		 * <tableJoinOnPairs> <col> newTableName.col11 <col> newTableName.col12
		 * <col> ... <col> <tableJoinOnPairs> <col> oldTableName.col21 <col>
		 * oldTableName.col22 <col> ... <col> <tableJoinOnPairs> <tableInfo>
		 * <table>
		 * 
		 * ...
		 * 
		 * <table>
		 */
		this.tables = new ArrayList<DBTable>();
		String[] tables = mapping[3].split("<table>", -1);

		if (tables == null) {
			throw new IOException("Line: " + line + ". The file is corrupted. No tables were defined.");
		} else if (!tables[0].matches("") || !tables[tables.length-1].matches("")) {
			throw new IOException("Line: " + line + ". The file is corrupted. Tables are not defined properly.");
		}
		for (int i = 1; i < tables.length -1; i++) {
			String[] tmpTablesJoins = tables[i].split("<tableInfo>",-1);
			if (!tmpTablesJoins[0].matches("") || tmpTablesJoins.length != 7) {
				throw new IOException("Line: " + line + ". The table no. " + i + ". is not well defined.");
			}

			String[] tableJoinOn = tmpTablesJoins[5].split("<tableJoinOnPairs>",-1);
			if (!tableJoinOn[0].matches("") || tableJoinOn.length != 4) {
				throw new IOException("Line: " + line + ". The table no. " + i + ". join is not well defined.");
			}

			List<String> newTableCol = new ArrayList<>();
			List<String> oldTableCol = new ArrayList<>();
			String[] tmpNewCols = tableJoinOn[1].split("<col>",-1);
			String[] tmpOldCols = tableJoinOn[2].split("<col>",-1);
			if (!tmpNewCols[0].matches("") || !tmpOldCols[0].matches("") || tmpNewCols.length != tmpOldCols.length) {
				throw new IOException("Line: " + line + ". The table no. " + i + ". join columns are not well defined.");
			}
			for (int j = 1; j < tmpNewCols.length -1; j++) {
				newTableCol.add(tmpNewCols[j]);
				oldTableCol.add(tmpOldCols[j]);
			}
			DBTable tmptable = new DBTable(tmpTablesJoins[1], tmpTablesJoins[2], tmpTablesJoins[3], tmpTablesJoins[4],
					newTableCol, oldTableCol);
			this.tables.add(tmptable);
		}
		/**
		 * Parsing <column> section
		 * 
		 * 
		 * <column> <colInfo> table name with alias <colInfo> table alias
		 * <colInfo> column Name <colInfo> is Column Float <colInfo> <column>
		 * 
		 * ...
		 * 
		 * <column>
		 */

		this.columns = new ArrayList<String[]>();

		String[] tmpColumns = mapping[4].split("<column>", -1);
		
		if (tmpColumns == null || tmpColumns.length < 2) {
			throw new IOException("Line: " + line + ". The file is corrupted. No columns were defined.");
		} else {
			if (!tmpColumns[0].matches("")) {
				throw new IOException("Line: " + line + ". The file is corrupted. Columns are not defined properly.");
			}
		}
		for (int i = 1; i < tmpColumns.length -1; i++) {
			String[] tmpColInfo = tmpColumns[i].split("<colInfo>", -1);
			if (tmpColInfo.length ==0 || !tmpColInfo[0].matches("") || tmpColInfo.length != 6){
				throw new IOException("Line: " + line + ". The column no. " + i + " is not well defined.");
			}
			String[] colInfo = new String[4];
			for (int j = 0; j < colInfo.length; j++) {
				colInfo[j] = tmpColInfo[j+1];
			}
			this.columns.add(colInfo);
		}

		/**
		 * Parsing <predicate> section <mapping> <predicate> predicate name
		 * <predicate> predicate arity <predicate> <mapping>
		 */
		String[] tmpPredicate = mapping[5].split("<predicate>", -1);

		if (tmpPredicate == null || !tmpPredicate[0].matches("") || tmpPredicate.length != 4) {
			throw new IOException("Line: " + line + ". The predicate is not well defined.");
		}
		try {
			this.arity = Integer.parseUnsignedInt(tmpPredicate[2]);
		} catch (NumberFormatException nfe) {
			throw new IOException("Line: " + line + ". The arrity of the predicate is not well defined.");
		}
		this.tableAliasNumber = this.tables.size() + 1;
		
		this.predicate = getPredicateFromName(tmpPredicate[1], vocabulary);
		this.originalPredicate = predicate;
		this.nPredicate = null;
	}

	/**
	 * Constructor for an arbitrary SQL - mapping
	 * @param odbcDriver
	 * @param sql
	 * @param arity
	 * @param predicate
	 */
	public DBMappingImpl(ODBCDriver odbcDriver, String sql, int arity, Predicate predicate) {
		super();
		this.odbcDriver = odbcDriver;
		this.tables = new ArrayList<DBTable>();
		this.columns = new ArrayList<String[]>();
		;
		this.predicate = predicate;
		this.originalPredicate = predicate;
		this.nPredicate = null;
		this.isSQL = true;
		this.arity = arity;
		this.sql = sql;
		this.tableAliasNumber = 0;
	}
	
	private Predicate getPredicateFromName(String predicateName, Vocabulary vocabulary) {
		final List<Term> kbTerms = new LinkedList<>();
		int size = this.columns.size();
		if (isSQL){
			size = getArity();
		}
		if (size == 0) {
			System.out.println("No columns added");
			// for test
			size = 1;
		}
		for (int i = 0; i < size; i++) {
			Term kbTerm = Model.var(CreatingMappings.getVar(size, i));
			kbTerms.add(kbTerm);
		}

		Rule tmp = Model.rule(Model.atom(vocabulary, predicateName, kbTerms));
		Predicate predicate = tmp.getHead().getFunctor();
		// x.getHead().getFunctor().accept(formatVisitor);

		return predicate;

	}

	@Override
	public List<String[]> getColumns() {
		if (columns == null)
			return Collections.<String[]>emptyList();
		return columns;
	}

	@Override
	public List<DBTable> getTables() {
		return tables;
	}

	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	@Override
	public Predicate getOriginalPredicate() {
		return originalPredicate;
	}

	@Override
	public Predicate getNPredicate() {
		return nPredicate;
	}

	@Override
	public ODBCDriver getODBC() {
		return odbcDriver;
	}

	@Override
	public void setAliasNumber(int n) {
		tableAliasNumber = n;
	}

	@Override
	public Integer getAliasNumber() {
		return tableAliasNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (tables == null ? 0 : tables.hashCode());
		result = prime * result + (columns == null ? 0 : columns.hashCode());
		result = prime * result + (sql == null ? 0 : sql.hashCode());
		result = prime * result + (predicate == null ? 0 : predicate.hashCode());
		return result;
	}

	// TODO redo, group by table
	@Override
	public String toString() {
		if (sql != null) {
			return predicate + " <- " + sql;
		}
		String tmpCols = new String("");
		for (int i = 0; i < columns.size(); i++) {
			tmpCols += columns.get(i)[1];

			if (i < columns.size() - 1) {
				tmpCols += ",";
			}
		}
		return predicate + "  <-  " + getTablesNames() + "(" + tmpCols + ")";
	}

	public String getTablesNames() {
		String temp = new String();
		for (int i = 0; i < tables.size(); i++) {
			if (i > 0)
				temp += ",";
			temp += tables.get(i).getNewTableName();
		}
		return temp;
	}

	@Override
	public String getFileSyntax() {
		/**
		 * <mapping> odbcDriver <mapping> sql <mapping>
		 * <table>
		 * <mapping> <column> <mapping> <predicate> <mapping>
		 */

		/** defining odbcDriver section */

		String tmp = new String("<mapping>");
		if (odbcDriver == null) {
			return null;
		}
		tmp = tmp.concat(odbcDriver.getConectionName() + "<mapping>");

		/** defining sql section */

		if (sql == null) {
			tmp = tmp.concat("<mapping>");
		} else {
			tmp = tmp.concat(sql + "<mapping>");
		}

		/**
		 * defining
		 * <table>
		 * section
		 * 
		 * 
		 * <table>
		 * <tableInfo> newTableName <tableInfo> oldTableName <tableInfo>
		 * newTableAlias <tableInfo> oldTableAlias <tableInfo>
		 * <tableJoinOnPairs> <col> newTableName.col11 <col> newTableName.col12
		 * <col> ... <col> <tableJoinOnPairs> <col> oldTableName.col21 <col>
		 * oldTableName.col22 <col> ... <col> <tableJoinOnPairs> <tableInfo>
		 * <table>
		 * 
		 * ...
		 * 
		 * <table>
		 */
		tmp = tmp.concat("<table>");
		for (int j = 0; j < tables.size(); j++) {
			DBTable tbl = tables.get(j);
			tmp = tmp.concat("<tableInfo>");
			tmp = tmp.concat(tbl.getNewTableName());
			tmp = tmp.concat("<tableInfo>");
			tmp = tmp.concat(tbl.getOldTableName());
			tmp = tmp.concat("<tableInfo>");
			tmp = tmp.concat(tbl.getNewTableAlias());
			tmp = tmp.concat("<tableInfo>");
			tmp = tmp.concat(tbl.getOldTableAlias());
			tmp = tmp.concat("<tableInfo>");

			List<String> newCols = tbl.getNewTableCol();
			List<String> oldCols = tbl.getOldTableCol();
			String newC = "<col>", oldC = "<col>";
			for (int i = 0; i < newCols.size(); i++) {
				newC += newCols.get(i) + "<col>";
				oldC += oldCols.get(i) + "<col>";
			}
			tmp = tmp.concat("<tableJoinOnPairs>");
			tmp = tmp.concat(newC);
			tmp = tmp.concat("<tableJoinOnPairs>");
			tmp = tmp.concat(oldC);
			tmp = tmp.concat("<tableJoinOnPairs>");

			tmp = tmp.concat("<tableInfo>");
			tmp = tmp.concat("<table>");
			if (j == tables.size() - 1) {
				tmp = tmp.concat("<mapping>");
			}
		}
		/**
		 * defining <column> section
		 * 
		 * 
		 * <column> <colInfo> table name with alias <colInfo> table alias
		 * <colInfo> column Name <colInfo> is Column Float <colInfo> <column>
		 * 
		 * ...
		 * 
		 * <column>
		 */
		tmp = tmp.concat("<column>");
		for (int i = 0; i < columns.size(); i++) {
			tmp = tmp.concat("<colInfo>");
			tmp = tmp.concat(columns.get(i)[0]);
			tmp = tmp.concat("<colInfo>");
			tmp = tmp.concat(columns.get(i)[1]);
			tmp = tmp.concat("<colInfo>");
			tmp = tmp.concat(columns.get(i)[2]);
			tmp = tmp.concat("<colInfo>");
			tmp = tmp.concat(columns.get(i)[3]);
			tmp = tmp.concat("<colInfo>");
			tmp = tmp.concat("<column>");

			if (i == columns.size() - 1) {
				tmp = tmp.concat("<mapping>");
			}
		}

		/**
		 * defining <predicate> section <mapping> <predicate> predicate name
		 * <predicate> predicate arity <predicate> <mapping>
		 */

		tmp = tmp.concat("<predicate>");
		tmp = tmp.concat(predicate.toString());
		tmp = tmp.concat("<predicate>");
		tmp = tmp.concat(predicate.getArity() + "");
		tmp = tmp.concat("<predicate>");
		tmp = tmp.concat("<mapping>");
		return tmp;
	}

	@Override
	public String getSQL() {
		return sql;
	}

	@Override
	public Integer getArity() {
		return arity;
	}

	@Override
	public boolean isSQL() {
		return isSQL;
	}
	
//	public void print(){
//		System.out.println("1");
//		System.out.println(odbcDriver);
//		if(tables != null){
//			for(DBTable t : tables){
//				System.out.println(t);
//			}
//		}
//		System.out.println("2");
//		if(columns != null){
//			for(String[] t1 : columns){
//				for(String t : t1){
//					System.out.println(t);
//				}
//			}
//		}
//		System.out.println("3");
//		System.out.println(predicate.getSignature());
//		System.out.println(isSQL);
//		System.out.println(arity);
//		System.out.println(sql);
//		System.out.println(tableAliasNumber);
//		System.out.println("4");
//	}

}

package pt.unl.fct.di.novalincs.nohr.model;

import java.util.ArrayList;
import java.util.List;

public class DBTable {

	/**
	 * Table that was introduced
	 */
	private String newTableName;
	/**
	 * Existing table that that was used in JOIN.
	 */
	private String oldTableName;
	/**
	 * Alias of the table {@link DBTable#newTableName}
	 */
	private String newTableAlias;
	/**
	 * Alias of the table {@link DBTable#oldTableName}
	 */
	private String oldTableAlias;
	private boolean isFirst;
	/**
	 * List of new columns (from {@link DBTable#newTableName} that JOIN was based on.
	 */
	private List<String> newTableCol;
	/**
	 * List of existing columns (from {@link DBTable#oldTableName} that JOIN was based on.
	 */
	private List<String> oldTableCol;

	/**
	 * Generating a new DBTable. 
	 * @param newTableName - Table that was introduced
	 * @param oldTableName - Existing table that that was used in JOIN.
	 * @param newTableAlias - Alias of the table {@link DBTable#newTableName}
	 * @param oldTableAlias - Alias of the table {@link DBTable#oldTableName}
	 * @param newTableCol - List of new columns (from {@link DBTable#newTableName}) that JOIN was based on.
	 * @param oldTableCol - List of existing columns (from {@link DBTable#oldTableName}) that JOIN was based on.
	 */
	public DBTable(String newTableName, String oldTableName, String newTableAlias, String oldTableAlias, List<String> newTableCol, List<String> oldTableCol) {
		super();
		this.newTableName = newTableName;
		this.oldTableName = oldTableName;
		this.newTableAlias = newTableAlias;
		this.oldTableAlias = oldTableAlias;
		this.newTableCol = new ArrayList<String>();
		this.oldTableCol = new ArrayList<String>();

		for (int i = 0; i < newTableCol.size(); i++) {
			this.newTableCol.add(newTableCol.get(i));
			this.oldTableCol.add(oldTableCol.get(i));
		}
		this.isFirst=false;
	}
	
	/**
	 * Generating a new DBTable. 
	 * @param newTableName - Table that was introduced
	 * @param oldTableName - Existing table that that was used in JOIN.
	 * @param newTableAlias - Alias of the table {@link DBTable#newTableName}
	 * @param oldTableAlias - Alias of the table {@link DBTable#oldTableName}
	 * @param newTableCol - List of new columns (from {@link DBTable#newTableName}) that JOIN was based on.
	 * @param oldTableCol - List of existing columns (from {@link DBTable#oldTableName}) that JOIN was based on.
	 * @param isFirst - {@code true} if it is a first table in the mapping, {@code false} otherwise
	 */
	public DBTable(String newTableName, String oldTableName, String newTableAlias, String oldTableAlias, List<String> newTableCol, List<String> oldTableCol, boolean isFirst) {
		super();
		this.newTableName = newTableName;
		this.oldTableName = oldTableName;
		this.newTableAlias = newTableAlias;
		this.oldTableAlias = oldTableAlias;
		this.newTableCol = new ArrayList<String>();
		this.oldTableCol = new ArrayList<String>();

		for (int i = 0; i < newTableCol.size(); i++) {
			this.newTableCol.add(newTableCol.get(i));
			this.oldTableCol.add(oldTableCol.get(i));
		}
		this.isFirst=isFirst;
	}

	public String getNewTableName() {
		return newTableName;
	}

	public void setNewTableName(String newTableName) {
		this.newTableName = newTableName;
	}

	public String getOldTableName() {
		return oldTableName;
	}

	public void setOldTableName(String oldTableName) {
		this.oldTableName = oldTableName;
	}

	public List<String> getNewTableCol() {
		return newTableCol;
	}

	public void setNewTableCol(List<String> newTableCol) {
		this.newTableCol = newTableCol;
	}

	public List<String> getOldTableCol() {
		return oldTableCol;
	}

	public void setOldTableCol(List<String> oldTableCol) {
		this.oldTableCol = oldTableCol;
	}
	

	public String getNewTableAlias() {
		return newTableAlias;
	}

	public void setNewTableAlias(String newTableAlias) {
		this.newTableAlias = newTableAlias;
	}

	public String getOldTableAlias() {
		return oldTableAlias;
	}

	public void setOldTableAlias(String oldTableAlias) {
		this.oldTableAlias = oldTableAlias;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
	public String getNewCols(){
		String newCols="";
		for(int i=0;i<newTableCol.size();i++){
			newCols+=newTableCol.get(i);
			if(i<newTableCol.size()-1){
				newCols+=",";
			}
		}
		return newCols;
	}
	
	public String getOldCols(){
		String oldCols="";
		for(int i=0;i<oldTableCol.size();i++){
			oldCols+=oldTableCol.get(i);
			if(i<oldTableCol.size()-1){
				oldCols+=",";
			}
		}
		return oldCols;
	}
	

}

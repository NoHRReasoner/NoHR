package pt.unl.fct.di.novalincs.nohr.model;

import java.util.ArrayList;
import java.util.List;

public class DBTable {

	private String newTableName;
	private String oldTableName;
	private boolean isFirst;
	private List<String> newTableCol;
	private List<String> oldTableCol;

	public DBTable(String newTableName, String oldTableName, List<String> newTableCol, List<String> oldTableCol) {
		super();
		this.newTableName = newTableName;
		this.oldTableName = oldTableName;
		this.newTableCol = new ArrayList<String>();
		this.oldTableCol = new ArrayList<String>();

		for (int i = 0; i < newTableCol.size(); i++) {
			this.newTableCol.add(newTableCol.get(i));
			this.oldTableCol.add(oldTableCol.get(i));
		}
		this.isFirst=false;
	}
	
	public DBTable(String newTableName, String oldTableName, List<String> newTableCol, List<String> oldTableCol, boolean isFirst) {
		super();
		this.newTableName = newTableName;
		this.oldTableName = oldTableName;
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

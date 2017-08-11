package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;



import javax.swing.table.AbstractTableModel;

import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;

import java.sql.Driver;
import java.util.*;

public class ODBCTableView extends AbstractTableModel {
    private static final long serialVersionUID = -7588371899390500462L;
    
    private List<ODBCDriver> drivers = new ArrayList<>();
    
	public enum Column {
		NAME("Name"), DATABASE("Database"),TYPE("Type");
		
		private String name;
		
		Column(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	

	
	public ODBCTableView(List<ODBCDriver> drivers) {
		if(drivers==null || drivers.size()==0)
			this.drivers = new ArrayList<ODBCDriver>();
		else
			this.drivers  = drivers;
	}

	public int getColumnCount() {
		return Column.values().length;
	}
	
	@Override
	public String getColumnName(int column) {
	    Column col = Column.values()[column];
	    return col.getName();
	}

	public int getRowCount() {
		return drivers.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		ODBCDriver info = drivers.get(rowIndex);
		switch (Column.values()[columnIndex]) {
		case NAME:
			return info != null ? info.getConnName() : null;
		case DATABASE:
			return info != null ? info.getDBName().toString() : null;
		case TYPE:
			return info != null ? info.getDBType() : null;
	    default:
	    	throw new UnsupportedOperationException("Error in ODBCTableView.getValueAt call.");
		}
	}
	
	
	public List<ODBCDriver> getDrivers() {
	    return drivers;
	}
	
	public void addDriver(ODBCDriver driver) {
		System.out.println("ODBCTableView.addDriver() called -> "+driver.toString());
		drivers.add(driver);
		fireTableStructureChanged();
	}
	
	public void removeDrivers(List<Integer> rows) {
		System.out.println("ODBCTableView.removeDrivers() called -> "+rows);
	    Collections.sort(rows);
	    Collections.reverse(rows);
	    for (Integer row : rows) {
	        drivers.remove(row.intValue());
	    }
	    fireTableStructureChanged();
	}
	
	public void replaceDriver(int row, ODBCDriver newDriver) {
		System.out.println("ODBCTableView.replaceDriver() called -> "+row +" .. "+newDriver.toString());
	    drivers.remove(row);
	    drivers.add(row, newDriver);
	    fireTableStructureChanged();
	}


}

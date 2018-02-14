package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.DBTable;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;

import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

public class DBMappingAddColumnForm extends JPanel {
	/**
	 * The class is used to define the view of a single database mapping
	 * (create/edit).
	 * 
	 *
	 * @author Vedran Kasalica
	 */
	private static final long serialVersionUID = -5207499377408633751L;
	private JTextField fieldColName;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblJoin;
	private JCheckBox checkIsFloat;
	private JLabel lblTableName;
	private JComboBox comboTable;
	private List<String> tables;

	/**
	 * Create the frame.
	 */
	public DBMappingAddColumnForm() {
		
		tables = new ArrayList<String>();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0 };
		setLayout(gridBagLayout);

		lblNewLabel_1 = new JLabel("Column information:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setToolTipText("Define the mapping");
		lblNewLabel_1.setBackground(Color.WHITE);
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\VedranPC\\Desktop\\plus.png"));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.gridwidth = 3;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		lblTableName = new JLabel("Table:");
		GridBagConstraints gbc_lblTableName = new GridBagConstraints();
		gbc_lblTableName.anchor = GridBagConstraints.EAST;
		gbc_lblTableName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableName.gridx = 1;
		gbc_lblTableName.gridy = 3;
		add(lblTableName, gbc_lblTableName);
		
		comboTable = new JComboBox();
		GridBagConstraints gbc_comboTable = new GridBagConstraints();
		gbc_comboTable.gridwidth = 5;
		gbc_comboTable.insets = new Insets(0, 0, 5, 5);
		gbc_comboTable.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboTable.gridx = 3;
		gbc_comboTable.gridy = 3;
		add(comboTable, gbc_comboTable);

		lblNewLabel = new JLabel("Column name:");
		lblNewLabel.setToolTipText("Name of the column");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 4;
		add(lblNewLabel, gbc_lblNewLabel);

		fieldColName = new JTextField();
		lblNewLabel.setLabelFor(fieldColName);
		GridBagConstraints gbc_fieldColName = new GridBagConstraints();
		gbc_fieldColName.gridwidth = 5;
		gbc_fieldColName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldColName.insets = new Insets(0, 0, 5, 5);
		gbc_fieldColName.gridx = 3;
		gbc_fieldColName.gridy = 4;
		// GhostText ghostText = new GhostText(nameTxtFeild, "Enter column
		// name...");
		add(fieldColName, gbc_fieldColName);
		fieldColName.setColumns(20);

		lblJoin = new JLabel("Is floating point:");
		lblJoin.setToolTipText("Not NULL and floating point representation");
		GridBagConstraints gbc_lblJoin = new GridBagConstraints();
		gbc_lblJoin.anchor = GridBagConstraints.EAST;
		gbc_lblJoin.insets = new Insets(0, 0, 5, 5);
		gbc_lblJoin.gridx = 1;
		gbc_lblJoin.gridy = 5;
		add(lblJoin, gbc_lblJoin);

		checkIsFloat = new JCheckBox("");
		lblJoin.setLabelFor(checkIsFloat);
		checkIsFloat.setToolTipText(
				"Check if the type of the column is float.\r\nNumber will be rounded down in order to be unifiable by XSB reasoner.");
		GridBagConstraints gbc_checkIsFloat = new GridBagConstraints();
		gbc_checkIsFloat.insets = new Insets(0, 0, 5, 5);
		gbc_checkIsFloat.gridx = 3;
		gbc_checkIsFloat.gridy = 5;
		add(checkIsFloat, gbc_checkIsFloat);

	}
	
	
	public String getColumn() {

		return fieldColName.getText();
	}

	public void setColumn(String column) {

		fieldColName.setText(column);
	}

	public boolean getIsFloat() {
		return checkIsFloat.isSelected();
	}

	public void setIsFloat(boolean isFloat) {
		checkIsFloat.setSelected(isFloat);
	}

	public void clear() {
		if (tables.size() > 0)
			comboTable.setSelectedIndex(0);
		else
			comboTable.setSelectedIndex(-1);
		fieldColName.setText("");
		checkIsFloat.setSelected(false);

	}
	
	public String[] getColDef(){
		String [] colDef = new String[4];
		colDef[0]=(String) comboTable.getSelectedItem();
		colDef[1]=((String) comboTable.getSelectedItem()).split(" as ")[1];
		colDef[2]=fieldColName.getText();
		if(checkIsFloat.isSelected())
			colDef[3]="true";
		else
			colDef[3]="false";
		return colDef;
	}

	public void edit(String[] col) {
		comboTable.setSelectedIndex(getTableIndex(col[0]));
		fieldColName.setText(col[2]);
		checkIsFloat.setSelected(col[3].matches("true"));

	}
	
	private int getTableIndex(String table) {
		if (table != null) {
			for (int i = 0; i < tables.size(); i++) {
				if (tables.get(i).matches(table))
					return i;
			}
		}
		System.out.println("Table no longer exists.");
		return -1;
	}
	
	public void setTables(List<DBTable> list) {
		tables.clear();
		for (int i = 0; i < list.size(); i++) {
				tables.add(list.get(i).getNewTableName() +" as "+list.get(i).getNewTableAlias());
		}
		refreshComboBox();
	}

	private void refreshComboBox() {
		comboTable.removeAllItems();
		for (String item : tables) {
			comboTable.addItem(item);
		}

	}
	

}

package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
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
	private JTextField fieldTblName;
	private JLabel lblTableName;

	/**
	 * Create the frame.
	 */
	public DBMappingAddColumnForm() {
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
		
		lblTableName = new JLabel("Table name:");
		GridBagConstraints gbc_lblTableName = new GridBagConstraints();
		gbc_lblTableName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableName.gridx = 1;
		gbc_lblTableName.gridy = 3;
		add(lblTableName, gbc_lblTableName);
		
		fieldTblName = new JTextField();
		lblTableName.setLabelFor(fieldTblName);
		GridBagConstraints gbc_fieldTblName = new GridBagConstraints();
		gbc_fieldTblName.gridwidth = 5;
		gbc_fieldTblName.insets = new Insets(0, 0, 5, 5);
		gbc_fieldTblName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldTblName.gridx = 3;
		gbc_fieldTblName.gridy = 3;
		add(fieldTblName, gbc_fieldTblName);
		fieldTblName.setColumns(10);

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

	public String getColTable() {

		return fieldTblName.getText();
	}

	public void setColTable(String table) {

		fieldTblName.setText(table);
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
		fieldTblName.setText("");
		fieldColName.setText("");
		checkIsFloat.setSelected(false);

	}
	
	public String[] getColDef(){
		String [] colDef = new String[3];
		colDef[0]=fieldTblName.getText();
		colDef[1]=fieldColName.getText();
		if(checkIsFloat.isSelected())
			colDef[2]="true";
		else
			colDef[2]="false";
		return colDef;
	}

	public void edit(String[] col) {
		fieldTblName.setText(col[0]);
		fieldColName.setText(col[1]);
		checkIsFloat.setSelected(col[2].matches("true"));

	}

}

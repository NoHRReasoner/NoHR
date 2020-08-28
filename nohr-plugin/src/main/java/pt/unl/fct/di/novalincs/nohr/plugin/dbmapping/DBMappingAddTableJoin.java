package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
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

public class DBMappingAddTableJoin extends JPanel {
	/**
	 * The class is used to define the view of a single database mapping
	 * (create/edit).
	 * 
	 *
	 * @author Vedran Kasalica
	 */
	private static final long serialVersionUID = -5207499377408633751L;
	private JTextField feildNewtable;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JTextField feildNewCol;
	private JLabel lblNewLabel_2;
	private JLabel label;
	private JTextField feildOldCol;
	private boolean firstTable;
	private JComboBox comboOldTable;
	private List<String> tables;
	private String tableAlias;

	/**
	 * Create the frame.
	 */
	public DBMappingAddTableJoin() {

		firstTable = true;
		tables = new ArrayList<String>();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0 };
		setLayout(gridBagLayout);

		lblNewLabel_1 = new JLabel("Table information:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setToolTipText("Define the mapping");
		lblNewLabel_1.setBackground(Color.WHITE);
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\VedranPC\\Desktop\\plus.png"));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.gridwidth = 6;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		lblNewLabel = new JLabel("Table name:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel);

		feildNewtable = new JTextField();
		GridBagConstraints gbc_feildNewtable = new GridBagConstraints();
		gbc_feildNewtable.fill = GridBagConstraints.HORIZONTAL;
		gbc_feildNewtable.insets = new Insets(0, 0, 5, 5);
		gbc_feildNewtable.gridx = 3;
		gbc_feildNewtable.gridy = 3;
		// GhostText ghostText1 = new GhostText(feildNewtable, "Enter new table
		// name...");
		add(feildNewtable, gbc_feildNewtable);
		feildNewtable.setColumns(20);

		label = new JLabel(".");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 4;
		gbc_label.gridy = 3;
		add(label, gbc_label);

		feildNewCol = new JTextField();
		GridBagConstraints gbc_feildNewCol = new GridBagConstraints();
		gbc_feildNewCol.insets = new Insets(0, 0, 5, 5);
		gbc_feildNewCol.fill = GridBagConstraints.HORIZONTAL;
		gbc_feildNewCol.gridx = 5;
		gbc_feildNewCol.gridy = 3;
		// GhostText ghostText2 = new GhostText(feildNewCol, "Enter column
		// name");
		add(feildNewCol, gbc_feildNewCol);
		feildNewCol.setColumns(10);

		lblNewLabel_2 = new JLabel("Join with:");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 5;
		add(lblNewLabel_2, gbc_lblNewLabel_2);

		comboOldTable = new JComboBox();
		GridBagConstraints gbc_comboOldTable = new GridBagConstraints();
		gbc_comboOldTable.insets = new Insets(0, 0, 5, 5);
		gbc_comboOldTable.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboOldTable.gridx = 3;
		gbc_comboOldTable.gridy = 5;
		add(comboOldTable, gbc_comboOldTable);

		label = new JLabel(".");
		GridBagConstraints gbc_label2 = new GridBagConstraints();
		gbc_label2.anchor = GridBagConstraints.EAST;
		gbc_label2.insets = new Insets(0, 0, 5, 5);
		gbc_label2.gridx = 4;
		gbc_label2.gridy = 5;
		add(label, gbc_label2);

		feildOldCol = new JTextField();
		GridBagConstraints gbc_feildOldCol = new GridBagConstraints();
		gbc_feildOldCol.insets = new Insets(0, 0, 5, 5);
		gbc_feildOldCol.fill = GridBagConstraints.HORIZONTAL;
		gbc_feildOldCol.gridx = 5;
		gbc_feildOldCol.gridy = 5;
		// GhostText ghostText4 = new GhostText(feildOldCol, "Enter column
		// name...");
		add(feildOldCol, gbc_feildOldCol);
		feildOldCol.setColumns(10);

	}

	public String[] getTable() {
		String[] tableJoin = new String[4];
		tableJoin[0] = feildNewtable.getText() + " as " + tableAlias;

		tableJoin[1] = feildNewCol.getText();
		if (firstTable) {
			tableJoin[2] = "";
		} else {
			tableJoin[2] = ((String) comboOldTable.getSelectedItem()).split(" as ")[1];
		}
		tableJoin[3] = feildOldCol.getText();

		// List<String> items = Arrays.asList(str.split("\\s*,\\s*"));

		return tableJoin;
	}

	public DBTable getTableModel() {

		List<String> newCol = Arrays.asList(feildNewCol.getText().split("\\s*,\\s*"));
		List<String> oldCol = Arrays.asList(feildOldCol.getText().split("\\s*,\\s*"));
		DBTable tableJoin;
		if (firstTable) {
			tableJoin = new DBTable(feildNewtable.getText(), "", tableAlias,"", newCol, oldCol, firstTable);
		} else {
			String[] oldTable = ((String) comboOldTable.getSelectedItem()).split(" as ");
			tableJoin = new DBTable(feildNewtable.getText(), oldTable[0], tableAlias, oldTable[1], newCol, oldCol,
					firstTable);
		}
		// List<String> items = Arrays.asList(str.split("\\s*,\\s*"));

		return tableJoin;
	}

	public void clear() {
		feildNewtable.setText("");
		feildNewCol.setText("");
		if (tables.size() > 0)
			comboOldTable.setSelectedIndex(0);
		else
			comboOldTable.setSelectedIndex(-1);
		feildOldCol.setText("");

	}

	public void edit(DBTable table) {
		feildNewtable.setText(table.getNewTableName());
		feildNewCol.setText(table.getNewCols());
		comboOldTable.setSelectedIndex(getTableIndex(table.getOldTableName() + " as " + table.getOldTableAlias()));
		feildOldCol.setText(table.getOldCols());

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

	public void first(boolean firstTable) {
		this.firstTable = firstTable;
		feildNewCol.setEditable(!firstTable);
		feildNewCol.setEnabled(!firstTable);
		comboOldTable.setEnabled(!firstTable);
		feildOldCol.setEditable(!firstTable);
		feildOldCol.setEnabled(!firstTable);

	}

	/**
	 * Setting up the table pop-up.
	 * @param list - list of tables
	 * @param curr - table to be loaded (-1 if empty)
	 * @param tableAlias
	 */
	public void setTables(List<DBTable> list, int curr, String tableAlias) {
		tables.clear();
		this.tableAlias = tableAlias;
		for (int i = 0; i < list.size(); i++) {
			if (i != curr) {
				tables.add(list.get(i).getNewTableName() + " as " + list.get(i).getNewTableAlias());
			} else {
				this.tableAlias = list.get(i).getNewTableAlias();
			}
		}
		refreshComboBox();
	}

	private void refreshComboBox() {
		comboOldTable.removeAllItems();
		for (String item : tables) {
			comboOldTable.addItem(item);
		}

	}
}

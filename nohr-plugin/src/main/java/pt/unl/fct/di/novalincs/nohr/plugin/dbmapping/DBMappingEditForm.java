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
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;

public class DBMappingEditForm extends JPanel {
	/**
	 * The class is used to define the view of a single database mapping
	 * (create/edit).
	 * 
	 *
	 * @author Vedran Kasalica
	 */
	private static final long serialVersionUID = -5207499377408633751L;

	private JLabel lblTable;
	private JTextField tableTxtFeild;
	private JLabel lblColumns;
	private JTextField colsTxtFeild;
	private JLabel lblPredicate;
	private JTextField predicateTxtFeild;
	private JLabel lblNewLabel;
	private JComboBox comboBoxODBC;
	private JLabel lblNewLabel_1;

	/**
	 * Create the frame.
	 */
	public DBMappingEditForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE, 0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 0 };
		setLayout(gridBagLayout);

		lblNewLabel_1 = new JLabel("   Define the mapping:");
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

		lblNewLabel = new JLabel("ODBC:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel);

		comboBoxODBC = new JComboBox();
		GridBagConstraints gbc_comboBoxODBC = new GridBagConstraints();
		gbc_comboBoxODBC.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxODBC.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxODBC.gridx = 3;
		gbc_comboBoxODBC.gridy = 3;
		refreshComboBox();
		add(comboBoxODBC, gbc_comboBoxODBC);

		lblTable = new JLabel("Table:");
		GridBagConstraints gbc_lblTable = new GridBagConstraints();
		gbc_lblTable.insets = new Insets(0, 0, 5, 5);
		gbc_lblTable.gridx = 1;
		gbc_lblTable.gridy = 4;
		add(lblTable, gbc_lblTable);

		tableTxtFeild = new JTextField();
		GridBagConstraints gbc_tableTxtFeild = new GridBagConstraints();
		gbc_tableTxtFeild.fill = GridBagConstraints.HORIZONTAL;
		gbc_tableTxtFeild.insets = new Insets(0, 0, 5, 5);
		gbc_tableTxtFeild.gridx = 3;
		gbc_tableTxtFeild.gridy = 4;
		add(tableTxtFeild, gbc_tableTxtFeild);
		tableTxtFeild.setColumns(20);

		lblColumns = new JLabel("Columns:");
		GridBagConstraints gbc_lblColumns = new GridBagConstraints();
		gbc_lblColumns.insets = new Insets(0, 0, 5, 5);
		gbc_lblColumns.gridx = 1;
		gbc_lblColumns.gridy = 5;
		add(lblColumns, gbc_lblColumns);

		colsTxtFeild = new JTextField();
		GridBagConstraints gbc_colsTxtFeild = new GridBagConstraints();
		gbc_colsTxtFeild.fill = GridBagConstraints.HORIZONTAL;
		gbc_colsTxtFeild.insets = new Insets(0, 0, 5, 5);
		gbc_colsTxtFeild.gridx = 3;
		gbc_colsTxtFeild.gridy = 5;
		add(colsTxtFeild, gbc_colsTxtFeild);
		colsTxtFeild.setColumns(20);

		lblPredicate = new JLabel("Predicate:");
		GridBagConstraints gbc_lblPredicate = new GridBagConstraints();
		gbc_lblPredicate.insets = new Insets(0, 0, 5, 5);
		gbc_lblPredicate.gridx = 1;
		gbc_lblPredicate.gridy = 6;
		add(lblPredicate, gbc_lblPredicate);

		predicateTxtFeild = new JTextField();
		GridBagConstraints gbc_predicateTxtFeild = new GridBagConstraints();
		gbc_predicateTxtFeild.fill = GridBagConstraints.HORIZONTAL;
		gbc_predicateTxtFeild.insets = new Insets(0, 0, 5, 5);
		gbc_predicateTxtFeild.gridx = 3;
		gbc_predicateTxtFeild.gridy = 6;
		add(predicateTxtFeild, gbc_predicateTxtFeild);
		predicateTxtFeild.setColumns(20);

	}

	public void refreshComboBox() {
		comboBoxODBC.removeAllItems();
		List<ODBCDriver> drivers = ODBCPreferences.getDrivers();
		for (ODBCDriver item : drivers) {
			comboBoxODBC.addItem(item);
		}
		
	}

	public void setTableText(String string) {
		tableTxtFeild.setText(string);

	}

	public String getTableText() {
		return tableTxtFeild.getText();

	}

	public void setColumnsText(String string) {
		colsTxtFeild.setText(string);

	}

	public String getColumnsText() {
		return colsTxtFeild.getText();

	}

	public String getPredicateText() {
		return predicateTxtFeild.getText();

	}

	public void setPredicateText(String string) {
		predicateTxtFeild.setText(string);

	}

	public ODBCDriver getODBCDriver() {
		return (ODBCDriver) comboBoxODBC.getSelectedItem();

	}
	
	public void setODBCDriver(ODBCDriver driver) {
		comboBoxODBC.setSelectedItem(driver);

	}

	public DBMapping getDBMapping() {
		DBMapping tmp = new DBMappingImpl(getODBCDriver(), getTableText(), getPredicateText(), getPredicateText());

		return tmp;
	}
}

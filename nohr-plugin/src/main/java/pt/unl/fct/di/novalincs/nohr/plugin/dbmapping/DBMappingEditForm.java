package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DBMappingEditForm extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5207499377408633751L;

	private JLabel lblTable;
	private JTextField tableTxtFeild;
	private JLabel lblColumns;
	private JTextField colsTxtFeild;
	private JLabel lblPredicate;
	private JTextField predicateTxtFeild;

	/**
	 * Create the frame.
	 */
	public DBMappingEditForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblTable = new JLabel("Table:");
		GridBagConstraints gbc_lblTable = new GridBagConstraints();
		gbc_lblTable.insets = new Insets(0, 0, 5, 5);
		gbc_lblTable.gridx = 0;
		gbc_lblTable.gridy = 1;
		add(lblTable, gbc_lblTable);

		tableTxtFeild = new JTextField();
		GridBagConstraints gbc_tableTxtFeild = new GridBagConstraints();
		gbc_tableTxtFeild.anchor = GridBagConstraints.WEST;
		gbc_tableTxtFeild.insets = new Insets(0, 0, 5, 0);
		gbc_tableTxtFeild.gridx = 3;
		gbc_tableTxtFeild.gridy = 1;
		add(tableTxtFeild, gbc_tableTxtFeild);
		tableTxtFeild.setColumns(10);

		lblColumns = new JLabel("Columns:");
		GridBagConstraints gbc_lblColumns = new GridBagConstraints();
		gbc_lblColumns.insets = new Insets(0, 0, 5, 5);
		gbc_lblColumns.gridx = 0;
		gbc_lblColumns.gridy = 3;
		add(lblColumns, gbc_lblColumns);

		colsTxtFeild = new JTextField();
		GridBagConstraints gbc_colsTxtFeild = new GridBagConstraints();
		gbc_colsTxtFeild.anchor = GridBagConstraints.WEST;
		gbc_colsTxtFeild.insets = new Insets(0, 0, 5, 0);
		gbc_colsTxtFeild.gridx = 3;
		gbc_colsTxtFeild.gridy = 3;
		add(colsTxtFeild, gbc_colsTxtFeild);
		colsTxtFeild.setColumns(10);

		lblPredicate = new JLabel("Predicate:");
		GridBagConstraints gbc_lblPredicate = new GridBagConstraints();
		gbc_lblPredicate.insets = new Insets(0, 0, 0, 5);
		gbc_lblPredicate.gridx = 0;
		gbc_lblPredicate.gridy = 5;
		add(lblPredicate, gbc_lblPredicate);

		predicateTxtFeild = new JTextField();
		GridBagConstraints gbc_predicateTxtFeild = new GridBagConstraints();
		gbc_predicateTxtFeild.anchor = GridBagConstraints.WEST;
		gbc_predicateTxtFeild.gridx = 3;
		gbc_predicateTxtFeild.gridy = 5;
		add(predicateTxtFeild, gbc_predicateTxtFeild);
		predicateTxtFeild.setColumns(10);

	}

	public void setTableText(String string) {
		tableTxtFeild.setText(string);

	}

	public void setColumnsText(String string) {
		colsTxtFeild.setText(string);

	}

	public void setPredicateText(String string) {
		predicateTxtFeild.setText(string);

	}
	
	public String getTableText() {
		return tableTxtFeild.getText();

	}

	public String getColumnsText() {
		return colsTxtFeild.getText();

	}

	public String getPredicateText() {
		return predicateTxtFeild.getText();

	}
}

package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;

import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.ui.UIHelper;

import javax.swing.event.ChangeEvent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class DBMappingEditForm extends JPanel {
	/**
	 * The class is used to define the view of a single database mapping
	 * (create/edit).
	 * 
	 *
	 * @author Vedran Kasalica
	 */
	private static final long serialVersionUID = -5207499377408633751L;
	private JLabel lblColumns;
	private JLabel lblPredicate;
	private JTextField predicateTxtFeild;
	private JLabel lblNewLabel;
	private JComboBox comboBoxODBC;
	private JLabel lblNewLabel_1;
	private JButton btnAddCol;
	private JButton btnRemCol;
	private JLabel lblTable_1;
	private JScrollPane scrollPane;
	private JTable tbTables;
	private JButton btnAddTable;
	private JButton btnRemTable;
	private List<String[]> tables;
	private DefaultTableModel tablesModel;
	private List<String> columns;
	private DefaultTableModel columnsModel;

	/**
	 * Create the frame.
	 */
	public DBMappingEditForm() {
		tables = new ArrayList<String[]>();
		columns = new ArrayList<String>();

		GridBagLayout gridBagLayout = new GridBagLayout();

		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.25, 0.2, 1.0, 0.25, Double.MIN_VALUE, };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0 };
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
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
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

		ButtonGroup group = new ButtonGroup();

		lblTable_1 = new JLabel("Tables:");
		GridBagConstraints gbc_lblTable_1 = new GridBagConstraints();
		gbc_lblTable_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblTable_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblTable_1.gridx = 1;
		gbc_lblTable_1.gridy = 6;
		add(lblTable_1, gbc_lblTable_1);

		tablesModel = new DefaultTableModel();
		tablesModel.addColumn("Table");
		tablesModel.addColumn("Column");
		tablesModel.addColumn("Table2");
		tablesModel.addColumn("Col2");

		tbTables = new JTable(tablesModel);
		scrollPane = new JScrollPane(tbTables);
		tbTables.setSize(comboBoxODBC.WIDTH, 50);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 3;
		gbc_scrollPane.gridy = 6;
		add(scrollPane, gbc_scrollPane);

		btnAddTable = new JButton("+");
		btnAddTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTable();
			}
		});
		GridBagConstraints gbc_btnAddTable = new GridBagConstraints();
		gbc_btnAddTable.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnAddTable.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddTable.gridx = 4;
		gbc_btnAddTable.gridy = 6;
		add(btnAddTable, gbc_btnAddTable);

		btnRemTable = new JButton("-");
		btnRemTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeTable(tbTables.getSelectedRows());
			}
		});
		GridBagConstraints gbc_btnRemTable = new GridBagConstraints();
		gbc_btnRemTable.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnRemTable.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemTable.gridx = 5;
		gbc_btnRemTable.gridy = 6;
		add(btnRemTable, gbc_btnRemTable);

		lblColumns = new JLabel("Columns:");
		GridBagConstraints gbc_lblColumns = new GridBagConstraints();
		gbc_lblColumns.anchor = GridBagConstraints.EAST;
		gbc_lblColumns.insets = new Insets(0, 0, 5, 5);
		gbc_lblColumns.gridx = 1;
		gbc_lblColumns.gridy = 8;
		add(lblColumns, gbc_lblColumns);

		columnsModel = new DefaultTableModel();
		columnsModel.addColumn("Columns");

		JTable tbColons = new JTable(columnsModel);
		tbTables.setSize(comboBoxODBC.WIDTH, 100);
		JScrollPane scrollPaneCol = new JScrollPane(tbColons);
		GridBagConstraints gbc_scrollPaneCol = new GridBagConstraints();
		gbc_scrollPaneCol.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneCol.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneCol.gridx = 3;
		gbc_scrollPaneCol.gridy = 8;
		add(scrollPaneCol, gbc_scrollPaneCol);

		btnAddCol = new JButton("+");
		btnAddCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addColumn();
			}
		});
		GridBagConstraints gbc_btnAddCol = new GridBagConstraints();
		gbc_btnAddCol.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnAddCol.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddCol.gridx = 4;
		gbc_btnAddCol.gridy = 8;
		add(btnAddCol, gbc_btnAddCol);

		btnRemCol = new JButton("-");
		btnRemCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeColumn(tbColons.getSelectedColumns());

			}
		});
		GridBagConstraints gbc_btnRemCol = new GridBagConstraints();
		gbc_btnRemCol.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnRemCol.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemCol.gridx = 5;
		gbc_btnRemCol.gridy = 8;
		add(btnRemCol, gbc_btnRemCol);

		lblPredicate = new JLabel("Predicate:");
		GridBagConstraints gbc_lblPredicate = new GridBagConstraints();
		gbc_lblPredicate.anchor = GridBagConstraints.EAST;
		gbc_lblPredicate.insets = new Insets(0, 0, 5, 5);
		gbc_lblPredicate.gridx = 1;
		gbc_lblPredicate.gridy = 9;
		add(lblPredicate, gbc_lblPredicate);

		predicateTxtFeild = new JTextField();
		GridBagConstraints gbc_predicateTxtFeild = new GridBagConstraints();
		gbc_predicateTxtFeild.fill = GridBagConstraints.HORIZONTAL;
		gbc_predicateTxtFeild.insets = new Insets(0, 0, 5, 5);
		gbc_predicateTxtFeild.gridx = 3;
		gbc_predicateTxtFeild.gridy = 9;
		add(predicateTxtFeild, gbc_predicateTxtFeild);
		predicateTxtFeild.setColumns(20);

		setSize(500, 400);
	}

	protected void addTable() {
		DBMappingAddTableJoin tablePopup = new DBMappingAddTableJoin();
		final int ret = JOptionPaneEx.showConfirmDialog(this, "Add a new table", tablePopup,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

		if (ret == JOptionPane.OK_OPTION) {
			String [] tbl = tablePopup.getTable();
			tables.add(tbl);
			tablesModel.addRow(tbl);
		}

	}

	protected void removeTable(int[] remove) {
		for (int i = remove.length - 1; i >= 0; i--) {
			int arr = remove[i];
			tablesModel.removeRow(arr);
			tables.remove(arr);
		}

	}

	protected void addColumn() {
		DBMappingAddColumnForm columnPopup = new DBMappingAddColumnForm();
		final int ret = JOptionPaneEx.showConfirmDialog(this, "Add a new column", columnPopup,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

		if (ret == JOptionPane.OK_OPTION) {
			String colName =columnPopup.getColumn(); 
			columns.add(colName);
			columnsModel.addRow(new Object[] { colName });
		}
	}

	protected void removeColumn(int[] remove) {
		for (int i = remove.length - 1; i >= 0; i--) {
			int arr = remove[i];
			columnsModel.removeRow(arr);
			columns.remove(arr);
		}

	}

	public void refreshComboBox() {
		comboBoxODBC.removeAllItems();
		List<ODBCDriver> drivers = ODBCPreferences.getDrivers();
		for (ODBCDriver item : drivers) {
			comboBoxODBC.addItem(item);
		}

	}

	public String getPredicateText() {
		return predicateTxtFeild.getText();

	}

	public void setPredicateText(String string) {
		predicateTxtFeild.setText(string);

	}

	public String[][] getTables() {
		String[][] tab = new String[tables.size()][4];
		return tab;

	}

	public void setTables(String[][] tab) {
		if (tab == null) {
			tables = new ArrayList<String[]>();
			tablesModel.setRowCount(0);
		} else {
			tables.clear();
			for (String[] s : tab)
				tables.add(s);
			tablesModel.setRowCount(0);
			for (String[] tmp : tab){
				String[]tmpCopy=new String[tmp.length];
				for(int i=0;i<tmp.length;i++)
					tmpCopy[i]=tmp[i];
				tablesModel.addRow(tmpCopy);
			}
		}
	}

	public String[] getColumns() {
		String[] tab = new String[columns.size()];
		return tab;

	}

	public void setColumns(List<String> col) {
		if (col == null) {
			columns = new ArrayList<String>();
			columnsModel.setRowCount(0);
			return;
		} else {
			columns.clear();
			for (String s : col)
				columns.add(s);
			columnsModel.setRowCount(0);
			for (String s : col)
				columnsModel.addRow(new Object[] { s });
		}
	}

	public ODBCDriver getODBCDriver() {
		return (ODBCDriver) comboBoxODBC.getSelectedItem();

	}

	public void setODBCDriver(ODBCDriver driver) {
		comboBoxODBC.setSelectedItem(driver);

	}

	public DBMapping getDBMapping() {
		String[][] tab = new String[tables.size()][4];
		for (int i = 0; i < tables.size(); i++)
			tab[i] = tables.get(i);
		DBMapping tmp = new DBMappingImpl(getODBCDriver(), tab, columns, getPredicateText());

		return tmp;
	}
}

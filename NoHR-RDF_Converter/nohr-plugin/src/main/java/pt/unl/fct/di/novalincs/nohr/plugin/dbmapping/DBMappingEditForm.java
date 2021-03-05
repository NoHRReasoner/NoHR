package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.core.ui.util.JOptionPaneEx;

import pt.unl.fct.di.novalincs.nohr.deductivedb.MappingGenerator;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.DBTable;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRScanner;
import pt.unl.fct.di.novalincs.nohr.parsing.TokenType;
import pt.unl.fct.di.novalincs.nohr.plugin.IconLoader;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;
import pt.unl.fct.di.novalincs.nohr.utils.CreatingMappings;
import java.awt.Component;
import javax.swing.SwingConstants;

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
	private JButton btnEditTable;
	private JButton btnEditColumn;
	private boolean firstTable;
	private boolean isSQL;

	private final List<DBTable> tables;
	private final DefaultTableModel tablesModel;
	private int tableNumber;

	private final List<String[]> columns;
	private final DefaultTableModel columnsModel;

	private final Vocabulary vocabulary;

	private final DBMappingAddTableJoin tablePopup;
	private final DBMappingAddColumnForm columnPopup;
	private JLabel lblSql;
	private JSpinner spArity;
	private JLabel lblArity;
	private JButton btnEditSQL;
	private JTextArea fieldSQL;
	private JSeparator separator;

	/**
	 * Create the frame.
	 */
	public DBMappingEditForm(Vocabulary vocabulary) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxHeight= (int) screenSize.getHeight();
		int maxWidth = (int) screenSize.getWidth();
		int height = 500;
		int width = 690;
		setPreferredSize(new Dimension(width, height));
		setLocation(new Point(((maxWidth-width)/3),(maxHeight-height)/3));
		setAutoscrolls(true);
		this.vocabulary = vocabulary;
		tables = new ArrayList<DBTable>();
		columns = new ArrayList<String[]>();
		tablePopup = new DBMappingAddTableJoin();
		columnPopup = new DBMappingAddColumnForm();

		columnsModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		columnsModel.addColumn("Table");
		columnsModel.addColumn("Column");
		tablesModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		tablesModel.addColumn("Table");
		tablesModel.addColumn("Column");
		tablesModel.addColumn("JOIN Table");
		tablesModel.addColumn("ON Column");

		firstTable = true;
		isSQL = false;

		
		
		load();
	}

	private void load() {

		
		GridBagLayout gridBagLayout = new GridBagLayout();

		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 0.25, 0.0, 0.0, 0.0, };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
				0.0 };
		setLayout(gridBagLayout);

		lblNewLabel_1 = new JLabel("Define the mapping:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setToolTipText("Define the mapping");
		lblNewLabel_1.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.gridwidth = 3;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		lblNewLabel = new JLabel("ODBC:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel);

		comboBoxODBC = new JComboBox();
		GridBagConstraints gbc_comboBoxODBC = new GridBagConstraints();
		gbc_comboBoxODBC.gridwidth = 3;
		gbc_comboBoxODBC.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxODBC.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxODBC.gridx = 2;
		gbc_comboBoxODBC.gridy = 3;
		refreshComboBox();
		comboBoxODBC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSQL();
			}
		});
		add(comboBoxODBC, gbc_comboBoxODBC);

		ButtonGroup group = new ButtonGroup();

		lblColumns = new JLabel("SELECT Columns:");
		lblColumns.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblColumns = new GridBagConstraints();
		gbc_lblColumns.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblColumns.insets = new Insets(0, 0, 5, 5);
		gbc_lblColumns.gridx = 1;
		gbc_lblColumns.gridy = 5;
		add(lblColumns, gbc_lblColumns);

		final JTable tbColons = new JTable(columnsModel);
		tbColons.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JScrollPane scrollPaneCol = new JScrollPane(tbColons);
		scrollPaneCol.setPreferredSize(new Dimension(380, 83));
		scrollPaneCol.setMinimumSize(new Dimension(23, 83));
		scrollPaneCol.setMaximumSize(new Dimension(32767, 83));
		GridBagConstraints gbc_scrollPaneCol = new GridBagConstraints();
		gbc_scrollPaneCol.gridheight = 2;
		gbc_scrollPaneCol.gridwidth = 3;
		gbc_scrollPaneCol.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneCol.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneCol.gridx = 2;
		gbc_scrollPaneCol.gridy = 5;
		add(scrollPaneCol, gbc_scrollPaneCol);

		btnAddCol = new JButton("+");
		btnAddCol.setPreferredSize(new Dimension(35, 23));
		btnAddCol.setToolTipText("Add column");
		btnAddCol.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAddCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addColumn();
			}
		});
		GridBagConstraints gbc_btnAddCol = new GridBagConstraints();
		gbc_btnAddCol.anchor = GridBagConstraints.NORTH;
		gbc_btnAddCol.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddCol.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddCol.gridx = 5;
		gbc_btnAddCol.gridy = 5;
		add(btnAddCol, gbc_btnAddCol);

		btnEditColumn = new JButton("Edit");
		btnEditColumn.setToolTipText("Edit column");
		btnEditColumn.setPreferredSize(new Dimension(35, 23));
		btnEditColumn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnEditColumn.setSize(20, 20);
		btnEditColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editColumn(tbColons.getSelectedRow());

			}
		});

		GridBagConstraints gbc_btnEditColumn = new GridBagConstraints();
		gbc_btnEditColumn.anchor = GridBagConstraints.NORTH;
		gbc_btnEditColumn.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditColumn.insets = new Insets(0, 0, 5, 5);
		gbc_btnEditColumn.gridx = 6;
		gbc_btnEditColumn.gridy = 5;
		add(btnEditColumn, gbc_btnEditColumn);

		btnRemCol = new JButton("-");
		btnRemCol.setPreferredSize(new Dimension(35, 23));
		btnRemCol.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnRemCol.setToolTipText("Remove column");
		btnRemCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeColumn(tbColons.getSelectedRow());

			}
		});

		GridBagConstraints gbc_btnRemCol = new GridBagConstraints();
		gbc_btnRemCol.anchor = GridBagConstraints.NORTH;
		gbc_btnRemCol.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemCol.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemCol.gridx = 7;
		gbc_btnRemCol.gridy = 5;
		add(btnRemCol, gbc_btnRemCol);

		lblTable_1 = new JLabel("FROM Tables:");
		lblTable_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTable_1 = new GridBagConstraints();
		gbc_lblTable_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblTable_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblTable_1.gridx = 1;
		gbc_lblTable_1.gridy = 7;
		add(lblTable_1, gbc_lblTable_1);

		tbTables = new JTable(tablesModel);
		tbTables.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		scrollPane = new JScrollPane(tbTables);
		scrollPane.setLocation(new Point(50, 30));
		scrollPane.setPreferredSize(new Dimension(380, 83));
		scrollPane.setMinimumSize(new Dimension(23, 83));
		scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		scrollPane.setMaximumSize(new Dimension(32767, 83));
		// tbTables.setSize(comboBoxODBC.WIDTH, 50);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 7;
		add(scrollPane, gbc_scrollPane);
		// scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(),
		// scrollPane.getWidth(), 50);

		btnAddTable = new JButton("+");
		btnAddTable.setPreferredSize(new Dimension(35, 23));
		btnAddTable.setToolTipText("Add table");
		btnAddTable.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAddTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTable();
			}
		});
		GridBagConstraints gbc_btnAddTable = new GridBagConstraints();
		gbc_btnAddTable.anchor = GridBagConstraints.NORTH;
		gbc_btnAddTable.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddTable.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddTable.gridx = 5;
		gbc_btnAddTable.gridy = 7;
		add(btnAddTable, gbc_btnAddTable);

		btnEditTable = new JButton("Edit");
		btnEditTable.setToolTipText("Edit table");
		btnEditTable.setPreferredSize(new Dimension(35, 23));
		btnEditTable.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnEditTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editTable(tbTables.getSelectedRow());
			}
		});
		GridBagConstraints gbc_btnEditTable = new GridBagConstraints();
		gbc_btnEditTable.anchor = GridBagConstraints.NORTH;
		gbc_btnEditTable.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditTable.insets = new Insets(0, 0, 5, 5);
		gbc_btnEditTable.gridx = 6;
		gbc_btnEditTable.gridy = 7;
		add(btnEditTable, gbc_btnEditTable);

		btnRemTable = new JButton("-");
		btnRemTable.setPreferredSize(new Dimension(35, 23));
		btnRemTable.setToolTipText("Remove table");
		btnRemTable.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnRemTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeTable(tbTables.getSelectedRow());
			}
		});

		GridBagConstraints gbc_btnRemTable = new GridBagConstraints();
		gbc_btnRemTable.anchor = GridBagConstraints.NORTH;
		gbc_btnRemTable.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemTable.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemTable.gridx = 7;
		gbc_btnRemTable.gridy = 7;
		add(btnRemTable, gbc_btnRemTable);

		lblPredicate = new JLabel("INTO Predicate:");
		lblPredicate.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblPredicate = new GridBagConstraints();
		gbc_lblPredicate.anchor = GridBagConstraints.EAST;
		gbc_lblPredicate.insets = new Insets(0, 0, 5, 5);
		gbc_lblPredicate.gridx = 1;
		gbc_lblPredicate.gridy = 9;
		add(lblPredicate, gbc_lblPredicate);

		predicateTxtFeild = new JTextField();
		GridBagConstraints gbc_predicateTxtFeild = new GridBagConstraints();
		gbc_predicateTxtFeild.gridwidth = 3;
		gbc_predicateTxtFeild.fill = GridBagConstraints.HORIZONTAL;
		gbc_predicateTxtFeild.insets = new Insets(0, 0, 5, 5);
		gbc_predicateTxtFeild.gridx = 2;
		gbc_predicateTxtFeild.gridy = 9;
		add(predicateTxtFeild, gbc_predicateTxtFeild);
		predicateTxtFeild.setColumns(20);
		predicateTxtFeild.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent evt) {
				updateSQL();
			}
		});

		lblArity = new JLabel("Arity:");
		lblArity.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblArity = new GridBagConstraints();
		gbc_lblArity.anchor = GridBagConstraints.EAST;
		gbc_lblArity.insets = new Insets(0, 0, 5, 5);
		gbc_lblArity.gridx = 1;
		gbc_lblArity.gridy = 10;
		add(lblArity, gbc_lblArity);

		spArity = new JSpinner();
		spArity.setToolTipText("Define the arity of the predicate");
		spArity.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		GridBagConstraints gbc_spArity = new GridBagConstraints();
		gbc_spArity.anchor = GridBagConstraints.WEST;
		gbc_spArity.insets = new Insets(0, 0, 5, 5);
		gbc_spArity.gridx = 2;
		gbc_spArity.gridy = 10;
		add(spArity, gbc_spArity);
		((DefaultEditor) spArity.getEditor()).getTextField().setEditable(false);
		spArity.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblArity.setLabelFor(spArity);

		separator = new JSeparator();
		separator.setMinimumSize(new Dimension(0, 22));
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 2;
		gbc_separator.gridy = 11;
		add(separator, gbc_separator);

		lblSql = new JLabel("SQL:");
		lblSql.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblSql = new GridBagConstraints();
		gbc_lblSql.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblSql.insets = new Insets(0, 0, 5, 5);
		gbc_lblSql.gridx = 1;
		gbc_lblSql.gridy = 12;
		add(lblSql, gbc_lblSql);

		btnEditSQL = new JButton("Write SQL");
		btnEditSQL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSQL();
			}
		});

		fieldSQL = new JTextArea();
		fieldSQL.setRows(4);
		fieldSQL.setFont(new Font("Tahoma", Font.PLAIN, 11));
		fieldSQL.setWrapStyleWord(true);
		fieldSQL.setLineWrap(true);
		
		JScrollPane scrollV = new JScrollPane (fieldSQL);
		scrollV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		GridBagConstraints gbc_fieldSQL = new GridBagConstraints();
		gbc_fieldSQL.gridwidth = 3;
		gbc_fieldSQL.insets = new Insets(0, 0, 5, 5);
		gbc_fieldSQL.fill = GridBagConstraints.BOTH;
		gbc_fieldSQL.gridx = 2;
		gbc_fieldSQL.gridy = 12;
		add(scrollV, gbc_fieldSQL);
		GridBagConstraints gbc_btnEditSQL = new GridBagConstraints();
		gbc_btnEditSQL.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditSQL.anchor = GridBagConstraints.NORTH;
		gbc_btnEditSQL.gridwidth = 3;
		gbc_btnEditSQL.insets = new Insets(0, 0, 5, 0);
		gbc_btnEditSQL.gridx = 5;
		gbc_btnEditSQL.gridy = 12;
		add(btnEditSQL, gbc_btnEditSQL);

		setSize(676, 624);
	}

	private void editSQL() {
		int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to switch to the manual SQL editor?",
				"Manual SQL ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (response == JOptionPane.YES_OPTION) {
			updateSQL();
			isSQL = true;
			isSQL(isSQL);
		}

	}

	private void addTable() {
		tablePopup.setTables(tables, -1, "t" + tableNumber);
		tablePopup.clear();
		tablePopup.first(firstTable);
		final int ret = JOptionPaneEx.showConfirmDialog(this, "Add a new table", tablePopup, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null);

		if (ret == JOptionPane.OK_OPTION) {
			tables.add(tablePopup.getTableModel());
			tablesModel.addRow(tablePopup.getTable());
			updateSQL();
			firstTable = false;
			tableNumber++;
		}

	}

	/**
	 * Edit the selected table. 
	 * @param edit - ordering number of the selected table (-1 if no table was selected)
	 */
	private void editTable(int edit) {
		if (edit == -1){
			return;
		}
		tablePopup.setTables(tables, edit, "");
		tablePopup.edit(tables.get(edit));
		tablePopup.first(firstTable || edit == 0);
		final int ret = JOptionPaneEx.showConfirmDialog(this, "Edit table", tablePopup, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null);

		if (ret == JOptionPane.OK_OPTION) {
			tables.set(edit, tablePopup.getTableModel());
			tablesModel.removeRow(edit);
			tablesModel.insertRow(edit, tablePopup.getTable());
			updateSQL();
		}

	}

	private void removeTable(int arr) {
		if (arr == -1)
			return;
		if (invalidTableDel(arr)) {
			JOptionPane.showMessageDialog(this, "The table is currently locked", "Invalid action",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		tablesModel.removeRow(arr);
		tables.remove(arr);
		if (tables.isEmpty()){
			firstTable = true;
			tableNumber=1;
		}
		updateSQL();
	}

	private boolean invalidTableDel(int arr) {
		for (DBTable curr : tables) {
			if (curr.getOldTableName() != null && tables.get(arr).getNewTableName().matches(curr.getOldTableName()))
				return true;
		}
		return false;
	}

	private void addColumn() {
		columnPopup.setTables(tables);
		columnPopup.clear();
		
		final int ret = JOptionPaneEx.showConfirmDialog(this, "Add a new column", columnPopup,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

		if (ret == JOptionPane.OK_OPTION) {
			String[] colName = columnPopup.getColDef();
			columns.add(colName);
			columnsModel.addRow(new Object[] { colName[1], colName[2] });
			spArity.getModel().setValue((int) spArity.getModel().getValue() + 1);
		}
		updateSQL();
	}

	private void editColumn(int edit) {
		if (edit == -1)
			return;
		columnPopup.setTables(tables);
		columnPopup.edit(columns.get(edit));
		final int ret = JOptionPaneEx.showConfirmDialog(this, "Edit column", columnPopup, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null);

		if (ret == JOptionPane.OK_OPTION) {
			String[] colName = columnPopup.getColDef();
			columns.set(edit, colName);
			columnsModel.removeRow(edit);
			columnsModel.insertRow(edit, new Object[] { colName[1], colName[2] });
		}
		updateSQL();

	}

	private void removeColumn(int arr) {
		if (arr == -1)
			return;
		columnsModel.removeRow(arr);
		columns.remove(arr);
		spArity.getModel().setValue((int) spArity.getModel().getValue() - 1);
		updateSQL();
	}

	private void refreshComboBox() {
		comboBoxODBC.removeAllItems();
		List<ODBCDriver> drivers = ODBCPreferences.getDrivers();
		for (ODBCDriver item : drivers) {
			comboBoxODBC.addItem(item);
		}

	}

	private String getPredicateText() {
		return predicateTxtFeild.getText();

	}

	private void setPredicateText(String string) {
		predicateTxtFeild.setText(string);

	}

	private String[][] getTables() {
		String[][] tab = new String[tables.size()][4];
		return tab;

	}

	private void setTables(List<DBTable> list) {
		if (list == null) {
			tables.clear();
			tablesModel.setRowCount(0);
			firstTable = true;
		} else {
			tables.clear();
			for (DBTable s : list)
				tables.add(s);
			tablesModel.setRowCount(0);
			for (DBTable tmp : list) {
				String[] tmpCopy = new String[4];
				tmpCopy[0] = tmp.getNewTableName() + " as " + tmp.getNewTableAlias();
				tmpCopy[1] = tmp.getNewCols();
				tmpCopy[2] = tmp.getOldTableAlias();
				tmpCopy[3] = tmp.getOldCols();

				tablesModel.addRow(tmpCopy);
			}
		}
		if (tables.isEmpty())
			firstTable = true;
	}

	private List<String[]> getColumns() {
		List<String[]> tab = new ArrayList<String[]>();
		for (String[] s : columns)
			tab.add(s);
		return tab;

	}

	private void setColumns(List<String[]> col) {
		if (col == null) {
			columns.clear();
			columnsModel.setRowCount(0);
			return;
		} else {
			columns.clear();
			for (String[] s : col){
				columns.add(s);
				for(String x:s)
					System.out.println("S_"+x);
			}
			columnsModel.setRowCount(0);
			for (String[] s : col)
				columnsModel.addRow(new Object[] { s[1], s[2] });
		}
	}

	private ODBCDriver getODBCDriver() {
		return (ODBCDriver) comboBoxODBC.getSelectedItem();

	}

	private void setODBCDriver(ODBCDriver driver) {
		comboBoxODBC.setSelectedItem(driver);

	}

	private void setSQL(String sql) {
		fieldSQL.setText(sql);

	}

	private String getSQL() {
		return fieldSQL.getText();

	}

	private void setArity(int arity) {
		spArity.setValue(arity);

	}

	private int getArity() {
		return (int) spArity.getValue();

	}

	public void setMapping(DBMapping dbMapping) {
		tableNumber=1;
		refreshComboBox();

		if (dbMapping == null) {
			isSQL(false);
			setSQL("");
			setODBCDriver(null);
			setTables(null);
			setColumns(null);
			setPredicateText("");
			setArity(0);

		} else {
			System.out.println("Mapping: " +dbMapping.toString());
			setSQL(dbMapping.getSQL());
			setODBCDriver(dbMapping.getODBC());
			setTables(dbMapping.getTables());
			setColumns(dbMapping.getColumns());
			setPredicateText(dbMapping.getPredicate().toString());
			setArity(dbMapping.getArity());
			isSQL(dbMapping.isSQL());
			tableNumber=dbMapping.getAliasNumber();

		}
		updateSQL();
	}

	private void isSQL(boolean sql) {
		isSQL = sql;
		if (sql) {
			btnAddCol.setEnabled(false);
			btnEditColumn.setEnabled(false);
			btnRemCol.setEnabled(false);
			btnAddTable.setEnabled(false);
			btnEditTable.setEnabled(false);
			btnRemTable.setEnabled(false);
			setColumns(null);
			setTables(null);

			fieldSQL.setEditable(true);
			spArity.setEnabled(true);
			btnEditSQL.setEnabled(false);

		} else {
			btnAddCol.setEnabled(true);
			btnEditColumn.setEnabled(true);
			btnRemCol.setEnabled(true);
			btnAddTable.setEnabled(true);
			btnEditTable.setEnabled(true);
			btnRemTable.setEnabled(true);

			fieldSQL.setEditable(false);
			spArity.setEnabled(false);
			btnEditSQL.setEnabled(true);
		}

	}

	public DBMapping getDBMapping() {

		if (!predicateCorrect())
			return null;

		if (isSQL) {
			if (getODBCDriver() == null || fieldSQL.getText().matches("")) {
				return null;
			}
			DBMapping tmp = new DBMappingImpl(getODBCDriver(), fieldSQL.getText(), getArity(), getPredicate());
			return tmp;
		} else {
			if (getODBCDriver() == null || tables.isEmpty() || columns.isEmpty()) {
				return null;
			}
			DBMapping tmp = new DBMappingImpl(getODBCDriver(), tables, columns, getPredicate(), tableNumber);
			return tmp;
		}
	}

	private boolean predicateCorrect() {
		if (predicateTxtFeild.getText().matches(""))
			return false;
		if (new NoHRScanner(predicateTxtFeild.getText()).next(TokenType.FUNCTOR))
			return true;
		else
			return true;
	}

	private Predicate getPredicate() {
		final List<Term> kbTerms = new LinkedList<>();
		int size = columns.size();
		if (isSQL)
			size = getArity();
		if (size == 0) {
			System.out.println("No columns added");
			// for test
			size = 1;
		}
		for (int i = 0; i < size; i++) {
			Term kbTerm = Model.var(CreatingMappings.getVar(size, i));
			kbTerms.add(kbTerm);
		}

		Rule tmp = Model.rule(Model.atom(vocabulary, getPredicateText(), kbTerms));
		Predicate predicate = tmp.getHead().getFunctor();
		// x.getHead().getFunctor().accept(formatVisitor);

		return predicate;

	}

	private void updateSQL() {
		if(isSQL)
			return;
		DBMapping tmpMap = getDBMapping();
		if (tmpMap == null)
			return;
		try {
			MappingGenerator map = new MappingGenerator(tmpMap, null);
			setSQL(map.createSQL());
		} catch (InvalidAttributesException e) {
			e.printStackTrace();
		}

	}
}

package rdf_converter;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.protege.editor.core.ui.util.JOptionPaneEx;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;
import java.util.*;

public class RDFConverter extends JPanel implements Serializable {
    private static final String PREFIX = "PREFIX ";
    private static final String LESSTHAN = "<";
    private static final String MORETHAN = ">";
    private static final String NEWLINE = "\n";
    private static final String HELP_MESSAGE = "To use the RDF Converter: 1- Select and rdf file in 1. \n" +
            "2 - Click on the + button to add a rdf triple to your search query. \n" +
            "2.5 - To edit or remove a triple, click on the triple and then click on the edit or - button. \n" +
            "3 - Press the + button to add the database you wish to use and the select in the dropdown box. \n" +
            "3.5 - To edit a database, press the edit button. \n" +
            "4 - Write the name of the table you are going to use. \n" +
            "5 - To check your created query press the create query button or if you prefer you can write your own and ignore step 2. \n" +
            "6 - Click on run. \n" +
            "Note: you must have your MySQL running to ensure no errors occur.";


    //private final DefaultTableModel predicatesModel;
    private final DefaultTableModel variablesModel;

    private File rdfFile;
    private JLabel lbl_Intro_label;
    private JLabel lbl_RDF_label;
    private JTextField rdfFileTextField;
    private JButton btnOpenFile;
    private List<String>[] tableValues;
    private Model model;
    private JLabel lblODBC;
    private JComboBox comboBoxODBC;
    private JLabel lblSPARQL;
    private JTextArea fieldSPARQL;
    private JButton btnEditSPARQL;
    private JButton createQueryButton;
    private Map<String, List<String>> rdfanswers;
    private String dbName;
    private JLabel lbl_query_params;
    private AddTuple tuples;
    private JButton addButton;
    private SortedSet<String> predicates;
    private Map<Integer, List<String>> selectQuery;
    private JButton editButton;
    private JButton removeButton;
    private JButton btnCheckSparql;
    private JButton btnHelp;
    private JButton addODBCButton;
    private ODBC_Form databaseForm;
    private SortedMap<String, ODBC> databases;
    private JLabel lblTableName;
    private JTextField tableNameTextField;
    private JButton editDatabaseButton;
    private boolean manualSparql;


    RDFConverter() {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = (int) screensize.getHeight();
        int maxWidth = (int) screensize.getWidth();
        int height = 300;
        int width = 690;
        setPreferredSize(new Dimension(width, height));
        setLocation(new Point(((maxWidth - width) / 3), (maxHeight - height) / 3));
        setAutoscrolls(true);
        selectQuery = new HashMap<>();
        variablesModel = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        variablesModel.addColumn("Subject");
        variablesModel.addColumn("Predicate");
        variablesModel.addColumn("Object");
        rdfanswers = new HashMap<>();
        databases = new TreeMap<>();
        manualSparql = false;
        initialize();
        load();
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("RDF Converter");
        frame.setSize(670, 470);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RDFConverter rdfConverter = new RDFConverter();
        frame.add(rdfConverter);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!rdfConverter.isInterfaceEmpty()) {
                    rdfConverter.saveODBC();
                }

                //SQLTutorials.dropDatabase(rdfConverter.getDbName(), rdfConverter.getDatabase(rdfConverter.getDbName()), rdfConverter.getTableName());
            }
        });

    }

    private void load() {
        try {
            String homeFolder = System.getProperty("user.home");
            String fileName = homeFolder + "\\Desktop\\databases.txt";
            ObjectInputStream file = new ObjectInputStream(
                    new FileInputStream(fileName));


            SortedMap<String, ODBC> tmpDatabases = (SortedMap<String, ODBC>) file.readObject();

            databases = tmpDatabases;

            file.close();

            System.out.println("File read");
            refreshComboBox();
        } catch (IOException e) {
            System.out.println("File Not Found");
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {

        GridBagLayout gridBagLayout = new GridBagLayout();

        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 0.25, 0.0, 0.0, 0.0};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
                0.0};
        setLayout(gridBagLayout);

        lbl_Intro_label = new JLabel("Define the RDF Mapping: ");
        lbl_Intro_label.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lbl_Intro_label.setToolTipText("Define the mapping for the RDF file");
        lbl_Intro_label.setBackground(Color.WHITE);
        GridBagConstraints gbc_lbl_Intro_label = new GridBagConstraints();
        gbc_lbl_Intro_label.anchor = GridBagConstraints.WEST;
        gbc_lbl_Intro_label.gridwidth = 3;
        gbc_lbl_Intro_label.insets = new Insets(0, 0, 5, 5);
        gbc_lbl_Intro_label.gridx = 1;
        gbc_lbl_Intro_label.gridy = 1;
        add(lbl_Intro_label, gbc_lbl_Intro_label);

        lbl_RDF_label = new JLabel("1 - RDF File");
        lbl_RDF_label.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lbl_RDF_label = new GridBagConstraints();
        gbc_lbl_RDF_label.anchor = GridBagConstraints.EAST;
        gbc_lbl_RDF_label.insets = new Insets(0, 0, 5, 5);
        gbc_lbl_RDF_label.gridx = 1;
        gbc_lbl_RDF_label.gridy = 3;
        add(lbl_RDF_label, gbc_lbl_RDF_label);

        rdfFileTextField = new JTextField();
        GridBagConstraints gbc_rdfFileTextField = new GridBagConstraints();
        gbc_rdfFileTextField.gridwidth = 2;
        gbc_rdfFileTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdfFileTextField.insets = new Insets(0, 0, 5, 5);
        gbc_rdfFileTextField.gridx = 2;
        gbc_rdfFileTextField.gridy = 3;
        add(rdfFileTextField, gbc_rdfFileTextField);
        rdfFileTextField.setText("");
        rdfFileTextField.setColumns(20);
        rdfFileTextField.setEditable(false);

        btnOpenFile = new JButton("Open");
        btnOpenFile.setPreferredSize(new Dimension(55, 23));
        btnOpenFile.setToolTipText("Add rdf file ");
        btnOpenFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
        btnOpenFile.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();

            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("RDF files", "rdf", "ttl", "nt", "n3", "nq");

            fc.setFileFilter(filter);

            final int returnVal = fc.showOpenDialog(btnOpenFile);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                rdfFile = fc.getSelectedFile();
                rdfFileTextField.setText(rdfFile.getPath());
                setRDFModel();
            }
        });

        GridBagConstraints gbc_btnOpenFile = new GridBagConstraints();
        gbc_btnOpenFile.anchor = GridBagConstraints.NORTH;
        gbc_btnOpenFile.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnOpenFile.insets = new Insets(0, 0, 5, 5);
        gbc_btnOpenFile.gridx = 4;
        gbc_btnOpenFile.gridy = 3;
        add(btnOpenFile, gbc_btnOpenFile);


        Icon c = new ImageIcon("images/information.png");
        btnHelp = new JButton(c);
        btnHelp.setPreferredSize(new Dimension(45, 23));
        btnHelp.setContentAreaFilled(false);
        btnHelp.setBorderPainted(false);
        btnHelp.addActionListener(e -> JOptionPane.showMessageDialog(btnHelp, HELP_MESSAGE, "Help message", JOptionPane.QUESTION_MESSAGE));

        GridBagConstraints gbc_btnHelp = new GridBagConstraints();
        gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
        gbc_btnHelp.gridx = 6;
        gbc_btnHelp.gridy = 3;
        add(btnHelp, gbc_btnHelp);


        // <html>2.5 - Select <BR> Query Parameters</html>
        lbl_query_params = new JLabel("<html>2 - Select <BR> Query <BR> Parameters</html>");
        lbl_query_params.setFont(new Font("Tahoma", Font.BOLD, 11));
        lbl_query_params.setToolTipText("Select what parameters you are searching for");
        GridBagConstraints gbc_lbl_query_params = new GridBagConstraints();
        gbc_lbl_query_params.anchor = GridBagConstraints.EAST;
        gbc_lbl_query_params.insets = new Insets(0, 0, 5, 5);
        gbc_lbl_query_params.gridx = 1;
        gbc_lbl_query_params.gridy = 4;
        add(lbl_query_params, gbc_lbl_query_params);


        final JTable tbVariableColumn = new JTable(variablesModel);
        tbVariableColumn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tbVariableColumn.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollVarCol = new JScrollPane(tbVariableColumn);
        scrollVarCol.setPreferredSize(new Dimension(380, 83));
        scrollVarCol.setMinimumSize(new Dimension(23, 83));
        scrollVarCol.setMaximumSize(new Dimension(32767, 83));
        GridBagConstraints gbc_scrollVarCol = new GridBagConstraints();
        gbc_scrollVarCol.gridheight = 2;
        gbc_scrollVarCol.gridwidth = 3;
        gbc_scrollVarCol.insets = new Insets(0, 0, 5, 5);
        gbc_scrollVarCol.fill = GridBagConstraints.BOTH;
        gbc_scrollVarCol.gridx = 2;
        gbc_scrollVarCol.gridy = 4;
        add(scrollVarCol, gbc_scrollVarCol);

        addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(45, 23));
        addButton.setToolTipText("Add a triple ");
        addButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        addButton.addActionListener(e -> {
            String[] tmp = predicates.toArray(new String[predicates.size()]);
            RDF_Tuple sol = null;
            do {
                tuples = new AddTuple(tmp);
                int ret = JOptionPaneEx.showConfirmDialog(addButton, "Add a triple", tuples, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
                if (ret == JOptionPane.OK_OPTION) {
                    sol = tuples.getTuple();
                    System.out.println(sol.getSubject());
                    System.out.println(sol.getPredicate());
                    System.out.println(sol.getObject());
                }
                if (ret == JOptionPane.CANCEL_OPTION || ret == JOptionPane.CLOSED_OPTION)
                    return;
            } while (sol.getSubject().equals("") || sol.getPredicate() == null || sol.getObject().equals(""));


            String[] array = new String[]{sol.getSubject(), sol.getPredicate(), sol.getObject()};
            variablesModel.addRow(array);

            int index = variablesModel.getRowCount();

            System.out.println("This is the row index: " + index);
            List<String> list = new LinkedList<>();

            //selectQuery.putIfAbsent(index, list);
            if (sol.getSubject().indexOf('?') != -1)
                list.add(sol.getSubject());
            //selectQuery.get(index).add(sol.getSubject());
            if (sol.getObject().indexOf('?') != -1)
                list.add(sol.getObject());
            //selectQuery.get(index).add(sol.getObject());
            if (list.size() > 0)
                selectQuery.putIfAbsent(index, list);
            System.out.println("Query size: " + selectQuery.size());

        });

        GridBagConstraints gbc_addButton = new GridBagConstraints();
        gbc_addButton.anchor = GridBagConstraints.NORTH;
        gbc_addButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_addButton.insets = new Insets(0, 0, 5, 5);
        gbc_addButton.gridx = 6;
        gbc_addButton.gridy = 4;
        add(addButton, gbc_addButton);

        editButton = new JButton("Edit");
        editButton.setPreferredSize(new Dimension(55, 23));
        editButton.setToolTipText("Edit a triple ");
        editButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        editButton.addActionListener(e -> {
            int index = tbVariableColumn.getSelectedRow();

            String subject = tbVariableColumn.getModel().getValueAt(index, 0).toString();
            String predicate = tbVariableColumn.getModel().getValueAt(index, 1).toString();
            String object = tbVariableColumn.getModel().getValueAt(index, 2).toString();


            selectQuery.remove(index + 1);

            RDF_Tuple tuple = new RDF_TupleImp(subject, predicate, object);

            String[] tmp = predicates.toArray(new String[predicates.size()]);

            RDF_Tuple sol = tuple;
            do {
                tuples = new AddTuple(tmp);
                tuples.editTuple(tuple);
                int ret = JOptionPaneEx.showConfirmDialog(editButton, "Edit a triple", tuples, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

                if (ret == JOptionPane.OK_OPTION) {
                    sol = tuples.getTuple();
                    System.out.println(sol.getSubject());
                    System.out.println(sol.getPredicate());
                    System.out.println(sol.getObject());
                }
                if (ret == JOptionPane.CANCEL_OPTION)
                    return;
            } while (sol.getSubject().equals("") || sol.getPredicate() == null || sol.getObject().equals(""));

            variablesModel.removeRow(index);
            String[] array = new String[]{sol.getSubject(), sol.getPredicate(), sol.getObject()};
            variablesModel.insertRow(index, array);


            List<String> list = new LinkedList<>();
            //selectQuery.putIfAbsent(index, list);
            if (sol.getSubject().indexOf('?') != -1)
                list.add(sol.getSubject());
            //selectQuery.get(index).add(sol.getSubject());
            if (sol.getObject().indexOf('?') != -1)
                list.add(sol.getObject());
            //selectQuery.get(index).add(sol.getObject());
            if (list.size() > 0)
                selectQuery.putIfAbsent(index + 1, list);

            System.out.println("Query size: " + selectQuery.size());

        });

        GridBagConstraints gbc_editButton = new GridBagConstraints();
        gbc_editButton.anchor = GridBagConstraints.NORTH;
        gbc_editButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_editButton.insets = new Insets(0, 0, 5, 5);
        gbc_editButton.gridx = 7;
        gbc_editButton.gridy = 4;
        add(editButton, gbc_editButton);

        removeButton = new JButton("-");
        removeButton.setPreferredSize(new Dimension(45, 23));
        removeButton.setToolTipText("Remove a triple");
        removeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        removeButton.addActionListener(e -> {
            int index = tbVariableColumn.getSelectedRow();

            selectQuery.remove(index + 1);

            System.out.println("Query size:" + selectQuery.size());

            variablesModel.removeRow(index);
        });

        GridBagConstraints gbc_removeButton = new GridBagConstraints();
        gbc_removeButton.anchor = GridBagConstraints.NORTH;
        gbc_removeButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_removeButton.insets = new Insets(0, 0, 5, 5);
        gbc_removeButton.gridx = 8;
        gbc_removeButton.gridy = 4;
        add(removeButton, gbc_removeButton);

        lblODBC = new JLabel("3 - Select ODBC:");
        lblODBC.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblODBC = new GridBagConstraints();
        gbc_lblODBC.anchor = GridBagConstraints.EAST;
        gbc_lblODBC.insets = new Insets(0, 0, 5, 5);
        gbc_lblODBC.gridx = 1;
        gbc_lblODBC.gridy = 6;
        add(lblODBC, gbc_lblODBC);


        comboBoxODBC = new JComboBox<String>();
        GridBagConstraints gbc_comboBoxODBC = new GridBagConstraints();
        gbc_comboBoxODBC.gridwidth = 3;
        gbc_comboBoxODBC.insets = new Insets(0, 0, 5, 5);
        gbc_comboBoxODBC.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBoxODBC.gridx = 2;
        gbc_comboBoxODBC.gridy = 6;
        refreshComboBox();
        comboBoxODBC.setSelectedIndex(-1);
        comboBoxODBC.addActionListener(e -> dbName = (String) comboBoxODBC.getSelectedItem());
        add(comboBoxODBC, gbc_comboBoxODBC);

        addODBCButton = new JButton("+");
        addODBCButton.setPreferredSize(new Dimension(45, 23));
        addODBCButton.setToolTipText("Add a database");
        addODBCButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        addODBCButton.addActionListener(e -> {
            ODBC database;
            do {
                databaseForm = new ODBC_Form();
                int ret = JOptionPaneEx.showConfirmDialog(addODBCButton, "Add a database", databaseForm, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
                if (ret == JOptionPane.OK_OPTION) {
                    database = databaseForm.getODBC();
                    System.out.println("Database name: " + database.getDatabaseName());
                    System.out.println("Database host:" + database.getHost());
                    System.out.println("Database port:" + database.getPort());
                    System.out.println("Database username: " + database.getUsername());
                    System.out.println("Database password:" + database.getPassword());
                } else
                    return;
            } while (database.getDatabaseName().equals("") || database.getHost().equals("") || database.getPort() == 0 || database.getUsername().equals("") || database.getPassword().equals(""));

            databases.putIfAbsent(database.getDatabaseName(), database);
            refreshComboBox();
        });

        GridBagConstraints gbc_addODBCButton = new GridBagConstraints();
        gbc_addODBCButton.anchor = GridBagConstraints.NORTH;
        gbc_addODBCButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_addODBCButton.insets = new Insets(0, 0, 5, 5);
        gbc_addODBCButton.gridx = 6;
        gbc_addODBCButton.gridy = 6;
        add(addODBCButton, gbc_addODBCButton);

        editDatabaseButton = new JButton("Edit");
        editDatabaseButton.setPreferredSize(new Dimension(55, 23));
        editDatabaseButton.setToolTipText("Edit a database");
        editDatabaseButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        editDatabaseButton.addActionListener(e -> {

            ODBC selected = databases.get(dbName);

            ODBC database = null;
            do {
                databaseForm = new ODBC_Form(selected);
                int ret = JOptionPaneEx.showConfirmDialog(editDatabaseButton, "Edit a database", databaseForm, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

                if (ret == JOptionPane.OK_OPTION) {
                    database = databaseForm.getODBC();
                    System.out.println("Database name: " + database.getDatabaseName());
                    System.out.println("Database host:" + database.getHost());
                    System.out.println("Database port:" + database.getPort());
                    System.out.println("Database username: " + database.getUsername());
                    System.out.println("Database password:" + database.getPassword());

                }
                if (ret == JOptionPane.CANCEL_OPTION || ret == JOptionPane.CLOSED_OPTION)
                    return;
            } while (database.getDatabaseName().equals("") || database.getHost().equals("") || database.getPort() == 0 || database.getUsername().equals("") || database.getPassword().equals(""));

            databases.remove(dbName);
            databases.putIfAbsent(database.getDatabaseName(), database);
            refreshComboBox();
        });

        GridBagConstraints gbc_editDatabaseButton = new GridBagConstraints();
        gbc_editDatabaseButton.anchor = GridBagConstraints.NORTH;
        gbc_editDatabaseButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_editDatabaseButton.insets = new Insets(0, 0, 5, 5);
        gbc_editDatabaseButton.gridx = 7;
        gbc_editDatabaseButton.gridy = 6;
        add(editDatabaseButton, gbc_editDatabaseButton);


        lblTableName = new JLabel("<html>4 - Enter <BR> table name: </BR> </html>");
        lblTableName.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblTableName.setToolTipText("Write the database table name");
        GridBagConstraints gbc_lblTableName = new GridBagConstraints();
        gbc_lblTableName.gridx = 1;
        gbc_lblTableName.gridy = 7;
        add(lblTableName, gbc_lblTableName);

        tableNameTextField = new JTextField(10);
        lblTableName.setLabelFor(tableNameTextField);
        GridBagConstraints gbc_tableNameTextField = new GridBagConstraints();
        gbc_tableNameTextField.anchor = GridBagConstraints.WEST;
        gbc_tableNameTextField.gridx = 2;
        gbc_tableNameTextField.gridy = 7;
        add(tableNameTextField, gbc_tableNameTextField);


        btnCheckSparql = new JButton("Create Query");
        btnCheckSparql.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldSPARQL.setText("");
                Map<String, String> prefixes = model.getNsPrefixMap();
                if (prefixes.size() > 0) {
                    prefixes.remove("");

                    for (Map.Entry<String, String> prefixEntry : prefixes.entrySet()) {
                        fieldSPARQL.append(PREFIX + prefixEntry.getKey() + ": " + LESSTHAN + prefixEntry.getValue() + MORETHAN + NEWLINE);
                    }

                    fieldSPARQL.append(NEWLINE);
                }
                fieldSPARQL.append(SQL_Query.getLabel(SQL_Query.SELECT));
                List<String> total = new ArrayList<>();
                for (List<String> lis : selectQuery.values()) {
                    total.addAll(lis);
                }
                for (String s : total) {
                    fieldSPARQL.append(s + " ");
                }
                fieldSPARQL.append(NEWLINE);
                fieldSPARQL.append(SQL_Query.getLabel(SQL_Query.WHERE));
                fieldSPARQL.append(NEWLINE);

                int counter = variablesModel.getRowCount();
                for (int i = 0; i < counter; i++) {
                    String subject = tbVariableColumn.getModel().getValueAt(i, 0).toString();
                    String predicate = tbVariableColumn.getModel().getValueAt(i, 1).toString();
                    String object = tbVariableColumn.getModel().getValueAt(i, 2).toString();

                    RDF_Tuple tmp = new RDF_TupleImp(subject, predicate, object);
                    if (!tmp.hasQuestionMark(subject))
                        tmp.setSubjectVariable();
                    if (!tmp.hasQuestionMark(object))
                        tmp.setObjectVariable();
                    fieldSPARQL.append(tmp.getSubject() + " " + tmp.getPredicate() + " " + tmp.getObject() + "." + NEWLINE);
                }
                fieldSPARQL.append(SQL_Query.getLabel(SQL_Query.RIGHTBRACKETS));
            }
        });

        GridBagConstraints gbc_btnCheckSparql = new GridBagConstraints();
        gbc_btnCheckSparql.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnCheckSparql.anchor = GridBagConstraints.NORTH;
        gbc_btnCheckSparql.gridwidth = 3;
        gbc_btnCheckSparql.insets = new Insets(0, 0, 5, 0);
        gbc_btnCheckSparql.gridx = 2;
        gbc_btnCheckSparql.gridy = 8;
        add(btnCheckSparql, gbc_btnCheckSparql);

        lblSPARQL = new JLabel("5 - SPARQL:");
        lblSPARQL.setToolTipText("This is were the SPARQL query result will show or where you can write your own");
        lblSPARQL.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblSql = new GridBagConstraints();
        gbc_lblSql.anchor = GridBagConstraints.NORTHEAST;
        gbc_lblSql.insets = new Insets(0, 0, 5, 5);
        gbc_lblSql.gridx = 1;
        gbc_lblSql.gridy = 10;
        add(lblSPARQL, gbc_lblSql);

        fieldSPARQL = new JTextArea();
        fieldSPARQL.setRows(6);
        fieldSPARQL.setFont(new Font("Tahoma", Font.PLAIN, 11));
        fieldSPARQL.setWrapStyleWord(true);
        fieldSPARQL.setLineWrap(true);
        fieldSPARQL.setEditable(false);

        JScrollPane scrollSPARQL = new JScrollPane(fieldSPARQL);
        scrollSPARQL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc_fieldSPARQL = new GridBagConstraints();
        gbc_fieldSPARQL.gridwidth = 3;
        gbc_fieldSPARQL.insets = new Insets(0, 0, 5, 5);
        gbc_fieldSPARQL.fill = GridBagConstraints.BOTH;
        gbc_fieldSPARQL.gridx = 2;
        gbc_fieldSPARQL.gridy = 10;
        add(scrollSPARQL, gbc_fieldSPARQL);

        btnEditSPARQL = new JButton("Write SPARQL");
        btnEditSPARQL.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(btnEditSPARQL, "Do you want to write your own SPARQL query?", "SPARQL Query Editor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {

                manualSparql = true;
                fieldSPARQL.setEditable(true);
                fieldSPARQL.setText("");
                Map<String, String> prefixes = model.getNsPrefixMap();
                if (prefixes.size() > 0) {
                    prefixes.remove("");

                    for (Map.Entry<String, String> prefixEntry : prefixes.entrySet()) {
                        fieldSPARQL.append(PREFIX + prefixEntry.getKey() + ": " + LESSTHAN + prefixEntry.getValue() + MORETHAN + NEWLINE);
                    }
                }
            }
        });

        GridBagConstraints gbc_btnEditSPARQL = new GridBagConstraints();
        gbc_btnEditSPARQL.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnEditSPARQL.anchor = GridBagConstraints.NORTH;
        gbc_btnEditSPARQL.gridwidth = 3;
        gbc_btnEditSPARQL.insets = new Insets(0, 0, 5, 0);
        gbc_btnEditSPARQL.gridx = 5;
        gbc_btnEditSPARQL.gridy = 10;
        add(btnEditSPARQL, gbc_btnEditSPARQL);

        createQueryButton = new JButton("Run");
        createQueryButton.addActionListener(e -> {

            if (!manualSparql) {
                if (variablesModel.getRowCount() == 0 || dbName.equals("") || tableNameTextField.getText().equals("") || !rdfFile.exists() || fieldSPARQL.getText().equals("")) {
                    JOptionPane.showMessageDialog(createQueryButton, "Please complete the form.");
                    return;
                }
            }

            manualSparql = false;
            System.out.println(fieldSPARQL.getText());

            rdfanswers.clear();
            Query query = QueryFactory.create(fieldSPARQL.getText());

            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect();
                ResultSetFormatter.out(System.out, results, query);
            }

            try (QueryExecution qexec2 = QueryExecutionFactory.create(query, model)) {
                Iterator<QuerySolution> results2 = qexec2.execSelect();
                for (; results2.hasNext(); ) {
                    QuerySolution sol = results2.next();
                    for (Iterator<String> it = sol.varNames(); it.hasNext(); ) {
                        String varname = it.next();
                        rdfanswers.putIfAbsent(varname, new LinkedList<>());
                        rdfanswers.get(varname).add(sol.get(varname).toString());
                    }
                }
            }

            ODBC database = databases.get(dbName);
            String tableName = tableNameTextField.getText();
            SQLTutorials.createDatabase(dbName, database, tableName, rdfanswers);
            JOptionPane.showMessageDialog(createQueryButton, "Query completed, check your database.", "Informative Message", JOptionPane.INFORMATION_MESSAGE);
        });

        GridBagConstraints gbc_createQueryButton = new GridBagConstraints();
        gbc_createQueryButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_createQueryButton.anchor = GridBagConstraints.NORTH;
        gbc_createQueryButton.gridwidth = 3;
        gbc_createQueryButton.insets = new Insets(0, 0, 5, 0);
        gbc_createQueryButton.gridx = 2;
        gbc_createQueryButton.gridy = 14;
        add(createQueryButton, gbc_createQueryButton);


        setSize(676, 624);
    }

    private void refreshComboBox() {
        comboBoxODBC.removeAllItems();

        for (Map.Entry<String, ODBC> d : databases.entrySet())
            comboBoxODBC.addItem(d.getKey());

        comboBoxODBC.setSelectedIndex(-1);
    }

    private void setRDFModel() {

        model = ModelFactory.createDefaultModel();
        model.read(rdfFile.getPath());


        if (model.hasNoMappings()) {
            System.out.println("No prefix");
            addPredicates();
        } else {
            System.out.println("Has prefix");
            addPredicatesWithPrefixes();
        }
    }

    private void addPredicatesWithPrefixes() {
        StmtIterator it = model.listStatements();
        predicates = new TreeSet<>();

        while (it.hasNext()) {
            Statement s = it.nextStatement();

            String namespace = s.getModel().getNsURIPrefix(s.getPredicate().getNameSpace());
            String localName = s.getPredicate().getLocalName();

            String string = namespace + ":" + localName;
            predicates.add(string);
        }

        int size = predicates.size();
        tableValues = new List[size];
        for (int i = 0; i < size; i++) {
            tableValues[i] = new ArrayList<>(3);
        }
    }

    private void addPredicates() {
        StmtIterator it = model.listStatements();
        predicates = new TreeSet<>();

        while (it.hasNext()) {
            Statement s = it.nextStatement();

            String predicate = s.getPredicate().toString();
            predicates.add(predicate);
        }
        int size = predicates.size();
        tableValues = new List[size];
        for (int i = 0; i < size; i++) {
            tableValues[i] = new ArrayList<>(3);
        }
    }

    public String getDbName() {
        return dbName;
    }

    public ODBC getDatabase(String databaseName) {
        return databases.get(databaseName);
    }

    public String getTableName() {
        return tableNameTextField.getText();
    }

    private boolean isInterfaceEmpty() {
        return rdfFileTextField.getText().equals("") && tableNameTextField.getText().equals("") && variablesModel.getRowCount() == 0;
    }

    private void saveODBC() {
        try {
            String homeFolder = System.getProperty("user.home");
            ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(new File(homeFolder + "\\Desktop", "databases.txt")));

            file.writeObject(databases);
            file.flush();
            file.close();
            System.out.println("File" + file.toString() + "was created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

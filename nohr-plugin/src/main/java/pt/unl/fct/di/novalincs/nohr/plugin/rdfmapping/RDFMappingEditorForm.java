package pt.unl.fct.di.novalincs.nohr.plugin.rdfmapping;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.RDFMapping;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.*;
import java.util.List;

//TODO finish this
public class RDFMappingEditorForm extends JPanel {

    private final DefaultTableModel predicatesModel;
    private final Vocabulary vocabulary;
    private File rdfFile;
    private JLabel lbl_Intro_label;
    private JLabel lbl_RDF_label;
    private JTextField rdfFileTextField;
    private JButton btnOpenFile;
    private JLabel lbl_predicateColumns;
    private JButton btnAddPredicate;
    private SortedMap<String, Set<String>> entries;
    private SortedMap<String, Set<String>> tableValues;
    private Model model;
    private JButton btnRemPredicate;
    private JLabel lblPredicate;
    private JTextField predicateTextField;
    private JLabel lblArity;
    private JSpinner spArity;
    private JSeparator separator;
    private JLabel lblODBC;
    private JComboBox comboBoxODBC;
    private JLabel lblSPARQL;
    private JTextArea fieldSPARQL;
    private JButton btnEditSPARQL;

    public RDFMappingEditorForm(Vocabulary vocabulary) {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = (int) screensize.getHeight();
        int maxWidth = (int) screensize.getWidth();
        int height = 500;
        int width = 690;
        setPreferredSize(new Dimension(width, height));
        setLocation(new Point(((maxWidth - width) / 3), (maxHeight - height) / 3));
        setAutoscrolls(true);
        this.vocabulary = vocabulary;
        predicatesModel = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        predicatesModel.addColumn("Namespace");
        predicatesModel.addColumn("Localname");

        load();
    }

    private void load() {
        GridBagLayout gridBagLayout = new GridBagLayout();

        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 0.25, 0.0, 0.0, 0.0,};
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
        btnOpenFile.setPreferredSize(new Dimension(35, 23));
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

        lbl_predicateColumns = new JLabel("2 - SELECT Predicates: ");
        lbl_predicateColumns.setFont(new Font("Tahoma", Font.BOLD, 11));
        lbl_predicateColumns.setToolTipText("Every predicate is made of two parts, the namespace prefix and the specific localname.");
        GridBagConstraints gbc_lbl_predicateColumns = new GridBagConstraints();
        gbc_lbl_predicateColumns.anchor = GridBagConstraints.NORTHEAST;
        gbc_lbl_predicateColumns.insets = new Insets(0, 0, 5, 5);
        gbc_lbl_predicateColumns.gridx = 1;
        gbc_lbl_predicateColumns.gridy = 4;
        add(lbl_predicateColumns, gbc_lbl_predicateColumns);

        final JTable tbpredicateColumns = new JTable(predicatesModel);
        tbpredicateColumns.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JScrollPane scrollPaneCol = new JScrollPane(tbpredicateColumns);
        scrollPaneCol.setPreferredSize(new Dimension(380, 83));
        scrollPaneCol.setMinimumSize(new Dimension(23, 83));
        scrollPaneCol.setMaximumSize(new Dimension(32767, 83));
        GridBagConstraints gbc_scrollPaneCol = new GridBagConstraints();
        gbc_scrollPaneCol.gridheight = 2;
        gbc_scrollPaneCol.gridwidth = 3;
        gbc_scrollPaneCol.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPaneCol.fill = GridBagConstraints.BOTH;
        gbc_scrollPaneCol.gridx = 2;
        gbc_scrollPaneCol.gridy = 4;
        add(scrollPaneCol, gbc_scrollPaneCol);

        btnAddPredicate = new JButton("+");
        btnAddPredicate.setPreferredSize(new Dimension(35, 23));
        btnAddPredicate.setToolTipText("Add predicate");
        btnAddPredicate.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnAddPredicate.addActionListener(e -> {
            Object[] objects = entries.keySet().toArray();
            if (objects.length == 0)
                JOptionPane.showMessageDialog(btnAddPredicate, "All predicates chosen");
            else {
                String result = (String) JOptionPane.showInputDialog(btnAddPredicate, "Choose a predicate", "Test", JOptionPane.QUESTION_MESSAGE, null, objects, objects[0]);

                System.out.println("The result " + result);

                if (result != null) {
                    Object[] options = entries.get(result).toArray();

                    String result_2 = (String) JOptionPane.showInputDialog(btnAddPredicate, "Choose the localname of the predicate", "Test2", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    Object[] finalResult = {result, result_2};
                    entries.get(result).remove(result_2);
                    System.out.println(entries.get(result));
                    if (entries.get(result).isEmpty())
                        entries.remove(result);
                    if (result_2 != null) {
                        predicatesModel.addRow(finalResult);
                        System.out.println("This is the final result to add" + finalResult);
                        tableValues.putIfAbsent(result, new HashSet<>());
                        tableValues.get(result).add(result_2);
                    }
                    System.out.println(result_2);
                }
            }
        });
        GridBagConstraints gbc_btnAddCol = new GridBagConstraints();
        gbc_btnAddCol.anchor = GridBagConstraints.NORTH;
        gbc_btnAddCol.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnAddCol.insets = new Insets(0, 0, 5, 5);
        gbc_btnAddCol.gridx = 6;
        gbc_btnAddCol.gridy = 4;
        add(btnAddPredicate, gbc_btnAddCol);

        btnRemPredicate = new JButton("-");
        btnRemPredicate.setPreferredSize(new Dimension(35, 23));
        btnRemPredicate.setToolTipText("Remove predicate");
        btnRemPredicate.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnRemPredicate.addActionListener(e -> {
            Object objects[] = tableValues.keySet().toArray();
            if (tableValues.isEmpty())
                JOptionPane.showMessageDialog(btnRemPredicate, "No predicates to remove");
            else {
                String toRemove = (String) JOptionPane.showInputDialog(btnRemPredicate, "Choose a predicate to remove", "Test", JOptionPane.QUESTION_MESSAGE, null, objects, objects[0]);
                if (toRemove != null) {
                    Object[] options = tableValues.get(toRemove).toArray();
                    String toRemove2 = (String) JOptionPane.showInputDialog(btnRemPredicate, "Choose localname to remove", "Test2", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    if (toRemove2 != null) {
                        Object[] finalResult = {toRemove, toRemove2};
                        System.out.println("Info is here " + predicatesModel.getDataVector());

                        Iterator t = predicatesModel.getDataVector().iterator();
                        while (t.hasNext()) {
                            Object result = t.next();
                            System.out.println("This is the result " + result.toString());
                            System.out.println("This is the final result " + Arrays.toString(finalResult));
                            if (result.toString().equals(Arrays.toString(finalResult))) {
                                int index = predicatesModel.getDataVector().indexOf(result);
                                System.out.println("This is the index: " + index);
                                predicatesModel.removeRow(index);
                                break;
                            }
                        }

                        System.out.println("Deleted information" + Arrays.toString(finalResult));
                        entries.putIfAbsent(toRemove, new HashSet<>());
                        entries.get(toRemove).add(toRemove2);
                        tableValues.get(toRemove).remove(toRemove2);
                        if (tableValues.get(toRemove).isEmpty()) {
                            tableValues.remove(toRemove);
                            System.out.println("Removed all entries from key " + toRemove);
                        }

                    }
                }
            }
        });
        GridBagConstraints gbc_btnRemCol = new GridBagConstraints();
        gbc_btnRemCol.anchor = GridBagConstraints.NORTH;
        gbc_btnRemCol.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnRemCol.insets = new Insets(0, 0, 5, 5);
        gbc_btnRemCol.gridx = 7;
        gbc_btnRemCol.gridy = 4;
        add(btnRemPredicate,gbc_btnRemCol);

        lblPredicate = new JLabel("3 - INTO Predicate:");
        lblPredicate.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblPredicate = new GridBagConstraints();
        gbc_lblPredicate.anchor = GridBagConstraints.EAST;
        gbc_lblPredicate.insets = new Insets(0, 0, 5, 5);
        gbc_lblPredicate.gridx = 1;
        gbc_lblPredicate.gridy = 6;
        add(lblPredicate, gbc_lblPredicate);

        predicateTextField = new JTextField();
        GridBagConstraints gbc_predicateTextField = new GridBagConstraints();
        gbc_predicateTextField.gridwidth = 3;
        gbc_predicateTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_predicateTextField.insets = new Insets(0, 0, 5, 5);
        gbc_predicateTextField.gridx = 2;
        gbc_predicateTextField.gridy = 6;
        add(predicateTextField, gbc_predicateTextField);
        predicateTextField.setColumns(20);
        predicateTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                predicateTextField.setText(predicateTextField.getText());
            }
        });

        lblArity = new JLabel("4 - Arity:");
        lblArity.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblArity = new GridBagConstraints();
        gbc_lblArity.anchor = GridBagConstraints.EAST;
        gbc_lblArity.insets = new Insets(0, 0, 5, 5);
        gbc_lblArity.gridx = 1;
        gbc_lblArity.gridy = 7;
        add(lblArity, gbc_lblArity);
        
        spArity = new JSpinner();
        spArity.setToolTipText("Define the arity of the predicate");
        spArity.setModel(new SpinnerNumberModel(0, 0, 99, 1));
        GridBagConstraints gbc_spArity = new GridBagConstraints();
        gbc_spArity.anchor = GridBagConstraints.WEST;
        gbc_spArity.insets = new Insets(0, 0, 5, 5);
        gbc_spArity.gridx = 2;
        gbc_spArity.gridy = 7;
        add(spArity, gbc_spArity);
        ((JSpinner.DefaultEditor) spArity.getEditor()).getTextField().setEditable(false);
        spArity.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblArity.setLabelFor(spArity);

        lblODBC = new JLabel("5 - Select ODBC:");
        lblODBC.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblODBC = new GridBagConstraints();
        gbc_lblODBC.anchor = GridBagConstraints.EAST;
        gbc_lblODBC.insets = new Insets(0, 0, 5, 5);
        gbc_lblODBC.gridx = 1;
        gbc_lblODBC.gridy = 8;
        add(lblODBC, gbc_lblODBC);

        comboBoxODBC = new JComboBox();
        GridBagConstraints gbc_comboBoxODBC = new GridBagConstraints();
        gbc_comboBoxODBC.gridwidth = 3;
        gbc_comboBoxODBC.insets = new Insets(0, 0, 5, 5);
        gbc_comboBoxODBC.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBoxODBC.gridx = 2;
        gbc_comboBoxODBC.gridy = 8;
        refreshComboBox();
        comboBoxODBC.setSelectedIndex(-1);
        comboBoxODBC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Select ODBC
            }
        });
        add(comboBoxODBC,gbc_comboBoxODBC);

        separator = new JSeparator();
        separator.setMinimumSize(new Dimension(0, 22));
        GridBagConstraints gbc_separator = new GridBagConstraints();
        gbc_separator.insets = new Insets(0, 0, 5, 5);
        gbc_separator.gridx = 2;
        gbc_separator.gridy = 9;
        add(separator, gbc_separator);

        lblSPARQL = new JLabel("6 - SPARQL:");
        lblSPARQL.setToolTipText("This is were the SPARQL query result will show or where you can write your own");
        lblSPARQL.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblSql = new GridBagConstraints();
        gbc_lblSql.anchor = GridBagConstraints.NORTHEAST;
        gbc_lblSql.insets = new Insets(0, 0, 5, 5);
        gbc_lblSql.gridx = 1;
        gbc_lblSql.gridy = 10;
        add(lblSPARQL, gbc_lblSql);

        fieldSPARQL = new JTextArea();
        fieldSPARQL.setRows(4);
        fieldSPARQL.setFont(new Font("Tahoma", Font.PLAIN, 11));
        fieldSPARQL.setWrapStyleWord(true);
        fieldSPARQL.setLineWrap(true);

        JScrollPane scrollSPARQL = new JScrollPane (fieldSPARQL);
        scrollSPARQL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc_fieldSPARQL = new GridBagConstraints();
        gbc_fieldSPARQL.gridwidth = 3;
        gbc_fieldSPARQL.insets = new Insets(0, 0, 5, 5);
        gbc_fieldSPARQL.fill = GridBagConstraints.BOTH;
        gbc_fieldSPARQL.gridx = 2;
        gbc_fieldSPARQL.gridy = 10;
        add(scrollSPARQL,gbc_fieldSPARQL);

        btnEditSPARQL = new JButton("Write SQL");
        btnEditSPARQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO SPARQL query manually use sparql code from protege
            }
        });

        GridBagConstraints gbc_btnEditSPARQL = new GridBagConstraints();
        gbc_btnEditSPARQL.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnEditSPARQL.anchor = GridBagConstraints.NORTH;
        gbc_btnEditSPARQL.gridwidth = 3;
        gbc_btnEditSPARQL.insets = new Insets(0, 0, 5, 0);
        gbc_btnEditSPARQL.gridx = 5;
        gbc_btnEditSPARQL.gridy = 10;
        add(btnEditSPARQL,gbc_btnEditSPARQL);

        setSize(676,624);
    }

    private void setRDFModel() {

        model = ModelFactory.createDefaultModel();
        model.read(rdfFile.getPath());
        StmtIterator it = model.listStatements();
        entries = new TreeMap<>();
        tableValues = new TreeMap<>();
        while (it.hasNext()) {
            Statement s = it.nextStatement();

            String namespace = s.getModel().getNsURIPrefix(s.getPredicate().getNameSpace());
            String localName = s.getPredicate().getLocalName();

            entries.putIfAbsent(namespace, new HashSet<>());
            entries.get(namespace).add(localName);
        }
    }
    private void refreshComboBox() {
        comboBoxODBC.removeAllItems();
        List<ODBCDriver> drivers = ODBCPreferences.getDrivers();
        for (ODBCDriver item : drivers) {
            comboBoxODBC.addItem(item);
        }
    }

    public void setMapping(RDFMapping rdfMapping) {
    }

    public RDFMapping getRDFMapping() {

        return null;
    }
}

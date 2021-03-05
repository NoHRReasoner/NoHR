package rdf_converter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTuple extends JPanel {


    private static final String HELP_MESSAGE = "A rdf triple is created from using 3 components: subject, predicate, object.\n " +
            "In this form, you need to specify the 3 components: \n" +
            "- The predicate can be select by using the dropdown slider and selecting the one you want.\n" +
            "- For the subject and the object, you can write a variable that you wish to use in the query with one important aspect. \n" +
            "- If you wish for the variable be to part of the select query you must put a ? before you write your variable. \n" +
            "Ex:\n subject: x \n predicate: foaf:name \n object: ?name \n" +
            "The respective query is: \n" +
            "select ?name \n" +
            "where { x foaf:name ?name }";

    private final JLabel lblIntroLabel;
    private final JLabel lbl_SubjectLabel;
    private final JTextField subjectTextField;
    private final JLabel predicateLabel;
    private final JComboBox<String> predicatesComboBox;
    private final JLabel objectLabel;
    private final JTextField objectTextField;
    private final JButton btnHelp;
    private String[] predicates;

    public AddTuple(String[] predicates) {
        this.predicates = predicates;

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0};

        setLayout(gridBagLayout);

        lblIntroLabel = new JLabel("Tuple creation: ");
        lblIntroLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblIntroLabel.setToolTipText("Create a tuple you want to use in a query search");
        GridBagConstraints gbc_lblIntroLabel = new GridBagConstraints();
        gbc_lblIntroLabel.anchor = GridBagConstraints.WEST;
        gbc_lblIntroLabel.gridwidth = 6;
        gbc_lblIntroLabel.insets = new Insets(0, 0, 5, 0);
        gbc_lblIntroLabel.gridx = 1;
        gbc_lblIntroLabel.gridy = 1;
        add(lblIntroLabel, gbc_lblIntroLabel);

        Icon c = new ImageIcon("images/information.png");
        btnHelp = new JButton(c);
        btnHelp.setPreferredSize(new Dimension(45, 23));
        btnHelp.setContentAreaFilled(false);
        btnHelp.setBorderPainted(false);
        btnHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(btnHelp, HELP_MESSAGE, "Help message", JOptionPane.QUESTION_MESSAGE);

            }
        });

        GridBagConstraints gbc_btnHelp = new GridBagConstraints();
        gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
        gbc_btnHelp.gridx = 3;
        gbc_btnHelp.gridy = 1;
        add(btnHelp, gbc_btnHelp);

        lbl_SubjectLabel = new JLabel("Subject");
        GridBagConstraints gbc_lbl_SubjectLabel = new GridBagConstraints();
        gbc_lbl_SubjectLabel.anchor = GridBagConstraints.EAST;
        gbc_lbl_SubjectLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lbl_SubjectLabel.gridx = 1;
        gbc_lbl_SubjectLabel.gridy = 3;
        add(lbl_SubjectLabel, gbc_lbl_SubjectLabel);

        subjectTextField = new JTextField();
        GridBagConstraints gbc_subjectTextField = new GridBagConstraints();
        gbc_subjectTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_subjectTextField.insets = new Insets(0, 0, 5, 5);
        gbc_subjectTextField.gridx = 3;
        gbc_subjectTextField.gridy = 3;
        add(subjectTextField, gbc_subjectTextField);

        predicateLabel = new JLabel("Predicate: ");
        GridBagConstraints gbc_predicateLabel = new GridBagConstraints();
        gbc_predicateLabel.anchor = GridBagConstraints.EAST;
        gbc_predicateLabel.insets = new Insets(0, 0, 5, 5);
        gbc_predicateLabel.gridx = 1;
        gbc_predicateLabel.gridy = 5;
        add(predicateLabel, gbc_predicateLabel);

        predicatesComboBox = new JComboBox<>();
        refreshComboBox();
        predicatesComboBox.setSelectedIndex(-1);
        GridBagConstraints gbc_predicatesComboBox = new GridBagConstraints();
        gbc_predicatesComboBox.insets = new Insets(0, 0, 5, 5);
        gbc_predicatesComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_predicatesComboBox.gridx = 3;
        gbc_predicatesComboBox.gridy = 5;
        add(predicatesComboBox, gbc_predicatesComboBox);

        objectLabel = new JLabel("Object");
        GridBagConstraints gbc_objectLabel = new GridBagConstraints();
        gbc_objectLabel.anchor = GridBagConstraints.EAST;
        gbc_objectLabel.insets = new Insets(0, 0, 5, 5);
        gbc_objectLabel.gridx = 1;
        gbc_objectLabel.gridy = 7;
        add(objectLabel, gbc_objectLabel);

        objectTextField = new JTextField();
        GridBagConstraints gbc_objectTextField = new GridBagConstraints();
        gbc_objectTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_objectTextField.insets = new Insets(0, 0, 5, 5);
        gbc_objectTextField.gridx = 3;
        gbc_objectTextField.gridy = 7;
        add(objectTextField, gbc_objectTextField);

    }


    public RDF_Tuple getTuple() {
        String subject = subjectTextField.getText();
        String predicate = (String) predicatesComboBox.getSelectedItem();
        String object = objectTextField.getText();

        RDF_Tuple tuple = new RDF_TupleImp(subject, predicate, object);

        return tuple;
    }

    public void editTuple(RDF_Tuple rdf) {
        subjectTextField.setText(rdf.getSubject());
        objectTextField.setText(rdf.getObject());
        predicatesComboBox.setSelectedItem(rdf.getPredicate());
    }

    private void refreshComboBox() {
        predicatesComboBox.removeAllItems();
        for (String item : predicates)
            predicatesComboBox.addItem(item);
    }


}

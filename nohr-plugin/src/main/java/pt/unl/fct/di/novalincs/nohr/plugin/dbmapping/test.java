package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import javax.swing.*;
/*
 * Created by JFormDesigner on Fri Aug 04 07:33:06 BST 2017
 */



/**
 * @author Vedran Kasalica
 */
public class test extends JPanel {
	public test() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Vedran Kasalica
		label2 = new JLabel();
		textField1 = new JTextField();
		label1 = new JLabel();
		textField2 = new JTextField();
		label3 = new JLabel();
		textField3 = new JTextField();
		button1 = new JButton();
		button2 = new JButton();
		button3 = new JButton();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


		//---- label2 ----
		label2.setText("Table:");
		add(label2, "cell 0 1");
		add(textField1, "cell 1 1 3 1");

		//---- label1 ----
		label1.setText("Columns:");
		add(label1, "cell 0 2");
		add(textField2, "cell 1 2 3 1");

		//---- label3 ----
		label3.setText("Predicate:");
		add(label3, "cell 0 3");
		add(textField3, "cell 1 3 3 1");

		//---- button1 ----
		button1.setText("Add");
		add(button1, "cell 1 5");

		//---- button2 ----
		button2.setText("Clear");
		add(button2, "cell 2 5");

		//---- button3 ----
		button3.setText("Cancel");
		add(button3, "cell 3 5");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Vedran Kasalica
	private JLabel label2;
	private JTextField textField1;
	private JLabel label1;
	private JTextField textField2;
	private JLabel label3;
	private JTextField textField3;
	private JButton button1;
	private JButton button2;
	private JButton button3;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}

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

public class DBMappingAddColumnForm extends JPanel {
	/**
	 * The class is used to define the view of a single database mapping
	 * (create/edit).
	 * 
	 *
	 * @author Vedran Kasalica
	 */
	private static final long serialVersionUID = -5207499377408633751L;
	private JTextField nameTxtFeild;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JRadioButton rNo;
	private JRadioButton rYes;
	private JLabel lblJoin;

	/**
	 * Create the frame.
	 */
	public DBMappingAddColumnForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0 };
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

		lblNewLabel = new JLabel("Name:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel);
		
				nameTxtFeild = new JTextField();
				GridBagConstraints gbc_nameTxtFeild = new GridBagConstraints();
				gbc_nameTxtFeild.gridwidth = 5;
				gbc_nameTxtFeild.fill = GridBagConstraints.HORIZONTAL;
				gbc_nameTxtFeild.insets = new Insets(0, 0, 5, 5);
				gbc_nameTxtFeild.gridx = 3;
				gbc_nameTxtFeild.gridy = 3;
				GhostText ghostText = new GhostText(nameTxtFeild, "Enter column name...");
				add(nameTxtFeild, gbc_nameTxtFeild);
				nameTxtFeild.setColumns(20);
		
		lblJoin = new JLabel("Floating point:");
		lblJoin.setToolTipText("Not NULL and floating point representation");
		GridBagConstraints gbc_lblJoin = new GridBagConstraints();
		gbc_lblJoin.anchor = GridBagConstraints.EAST;
		gbc_lblJoin.insets = new Insets(0, 0, 5, 5);
		gbc_lblJoin.gridx = 1;
		gbc_lblJoin.gridy = 4;
		add(lblJoin, gbc_lblJoin);
		
		rNo = new JRadioButton("No");
		rNo.setToolTipText("Data will not be manipulated");
		rNo.setSelected(true);
		GridBagConstraints gbc_rNo = new GridBagConstraints();
		gbc_rNo.insets = new Insets(0, 0, 5, 5);
		gbc_rNo.gridx = 3;
		gbc_rNo.gridy = 4;
		add(rNo, gbc_rNo);
						
						rYes = new JRadioButton("Yes");
						rYes.setToolTipText("Number will be rounded down in order to be unifiable");
						GridBagConstraints gbc_rYes = new GridBagConstraints();
						gbc_rYes.insets = new Insets(0, 0, 5, 5);
						gbc_rYes.gridx = 4;
						gbc_rYes.gridy = 4;
						add(rYes, gbc_rYes);
						
						ButtonGroup group = new ButtonGroup();
				        group.add(rNo);
				        group.add(rYes);

	}


	public String getColumn() {

		return nameTxtFeild.getText();
	}
	
	public void setColumn(String column) {

		nameTxtFeild.setText(column);
	}
}

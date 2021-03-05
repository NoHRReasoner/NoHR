package pt.unl.fct.di.novalincs.nohr.plugin.odbc;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import pt.unl.fct.di.novalincs.nohr.model.DatabaseType;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriverImpl;

import java.awt.Insets;
import java.util.List;

import javax.swing.JPasswordField;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;

public class ODBCDriverEditForm extends JPanel {
	private final String odbcID;
	private JTextField textFieldODBCName;
	private JTextField textFieldDBName;
	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private JLabel lblPassword;
	private JLabel lblDatabaseType;
	private JComboBox<DatabaseType> comboBoxDBType;
	private JLabel lblSpecifyTheOdbc;
	private JLabel label;
	private JLabel lblNoteTheOdbc;

	/**
	 * Create the panel.
	 */
	
	
	public ODBCDriverEditForm() {
		odbcID="ODBCDriverEditForm";
		initialize();
	}
	
	public ODBCDriverEditForm(ODBCDriver driver) {
		initialize();
		this.odbcID=driver.getOdbcID();
		this.textFieldODBCName.setText(driver.getConectionName());
		this.textFieldUsername.setText(driver.getUsername());
		this.passwordField.setText(driver.getPassword());
		this.textFieldDBName.setText(driver.getDatabaseName());
		this.comboBoxDBType.setSelectedItem(driver.getDatabaseType());
	}
	
	private void initialize(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 0;
		add(label, gbc_label);
		
		lblSpecifyTheOdbc = new JLabel("Specify the ODBC Drivier:");
		GridBagConstraints gbc_lblSpecifyTheOdbc = new GridBagConstraints();
		gbc_lblSpecifyTheOdbc.gridheight = 2;
		gbc_lblSpecifyTheOdbc.fill = GridBagConstraints.VERTICAL;
		gbc_lblSpecifyTheOdbc.anchor = GridBagConstraints.WEST;
		gbc_lblSpecifyTheOdbc.gridwidth = 2;
		gbc_lblSpecifyTheOdbc.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpecifyTheOdbc.gridx = 1;
		gbc_lblSpecifyTheOdbc.gridy = 1;
		add(lblSpecifyTheOdbc, gbc_lblSpecifyTheOdbc);
		
		JLabel lblOdbcDriverUnique = new JLabel("ODBC driver unique name:");
		GridBagConstraints gbc_lblOdbcDriverUnique = new GridBagConstraints();
		gbc_lblOdbcDriverUnique.insets = new Insets(0, 0, 5, 5);
		gbc_lblOdbcDriverUnique.gridx = 2;
		gbc_lblOdbcDriverUnique.gridy = 3;
		add(lblOdbcDriverUnique, gbc_lblOdbcDriverUnique);
		
		textFieldODBCName = new JTextField();
		lblOdbcDriverUnique.setLabelFor(textFieldODBCName);
		GridBagConstraints gbc_textFieldODBCName = new GridBagConstraints();
		gbc_textFieldODBCName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldODBCName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldODBCName.gridx = 4;
		gbc_textFieldODBCName.gridy = 3;
		add(textFieldODBCName, gbc_textFieldODBCName);
		textFieldODBCName.setColumns(10);
		
		JLabel lblDatabaseName = new JLabel("Database name:");
		GridBagConstraints gbc_lblDatabaseName = new GridBagConstraints();
		gbc_lblDatabaseName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseName.gridx = 2;
		gbc_lblDatabaseName.gridy = 4;
		add(lblDatabaseName, gbc_lblDatabaseName);
		
		textFieldDBName = new JTextField();
		lblDatabaseName.setLabelFor(textFieldDBName);
		GridBagConstraints gbc_textFieldDBName = new GridBagConstraints();
		gbc_textFieldDBName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldDBName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldDBName.gridx = 4;
		gbc_textFieldDBName.gridy = 4;
		add(textFieldDBName, gbc_textFieldDBName);
		textFieldDBName.setColumns(10);
		
		lblDatabaseType = new JLabel("Database Type:");
		GridBagConstraints gbc_lblDatabaseType = new GridBagConstraints();
		gbc_lblDatabaseType.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseType.gridx = 2;
		gbc_lblDatabaseType.gridy = 5;
		add(lblDatabaseType, gbc_lblDatabaseType);
		
		comboBoxDBType = new JComboBox<DatabaseType>();
		lblDatabaseType.setLabelFor(comboBoxDBType);
		GridBagConstraints gbc_comboBoxDBType = new GridBagConstraints();
		gbc_comboBoxDBType.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxDBType.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxDBType.gridx = 4;
		gbc_comboBoxDBType.gridy = 5;
		for(DatabaseType dbType : DatabaseType.getDBTypes()){
			comboBoxDBType.addItem(dbType);
		}
		add(comboBoxDBType, gbc_comboBoxDBType);
		
		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 2;
		gbc_lblUsername.gridy = 6;
		add(lblUsername, gbc_lblUsername);
		
		textFieldUsername = new JTextField();
		lblUsername.setLabelFor(textFieldUsername);
		GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
		gbc_textFieldUsername.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUsername.gridx = 4;
		gbc_textFieldUsername.gridy = 6;
		add(textFieldUsername, gbc_textFieldUsername);
		textFieldUsername.setColumns(10);
		
		lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 2;
		gbc_lblPassword.gridy = 7;
		add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		lblPassword.setLabelFor(passwordField);
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 4;
		gbc_passwordField.gridy = 7;
		add(passwordField, gbc_passwordField);
		
		lblNoteTheOdbc = new JLabel("<html><b>Note:</b><br> The ODBC Driver needs to be avaliable, i.e. it has an entry in \"odbc.ini\" file.<br> This can be checked by running Microsoft ODBC Administrator or similar tool, depending on the operating sistem.</html>");
		GridBagConstraints gbc_lblNoteTheOdbc = new GridBagConstraints();
		gbc_lblNoteTheOdbc.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNoteTheOdbc.gridwidth = 4;
		gbc_lblNoteTheOdbc.insets = new Insets(0, 0, 0, 5);
		gbc_lblNoteTheOdbc.gridx = 2;
		gbc_lblNoteTheOdbc.gridy = 9;
		add(lblNoteTheOdbc, gbc_lblNoteTheOdbc);
		
		
	}

	
	public void setODBCName(String string) {
		textFieldODBCName.setText(string);

	}
	
	public String getODBCName() {
		return textFieldODBCName.getText();

	}
	
	public void setDBName(String string) {
		textFieldDBName.setText(string);

	}
	
	public String getDBName() {
		return textFieldDBName.getText();

	}
	
	public void setUsername(String string) {
		textFieldUsername.setText(string);

	}
	
	public String getUsername() {
		System.out.println("test:"+textFieldUsername.getText());
		return textFieldUsername.getText();

	}
	
	public void setPassword(String string) {
		passwordField.setText(string);

	}
	
	public String getPassword() {
		return new String(passwordField.getPassword());

	}
	
	public void setDBType(DatabaseType dbType) {
		comboBoxDBType.setSelectedItem(dbType);

	}
	
	public DatabaseType getDBType() {
		return (DatabaseType) comboBoxDBType.getSelectedItem();

	}
	
	
	public ODBCDriver getODBCDriver(){
		ODBCDriver tmp=new ODBCDriverImpl(odbcID, getODBCName(), getUsername(), getPassword(), getDBName(), getDBType());
		return tmp;
	}

}

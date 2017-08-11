package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import pt.unl.fct.di.novalincs.nohr.model.DatabaseType;

import java.awt.Insets;
import java.util.List;

import javax.swing.JPasswordField;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class ODBCDriverEditForm extends JPanel {
	private JTextField textFieldName;
	private JTextField textFieldDBName;
	private JTextField textFieldUser;
	private JPasswordField passwordField;
	private JLabel lblPassword;
	private JLabel lblDatabaseType;
	private JComboBox<DatabaseType> comboBoxDBType;
	private List<DatabaseType> drivers;

	/**
	 * Create the panel.
	 */
	public ODBCDriverEditForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblOdbcDriverUnique = new JLabel("ODBC driver unique name:");
		GridBagConstraints gbc_lblOdbcDriverUnique = new GridBagConstraints();
		gbc_lblOdbcDriverUnique.insets = new Insets(0, 0, 5, 5);
		gbc_lblOdbcDriverUnique.gridx = 1;
		gbc_lblOdbcDriverUnique.gridy = 1;
		add(lblOdbcDriverUnique, gbc_lblOdbcDriverUnique);
		
		textFieldName = new JTextField();
		GridBagConstraints gbc_textFieldName = new GridBagConstraints();
		gbc_textFieldName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldName.gridx = 3;
		gbc_textFieldName.gridy = 1;
		add(textFieldName, gbc_textFieldName);
		textFieldName.setColumns(10);
		
		JLabel lblDatabaseName = new JLabel("Database name:");
		GridBagConstraints gbc_lblDatabaseName = new GridBagConstraints();
		gbc_lblDatabaseName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseName.gridx = 1;
		gbc_lblDatabaseName.gridy = 2;
		add(lblDatabaseName, gbc_lblDatabaseName);
		
		textFieldDBName = new JTextField();
		GridBagConstraints gbc_textFieldDBName = new GridBagConstraints();
		gbc_textFieldDBName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldDBName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldDBName.gridx = 3;
		gbc_textFieldDBName.gridy = 2;
		add(textFieldDBName, gbc_textFieldDBName);
		textFieldDBName.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 3;
		add(lblUsername, gbc_lblUsername);
		
		textFieldUser = new JTextField();
		GridBagConstraints gbc_textFieldUser = new GridBagConstraints();
		gbc_textFieldUser.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUser.gridx = 3;
		gbc_textFieldUser.gridy = 3;
		add(textFieldUser, gbc_textFieldUser);
		textFieldUser.setColumns(10);
		
		lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 4;
		add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 3;
		gbc_passwordField.gridy = 4;
		add(passwordField, gbc_passwordField);
		
		lblDatabaseType = new JLabel("Database Type:");
		GridBagConstraints gbc_lblDatabaseType = new GridBagConstraints();
		gbc_lblDatabaseType.insets = new Insets(0, 0, 0, 5);
		gbc_lblDatabaseType.gridx = 1;
		gbc_lblDatabaseType.gridy = 5;
		add(lblDatabaseType, gbc_lblDatabaseType);
		
		comboBoxDBType = new JComboBox<DatabaseType>();
		GridBagConstraints gbc_comboBoxDBType = new GridBagConstraints();
		gbc_comboBoxDBType.insets = new Insets(0, 0, 0, 5);
		gbc_comboBoxDBType.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxDBType.gridx = 3;
		gbc_comboBoxDBType.gridy = 5;
		for(DatabaseType dbType : DatabaseType.getDBTypes()){
			comboBoxDBType.addItem(dbType);
		}
		add(comboBoxDBType, gbc_comboBoxDBType);
		
	}

	
	public void setODBCName(String string) {
		textFieldName.setText(string);

	}
	
	public String getODBCName() {
		return textFieldName.getText();

	}
	
	public void setDBName(String string) {
		textFieldDBName.setText(string);

	}
	
	public String getDBName() {
		return textFieldDBName.getText();

	}
	
	public void setUsername(String string) {
		textFieldUser.setText(string);

	}
	
	public String getUsername() {
		return textFieldUser.getText();

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
	

}

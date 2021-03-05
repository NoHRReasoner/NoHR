package rdf_converter;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ODBC_Form extends JPanel {

    private JLabel introLabel;
    private JLabel lblDatabaseName;
    private JTextField databaseName_TextField;
    private JLabel lblHostName;
    private JTextField hostNameTextField;
    private JLabel lblPort;
    private JTextField portTextField;
    private JLabel lblUsername;
    private JTextField usernameTextField;
    private JLabel lblPassword;
    private JPasswordField passwordTextField;

    public ODBC_Form() {
        initialise();
    }

    public ODBC_Form(ODBC selected) {
        initialise();
        databaseName_TextField.setText(selected.getDatabaseName());
        hostNameTextField.setText(selected.getHost());
        portTextField.setText(String.valueOf(selected.getPort()));
        usernameTextField.setText(selected.getUsername());
        passwordTextField.setText(selected.getPassword());
    }

    private void initialise() {

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);


        introLabel = new JLabel("Enter database info:");
        introLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        introLabel.setToolTipText("Enter all the info needed to estabilish a MySQL connection");
        introLabel.setBackground(Color.WHITE);
        GridBagConstraints gbc_introLabel = new GridBagConstraints();
        gbc_introLabel.insets = new Insets(0, 0, 5, 5);
        gbc_introLabel.gridx = 1;
        gbc_introLabel.gridy = 1;
        add(introLabel, gbc_introLabel);

        lblDatabaseName = new JLabel("Enter database schema name:");
        lblDatabaseName.setFont(new Font("Tahoma", Font.PLAIN, 12));
        introLabel.setBackground(Color.WHITE);
        GridBagConstraints gbc_lblDatabaseName = new GridBagConstraints();
        gbc_lblDatabaseName.insets = new Insets(0, 0, 5, 5);
        gbc_lblDatabaseName.gridx = 1;
        gbc_lblDatabaseName.gridy = 2;
        add(lblDatabaseName, gbc_lblDatabaseName);

        databaseName_TextField = new JTextField(10);
        lblDatabaseName.setLabelFor(databaseName_TextField);
        GridBagConstraints gbc_databaseTextField = new GridBagConstraints();
        gbc_databaseTextField.insets = new Insets(0, 0, 5, 5);
        gbc_databaseTextField.gridx = 2;
        gbc_databaseTextField.gridy = 2;
        add(databaseName_TextField, gbc_databaseTextField);

        lblHostName = new JLabel("Enter Host Name:");
        lblHostName.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblHostName.setBackground(Color.WHITE);
        lblHostName.setToolTipText("Enter the MySQL host name that is in your database, the default is localhost or 127.0.0.1");
        GridBagConstraints gbc_lblHostName = new GridBagConstraints();
        gbc_lblHostName.insets = new Insets(0, 0, 5, 5);
        gbc_lblHostName.gridx = 1;
        gbc_lblHostName.gridy = 3;
        add(lblHostName, gbc_lblHostName);

        hostNameTextField = new JTextField(10);
        lblHostName.setLabelFor(hostNameTextField);
        GridBagConstraints gbc_hostNameTextField = new GridBagConstraints();
        gbc_hostNameTextField.insets = new Insets(0, 0, 5, 5);
        gbc_hostNameTextField.gridx = 2;
        gbc_hostNameTextField.gridy = 3;
        add(hostNameTextField, gbc_hostNameTextField);

        lblPort = new JLabel("Enter Port:");
        lblPort.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblPort.setBackground(Color.WHITE);
        lblPort.setToolTipText("Enter the MySQL port that is in your database, the default is 3306");
        GridBagConstraints gbc_lblPort = new GridBagConstraints();
        gbc_lblPort.insets = new Insets(0, 0, 5, 5);
        gbc_lblPort.gridx = 1;
        gbc_lblPort.gridy = 4;
        add(lblPort, gbc_lblPort);

        portTextField = new JTextField(10);
        lblPort.setLabelFor(portTextField);
        portTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isaNumber(e) || onlyAllowedKeys(e)) {
                    portTextField.setEditable(true);
                } else
                    portTextField.setEditable(false);
            }
        });
        GridBagConstraints gbc_portTextField = new GridBagConstraints();
        gbc_portTextField.insets = new Insets(0, 0, 5, 5);
        gbc_portTextField.gridx = 2;
        gbc_portTextField.gridy = 4;
        add(portTextField, gbc_portTextField);

        lblUsername = new JLabel("Enter Username: ");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblUsername.setBackground(Color.WHITE);
        lblUsername.setToolTipText("Enter the username allowed in your database.");
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.gridx = 1;
        gbc_lblUsername.gridy = 5;
        add(lblUsername, gbc_lblUsername);

        usernameTextField = new JTextField(10);
        lblUsername.setLabelFor(usernameTextField);
        GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
        gbc_usernameTextField.insets = new Insets(0, 0, 5, 5);
        gbc_usernameTextField.gridx = 2;
        gbc_usernameTextField.gridy = 5;
        add(usernameTextField, gbc_usernameTextField);

        lblPassword = new JLabel("Enter Password: ");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblPassword.setBackground(Color.WHITE);
        lblPassword.setToolTipText("Enter the password of the user allowed in your database.");
        GridBagConstraints gbc_lblPassword = new GridBagConstraints();
        gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
        gbc_lblPassword.gridx = 1;
        gbc_lblPassword.gridy = 6;
        add(lblPassword, gbc_lblPassword);

        passwordTextField = new JPasswordField(10);
        lblPassword.setLabelFor(passwordTextField);
        GridBagConstraints gbc_passwordTextField = new GridBagConstraints();
        gbc_passwordTextField.insets = new Insets(0, 0, 5, 5);
        gbc_passwordTextField.gridx = 2;
        gbc_passwordTextField.gridy = 6;
        add(passwordTextField, gbc_passwordTextField);


    }

    public ODBC getODBC() {
        ODBC tmp = new ODBCImpl(getDatabaseName(), getHostName(), getPort(), getUsername(), getPassword());

        return tmp;
    }

    @NotNull
    private String getPassword() {
        return new String(passwordTextField.getPassword());
    }

    private String getUsername() {
        return usernameTextField.getText();
    }

    private int getPort() {
        String text = portTextField.getText();

        return text.equals("") ? 0 : Integer.parseInt(text);
    }

    private String getHostName() {
        return hostNameTextField.getText();
    }

    public String getDatabaseName() {
        return databaseName_TextField.getText();
    }

    private boolean isaNumber(KeyEvent e) {
        return e.getKeyChar() >= '0' && e.getKeyChar() <= '9';
    }

    private boolean onlyAllowedKeys(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT;
    }
}

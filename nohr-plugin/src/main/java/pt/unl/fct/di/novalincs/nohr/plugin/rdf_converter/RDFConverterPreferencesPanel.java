package pt.unl.fct.di.novalincs.nohr.plugin.rdf_converter;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.plugin.NoHRPreferences;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * The class is used to define the panel for the RDF Converter. It represents a new tab in the preferences
 *
 * @author Bruno Castelo
 */
public class RDFConverterPreferencesPanel extends OWLPreferencesPanel {

    private static final Dimension MAX_HEIGHT_DIMENSION = new Dimension(Integer.MAX_VALUE, 1);

    private JTextField address;
    private JTextField port;
    private File toconvertFile;
    private JTextField toconvertFileTextField;
    private JComboBox<ODBCDriver> drivers;
    private int selectedDriver;
    private JButton convert;
    private NoHRPreferences instance;


    public RDFConverterPreferencesPanel() {
        toconvertFileTextField = new JTextField(10);
        toconvertFileTextField.setEditable(false);
        drivers = new JComboBox(ODBCPreferences.getDrivers().toArray());
        selectedDriver = -1;
        address = new JTextField(10);
        port = new JTextField(10);
        convert = new JButton("Convert");
        instance = NoHRPreferences.getInstance();
    }

    @Override
    public void applyChanges() {

    }

    @Override
    public void initialise() {

        setPreferredSize(new Dimension(620, 300));
        setLayout(new GridBagLayout());
        GridBagConstraints listConstraints = new GridBagConstraints();
        listConstraints.fill = GridBagConstraints.BOTH;
        listConstraints.gridx = 0;
        listConstraints.gridy = 0;
        listConstraints.gridwidth = 3;
        listConstraints.gridheight = 3;
        listConstraints.weightx = 1;
        listConstraints.weighty = 1;
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.gridx = 2;
        buttonsConstraints.gridy = 4;
        drivers.setSelectedIndex(selectedDriver);
        add(createTable(), listConstraints);
        add(createConvertButton(), buttonsConstraints);

    }

    private boolean showConvertButton() {
        return (toconvertFile != null) && (selectedDriver != -1) && (!address.getText().equals("")) && (getPort()!=0);
    }
    private int getPort() {
        String text = port.getText();

        return text.equals("") ? 0 : Integer.parseInt(text);
    }
    private JComponent createConvertButton() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        Boolean show = showConvertButton();
        panel.add(convert);
        convert.setEnabled(show);
        convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ODBCDriver chosen = getDriver(selectedDriver);
                System.out.println(getConverterCommand(chosen,address,port));
                CommandLine cmd = CommandLine.parse(getConverterCommand(chosen, address, port));

                DefaultExecutor executor = new DefaultExecutor();

                int exitValue = 0;
                try {
                    exitValue = executor.execute(cmd);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (exitValue == 0)
                    JOptionPane.showMessageDialog(null, "OK");

            }
        });

        return panel;
    }

    private JComponent createTable() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(new JLabel("Choose RDF File"));
        panel.add(toconvertFileTextField);
        panel.add(createChooseRDFFileButton());

        panel.add(new JLabel("Choose Database"));
        panel.add(drivers);
        drivers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDriver = drivers.getSelectedIndex();
                convert.setEnabled(showConvertButton());
            }
        });
        panel.add(new JLabel("Enter host address"));
        panel.add(address);
        address.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convert.setEnabled(showConvertButton());
            }
        });
        panel.add(new JLabel("Enter Port"));
        panel.add(port);
        port.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isaNumber(e) || onlyAllowedKeys(e)) {
                    port.setEditable(true);
                    convert.setEnabled(showConvertButton());
                } else
                    port.setEditable(false);
            }
        });

        return panel;
    }

    private boolean isaNumber(KeyEvent e) {
        return e.getKeyChar() >= '0' && e.getKeyChar() <= '9';
    }

    private boolean onlyAllowedKeys(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT;
    }

    /*
    String fullcmdLine  = start + directory + and + maven + inputCommand
                + inputFile + dbcommand + dburlcommand +
                connectionName + connectionNameNecessity + dbuser + username + dbpassword + password;
    * */

    private String getConverterCommand(ODBCDriver o, JTextField address, JTextField port) {
        return Commands.START.label + instance.getRDF2xDirectory() + " " + Commands.AND.label + Commands.MAVEN.label + Commands.INPUTCOMMAND.label +
                toconvertFile.getAbsolutePath() + " " + Commands.DBCOMMAND.label + Commands.DBURLCOMMAND.label + Commands.CONNECTIONURL.label + address.getText()+ ":" + Integer.parseInt(port.getText()) + "/" +  o.getDatabaseName() + Commands.CONNECTIONURLNECESSITY.label +
                Commands.DBUSER.label + o.getUsername() + " " + Commands.DBPASSWORD.label + o.getPassword() + "\"";
    }

    private ODBCDriver getDriver(int index) {
        ODBCDriver driver = ODBCPreferences.getDrivers().get(index);

        return driver;
    }


    private JButton createChooseRDFFileButton() {
        final JButton result = new JButton("Open...");

        result.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("RDF files", "rdf", "ttl", "nt", "n3", "nq");

                fc.setFileFilter(filter);

                final int returnVal = fc.showOpenDialog(RDFConverterPreferencesPanel.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setFileLocation(fc.getSelectedFile());
                    convert.setEnabled(showConvertButton());
                }
            }
        });
        return result;
    }

    private void setFileLocation(File value) {
        toconvertFile = value;
        toconvertFileTextField.setText(toconvertFile.getPath());
        System.out.println("This is convertFile " + toconvertFile);
    }


    @Override
    public void dispose() throws Exception {

    }
}

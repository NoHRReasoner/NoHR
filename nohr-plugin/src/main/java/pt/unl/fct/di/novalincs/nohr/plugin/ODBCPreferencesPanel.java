package pt.unl.fct.di.novalincs.nohr.plugin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriverImpl;
import pt.unl.fct.di.novalincs.nohr.plugin.dbmapping.ODBCDriverEditForm;
import pt.unl.fct.di.novalincs.nohr.plugin.dbmapping.ODBCTableView;

/**
 *  The class is used to define the panel for ODBC drivers. It represents new tab in preferences.
 * 
 *
 * @author Vedran Kasalica
 */
public class ODBCPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = 6095681530689446528L;

	private static final Dimension MAX_HEIGHT_DIMENSION = new Dimension(Integer.MAX_VALUE, 1);


    private JTable table;
    private ODBCTableView driverTableModel;

    public ODBCPreferencesPanel() {
    }


    

    @Override
    public void dispose() throws Exception {
    }

    @Override
    public void initialise() throws Exception {

        
         setPreferredSize(new java.awt.Dimension(620, 300));
         setLayout(new GridBagLayout());
         GridBagConstraints listConstraints = new GridBagConstraints();
         listConstraints.fill = GridBagConstraints.BOTH;
         listConstraints.gridx = 0;
         listConstraints.gridy = 0;
         listConstraints.gridwidth = 3;
         listConstraints.gridheight = 3;
         listConstraints.weightx=1;
         listConstraints.weighty=1;
         GridBagConstraints buttonsConstraints = new GridBagConstraints();
         buttonsConstraints.gridx = 0;
         buttonsConstraints.gridy = 4;
         add(createList(), listConstraints);
         add(createButtons(), buttonsConstraints);
    	
    	
    }
    
    private JComponent createList() {
    	driverTableModel = new ODBCTableView(ODBCPreferences.getDrivers());
        table = new JTable(driverTableModel);
        return new JScrollPane(table);
    }
    
    private JComponent createButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JButton add = new JButton("Add");
        add.addActionListener(new ActionListener() { 
        	@Override
        	public void actionPerformed(ActionEvent e) {
            	final UIHelper uiHelper = new UIHelper(getOWLEditorKit());
                ODBCDriverEditForm editor=new ODBCDriverEditForm();
                final int ret = uiHelper.showDialog("Database-Mapping Editor", editor, null);
                if (ret == JOptionPane.OK_OPTION) {
                	ODBCDriver info=new ODBCDriverImpl(editor.getODBCName(), editor.getName(), editor.getPassword(), editor.getDBName(), editor.getDBType());
                	System.out.println("ODBCPreferencesPanel.handleAdd() called");
                	driverTableModel.addDriver(info);
                }

            }

        		} );
        panel.add(add);
        JButton remove = new JButton("Remove");
        panel.add(remove);
        remove.addActionListener(new ActionListener() { 
        	@Override
        	public void actionPerformed(ActionEvent e) {
            	System.out.println("ODBCPreferencesPanel.handleRemove() called");
                int[] rows = table.getSelectedRows();
                List<Integer> rowList = new ArrayList<>();
                for (int row : rows) {
                    rowList.add(row);
                }
                driverTableModel.removeDrivers(rowList);
            }

        		} );
        JButton edit = new JButton("Edit");
        panel.add(edit);
        edit.addActionListener(new ActionListener() { 
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("ODBCPreferencesPanel.handleEdit() called");}

        		} );
        
        return panel;
    }


    @Override
    public void applyChanges() {
    	ODBCPreferences.setDrivers(driverTableModel.getDrivers());
    }

    
    

    public void handleAdd(ActionEvent e) {
    	final UIHelper uiHelper = new UIHelper(getOWLEditorKit());
        ODBCDriverEditForm editor=new ODBCDriverEditForm();
        final int ret = uiHelper.showDialog("Database-Mapping Editor", editor, null);
        if (ret == JOptionPane.OK_OPTION) {
        	ODBCDriver info=new ODBCDriverImpl(editor.getODBCName(), editor.getUsername(), editor.getPassword(), editor.getDBName(), editor.getDBType());
        	System.out.println("ODBCPreferencesPanel.handleAdd() called");
        	driverTableModel.addDriver(info);
        }

    }

    public void handleRemove(ActionEvent e) {
    	System.out.println("ODBCPreferencesPanel.handleRemove() called");
        int[] rows = table.getSelectedRows();
        List<Integer> rowList = new ArrayList<>();
        for (int row : rows) {
            rowList.add(row);
        }
        driverTableModel.removeDrivers(rowList);
    }

    public void handleEdit(ActionEvent e) {
    	System.out.println("ODBCPreferencesPanel.handleEdit() called");
//        int row = table.getSelectedRow();
//        ODBCDriver existing = driverTableModel.getDrivers().get(row);
//        ODBCDriverEditForm editor = new ODBCDriverEditForm(jdbcRegistryTracker,
//                existing.getDescription(),
//                existing.getClassName(),
//                existing.getDriverLocation());
//        DriverInfo info = editor.askUserForDriverInfo();
//        driverTableModel.replaceDriver(row, info);
    }

}

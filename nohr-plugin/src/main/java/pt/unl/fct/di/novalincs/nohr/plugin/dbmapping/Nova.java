package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class Nova extends JFrame {

	private JTextField textField;
	private JTextField textField_2;
	private JTextField textField_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Nova frame = new Nova();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Nova() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{434, 0};
		gridBagLayout.rowHeights = new int[]{261, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JTabbedPane main = new JTabbedPane(JTabbedPane.TOP);
		main.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		GridBagConstraints gbc_main = new GridBagConstraints();
		gbc_main.insets = new Insets(0, 0, 5, 0);
		gbc_main.fill = GridBagConstraints.BOTH;
		gbc_main.gridx = 0;
		gbc_main.gridy = 0;
		getContentPane().add(main, gbc_main);
		
		JPanel basic = new JPanel();
		
		JPanel panel = new JPanel();
		GroupLayout gl_basic = new GroupLayout(basic);
		gl_basic.setHorizontalGroup(
			gl_basic.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_basic.createSequentialGroup()
					.addGap(0)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(0, Short.MAX_VALUE))
		);
		gl_basic.setVerticalGroup(
			gl_basic.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_basic.createSequentialGroup()
					.addGap(0)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(0, Short.MAX_VALUE))
		);
		
		JLabel lblTable = new JLabel("Table:");
		panel.add(lblTable);
		
		textField_1 = new JTextField();
		panel.add(textField_1);
		textField_1.setColumns(6);
		
		JLabel lblColumns = new JLabel("Columns:");
		panel.add(lblColumns);
		
		textField_2 = new JTextField();
		panel.add(textField_2);
		textField_2.setColumns(6);
		
		JLabel lblPredicate = new JLabel("Predicate:");
		panel.add(lblPredicate);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(6);
		basic.setLayout(gl_basic);
		
		JPanel advanced = new JPanel();
	}
}

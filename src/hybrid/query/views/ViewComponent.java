package hybrid.query.views;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.owl.example.Metrics;


public class ViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
    private static final Logger log = Logger.getLogger(ViewComponent.class);
    
    private Metrics metricsComponent;
    private Query _query;
    private JTextArea _textArea;
    private JTextField _textField;
	
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(12,12));
        //metricsComponent = new Metrics(getOWLModelManager());
        //add(metricsComponent, BorderLayout.BEFORE_FIRST_LINE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints subC = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridwidth=1;
        c.gridheight = 1;
        c.weightx = 1;
        
        subC.fill = GridBagConstraints.HORIZONTAL;
        subC.gridx = 3;
        subC.gridy = 0;
        subC.gridwidth=1;
        subC.gridheight=1;
        subC.weightx = 0.05;
        subC.ipady = 0;
        JScrollPane scrollPane;
        
        JPanel queryPanel = new JPanel(new GridBagLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
        c.gridy = 1;
        c.weighty = 0.3;
        queryPanel.add(addProcessButton(),subC);
        c.gridy = 2;
        c.weighty = 0.3;
        subC.gridx = 0;
        subC.gridwidth=3;
        subC.ipady=10;
        subC.weightx=0.95;
        queryPanel.add(addQueryField(), subC);
        panel.add(queryPanel, c);
        subC.ipady=0;
        subC.fill = GridBagConstraints.BOTH;
        
        JPanel resultPanel = new JPanel(new GridBagLayout());
        
        JPanel tabPanel = new JPanel(new GridBagLayout());
        JPanel outputPanel = new JPanel(new GridBagLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBorder(BorderFactory.createTitledBorder("Output"));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        c.gridy = 3;
        c.weighty = 3;
        
        subC.weighty = 1;
        _textArea = new JTextArea();
        _textArea.setLineWrap(true);
        _textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret)_textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        scrollPane = new JScrollPane(_textArea);
        outputPanel.add(scrollPane, subC);
        
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
//        table.setBackground(Color.gray);
//        table.set
        tabbedPane.addTab("Result", new JScrollPane(table));
        tabbedPane.addTab("Log", outputPanel);
        tabPanel.add(tabbedPane, subC);
//        subC.fill = GridBagConstraints.BOTH;
        subC.gridx = 0;
        subC.gridwidth=1;
        subC.gridheight=1;
        subC.gridy = 0;
        subC.weightx = 0.95;
        resultPanel.add(tabPanel, subC);
        subC.anchor = GridBagConstraints.NORTHWEST;
        subC.gridx = 1;
        subC.weightx = 0.05;
        resultPanel.add(addSettingsPanel(), subC);
        
        panel.add(resultPanel, c);
        
        add(panel, BorderLayout.CENTER);

        _query = new Query(getOWLModelManager(), _textArea, tableModel);
        
        
//        log.info("Example View Component initialized");
    }

	
	@Override
	protected void disposeOWLView() {
		metricsComponent.dispose();
//		_query.dispose();
	}
	
	protected JButton addProcessButton(){
		JButton button = new JButton("Execute");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(_textField.getText().length()>0){
					_query.query(_textField.getText());
	            	_textField.setText("");
				}
			}
		});
		
		return button;
	}
	protected JTextField addQueryField(){
		_textField = new JTextField();
		_textField.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					updateText(e);
				}
				private void updateText(KeyEvent e) {
		            if( e.getKeyCode() == KeyEvent.VK_ENTER && _textField.getText().length()>0 )  {
		            	_query.query(_textField.getText());
		            	_textField.setText("");
		            }
				}
				@Override
				public void keyReleased(KeyEvent e) {
					
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
		 return _textField;
	}
	protected JPanel addSettingsPanel() {
		JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Solutions"));
        
        JPanel panelTop = new JPanel(new GridBagLayout());
        JPanel panelBottom = new JPanel(new GridBagLayout());
        JPanel panelTopBottom = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth=1;
        c.gridheight = 1;
        c.weightx = 1;
        
        JRadioButton oneChB = new JRadioButton("one");
        JRadioButton allChB = new JRadioButton("all",true);
        ButtonGroup group = new ButtonGroup();
        group.add(oneChB);
        group.add(allChB);
        JCheckBox trueChB = new JCheckBox("true");
        JCheckBox undefinedChB = new JCheckBox("undefined");
        JCheckBox inconsistentChB = new JCheckBox("inconsistent");
        
        panelTop.add(oneChB,c);
        c.gridy=2;
        panelTop.add(allChB,c);
        
        c.gridy=1;
        panelBottom.add(trueChB,c);
        c.gridy=2;
        panelBottom.add(undefinedChB,c);
        c.gridy=3;
        panelBottom.add(inconsistentChB,c);
        
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 0;
        c.gridy=1;
        settingsPanel.add(panelTop, c);
        c.gridy=2;
        c.ipady = 10;
        settingsPanel.add(panelBottom, c);
        c.weighty = 0.8;
        c.gridy=3;
        settingsPanel.add(panelTopBottom, c);
        
        return settingsPanel;
	}
	
}

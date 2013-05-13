package hybrid.query.views;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Font;
//import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;


//import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.owl.example.Metrics;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


public class ViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
//    private static final Logger log = Logger.getLogger(ViewComponent.class);
    
    private Metrics metricsComponent;
    private Query _query;
    private JTextArea _textArea;
    private JTextField _textField;
//    private JProgressBar progressBar;
    private JFrame progressFrame;
    private JLabel progressLabel;
	
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
        subC.gridx = 0;
        subC.gridy = 0;
        subC.gridwidth=1;
        subC.gridheight=1;
        subC.weightx = 1;
        subC.ipady = 0;
        JScrollPane scrollPane;
        
        JPanel queryPanel = new JPanel(new GridBagLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
        c.gridy = 1;
        c.weighty = 0.3;
        subC.ipady=10;
        queryPanel.add(addQueryField(),subC);
        c.gridy = 2;
        c.weighty = 0.3;
//        subC.gridx = 0;
        subC.fill = GridBagConstraints.NONE;
//        subC.gridwidth=3;
        subC.gridy = 1;
        subC.ipady=0;
        subC.anchor = GridBagConstraints.WEST;
//        subC.weightx=0.95;
        queryPanel.add(addProcessButton(), subC);
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
        
        final DefaultTableModel tableModel = new DefaultTableModel();
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	addProgressFrame();
            	//Query.progressFrame.setVisible(true);
            }
        });
        _textField.requestFocus();
        _textField.requestFocusInWindow();
        
//        log.info("Example View Component initialized");
    }

	
	@Override
	protected void disposeOWLView() {
//		metricsComponent.dispose();
		_query.disposeQuery();
	}
	
	protected JButton addProcessButton(){
		JButton button = new JButton("Execute");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(_textField.getText().length()>0){
					try {
						_query.query(_textField.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            	_textField.setText("");
				}
			}
		});
		
		return button;
	}
	protected JTextField addQueryField(){
		_textField = new JTextField("has(M,N),p(X)");
		_textField.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					updateText(e);
				}
				private void updateText(KeyEvent e) {
		            if( e.getKeyCode() == KeyEvent.VK_ENTER && _textField.getText().length()>0 )  {
		            	try {
							_query.query(_textField.getText());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
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
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        
        JPanel panelTop = new JPanel(new GridBagLayout());
        panelTop.setBorder(BorderFactory.createTitledBorder("Solutions"));
        JPanel panelBottom = new JPanel(new GridBagLayout());
        panelBottom.setBorder(BorderFactory.createTitledBorder("Valuation"));
        JPanel panelTopBottom = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth=1;
        c.gridheight = 1;
        c.weightx = 1;
        
        JRadioButton oneChB = new JRadioButton("one", false);
        JRadioButton allChB = new JRadioButton("all",true);
        ButtonGroup group = new ButtonGroup();
        group.add(oneChB);
        group.add(allChB);
        JCheckBox trueChB = new JCheckBox("true", true);
        JCheckBox undefinedChB = new JCheckBox("undefined", true);
        JCheckBox inconsistentChB = new JCheckBox("inconsistent", true);
        
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
	
	private void addProgressFrame(){
		
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
//        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("Half way there!");
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(new EmptyBorder(0, 10, 0, 10) );
        progressPanel.add(new JLabel("Rule translation process",SwingConstants.CENTER),BorderLayout.BEFORE_FIRST_LINE);
        
        
        GridBagConstraints c = new GridBagConstraints();
//        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth=1;
        c.gridheight = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        
        JPanel panel = new JPanel(new GridBagLayout());
        Query.progressLabel = new JLabel("Rule translating");
//        progressLabel.setHorizontalTextPosition(SwingConstants.CENTER);
//        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Query.progressLabel.setFont(new Font(Query.progressLabel.getFont().getFontName(),Font.PLAIN, Query.progressLabel.getFont().getSize()+4));
        panel.add(Query.progressLabel, c);
        c.gridy=1;
        panel.add(progressBar, c);
        progressPanel.add(panel, BorderLayout.CENTER);
        
        Query.progressFrame = new JFrame("Rule translation process");
//        progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Query.progressFrame.setSize(300, 100);
        Query.progressFrame.setLocationByPlatform(true);
        Query.progressFrame.setUndecorated(true);
        Query.progressFrame.setContentPane(progressPanel);
//        frame.pack();
        Query.progressFrame.setLocationRelativeTo(ViewComponent.this);
//        progressFrame.setVisible(true);
	}
	
}

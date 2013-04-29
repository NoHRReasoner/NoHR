package hybrid.query.views;
import local.translate.Ontology;

import java.util.*;

import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.owl.example.Metrics;
import org.protege.editor.core.ui.util.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;
//import org.semanticweb.owlapi.model.OWLClass;

import com.declarativa.interprolog.gui.XSBSubprocessEngineWindow;


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
        
        JTable table = new JTable();
        tabbedPane.addTab("Result", table);
        tabbedPane.addTab("Log", outputPanel);
        
        tabPanel.add(tabbedPane, subC);
//        subC.fill = GridBagConstraints.BOTH;
        subC.gridx = 0;
        subC.gridwidth=1;
        subC.gridheight=1;
        subC.gridy = 0;
        subC.weightx = 0.8;
        resultPanel.add(tabPanel, subC);
        subC.gridx = 1;
        subC.weightx = 0.2;
        resultPanel.add(addSettingsPanel(), subC);
        
        panel.add(resultPanel, c);
        
        add(panel, BorderLayout.CENTER);

        _query = new Query(getOWLModelManager(), _textArea);
        
        
        log.info("Example View Component initialized");
    }

	
	@Override
	protected void disposeOWLView() {
		metricsComponent.dispose();
	}
	
	protected JButton addProcessButton(){
		JButton button = new JButton("Execute");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_query.query(_textField.getText());
            	_textField.setText("");
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
        settingsPanel.setBorder(BorderFactory.createTitledBorder("settings"));
        
        JLabel label = new JLabel("Setting 1");
        JCheckBox checkBox = new JCheckBox("checkbox 1");
        
        settingsPanel.add(label);
        settingsPanel.add(checkBox);
        return settingsPanel;
	}
	
}

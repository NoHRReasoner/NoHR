package org.protege.editor.owl.examples.tab;
//import com.declarativa.interprolog.*;
import local.interprolog.*;
import local.translate.Ontology;

import java.util.*;

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


public class ExampleViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
    private static final Logger log = Logger.getLogger(ExampleViewComponent.class);
    
    private Metrics metricsComponent;
    private JTextArea _textArea;
    private JTextArea _rulesTextArea;
    private JTextField _textField;
    private JButton _addRules;
    private JButton _translate;
    private JFileChooser _fileChooser;
    private InterPrologInteraction _prologInteraction;
	private Ontology _ontology;
	private List<String> _rules;
	private File _ruleFile;
	private boolean rulesChanged = false;
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(12,12));
        boolean isDebug = true;
//    	setLayout(new RelativeLayout());
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
        subC.ipady = 10;
        JScrollPane scrollPane;
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
//        buttonPanel.setBorder(BorderFactory.createTitledBorder("buttons"));
        c.gridy = 1;
        c.weighty = 0.3;
        buttonPanel.add(addProcessButton(),subC);
        panel.add(buttonPanel, c);
        
        JPanel queryPanel = new JPanel(new GridBagLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
        c.gridy = 2;
        c.weighty = 0.3;
        queryPanel.add(addQueryField(), subC);
        panel.add(queryPanel, c);
        
        JPanel outputPanel = new JPanel(new GridBagLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        c.gridy = 3;
        c.weighty = 3;
        subC.ipady=0;
        subC.fill = GridBagConstraints.BOTH;
        subC.weighty = 1;
        _textArea = new JTextArea();
        _textArea.setLineWrap(true);
        _textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret)_textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        scrollPane = new JScrollPane(_textArea);
        outputPanel.add(scrollPane, subC);
        panel.add(outputPanel, c);
        
        JPanel rulesPanel = new JPanel(new GridBagLayout());
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        c.gridy = 0;
        c.weighty = 3;
        
        subC.gridx=0;
        subC.gridy=0;
        subC.gridwidth=4;
        subC.gridheight=1;
        subC.weighty = 3;
        _rulesTextArea = new JTextArea();
        _rulesTextArea.setLineWrap(true);
        addRulesEditorEvents();
        scrollPane = new JScrollPane(_rulesTextArea);
        rulesPanel.add(scrollPane, subC);
        subC.fill = GridBagConstraints.HORIZONTAL;
        subC.ipady = 10;
        subC.gridy=1;
        subC.gridx=2;
        subC.gridwidth=1;
        subC.weighty = 0.3;
        rulesPanel.add(addLoadRulesButton(),subC);
        subC.gridx=3;
        rulesPanel.add(addSaveRulesButton(),subC);
        panel.add(rulesPanel, c);
    	
        add(panel, BorderLayout.CENTER);

        _fileChooser = new JFileChooser();
        _prologInteraction = new InterPrologInteraction(_textArea, _textField, isDebug);
	    _ontology = new Ontology(getOWLModelManager(), _textArea, isDebug);
	    _rules = new ArrayList<String>();
	    getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
	    getOWLModelManager().addListener(modelManagerListener);
        log.info("Example View Component initialized");
    }

	// Fired when axioms are added and removed
	private OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
			_ontology.isOntologyChanged = true;
		}
	};
	private OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {
		
		@Override
		public void handleChange(OWLModelManagerChangeEvent arg0) {
			_ontology.isOntologyChanged = true;
		}
	};
	@Override
	protected void disposeOWLView() {
		metricsComponent.dispose();
	}
	
	protected JButton addLoadRulesButton(){
		JButton button = new JButton("Load");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int val = _fileChooser.showDialog(null, "Open");
				if(val==JFileChooser.APPROVE_OPTION){
//					_rules = new ArrayList<String>();
					_rulesTextArea.setText("");
					try {
						_ruleFile = _fileChooser.getSelectedFile();
						FileInputStream fstream = new FileInputStream(_ruleFile);
						DataInputStream in = new DataInputStream(fstream);
					    BufferedReader br = new BufferedReader(new InputStreamReader(in));
						String strLine;
						//Read File Line By Line
						while ((strLine = br.readLine()) != null)   {
			                if(strLine.length()>0){
			                	_rulesTextArea.append(strLine+"\n");
//			                	_rules.add(strLine);
			                }
						}
						in.close();
						collectRules();
						
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						_textArea.append(e1.toString()+"\n");
					}
					// Get the object of DataInputStream
					catch (IOException e1) {
						e1.printStackTrace();
						_textArea.append(e1.toString()+"\n");
					}
				    
				}
			}
		});
		return button;
	}
	protected JButton addSaveRulesButton(){
		JButton button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(_ruleFile==null)
					_ruleFile = new File("rules.p");
				_fileChooser.setSelectedFile(_ruleFile);
				int val = _fileChooser.showSaveDialog(_fileChooser.getParent());
				if(val == JFileChooser.APPROVE_OPTION){
					_ruleFile = _fileChooser.getSelectedFile();
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(_ruleFile));
						bw.write(_rulesTextArea.getText());
		                bw.flush();
		                bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		return button;
	}
	protected JButton addRulesButton(){
		JButton rulesButton = new JButton("Add rules");
		rulesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(!_rules.isEmpty()){
						_ontology.appendRules(_rules);
						if(!_prologInteraction.setConsultAbsolute(_ontology.Finish())){
							_textArea.append("Unfortuantely, the rules was not load properly\n");
						}
					}
				} catch (OWLOntologyCreationException e2) {
					e2.printStackTrace();
					_textArea.append(e2.toString()+"\n");
				} catch (OWLOntologyStorageException e2) {
					e2.printStackTrace();
					_textArea.append(e2.toString()+"\n");
				} catch (IOException e2) {
					e2.printStackTrace();
					_textArea.append(e2.toString()+"\n");
				} catch (ParserException e1) {
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				}
			}
		});
		return rulesButton;
	}
	protected JButton addProcessButton(){
		JButton button = new JButton("Process for querying");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(rulesChanged){
						collectRules();
					}
					if(_ontology.isOntologyChanged){
						_ontology.PrepareForTranslating();
						_ontology.proceed();
						_ontology.isOntologyChanged = false;
					}
					//if(!_rules.isEmpty()){
					_ontology.appendRules(_rules);
					//}
					_prologInteraction.setConsultAbsolute(_ontology.Finish());
				} catch (IOException e1) {
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				} catch (ParserException e1) {
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				} catch (OWLOntologyCreationException e1) {
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				} catch (OWLOntologyStorageException e1) {
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
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
		            	_prologInteraction.makeQuery(_textField.getText());
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
	private void addRulesEditorEvents(){
		_rulesTextArea.getDocument().addDocumentListener(new DocumentListener() {

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	rulesChanged = true;
	        }

	        @Override
	        public void insertUpdate(DocumentEvent e) {
	        	rulesChanged = true;
	        }

	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        	rulesChanged = true;
	        }
	    });
	}
	private void collectRules(){
		StringReader sr = new StringReader(_rulesTextArea.getText()); 
		BufferedReader br = new BufferedReader(sr); 
		String nextLine = ""; 
		_rules = new ArrayList<String>();
		try {
			while ((nextLine = br.readLine()) != null){ 
				_rules.add(nextLine);
//				_textArea.append(nextLine+"\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		sr.close();
		rulesChanged = false;
	}
	
}

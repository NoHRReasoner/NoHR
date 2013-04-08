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
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.owl.example.Metrics;
import org.protege.editor.core.ui.util.*;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
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
	private int _width = 576;
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(12,12));
        //metricsComponent = new Metrics(getOWLModelManager());
        //add(metricsComponent, BorderLayout.BEFORE_FIRST_LINE);
        add(createCenterPanel(),BorderLayout.CENTER);
	    add(createBottomPanel(), BorderLayout.SOUTH);
	    _fileChooser = new JFileChooser();
	    _prologInteraction = new InterPrologInteraction(_textArea, _textField);
	    _ontology = new Ontology(getOWLModelManager(), _textArea, true);
	    _rules = new ArrayList<String>();
	    add(createTopPanel(), BorderLayout.BEFORE_FIRST_LINE);
	    
        log.info("Example View Component initialized");
    }

	@Override
	protected void disposeOWLView() {
		metricsComponent.dispose();
	}
	
	protected JPanel createCenterPanel(){
		JPanel panel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.RIGHT));
		_textArea = new JTextArea();
		_rulesTextArea = new JTextArea("rules");
		JButton rulesButton = new JButton("Add rules");
		panel.add(createTextArea(_rulesTextArea, 110),BorderLayout.BEFORE_FIRST_LINE);
		panel.add(rulesButton,BorderLayout.EAST);
		panel.add(createTextArea(_textArea, 220),BorderLayout.SOUTH);
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
					// TODO Auto-generated catch block
					e2.printStackTrace();
					_textArea.append(e2.toString()+"\n");
				} catch (OWLOntologyStorageException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					_textArea.append(e2.toString()+"\n");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					_textArea.append(e2.toString()+"\n");
				} catch (ParserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				}
			}
		});
        add(panel, BorderLayout.CENTER);
		return panel;
	}
	protected JPanel createTopPanel(){
		JPanel panel = new JPanel();
		_addRules = new JButton("Load aditional rules");
	    _translate = new JButton("Translate ontology to rules");
	    _addRules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int val = _fileChooser.showDialog(null, "Open");
				if(val==JFileChooser.APPROVE_OPTION){
					_rules = new ArrayList<String>();
					_rulesTextArea.setText("");
					try {
						FileInputStream fstream = new FileInputStream(_fileChooser.getSelectedFile());
						DataInputStream in = new DataInputStream(fstream);
					    BufferedReader br = new BufferedReader(new InputStreamReader(in));
						String strLine;
						//Read File Line By Line
						while ((strLine = br.readLine()) != null)   {
			                if(strLine.length()>0){
			                	_rulesTextArea.append(strLine+"\n");
			                	_rules.add(strLine);
			                }
						}
						in.close();
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						_textArea.append(e1.toString()+"\n");
					}
					// Get the object of DataInputStream
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						_textArea.append(e1.toString()+"\n");
					}
				    
				}
			}
		});
	    _translate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					_ontology.PrepareForTranslating();
					_ontology.proceed();
//					File f = _ontology.Finish();
					//_textArea.append(""+Boolean.valueOf(f.canRead())+"\n");
//					FileReader reader = new FileReader(f);
//					_textArea.read(reader, null);
//					reader.close();
					_prologInteraction.setConsultAbsolute(_ontology.Finish());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				} catch (ParserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				} catch (OWLOntologyCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				} catch (OWLOntologyStorageException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					_textArea.append(e1.toString()+"\n");
				}
			}
		});
	    panel.add(_addRules);
	    panel.add(_translate);
		return panel;
	}
	protected JPanel createBottomPanel(){
		JPanel panel = new JPanel();
//		panel.setBackground(Color.black);
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
					// TODO Auto-generated method stub
					
				}
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
		 _textField.setPreferredSize(new Dimension(_width, 30));
		 panel.add(_textField, BorderLayout.CENTER);
		return panel;
	}
	private JScrollPane createTextArea(JTextArea textArea, int h){
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setEditable(false);
        textArea.setMaximumSize(new Dimension(_width, h));
//        textArea.setRows(10);
		JScrollPane scrollPane = new JScrollPane(textArea);
//		scrollPane.setBackground(Color.blue);
		scrollPane.setPreferredSize(new Dimension(_width, h));
		return scrollPane;
	}
	
}

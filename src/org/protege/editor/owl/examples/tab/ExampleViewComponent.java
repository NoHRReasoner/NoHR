package org.protege.editor.owl.examples.tab;
//import com.declarativa.interprolog.*;
import local.interprolog.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.owl.example.Metrics;
import org.protege.editor.core.ui.util.*;
//import org.semanticweb.owlapi.model.OWLClass;

import com.declarativa.interprolog.gui.XSBSubprocessEngineWindow;

public class ExampleViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
    private static final Logger log = Logger.getLogger(ExampleViewComponent.class);
    
    private Metrics metricsComponent;
    private JTextArea _textArea;
    private JTextField _textField;
    private JScrollPane _scrollPane;
    private JButton _button;
    private JFileChooser _fileChooser;
    private InterPrologInteraction _prologInteraction;
	
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(6,6));
        //metricsComponent = new Metrics(getOWLModelManager());
        //add(metricsComponent, BorderLayout.BEFORE_FIRST_LINE);
        _textArea = new JTextArea();
        _scrollPane = new JScrollPane(_textArea);
	    add(_scrollPane, BorderLayout.CENTER);
	    _textArea.setRows(10);
	    _textArea.setEditable(false);
	    _textArea.setMaximumSize(new Dimension(400, 200));
	    _textField = new JTextField();
	    add(_textField, BorderLayout.SOUTH);
	    _textField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				updateText(e);
			}
			private void updateText(KeyEvent e) {
				//_textArea.append(String.valueOf(e.getKeyCode())+" "+KeyEvent.VK_ENTER+"\n");
	            if( e.getKeyCode() == KeyEvent.VK_ENTER && _textField.getText().length()>0 )  {
	            	_prologInteraction.makeQuery(_textField.getText());
	            	//_textField.setText("");
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
	    
	    _button = new JButton();
	    _fileChooser = new JFileChooser();
	    _prologInteraction = new InterPrologInteraction(_textArea, _textField);
	    if(!_prologInteraction.isXSBbin){
	    	_textArea.append("Please, set up your XSB_BIN_DIRECTORY\n For mac os consider the example: launchctl setenv XSB_BIN_DIRECTORY /Full/Path/To/XSB/bin");
	    }
	    if(!_prologInteraction.startEngine()){
	    	_textArea.append("Query Engine was not started\n");
	    }
	    
	    _button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_fileChooser.showDialog(null, "Choose ontology");
				if(!_prologInteraction.setConsultAbsolute(_fileChooser.getSelectedFile())){
					_textArea.append("Unfortuantely, the rules was not load properly\n");
				}
			}
		});
	    _button.setText("Add aditional rules");
	    add(_button, BorderLayout.BEFORE_FIRST_LINE);
        log.info("Example View Component initialized");
    }

	@Override
	protected void disposeOWLView() {
		metricsComponent.dispose();
	}

}

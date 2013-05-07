package hybrid.query.views;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JTextArea;

public class Rules {
//	private static HashSet<String> _rules = new HashSet<String>();
	private static ArrayList<String> _rules = new ArrayList<String>();
	private static HashSet<JTextArea> _listners = new HashSet<JTextArea>();
	public static boolean isRulesChanged;
	public static boolean isRulesOntologyChanged;
	private static JTextArea _currentTextArea;
	
	public static void addRule(String rule, JTextArea textarea){
		_rules.add(rule);
		for (JTextArea textArea : _listners) {
			if(textArea!=textarea)
				textArea.append(rule+Config.nl);
		}
	}
	public static void addRule(String rule) {
		isRulesChanged = true;
		isRulesOntologyChanged = true;
		_rules.add(rule);
		for (JTextArea textArea : _listners) {
			textArea.append(rule+Config.nl);
		}
		
	}
	public static void addRules(List<String> rules, JTextArea textArea) {
		
	}
	
	public static void addListener(JTextArea textarea){
		_listners.add(textarea);
		addEventsForTextArea(textarea);
		textarea.setText("");
		for (String rule : _rules) {
			textarea.append(rule+Config.nl);
		}
	}
	
	public static void deleteAllRules(){
		resetRules();
		for (JTextArea textArea : _listners) {
			textArea.setText("");
		}
	}
	
	private static void printRulesToTextArea(){
		for (JTextArea textArea : _listners) {
			if(textArea!=_currentTextArea){
				textArea.setText("");
				for (String rule : _rules) {
					textArea.append(rule+Config.nl);
				}
			}
		}
	}
	public static void recollectRules(boolean isRePrint){
		if(isRulesChanged){
			isRulesChanged = false;
			isRulesOntologyChanged = true;
			StringReader sr = new StringReader(_currentTextArea.getText()); 
			BufferedReader br = new BufferedReader(sr); 
			String nextLine = ""; 
			resetRules();
			try {
				while ((nextLine = br.readLine()) != null){
					nextLine=nextLine.trim();
					if(nextLine.length()>0){
//						_currentTextArea.append(nextLine+" !!!\n");
						_rules.add(nextLine);
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			sr.close();
			if(isRePrint)
				printRulesToTextArea();
		}
	}
	
	private static void addEventsForTextArea(final JTextArea textarea){
		/*textarea.getDocument().addDocumentListener(new DocumentListener() {

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	isRulesChanged = true;
	        	_currentTextArea=textarea;
//	        	textarea.append("removed"+Config.nl);
	        }

	        @Override
	        public void insertUpdate(DocumentEvent e) {
//	        	e.
	        	isRulesChanged = true;
	        	_currentTextArea=textarea;
//	        	textarea.append("inserted"+Config.nl);
	        }

	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        	isRulesChanged = true;
	        	_currentTextArea=textarea;
//	        	textarea.append("changed"+Config.nl);
	        }
	    });*/
		textarea.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
//				textarea.append("Key Pressed "+ arg0.getKeyCode()+Config.nl);
				
				isRulesChanged=true;
				isRulesOntologyChanged = true;
				_currentTextArea = textarea;
			}
		});
	}
	
	public static void dispose() {
		for (JTextArea textArea : _listners) {
			textArea.setText("");
		}
		resetRules();
	}
	public static void setCurrentTextArea(JTextArea textarea) {
		_currentTextArea=textarea;
	}
	/*public static HashSet<String> getRules() {
		return _rules;
	}*/
	public static ArrayList<String> getRules() {
		recollectRules(false);
		return _rules;
	}
	private static void resetRules(){
		_rules = new ArrayList<String>();
	}
	
}

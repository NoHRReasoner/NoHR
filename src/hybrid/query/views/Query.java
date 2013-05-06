package hybrid.query.views;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import local.translate.Ontology;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.PrologOutputListener;
import com.declarativa.interprolog.SubprocessEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;

public class Query implements PrologOutputListener{
	private JTextArea _outPutLog;
	private DefaultTableModel _outTableModel;
//	private JTable _outTable;
	private DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
	
	private static OWLModelManager _owlModelManager;
	private static boolean isOntologyChanged;
	private boolean isCompiled;
	private boolean isEngineStarted;
	private XSBSubprocessEngine _engine;
	private Ontology _ontology;
	private ArrayList<String> _variables = new ArrayList<String>();
	private Dictionary<String, String> _answers = new Hashtable<String, String>();
	private String _variablesSearch;
	private Pattern p;
	private Pattern headerPattern = Pattern.compile("\\((.*?)\\)");
	private String _answer;
	
	private boolean waitingForVariables = false;
	private boolean waitingForAnswer = false;
	
	private int j=0;
	public Query(OWLModelManager owlModelManager, JTextArea textArea, DefaultTableModel tableModel) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
		_owlModelManager = owlModelManager;
		_outPutLog = textArea;
		_outTableModel = tableModel;
//		_outTable = table;
		Init();
		headerRenderer.setBackground(new Color(239, 198, 46));
//		clearTable();
		
//		_outTable.addColumn("1");
	}
	
	private void Init() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
		
		_owlModelManager.addOntologyChangeListener(ontologyChangeListener);
		_owlModelManager.addListener(modelManagerListener);
		InitInterPrologInteraction();
	    _ontology = new Ontology(_owlModelManager, _outPutLog, Config.isDebug);
	    
	}
	
	public static void dispose(){
		_owlModelManager.removeOntologyChangeListener(ontologyChangeListener);
		_owlModelManager.removeListener(modelManagerListener);
		Rules.dispose();
	}
	
	public void printLog(String text) {
		if(Config.isDebug){
			printInfo(text);
		}
	}
	public void printInfo(String text) {
		_outPutLog.append(text+Config.nl);
	}
	private boolean isChanged(){
		return Rules.isRulesOntologyChanged || isOntologyChanged;
	}
	
	// Fired when axioms are added and removed
	private static OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
			isOntologyChanged = true;
		}
	};
	private static OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {
		
		@Override
		public void handleChange(OWLModelManagerChangeEvent arg0) {
			isOntologyChanged = true;
		}
	};

	private void InitInterPrologInteraction(){
		String xsbBin = System.getenv("XSB_BIN_DIRECTORY");
		printLog("Starting query engine"+Config.nl);
		printLog(Config.tempDir+Config.nl);
		
		if(xsbBin!=null){
			xsbBin+="/xsb";
		}else{
			print("Please, set up your XSB_BIN_DIRECTORY"+Config.nl+" For mac os consider the example: launchctl setenv XSB_BIN_DIRECTORY /Full/Path/To/XSB/bin"+Config.nl);
		}
		startEngine(xsbBin);
		
	}
	
	private void startEngine(String xsbBin) {
		isEngineStarted=true;
		try{
			_engine = new XSBSubprocessEngine(xsbBin);
			_engine.addPrologOutputListener(this);
			printLog("Engine started"+Config.nl);
		}catch(Exception e){
			isEngineStarted=false;
			printLog("Query Engine was not started"+Config.nl);
			printInfo(e.toString()+Config.nl);
		}
	}

	public void query(String command) {
		
		if(!isCompiled){
			try {
				_ontology.PrepareForTranslating();
				_ontology.proceed();
				_ontology.appendRules(Rules.getRules());
				compileFile(_ontology.Finish());
				isOntologyChanged=false;
				Rules.isRulesOntologyChanged = false;
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserException e) {
				e.printStackTrace();
			}
			
		}else if(isChanged()){
			int dialogResult = JOptionPane.showConfirmDialog (null, "Some changes have been made, would you like to recompile?", "Warning", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				try {
					if(isOntologyChanged){
						_ontology.PrepareForTranslating();
						_ontology.proceed();
						isOntologyChanged = false;
					}
					if(Rules.isRulesOntologyChanged){
						_ontology.appendRules(Rules.getRules());
						Rules.isRulesOntologyChanged = false;
					}
					compileFile(_ontology.Finish());
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(isQueriable()){
			
			printInfo(command+Config.nl);
			if(command.endsWith(".")){
				command = command.substring(0, command.length()-1);
			}
			command = _ontology.ruleToLowerCase(command);
			command = _ontology.replaceSymbolsInWholeRule(command);
			fillTableHeader(command);
			((SubprocessEngine)_engine).sendAndFlushLn(command+".");
		}
		
		
		
	}
	private void sendSemiColomn(){
		((SubprocessEngine)_engine).sendAndFlushLn(";");
	}
	public boolean compileFile(File file) {
		isCompiled=false;
		if(isEngineStarted && _engine.load_dynAbsolute(file))
			isCompiled=true;
		return isCompiled;
	}
	
	private boolean isQueriable(){
		return isEngineStarted && isCompiled;
	}
	
	
	@Override
	public void print(String s) {
		s = s.replace(Config.tempDir, "");
		s = s.trim();
		if(s.length()==0)
			return;
		printInfo(s);
//		s = s.replace(Config.nl, "");
		if(!s.contains("++Error[XSB/Runtime/P]")){
//			System.out.println(s);
			
//			System.out.println(":"+s+": matches?:"+m.find());
			if(waitingForVariables){
				Matcher m = p.matcher(s);
				if(m.find())
					fillVariables(s);
			}else if(waitingForAnswer)
				getAnswer(s);
		}
	}
	
	public void clearTable(){
		for(int i=_outTableModel.getRowCount()-1;i>=0;i--){
			_outTableModel.removeRow(i);
		}
		_outTableModel.setColumnCount(0);
		_variables = new ArrayList<String>();
		_variablesSearch = "";
		_outTableModel.addColumn("truth value");
	}
	
	private void fillVariables(String s){
		waitingForVariables=false;
//		j++;
		StringReader sr = new StringReader(s); 
		BufferedReader br = new BufferedReader(sr); 
		String nextLine = ""; 
		String[] _;
		try {
			while ((nextLine = br.readLine()) != null){
				nextLine=nextLine.trim();
				if(nextLine.contains("=")){
					_=nextLine.split("=");
					_answers.put(_[0].trim(), _[1].trim());
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		sr.close();
		waitingForAnswer = true;
		sendSemiColomn();
	}
	
	private void getAnswer(String s){
		
		StringReader sr = new StringReader(s); 
		BufferedReader br = new BufferedReader(sr); 
		String nextLine = ""; 
		try {
			while ((nextLine = br.readLine()) != null){
				nextLine=nextLine.trim();
				if(!nextLine.contains("| ?-")){
					_answer = nextLine;
				}
			}
			br.close();
			
			waitingForAnswer = false;
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		sr.close();
		fillTable();
	}
	
	private void fillTableHeader(String command){
		clearTable();
		try {
    		Matcher m = headerPattern.matcher(command);
            StringBuffer sb = new StringBuffer();
            String rule;
            while (m.find()) {
                m.appendReplacement(sb, m.group());
                rule = m.group();
                rule = rule.substring(1, rule.length()-1);
                for (String s : rule.split(",")) {
    				s = s.trim();
    				_variables.add(s);
    				if(_variablesSearch.length()!=0)
    					_variablesSearch+="|";
    				_variablesSearch += "(.*)"+s+" =";
    				_outTableModel.addColumn(s);
    			}
            }
            sb.setLength(0);
            p = Pattern.compile(_variablesSearch);
            waitingForVariables = true;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	private void fillTable(){
		ArrayList<String> row = new ArrayList<String>();
		row.add( _answer);
		for (String string : _variables) {
			row.add(_answers.get(string));
		}
		_outTableModel.addRow(row.toArray());
	}
	private boolean containsVariable(String s){
		for (String variable : _variables) {
			if (s.contains(variable+" = "))
				return true;
		}
		return false;
	}
	private void printTable(){
//		for (String s : _answers) {
//			_outTableModel.addRow(new Object[]{s});
//		}
//		_answers = new ArrayList<String>();
	}
}








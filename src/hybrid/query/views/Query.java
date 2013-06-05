package hybrid.query.views;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import local.translate.Ontology;
import local.translate.Utils;

import org.apache.log4j.Logger;
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
import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.XSBSubprocessEngine;

public class Query implements PrologOutputListener{
	private JTextArea outPutLog;
	private DefaultTableModel outTableModel;
	private TableRowSorter<DefaultTableModel> tableSorter;
	private RowFilter<DefaultTableModel,Object> rowFilter;
	private DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
	public static JLabel progressLabel;
	public static JFrame progressFrame;
	private static OWLModelManager owlModelManager;
	private static boolean isOntologyChanged;
	private boolean isCompiled;
	private boolean isEngineStarted;
	private XSBSubprocessEngine _engine;
	private Ontology _ontology;
//	private HashSet<String> _variables = new HashSet<String>();
	private ArrayList<String> _variablesList = new ArrayList<String>();
	private ArrayList<ArrayList<String>> _answers = new ArrayList<ArrayList<String>>();
	private Pattern headerPattern = Pattern.compile("\\((.*?)\\)");
	private String queryString;
	private QueryXSB queryXSB;
	private String previousQuery = "";
	private static final Logger log = Logger.getLogger(Query.class);
	
	public Query(OWLModelManager OwlModelManager, JTextArea TextArea, DefaultTableModel TableModel, TableRowSorter<DefaultTableModel> sorter){
		owlModelManager = OwlModelManager;
		outPutLog = TextArea;
		outTableModel = TableModel;
		tableSorter = sorter;
		Init();
		headerRenderer.setBackground(new Color(239, 198, 46));
		log.setLevel(Config.logLevel);
	}
	
	private void Init(){
		
		owlModelManager.addOntologyChangeListener(ontologyChangeListener);
		owlModelManager.addListener(modelManagerListener);
		InitInterPrologInteraction();
		InitOntology();
	}
	private void InitOntology(){
		try{
			_ontology = new Ontology(owlModelManager, outPutLog, progressLabel, Config.isDebug);
		}catch(Exception e){
			log.error(e);
		}
	}
	
	public static void dispose(){
		owlModelManager.removeOntologyChangeListener(ontologyChangeListener);
		owlModelManager.removeListener(modelManagerListener);
		
		Rules.dispose();
	}
	public void disposeQuery(){
		owlModelManager.removeOntologyChangeListener(ontologyChangeListener);
		owlModelManager.removeListener(modelManagerListener);
		_ontology.clear();
		_engine.shutdown();
		Rules.dispose();
	}
	
	public void printLog(String text) {
		if(Config.isDebug){
			printInfo(text);
		}
	}
	public void printInfo(String text) {
		outPutLog.append(text+Config.nl);
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
//			print("Please, set up your XSB_BIN_DIRECTORY"+Config.nl+" For mac os consider the example: launchctl setenv XSB_BIN_DIRECTORY /Full/Path/To/XSB/bin"+Config.nl);
			printInfo("Please, set up your XSB_BIN_DIRECTORY");
//			printInfo("Up until Mountain Lion (10.8) you can set them in");
//			printInfo("~/.MacOSX/environment.plist"+Config.nl);
//			printInfo("See:"+Config.nl);
//			printInfo("http://developer.apple.com/library/mac/#qa/qa1067/_index.html");
//			printInfo("http://developer.apple.com/library/mac/#documentation/MacOSX/Conceptual/BPRuntimeConfig/Articles/EnvironmentVars.html");
//			printInfo("For PATH in the Terminal, you should be able to set in .bash_profile or .profile (you'll probably have to create it though)");
//			printInfo("For mountain lion and beyond you need to use launchd and launchctl (http://david-martinez.tumblr.com/post/28083831730/environment-variables-and-mountain-lion)");
//			printInfo("consider the example: setenv XSB_BIN_DIRECTORY /Full/Path/To/XSB/config/bin");
			
			
		}
		startEngine(xsbBin);
		
	}
	
	private void startEngine(String xsbBin) {
		if(_engine!=null){
			
			_engine.shutdown();
			_engine = null;
		}
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

	public void queryXSB(){
		String command = queryString;
		checkEngine();
		if(isQueriable()){
			printInfo(command+Config.nl);
			if(command.endsWith(".")){
				command = command.substring(0, command.length()-1);
			}
			command = _ontology.prepareQuery(command);
//			previousQuery="";
			if(!command.equals(previousQuery)){
				previousQuery = command;
				printLog("prepared query: "+command);
				fillTableHeader(command);
				String detGoal = generateDetermenisticGoal(command);
				printLog("detGoal: "+detGoal);
				
				Object[] bindings = _engine.deterministicGoal(detGoal,"[TM]");
				TermModel list = (TermModel)bindings[0]; // this gets you the list as a binary tree
				TermModel[] flattted = list.flatList();
				
				ArrayList<String> row = new ArrayList<String>();
				String value;
				for(int i=0;i< flattted.length;i++){
					value = flattted[i].getChild(0).toString();	
					if(value.length()>0){
						row = new ArrayList<String>();
						row.add(value);
						for(int j=1; j<=_variablesList.size();j++){
							row.add(_ontology.getLabelByHash(flattted[i].getChild(j).toString()));
						}
						if(!_ontology.isAnyDisjointWithStatement())
							_answers.add(row);
						else{					
							if(value.equals("true") || value.equals("undefined")){
								printLog("SubDetGoal is: "+generateDetermenisticGoal(generateSubQuery(Utils._dAllrule(command), flattted[i])));
								Object[] subBindings = _engine.deterministicGoal(generateDetermenisticGoal(generateSubQuery(Utils._dAllrule(command), flattted[i])),"[TM]");
								TermModel subList = (TermModel)subBindings[0]; // this gets you the list as a binary tree
								TermModel[] subFlattted = subList.flatList();
								if(subFlattted.length>0){
									String subAnswer = subFlattted[0].getChild(0).toString();
									if(subAnswer.equals("no")){
										if(value.equals("true")){
											row.set(0, "inconsistent");
											_answers.add(row);
										}
									}else{
										_answers.add(row);
									}
								}else{
									if(value.equals("true")){
										row.set(0, "inconsistent");
										_answers.add(row);
									}
								}
							}else
								_answers.add(row);
						}
					}
					
				}
				if(flattted.length==0){
					clearTable();
					row = new ArrayList<String>();
					if(_engine.deterministicGoal(command))
						row.add("yes");
					else
						row.add("no");
					_answers.add(row);
				}
				if(_answers.size()==0){
					clearTable();
					row = new ArrayList<String>();
					row.add("no");
					_answers.add(row);
				}
				fillTable(0);
				/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	log.info("filter table:");
		            	log.info(rowFilter);
		            	
	            		tableSorter.setRowFilter(rowFilter);
	            		tableSorter.sort();
		            }
				});
				*/
			}
		}
		
	}
	private void checkEngine(){
		if(!isCompiled){
			try {
				progressFrame.setVisible(true);
				_ontology.proceed();
				_ontology.appendRules(Rules.getRules());
				compileFile(_ontology.Finish());
				isOntologyChanged=false;
				Rules.isRulesOntologyChanged = false;
				progressFrame.setVisible(false);
				_ontology.printAllLabels();
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				progressFrame.setVisible(false);
			}
			
		}else if(isChanged()){
			int dialogResult = JOptionPane.showConfirmDialog (null, "Some changes have been made, would you like to recompile?", "Warning", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				try {
					boolean disjointStatement = _ontology.isAnyDisjointWithStatement();
					if(isOntologyChanged){
						progressFrame.setVisible(true);
						_ontology.PrepareForTranslating();
						_ontology.proceed();
						isOntologyChanged = false;
						log.info("Ontology recompilation");
						progressFrame.setVisible(false);
					}
					if(disjointStatement != _ontology.isAnyDisjointWithStatement() || Rules.isRulesOntologyChanged){
						_ontology.appendRules(Rules.getRules());
						log.info("Rule recompilation");
						Rules.isRulesOntologyChanged = false;
					}
					previousQuery="";
					compileFile(_ontology.Finish());
					_ontology.printAllLabels();
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					progressFrame.setVisible(false);
					previousQuery="";
				}
			}
		}
	}
	
	private String generateDetermenisticGoal(String command){
		String detGoal = "findall(myTuple(TV";
		if(_variablesList.size()>0){
			detGoal+=", ";
			detGoal+=_variablesList.toString().replace("[", "").replace("]", "");
		}
		detGoal+="), call_tv(("+command+"), TV), List), buildTermModel(List,TM)";
		
		return detGoal;
	}
	
	private String generateSubQuery(String command, TermModel model){
		String result = "";
		int index;
		String vars = "";
		if(_variablesList.size()>0){
			for(String s: command.split("\\)\\s*,")){
				index = s.lastIndexOf("(");
				if(index>0){
					result += s.substring(0, index);
					vars = s.substring(index+1, s.length());
					for(int j=1; j<=_variablesList.size();j++){
						vars = vars.replace(_variablesList.get(j-1), model.getChild(j).toString());
					}
					result +="("+vars;
					if(!result.endsWith(")"))
						result+=")";
					result+=", ";
					
				}else
					result += s+", ";
			}
			result = result.substring(0, result.length()-2);
			return result;
		}
		return command;
	}
	
	public void query(String command, RowFilter<DefaultTableModel,Object> filter){
		queryString = command;
		rowFilter = filter;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	queryXSB = new QueryXSB();
                queryXSB.execute();
            }
        });
	
	}
	
	private void sendSemiColomn(){
		((SubprocessEngine)_engine).sendAndFlushLn(";");
	}
	public boolean compileFile(File file) {
		InitInterPrologInteraction();
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
		if(!s.contains("++Error[XSB/Runtime/P]")){
			if(s.contains(" = "))//{
				sendSemiColomn();
		}
	}
	
	public void clearTable(){
		clearTableBody();
		outTableModel.setColumnCount(0);
		_variablesList = new ArrayList<String>();
		outTableModel.addColumn("valuation");
		_answers = new ArrayList<ArrayList<String>>();
	}
	private void clearTableBody(){
		for(int i=outTableModel.getRowCount()-1;i>=0;i--){
			outTableModel.removeRow(i);
		}
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
//    				_variables.add(s);
    				if(Character.isUpperCase(s.charAt(0)) && !_variablesList.contains(s))
    					_variablesList.add(s);
    			}
            }
            sb.setLength(0);
            for(String s: _variablesList){
        		outTableModel.addColumn(s);
            }
		} catch (Exception e) {
			log.error("fillTableHeader: "+e.toString());
		}
	}
	public void fillTable(final int rowCount){
		
		try{
			clearTableBody();
			for(ArrayList<String> row :_answers){
//				log.info("row to add: "+Arrays.deepToString(row.toArray()));
				outTableModel.addRow(row.toArray());
				if(rowCount==1)
					break;
			}
		}catch(Exception e){
			clearTable();
			ArrayList<String> row = new ArrayList<String>();
			row.add("no");
			outTableModel.addRow(row.toArray());
			log.error( "FillTable: "+e.toString());
		}
	}
	public void showProgressFrame() {
    	progressFrame.setVisible(true);
    	progressFrame.validate();
    	progressFrame.repaint();
	}
	public void hideProgressFrame() {
//		SwingUtilities.invokeLater(new Runnable() {
//		    public void run() {
		    	progressFrame.setVisible(false);
//		    }
//		});
	}
	
	class QueryXSB extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            try {
				queryXSB();
			} catch (Exception e) {
				log.error("Query: ");
				e.printStackTrace();
			}
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
        	progressFrame.setVisible(false);
        }
    }
 
	
}	
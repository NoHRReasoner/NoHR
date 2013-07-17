package hybrid.query.model;
import hybrid.query.views.Rules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import local.translate.Ontology;
import local.translate.OntologyLogger;
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

import union.logger.UnionLogger;

import com.declarativa.interprolog.TermModel;

public class Query{
	private static OWLModelManager owlModelManager;
	private static boolean isOntologyChanged;
	private boolean isCompiled=false;
	private QueryEngine queryEngine;
	private Ontology _ontology;
	private ArrayList<String> _variablesList = new ArrayList<String>();
	private ArrayList<ArrayList<String>> _answers = new ArrayList<ArrayList<String>>();
	private Pattern headerPattern = Pattern.compile("\\((.*?)\\)");
	private String queryString;
	private String previousQuery = "";
	private static final Logger log = Logger.getLogger(Query.class);
	
	public Query(OWLModelManager OwlModelManager) throws Exception{
		owlModelManager = OwlModelManager;
		owlModelManager.addOntologyChangeListener(ontologyChangeListener);
		owlModelManager.addListener(modelManagerListener);
//		queryEngine = new QueryEngine();
		
		log.setLevel(Config.logLevel);
	}
	
	private void InitOntology(){
		try{
			_ontology = new Ontology(owlModelManager);
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
		if(_ontology!=null)
			_ontology.clear();
		if(queryEngine!=null)
			queryEngine.shutdown();
		Rules.dispose();
	}
	
	public void printLog(String text) {
		if(Config.isDebug){
			printInfo(text);
		}
	}
	public void printInfo(String text) {
		//outPutLog.append(text+Config.nl);
		log.info(text);
		UnionLogger.logger.log(text);
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
	

	public ArrayList<ArrayList<String>> queryXSB(){
		String command = queryString;
		checkAndStartEngine();
		if(isQueriable()){
			printInfo("You queried: "+command);
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
				String subDetGoal;
				printLog("detGoal: "+detGoal);
				Date queryStart = new Date();
				Date subQueryTime;
				Object[] bindings = queryEngine.deterministicGoal(detGoal);
				OntologyLogger.getDiffTime(queryStart, "Main query time: ");
				ArrayList<String> row = new ArrayList<String>();
				String value;
				if(bindings!=null){
					
					TermModel list = (TermModel)bindings[0]; // this gets you the list as a binary tree
					TermModel[] flattted = list.flatList();
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
//									printLog("_dRule: "+Utils._dAllrule(command));
									subDetGoal = generateDetermenisticGoal(generateSubQuery(Utils._dAllrule(command), flattted[i]));
									printLog("SubDetGoal is: "+subDetGoal);
									subQueryTime = new Date();
									Object[] subBindings = queryEngine.deterministicGoal(subDetGoal);
									OntologyLogger.getDiffTime(subQueryTime, "Doubled subgoal time: ");
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
						if(queryEngine.deterministicGoalBool(command))
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
				}else{
					clearTable();
					row = new ArrayList<String>();
					row.add("no");
					_answers.add(row);
					log.error("Query was interrupted by engine.");
					try {
						compileFile(_ontology.Finish());
					} catch (IOException e) {
						log.error(e);
					} catch (Exception e) {
						log.error(e);
					}
				}
				OntologyLogger.getDiffTime(queryStart, "Total query time: ");
			}
		}
		return getData();
	}
	private void checkAndStartEngine(){
		if(!isCompiled){
			try {
				Date initAndTranslateTime = new Date();
				InitOntology();
				_ontology.proceed();
				_ontology.appendRules(Rules.getRules());
				File xsbFile = _ontology.Finish();
				OntologyLogger.getDiffTime(initAndTranslateTime, "Total translating time: ");
				OntologyLogger.log("");
				compileFile(xsbFile);
				isOntologyChanged=false;
				Rules.isRulesOntologyChanged = false;
//				_ontology.printAllLabels();
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
			}
		}else if(isChanged()){
			int dialogResult = JOptionPane.showConfirmDialog (null, "Some changes have been made, would you like to recompile?", "Warning", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				try {
					boolean disjointStatement = _ontology.isAnyDisjointWithStatement();
					Date initAndTranslateTime = new Date();
					if(isOntologyChanged){
						_ontology.PrepareForTranslating();
						_ontology.proceed();
						isOntologyChanged = false;
						log.info("Ontology recompilation");
					}
					if(disjointStatement != _ontology.isAnyDisjointWithStatement() || Rules.isRulesOntologyChanged){
						_ontology.appendRules(Rules.getRules());
						log.info("Rule recompilation");
						Rules.isRulesOntologyChanged = false;
					}
					File xsbFile = _ontology.Finish();
					OntologyLogger.getDiffTime(initAndTranslateTime, "Total translating time: ");
					OntologyLogger.log("");
					compileFile(xsbFile);
//					_ontology.printAllLabels();
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
	
	public ArrayList<ArrayList<String>> query(String command){
		queryString = command;
		return queryXSB();
		
	}
	public boolean compileFile(File file) throws Exception {
		Date loadingFileTime = new Date();
		queryEngine = new QueryEngine();
		isCompiled=false;
		if(queryEngine.isEngineStarted() && queryEngine.load(file))
			isCompiled=true;
		if(isQueriable())
			queryEngine.deterministicGoal(generateDetermenisticGoal("initQuery"));
		OntologyLogger.getDiffTime(loadingFileTime, "XSB loading file time: ");
		OntologyLogger.log("");
		return isCompiled;
	}
	
	public boolean isQueriable(){
		return queryEngine!=null && queryEngine.isEngineStarted() && isCompiled;
	}
	
	public void clearTable(){
		_variablesList = new ArrayList<String>();
		_answers = new ArrayList<ArrayList<String>>();
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
    				if(Character.isUpperCase(s.charAt(0)) && !_variablesList.contains(s))
    					_variablesList.add(s);
    			}
            }
            sb.setLength(0);

		} catch (Exception e) {
			log.error("fillTableHeader: "+e.toString());
		}
	}
	
	private ArrayList<ArrayList<String>> getData(){
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<String>> rows = (ArrayList<ArrayList<String>>) _answers.clone();
		rows.add(0, _variablesList);
		return rows;
	}
}	
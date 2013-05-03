package hybrid.query.views;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

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
	private static OWLModelManager _owlModelManager;
	private static boolean isOntologyChanged;
	private boolean isCompiled;
	private boolean isEngineStarted;
	private XSBSubprocessEngine _engine;
	private Ontology _ontology;
	
	public Query(OWLModelManager owlModelManager, JTextArea textArea) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
		_owlModelManager = owlModelManager;
		_outPutLog = textArea;
		Init();
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
			int dialogResult = JOptionPane.showConfirmDialog (null, "Some changes has been made, would you like to recompile?", "Warning", JOptionPane.YES_NO_OPTION);
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
			((SubprocessEngine)_engine).sendAndFlushLn(command+".");
		}
		
		
		
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
		printInfo(s);
	}
}

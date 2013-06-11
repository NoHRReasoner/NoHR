package hybrid.query.model;

import java.io.File;

import com.declarativa.interprolog.XSBSubprocessEngine;

public class QueryEngine {
	private XSBSubprocessEngine _engine;
	private boolean isEngineStarted;
	
	
	public QueryEngine() throws Exception{
		String xsbBin = System.getenv("XSB_BIN_DIRECTORY");
		printLog("Starting query engine"+Config.nl);
		printLog(Config.tempDir+Config.nl);
		
		if(xsbBin!=null){
			xsbBin+="/xsb";
		}else{
			throw new Exception("Please, set up your XSB_BIN_DIRECTORY");
		}
		startEngine(xsbBin);
	}
	
	private void startEngine(String xsbBin) throws Exception {
		if(_engine!=null){
			
			_engine.shutdown();
			_engine = null;
		}
		isEngineStarted=true;
		try{
			_engine = new XSBSubprocessEngine(xsbBin);
			//_engine.addPrologOutputListener(this);
			printLog("Engine started"+Config.nl);
		}catch(Exception e){
			isEngineStarted=false;
			throw new Exception("Query Engine was not started"+Config.nl+e.toString()+Config.nl);
		}
	}
	
	public void shutdown() {
		_engine.shutdown();
	}
	public Object[] deterministicGoal(String detGoal){
		return _engine.deterministicGoal(detGoal,"[TM]");
	}
	public boolean deterministicGoalBool(String command){
		return _engine.deterministicGoal(command);
	}
	public boolean load(File file){
		return _engine.load_dynAbsolute(file);
	}
	public boolean isEngineStarted(){
		return isEngineStarted;
	}
	private void printLog(String message){
		
	}
}

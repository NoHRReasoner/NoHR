package local.interprolog;
import java.io.File;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.owl.examples.tab.ExampleViewComponent;

import local.interprolog.*;

import com.declarativa.interprolog.AbstractPrologEngine;
import com.declarativa.interprolog.PrologOutputListener;
import com.declarativa.interprolog.SubprocessEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPAbortedException;
import com.declarativa.interprolog.util.IPException;
import com.declarativa.interprolog.util.IPInterruptedException;
import com.xsb.interprolog.*;
public class InterPrologInteraction implements PrologOutputListener{
	private XSBSubprocessEngine _xsbSubprocessEngine;
	private AbstractPrologEngine _engine;
	private NativeEngine _nativeEngine;
	private boolean _isQueryable=false;
	private String _xsbBin;
	public boolean isXSBbin=false;
	private static final Logger log =Logger.getLogger(ExampleViewComponent.class);
	private final JTextArea _textArea;
	private final JTextField _textField;
	private String nl = System.getProperty("line.separator");
	public InterPrologInteraction(final JTextArea textArea, JTextField textField) {
		_textArea=textArea;
		_textField=textField;
		_xsbBin = System.getenv("XSB_BIN_DIRECTORY");
		
		if(_xsbBin!=null){
			_xsbBin+="/xsb";
			isXSBbin = true;
		}else
			_textArea.append("Please, set up your XSB_BIN_DIRECTORY\n For mac os consider the example: launchctl setenv XSB_BIN_DIRECTORY /Full/Path/To/XSB/bin");
		if(!startEngine()){
	    	_textArea.append("Query Engine was not started\n");
	    }
	}
	
	public boolean startEngine() {
		boolean engineStarted=true;
		try{
			_xsbSubprocessEngine = new XSBSubprocessEngine(_xsbBin);
			_xsbSubprocessEngine.addPrologOutputListener(this);
		}catch(Exception e){
			engineStarted=false;
			_textArea.append(e.toString()+nl);
		}
		return engineStarted;
	}
	
	public boolean setConsultAbsolute(File file) {
		if(_xsbSubprocessEngine.load_dynAbsolute(file))
//		if(_xsbSubprocessEngine.consultAbsolute(file))
//		if(_nativeEngine.consultAbsolute(file))
			_isQueryable=true;
		return _isQueryable;
	}
	public void makeQuery(String command){
		_textArea.append(command+nl);
		((SubprocessEngine)_xsbSubprocessEngine).sendAndFlushLn(command);
	}
	 protected String formatGoalResult(Object[] bindings){
         if (bindings == null) {return("FAILED\n");}
         else return(bindings[0].toString()+"\n");
     }
	 protected void focusInput(){
		 _textField.selectAll();
		 _textField.requestFocus();
	 }

	@Override
	public void print(String s) {
		_textArea.append(s+nl);
	}
}


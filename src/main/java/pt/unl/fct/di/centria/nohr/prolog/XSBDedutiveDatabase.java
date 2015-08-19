/**
 *
 */
package pt.unl.fct.di.centria.nohr.prolog;

import java.io.File;
import java.util.concurrent.Callable;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

/**
 * @author Nuno Costa
 */
public class XSBDedutiveDatabase extends PrologDedutiveDatabase {

	/**
	 * @param binDirectory
	 * @throws IPException
	 * @throws DatabaseCreationException
	 */
	public XSBDedutiveDatabase(File binDirectory) throws IPException, DatabaseCreationException {
		super(binDirectory);
	}

	@Override
	protected PrologEngine createPrologEngine() throws IPException, DatabaseCreationException {
		final PrologEngine prologEngine = tryPrologEngineCreation(new Callable<PrologEngine>() {

			@Override
			public XSBSubprocessEngine call() throws Exception {
				return new XSBSubprocessEngine(binDirectory.toPath().toAbsolutePath().toString());
			}
		});
		final DedutiveDatabase self = this;
		prologEngine.consultFromPackage("startup", self);
		prologEngine.deterministicGoal("set_prolog_flag(unknown, fail)");
		return prologEngine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabase#isTrivalued()
	 */
	@Override
	public boolean isTrivalued() {
		return true;
	}

}

/**
 *
 */
package pt.unl.fct.di.centria.nohr.deductivedb;

import java.io.File;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.centria.nohr.reasoner.VocabularyMapping;

/**
 * Implements an {@link DeductiveDatabaseManager} backed by a XSB Prolog system.
 *
 * @author Nuno Costa
 */
public class XSBDeductiveDatabaseManager extends PrologDeductiveDatabaseManager {

	/**
	 * Constructs a {@link DeductiveDatabaseManager} with the XSB Prolog system located in a given directory as underlying Prolog engine.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system that will be used as underlying Prolog engine is located.
	 * @throws IPException
	 *             if some exception was thrown by the Interprolog API.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out. That could mean that the Prolog system located at {@code binDirectory}
	 *             isn't an operational Prolog system.
	 */
	public XSBDeductiveDatabaseManager(File binDirectory, VocabularyMapping vocabularyMapping)
			throws IPException, PrologEngineCreationException {
		super(binDirectory, "startup", vocabularyMapping);
	}

	@Override
	protected PrologEngine createPrologEngine() {
		return new XSBSubprocessEngine(binDirectory.toPath().toAbsolutePath().toString());
	}

	@Override
	public boolean hasWFS() {
		return true;
	}

	@Override
	protected void initializePrologEngine() {
		prologEngine.deterministicGoal("set_prolog_flag(unknown, fail)");
	}

}

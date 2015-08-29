package pt.unl.fct.di.centria.nohr.deductivedb;

import static pt.unl.fct.di.centria.nohr.model.concrete.Model.*;

import java.io.File;
import java.io.IOException;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;

/**
 * Implements an {@link DeductiveDatabase} backed by a XSB Prolog system.
 *
 * @author Nuno Costa
 */
public class XSBDeductiveDatabase extends PrologDeductiveDatabase {

	/**
	 * Constructs a {@link DeductiveDatabase} with the XSB Prolog system located in a given directory as underlying Prolog engine.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system that will be used as underlying Prolog engine is located.
	 * @throws IPException
	 *             if some exception was thrown by the Interprolog API.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out. That could mean that the Prolog system located at {@code binDirectory}
	 *             isn't an operational Prolog system.
	 * @throws IOException
	 */
	public XSBDeductiveDatabase(File binDirectory, VocabularyMapping vocabularyMapping)
			throws PrologEngineCreationException {
		super(binDirectory, "xsbmodule", new XSBFormatVisitor(), vocabularyMapping);
	}

	@Override
	protected PrologEngine createPrologEngine() {
		return new XSBSubprocessEngine(binDirectory.toPath().toAbsolutePath().toString());
	}

	@Override
	protected String failRule(Predicate pred) {
		return rule(atom(pred), atom("fail")).accept(formatVisitor);
	}

	@Override
	public boolean hasWFS() {
		return true;
	}

	@Override
	protected void initializePrologEngine() {
		prologEngine.deterministicGoal("set_prolog_flag(unknown, fail)");
	}

	@Override
	protected void load() {
		if (!prologEngine.load_dynAbsolute(file.getAbsoluteFile()))
			// if (!prologEngine.deterministicGoal("load_dyn('" + file.getAbsolutePath().toString() + "')"))
			throw new IPException("file not loaded");
	}

	@Override
	protected String tableDirective(Predicate pred) {
		return ":- table " + pred.accept(formatVisitor) + "/" + pred.getArity() + " as subsumptive.";
	}

}

package pt.unl.fct.di.novalincs.nohr.deductivedb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import static pt.unl.fct.di.novalincs.nohr.model.Model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

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
	public XSBDeductiveDatabase(File binDirectory, Vocabulary vocabularyMapping) throws PrologEngineCreationException {
		super(binDirectory, "xsbmodule", new XSBFormatVisitor(), vocabularyMapping);
	}

	@Override
	protected PrologEngine createPrologEngine() {
		return new XSBSubprocessEngine(binDirectory.toPath().toAbsolutePath().toString());
	}

	@Override
	protected String failRule(Predicate pred) {
		return rule(atom(pred), atom(vocabulary, "fail")).accept(formatVisitor);
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
		File dest = new File("C:\\Users\\VedranPC\\Desktop\\rules.txt");
		try {
			//connection to the database
			FileWriter  out = new FileWriter (file,true);
			out.write(":- import odbc_open/3 from odbc_call.\n"+
			":- import odbc_sql/3 from odbc_call.\n"+
			":- import odbc_import/2 from odbc_call.\n"+
			":- import odbc_close/0 from odbc_call.\n"+
			":- import odbc_data_sources/2 from odbc_call.\n"+
			"?- odbc_open(test,root,root).\n"+
//			"?- odbc_import(crimes('id', 'Case_Number'), aq01a).\n"+
//			"?- odbc_import(crimes('id', 'Case_Number'), dq01d).\n"+
//			"?- odbc_import(crimesindex('id', 'Case_Number'), aq02a).\n"+
//			"?- odbc_import(crimesindex('id', 'Case_Number'), dq02d).\n"+
//			"aq11(X) :- odbc_sql([1],'SELECT id FROM test.crimes where Case_Number=\\\'HP409947\\\'', [X]).\n"+
//			"aq12(X) :- odbc_sql([1],'SELECT id FROM test.crimesindex where Case_Number=\\\'HP409947\\\'', [X]).\n"+
//			"aq21(X) :- odbc_sql([1],'SELECT Case_Number FROM test.crimes where id=\\\'6320453\\\'', [X]).\n"+
			"aq22(X) :- odbc_sql([1],'SELECT Case_Number FROM test.crimesindex where id=\\\'6320453\\\'', [X]).\n"+
			"dq22(X) :- odbc_sql([1],'SELECT Case_Number FROM test.crimesindex where id=\\\'6320453\\\'', [X]).\n"+
//			"aq31(X,Y) :- odbc_sql([1],'SELECT id, Case_Number FROM test.crimes', [X,Y]).\n"+
//			"aq32(X,Y) :- odbc_sql([1],'SELECT id, Case_Number FROM test.crimesindex', [X,Y]).\n"+
			"anew3(X,Y) :- nonvar(X),nonvar(Y),odbc_sql([X,Y],'SELECT id, Case_Number FROM test.crimesindex where id = ? and Case_Number = ?', [X,Y]).\n"+
			"anew3(X,Y) :- nonvar(X),odbc_sql([X],'SELECT id, Case_Number FROM test.crimesindex where id = ?', [X,Y]).\n"+
			"anew3(X,Y) :- nonvar(Y),odbc_sql([Y],'SELECT id, Case_Number FROM test.crimesindex where Case_Number = ?', [X,Y]).\n"+
			"dnew3(X,Y) :- nonvar(X),nonvar(Y),odbc_sql([X,Y],'SELECT id, Case_Number FROM test.crimesindex where id = ? and Case_Number = ?', [X,Y]).\n"+
			"dnew3(X,Y) :- nonvar(X),odbc_sql([X],'SELECT id, Case_Number FROM test.crimesindex where id = ?', [X,Y]).\n"+
			"dnew3(X,Y) :- nonvar(Y),odbc_sql([Y],'SELECT id, Case_Number FROM test.crimesindex where Case_Number = ?', [X,Y]).\n");
			out.close();
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			System.err.println("Greskaa!");
		    e.printStackTrace();
		}
		if (!prologEngine.load_dynAbsolute(file.getAbsoluteFile()))
			throw new IPException("file not loaded");
	}

	@Override
	protected String tableDirective(Predicate pred) {
		return ":- table " + pred.accept(formatVisitor) + "/" + pred.getArity() + " as subsumptive.";
	}

}

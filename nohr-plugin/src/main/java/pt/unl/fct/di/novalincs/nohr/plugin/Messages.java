/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JOptionPane;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.parsing.TokenType;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * The messages shown in the NoHR views.
 *
 * @author Nuno Costa
 */
public class Messages {

	/** The message shown when the user must check the XSB installation */
	private static String CHECK_XSB_INSTALLATION = System.lineSeparator()
			+ "Please make sure that the chosen XSB directory corresponds to a working XSB installation.";

	public static void invalidExpression(Component parent, final PrologParserException e) {
		final String msg = String.format("Syntax error at line %d column %d." + System.lineSeparator() + e.getMessage(),
				e.getLineNumber(), e.getStringPosition());
		JOptionPane.showMessageDialog(parent, msg, "Syntax error", JOptionPane.WARNING_MESSAGE);
	}

	/** The message shown when a rule or query expression is invalid. */
	public static String invalidExpressionMessage(String str, final ParseException e) {
		String result;
		if (e.getBegin() < str.length())
			result = String.format("Encountered %s at column %d. Expecting one of: ",
					str.substring(e.getBegin(), e.getEnd()), e.getBegin());
		else
			result = "Expecting one of: ";
		for (final TokenType tokenType : e.getExpectedTokens())
			result += System.lineSeparator() + "\t" + tokenType;
		return result;
	}

	/**
	 * The message shown when a given expected XSB directory is not found.
	 *
	 * @param parent
	 * @param notFoundPath
	 */
	public static void invalidXSBDirectory(Component parent, Path notFoundPath) {
		JOptionPane.showMessageDialog(parent, notFoundPath.toString() + " not found. " + CHECK_XSB_INSTALLATION, "XSB",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * The message shown when the {@code XSB/config/} directory has more than one subdirectory.
	 *
	 * @param parent
	 * @param platforms
	 * @return the chosen platform.
	 */
	public static String selectPlataform(Component parent, final String[] platforms) {
		return (String) JOptionPane.showInputDialog(parent, "Please select a platform", "Platform",
				JOptionPane.PLAIN_MESSAGE, null, platforms, platforms[0]);
	}

	/**
	 * The message shown when a ParseException occurs while trying to load rule files.
	 *
	 * @param rulesViewComponent
	 * @param e
	 */
	public static void unsucceccfulLoad(RulesViewComponent rulesViewComponent) {
		JOptionPane.showMessageDialog(rulesViewComponent,
				"Could not load the rules.",
				"Corrupted file", JOptionPane.WARNING_MESSAGE);
	}
	
	
	
	/**
	 * The message shown when a IOException occurs while trying to save the rules.
	 *
	 * @param rulesViewComponent
	 * @param e
	 */
	public static void unsucceccfulSave(RulesViewComponent rulesViewComponent, IOException e) {
		JOptionPane.showMessageDialog(rulesViewComponent,
				"Could not save the rules." + System.lineSeparator() + System.lineSeparator() + e.getMessage(),
				"Unsuccessful save", JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * The message shown when a IOException occurs while trying to save the DB mapping.
	 *
	 * @param dbMappingViewComponent
	 * @param e
	 */
	public static void unsucceccfulSave(DBMappingViewComponent dbMappingViewComponent, IOException e) {
		JOptionPane.showMessageDialog(dbMappingViewComponent,
				"Could not save the DB mapping." + System.lineSeparator() + System.lineSeparator() + e.getMessage(),
				"Unsuccessful save", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void invalidmappingFile(Component parent, final Exception e) {
		JOptionPane.showMessageDialog(parent, e.getMessage(), "File error", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * The message shown when the ontology has some axiom of an unsupported type.
	 *
	 * @param parent
	 * @param exception
	 */
	public static void violations(Component parent, UnsupportedAxiomsException exception) {
		final String unsupportedList;
		if (exception.getUnsupportedAxioms().size() < 10)
			unsupportedList = StringUtils.concat(System.lineSeparator(), exception.getUnsupportedAxioms().toArray());
		else
			unsupportedList = "";
		JOptionPane.showMessageDialog(parent,
				"The active ontology contains some unsupported axioms." + System.lineSeparator() + unsupportedList,
				"Unsupported Axioms", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * The message shown when the XSB bin directory has not been set yet.
	 *
	 * @param parent
	 */
	public static void xsbBinDirectoryNotSet(Component parent) {
		JOptionPane.showMessageDialog(parent, "Please open the Preferences panel and set the XSB directory.", "XSB",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * The message shown when the creation of the underlying Prolog engine instance fails.
	 *
	 * @param parent
	 * @param e
	 */
	public static void xsbDatabaseCreationProblems(Component parent, PrologEngineCreationException e) {
		JOptionPane.showMessageDialog(parent, "Can not run the XSB." + CHECK_XSB_INSTALLATION, "XSB",
				JOptionPane.ERROR_MESSAGE);
	}

}

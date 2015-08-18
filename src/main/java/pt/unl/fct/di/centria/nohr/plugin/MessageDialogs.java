/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JOptionPane;

import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.centria.nohr.StringUtils;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabaseCreationException;

/**
 * @author nunocosta
 */
public class MessageDialogs {

	private static String CHECK_XSB_INSTALLATION = System.lineSeparator()
			+ "Please make sure that the chosen XSB directory corresponds to an working XSB installation.";

	public static void invalidXSBDirectory(Component parent, Path notFoundPath) {
		JOptionPane.showMessageDialog(parent, notFoundPath.toString() + " not found. " + CHECK_XSB_INSTALLATION, "XSB",
				JOptionPane.WARNING_MESSAGE);
	}

	public static String selectPlataform(Component parent, final String[] platforms) {
		return (String) JOptionPane.showInputDialog(parent, "Please select a platform", "Platform",
				JOptionPane.PLAIN_MESSAGE, null, platforms, platforms[0]);
	}

	public static void translationFileProblems(Component parent, IOException exception) {
		final String msg = exception.getMessage();
		JOptionPane.showMessageDialog(parent, msg, "Translation File", JOptionPane.ERROR_MESSAGE);
	}

	public static boolean violations(Component parent, UnsupportedAxiomsException exception) {
		final String unsupportedList = StringUtils.concat(", ", exception.getUnsupportedAxioms().toArray());
		return JOptionPane.showConfirmDialog(parent,
				"The following axioms aren't supported:" + System.lineSeparator() + unsupportedList
						+ System.lineSeparator() + "Do you want proceed ignoring them?",
				"Unsupported Axioms", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

	public static void xsbBinDirectoryNotDefined(Component parent) {
		JOptionPane.showMessageDialog(parent, "Please open the Preferences panel and set the XSB directory.", "XSB",
				JOptionPane.WARNING_MESSAGE);
	}

	public static void xsbDatabaseCreationProblems(Component parent, XSBDatabaseCreationException e) {
		JOptionPane.showMessageDialog(parent, "Can not run the XSB." + CHECK_XSB_INSTALLATION, "XSB",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void xsbProblems(Component parent, IPException e) {
		JOptionPane.showMessageDialog(parent, e.getMessage() + CHECK_XSB_INSTALLATION, "XSB",
				JOptionPane.ERROR_MESSAGE);
	}

}

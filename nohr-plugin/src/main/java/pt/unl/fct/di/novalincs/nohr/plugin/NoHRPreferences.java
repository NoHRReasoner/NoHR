/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin;

import java.io.File;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Represents the NoHR preferences.
 *
 * @author Nuno Costa
 */
public class NoHRPreferences {

	private static NoHRPreferences instance;

	private static final String XSB_BIN_DIRECTORY = "XSB_DIR";

	public static synchronized NoHRPreferences getInstance() {
		if (instance == null)
			instance = new NoHRPreferences();
		return instance;
	}

	private Preferences getPreferences() {
		return PreferencesManager.getInstance().getApplicationPreferences(this.getClass());
	}

	public File getXSBBinDirectory() {
		final String pathname = getPreferences().getString(XSB_BIN_DIRECTORY, null);
		if (pathname == null)
			return null;
		return new File(pathname);
	}

	public void setXSBBinDirectory(File xsbBinDirectory) {
		if (xsbBinDirectory != null)
			getPreferences().putString(XSB_BIN_DIRECTORY, xsbBinDirectory.getAbsolutePath());
	}

}

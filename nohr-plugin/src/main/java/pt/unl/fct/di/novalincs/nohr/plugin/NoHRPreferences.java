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

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
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

/**
 * Represents the NoHR preferences.
 *
 * @author Nuno Costa
 */
public final class NoHRPreferences {

    private static NoHRPreferences instance;

    private static final String DL_INFERENCE_ENGINE = "DL_INFERENCE_ENGINE";

    private static final String DL_INFERENCE_ENGINE_EL = "DL_INFERENCE_ENGINE_EL";

    private static final String DL_INFERENCE_ENGINE_QL = "DL_INFERENCE_ENGINE_QL";

    private static final String KONCLUDE_BINARY = "KONCLUDE_BINARY";

    private static final String XSB_DIRECTORY = "XSB_DIR";

    private NoHRPreferences() {
    }

    public NoHRHybridKBConfiguration getConfiguration() {
        return new NoHRHybridKBConfiguration(getXsbDirectory(), getKoncludeBinary(), getDLInferenceEngineEL(), getDLInferenceEngineQL(), getDLInferenceEngine());
    }

    public DLInferenceEngine getDLInferenceEngine() {
        return DLInferenceEngine.getDLMode(getPreferences().getString(DL_INFERENCE_ENGINE, "HERMIT"));
    }

    public static synchronized NoHRPreferences getInstance() {
        if (instance == null) {
            instance = new NoHRPreferences();
        }

        return instance;
    }

    public File getKoncludeBinary() {
        final String pathname = getPreferences().getString(KONCLUDE_BINARY, null);

        if (pathname == null) {
            return null;
        }

        return new File(pathname);
    }

    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(this.getClass());
    }

    public boolean getDLInferenceEngineEL() {
        return getPreferences().getBoolean(DL_INFERENCE_ENGINE_EL, false);
    }

    public boolean getDLInferenceEngineQL() {
        return getPreferences().getBoolean(DL_INFERENCE_ENGINE_QL, false);
    }

    public File getXsbDirectory() {
        final String pathname = getPreferences().getString(XSB_DIRECTORY, null);

        if (pathname == null) {
            return null;
        }

        return new File(pathname);
    }

    public void setDLInferenceEngine(DLInferenceEngine value) {
        if (value != null) {
            getPreferences().putString(DL_INFERENCE_ENGINE, value.toString());
        }
    }

    public void setKoncludeBinary(File value) {
        if (value != null) {
            getPreferences().putString(KONCLUDE_BINARY, value.getAbsolutePath());
        }
    }

    public void setDLInferenceEngineEL(boolean value) {
        getPreferences().putBoolean(DL_INFERENCE_ENGINE_EL, value);
    }

    public void setDLInferenceEngineQL(boolean value) {
        getPreferences().putBoolean(DL_INFERENCE_ENGINE_QL, value);
    }

    public void setXsbDirectory(File value) {
        if (value != null) {
            getPreferences().putString(XSB_DIRECTORY, value.getAbsolutePath());
        }
    }
}

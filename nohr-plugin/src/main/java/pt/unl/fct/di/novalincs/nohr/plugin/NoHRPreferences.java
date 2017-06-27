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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.model.AxiomType;
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
    private static final String DL_INFERENCE_ENGINE_RL = "DL_INFERENCE_ENGINE_RL";
    private static final String IGNORE_ALL_UNSUPPORTED_AXIOMS = "IGNORE_ALL_UNSUPPORTED_AXIOMS";
    private static final String IGNORED_UNSUPPORTED_AXIOMS = "IGNORED_UNSUPPORTED_AXIOMS";
    private static final String KONCLUDE_BINARY = "KONCLUDE_BINARY";
    private static final String XSB_DIRECTORY = "XSB_DIR";

    private final AxiomType<?>[] ignorableAxioms;

    private NoHRPreferences() {
        ignorableAxioms = new AxiomType<?>[]{
            AxiomType.DATA_PROPERTY_RANGE,
            AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
            AxiomType.OBJECT_PROPERTY_RANGE
        };
    }

    public NoHRHybridKBConfiguration getConfiguration() {
        final NoHRHybridKBConfiguration configuration = new NoHRHybridKBConfiguration(getXsbDirectory(), getKoncludeBinary(), getDLInferenceEngineEL(), getDLInferenceEngineQL(), false, getDLInferenceEngine());

        configuration.getOntologyTranslationConfiguration().setIgnoreAllunsupportedAxioms(this.getIgnoreAllUnsupportedAxioms());
        final Set<AxiomType<?>> ignoredUnsupportedAxioms = this.getIgnoredUnsupportedAxioms();

        for (AxiomType<?> i : ignorableAxioms) {
            boolean containedAxiom = configuration.getOntologyTranslationConfiguration().getIgnoredUnsupportedAxioms().contains(i);
            boolean containsAxiom = ignoredUnsupportedAxioms.contains(i);

            if (containedAxiom && !containsAxiom) {
                configuration.getOntologyTranslationConfiguration().getIgnoredUnsupportedAxioms().remove(i);
            } else if (!containedAxiom && containsAxiom) {
                configuration.getOntologyTranslationConfiguration().getIgnoredUnsupportedAxioms().add(i);
            }
        }

        return configuration;
    }

    public DLInferenceEngine getDLInferenceEngine() {
        return DLInferenceEngine.getDLInferenceEngine(getPreferences().getString(DL_INFERENCE_ENGINE, "HERMIT"));
    }

    public boolean getDLInferenceEngineEL() {
        return getPreferences().getBoolean(DL_INFERENCE_ENGINE_EL, false);
    }

    public boolean getDLInferenceEngineQL() {
        return getPreferences().getBoolean(DL_INFERENCE_ENGINE_QL, false);
    }

    public boolean getDLInferenceEngineRL() {
        return getPreferences().getBoolean(DL_INFERENCE_ENGINE_RL, false);
    }

    public AxiomType<?>[] getIgnorableAxioms() {
        return this.ignorableAxioms;
    }

    public boolean getIgnoreAllUnsupportedAxioms() {
        return getPreferences().getBoolean(IGNORE_ALL_UNSUPPORTED_AXIOMS, false);
    }

    public Set<AxiomType<?>> getIgnoredUnsupportedAxioms() {
        final List<String> axioms = getPreferences().getStringList(IGNORED_UNSUPPORTED_AXIOMS, Collections.EMPTY_LIST);
        final Set<AxiomType<?>> ignoredUnsupportedAxioms = new HashSet<>();

        for (String i : axioms) {
            ignoredUnsupportedAxioms.add(AxiomType.getAxiomType(i));
        }

        return ignoredUnsupportedAxioms;
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

    public void setDLInferenceEngineEL(boolean value) {
        getPreferences().putBoolean(DL_INFERENCE_ENGINE_EL, value);
    }

    public void setDLInferenceEngineQL(boolean value) {
        getPreferences().putBoolean(DL_INFERENCE_ENGINE_QL, value);
    }

    public void setDLInferenceEngineRL(boolean value) {
        getPreferences().putBoolean(DL_INFERENCE_ENGINE_RL, value);
    }

    public void setIgnoreAllUnsupportedAxioms(boolean value) {
        getPreferences().putBoolean(IGNORE_ALL_UNSUPPORTED_AXIOMS, value);
    }

    public void setIgnoredUnsupportedAxioms(Set<AxiomType<?>> value) {
        final List<String> list = new ArrayList<>(value.size());

        for (AxiomType<?> i : value) {
            list.add(i.getName());
        }

        getPreferences().putStringList(IGNORED_UNSUPPORTED_AXIOMS, list);
    }

    public void setKoncludeBinary(File value) {
        if (value != null) {
            getPreferences().putString(KONCLUDE_BINARY, value.getAbsolutePath());
        }
    }

    public void setXsbDirectory(File value) {
        if (value != null) {
            getPreferences().putString(XSB_DIRECTORY, value.getAbsolutePath());
        }
    }
}

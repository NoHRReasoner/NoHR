package pt.unl.fct.di.novalincs.nohr.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.model.AxiomType;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.model.DatabaseType;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriverImpl;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

/**
 * Represents the ODBC drivers preferences. 
 *
 * @author Vedran Kasalica
 */

public final class ODBCPreferences {

    private static ODBCPreferences instance;

    public static final String PREFERENCES_SET="pt.unl.fct.di.novalincs.nohr";
    public static final String DRIVER_PREFERENCES_KEY="plugin.obdcdriver.list";

    private final AxiomType<?>[] ignorableAxioms;

    private ODBCPreferences() {
        ignorableAxioms = new AxiomType<?>[]{
            AxiomType.DATA_PROPERTY_RANGE,
            AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
            AxiomType.OBJECT_PROPERTY_RANGE
        };
    }

    
    static List<ODBCDriver> getDrivers() {
    	List<ODBCDriver> drivers  = new ArrayList<>();
    	Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(PREFERENCES_SET, DRIVER_PREFERENCES_KEY);
    	Iterator<String> driverStrings  = prefs.getStringList(DRIVER_PREFERENCES_KEY, new ArrayList<>()).iterator();
    	while (driverStrings.hasNext()) {
    		drivers.add(new ODBCDriverImpl(driverStrings.next(), driverStrings.next(), driverStrings.next(), driverStrings.next(),driverStrings.next(),new DatabaseType(driverStrings.next())));
    	}
    	return drivers;
    }
    static void setDrivers(List<ODBCDriver> drivers) {
    	Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(PREFERENCES_SET, DRIVER_PREFERENCES_KEY);
    	List<String>  prefsStringList = new ArrayList<>();
    	for  (ODBCDriver driver : drivers) {
    		prefsStringList.add(driver.getID());
    		prefsStringList.add(driver.getConnName());
    		prefsStringList.add(driver.getUsername());
    		prefsStringList.add(driver.getPass());
    		prefsStringList.add(driver.getDBName());
    		prefsStringList.add(driver.getDBType().toString());
    	}
        prefs.clear();
    	prefs.putStringList(DRIVER_PREFERENCES_KEY, prefsStringList);
    }
    
//    public NoHRHybridKBConfiguration getConfiguration() {
//        final NoHRHybridKBConfiguration configuration = new NoHRHybridKBConfiguration(getXsbDirectory(), getKoncludeBinary(), getDLInferenceEngineEL(), getDLInferenceEngineQL(), false, getDLInferenceEngine());
//
//
//        return configuration;
//    }


    public static synchronized ODBCPreferences getInstance() {
        if (instance == null) {
            instance = new ODBCPreferences();
        }

        return instance;
    }

    
}

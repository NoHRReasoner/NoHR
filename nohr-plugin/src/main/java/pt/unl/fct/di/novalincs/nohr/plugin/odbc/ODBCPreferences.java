package pt.unl.fct.di.novalincs.nohr.plugin.odbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.model.AxiomType;

import pt.unl.fct.di.novalincs.nohr.model.DatabaseType;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriverImpl;

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

    
    public static List<ODBCDriver> getDrivers() {
    	List<ODBCDriver> drivers  = new ArrayList<ODBCDriver>();
    	Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(PREFERENCES_SET, DRIVER_PREFERENCES_KEY);
    	Iterator<String> driverStrings  = prefs.getStringList(DRIVER_PREFERENCES_KEY, new ArrayList<String>()).iterator();
    	while (driverStrings.hasNext()) {
    		ODBCDriver tmp = new ODBCDriverImpl(driverStrings.next(), driverStrings.next(), driverStrings.next(), driverStrings.next(),driverStrings.next(),new DatabaseType(driverStrings.next()));
    		drivers.add(tmp);
    	}
    	return drivers;
    }
    static void setDrivers(List<ODBCDriver> drivers) {
    	Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(PREFERENCES_SET, DRIVER_PREFERENCES_KEY);
    	List<String>  prefsStringList = new ArrayList<>();
    	for  (ODBCDriver driver : drivers) {
    		prefsStringList.add(driver.getOdbcID());
    		prefsStringList.add(driver.getConectionName());
    		prefsStringList.add(driver.getUsername());
    		prefsStringList.add(driver.getPassword());
    		prefsStringList.add(driver.getDatabaseName());
    		prefsStringList.add(driver.getDatabaseType().toString());
    	}
        prefs.clear();
    	prefs.putStringList(DRIVER_PREFERENCES_KEY, prefsStringList);
    }
    
//    static void getNumberOfDrivers(){
//    	
//    }

    public static synchronized ODBCPreferences getInstance() {
        if (instance == null) {
            instance = new ODBCPreferences();
        }

        return instance;
    }

    
}

package pt.unl.fct.di.novalincs.nohr.model;

import java.util.Set;

public interface RDFMappingSet extends Set<RDFMapping> {

    void addListener(RDFMappingsSetChangeListener listener);

    void removeListener(RDFMappingsSetChangeListener listener);

    boolean update(RDFMapping oldRDFMapping, RDFMapping newRDFMapping);
}

package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;

public interface RDFMapping {

    List<String> getPredicates();

    String getSPARQL();

    String getFileSyntax();

    //TODO Finish this interface
}

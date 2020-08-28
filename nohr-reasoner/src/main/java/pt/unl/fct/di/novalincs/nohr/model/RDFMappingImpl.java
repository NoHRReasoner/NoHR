package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

import java.util.List;

public class RDFMappingImpl implements RDFMapping {
    public RDFMappingImpl(String mapping, List<ODBCDriver> list, int line, Vocabulary vocabulary) {
    }

    @Override
    public List<String> getPredicates() {
        return null;
    }

    @Override
    public String getSPARQL() {
        return null;
    }

    @Override
    public String getFileSyntax() {
        return null;
    }
}

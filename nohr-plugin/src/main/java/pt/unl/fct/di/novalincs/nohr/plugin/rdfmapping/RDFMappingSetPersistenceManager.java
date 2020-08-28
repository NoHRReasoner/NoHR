package pt.unl.fct.di.novalincs.nohr.plugin.rdfmapping;

import pt.unl.fct.di.novalincs.nohr.model.RDFMapping;
import pt.unl.fct.di.novalincs.nohr.model.RDFMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


//TODO commment
public class RDFMappingSetPersistenceManager {

    private final NoHRParser parser;

    private Vocabulary vocabulary;

    public RDFMappingSetPersistenceManager(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;

        parser = new NoHRRecursiveDescentParser(this.vocabulary);
    }

    public static void write(RDFMappingSet rdfMappingSet, File file) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            for (final RDFMapping rdfMapping : rdfMappingSet){
                writer.write(rdfMapping.getFileSyntax());
                writer.newLine();
            }
        }
    }


    public void load(File file, RDFMappingSet rdfMappingSet) throws IOException {
        parser.parseRDFMappingSet(file, rdfMappingSet, ODBCPreferences.getDrivers());
    }
}

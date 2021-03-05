package pt.unl.fct.di.novalincs.nohr.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.odbc.ODBCPreferences;

/**
 * Reads and writes {@link DBMappingSet dbMappingSet} with the Prolog syntax. Uses
 * {@link <a href="https://github.com/raydac/java-prolog-parser">java-prolog-parser</a>}
 * to parse the programs.
 *
 * @author Vedran Kasalica
 * 
 *
 */
public class DBMappingSetPersistenceManager {

	private final NoHRParser parser;
	/**
     * The vocabulary used to recognize the predicates and constants of the
     * readed program.
     */
    private Vocabulary vocabul;
    /**
     * Constructs a new {@link ProgramPersistenceManager} with a given
     * {@link Vocabulary vocabulary}.
     *
     * @param vocabulary the vocabulary used to recognize the predicates and
     * constants of the readed program.
     */
	
    public DBMappingSetPersistenceManager(Vocabulary vocabulary) {
        vocabul = vocabulary;
        //parser = new PrologParser(null);
        parser = new NoHRRecursiveDescentParser(vocabul);
    }


    /**
     * Writes a given set of database mappings to a given file.
     *
     * @param program the db mappings set the write.
     * @param file the file to where the program will be written.
     * @throws java.io.IOException
     */
    public static void write(DBMappingSet dbMappingSet, File file) throws IOException {
        final FormatVisitor format = new NoHRFormatVisitor();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (final DBMapping dbMapping : dbMappingSet) {
            	writer.write(dbMapping.getFileSyntax());
                writer.newLine();
            }
        }
    }
    /**
     * Loads the mappings of a given file.
     *
     * @param file the file from where to read the mappings.
     * @param vocabulary 
     * @param DBMappingSet the DBMappingSet where the mappings will be loaded.
     * @throws IOException
     * @throws ParseException 
     */
    public void load(File file, DBMappingSet dbMappingSet) throws IOException, ParseException{
        parser.parseDBMappingSet(file, dbMappingSet, ODBCPreferences.getDrivers());

//    	FileReader in = new FileReader(file);
//        BufferedReader input = new BufferedReader(in);
//        String mapping;
//        int line = 1;
//        while ((mapping = input.readLine()) != null) {
//        	DBMapping tmpMapping = new DBMappingImpl(mapping, ODBCPreferences.getDrivers(), line, vocabul);
//        	dbMappingSet.add(tmpMapping);
//        	line++;
//        }
        
    }

//    /**
//     * Reads a {@link Program program} from a given file.
//     *
//     * @param file the file from where to read the program.
//     * @return the read program.
//     * @throws IOException
//     * @throws PrologParserException if the file have syntax errors.
//     * @throws pt.unl.fct.di.novalincs.nohr.parsing.ParseException
//     */
//    public DBMappingSet read(File file) throws IOException, PrologParserException, ParseException {
//        final DBMappingSet dbMappingSet = Model.dbMappingSet();
//        load(file, dbMappingSet);
//        return dbMappingSet;
//    }


}

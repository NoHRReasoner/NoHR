package pt.unl.fct.di.novalincs.nohr.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;

/**
 * Reads and writes {@link DBMappingSet dbMappingSet} with the Prolog syntax. Uses
 * {@link <a href="https://github.com/raydac/java-prolog-parser">java-prolog-parser</a>}
 * to parse the programs.
 *
 * @author Vedran Kasalica
 * 
 * I don't think we are using it so far.
 */
public class DBMappingSetPersistenceManager {


    /**
     * Constructs a new {@link ProgramPersistenceManager} with a given
     * {@link Vocabulary vocabulary}.
     *
     * @param vocabulary the vocabulary used to recognize the predicates and
     * constants of the readed program.
     */
    public DBMappingSetPersistenceManager() {
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
                writer.write(";");
                writer.newLine();
            }
        }
    }
    /**
     * Loads the mappings of a given file.
     *
     * @param file the file from where to read the mappings.
     * @param DBMappingSet the DBMappingSet where the mappings will be loaded.
     * @throws IOException
     */
    public void load(File file, DBMappingSet dbMappingSet) throws IOException{

    }

//    private Rule nextRule(PrologCharDataSource src) throws IOException, PrologParserException {
//        final AbstractPrologTerm term = parser.nextSentence(src);
//        if (term == null) {
//            return null;
//        }
//        return rule(term);
//    }
    /**
     * Reads a {@link Program program} from a given file.
     *
     * @param file the file from where to read the program.
     * @return the read program.
     * @throws IOException
     * @throws PrologParserException if the file have syntax errors.
     * @throws pt.unl.fct.di.novalincs.nohr.parsing.ParseException
     */
    public DBMappingSet read(File file) throws IOException, PrologParserException, ParseException {
        final DBMappingSet dbMappingSet = Model.dbMappingSet();
        load(file, dbMappingSet);
        return dbMappingSet;
    }

//    private DBMapping dbMappingSet(AbstractPrologTerm term) throws IOException, PrologParserException {
//        // Main case for any rule but a fact of arity 0
//        if (term.getType() == PrologTermType.STRUCT) {
//            final PrologStructure structure = (PrologStructure) term;
//            // Case for a fact of arity > 0
//            if (!structure.getFunctor().getText().equals(":-")) {
//                return Model.rule((Atom) literal(structure));
//            }
//            // Otherwise there exist distinct head and body elements
//            final Atom head = (Atom) literal(structure.getElement(0));
//            final List<Literal> body = new LinkedList<Literal>();
//            final AbstractPrologTerm bodyTerm = structure.getElement(1);
//            if (bodyTerm != null) // Main case for any body but a single atom of arity 0
//            {
//                if (bodyTerm.getType() == PrologTermType.STRUCT) {
//                    literalsList((PrologStructure) bodyTerm, body);
//                } // Alternative case for a body containing only a single atom of arity 0
//                else if (bodyTerm.getType() == PrologTermType.ATOM) {
//                    body.add(atom(bodyTerm));
//                } else {
//                    throw new IllegalArgumentException("This is not a rule body in the correct format.");
//                }
//            }
//
//            return Model.rule(head, body);
//            // Alternative case for a fact of arity 0
//        } else if (term.getType() == PrologTermType.ATOM) {
//            return Model.rule(atom(term));
//        } else {
//            throw new IllegalArgumentException("This is not a rule in the correct format.");
//        }
//    }


    private String unquote(String symbol) {
        // As the parser removes any outer "'", either also remove the " introduced when writing the file or
        // add the outer "'" again as they were originally part of the symbol name
        if (symbol.startsWith("\"") && symbol.endsWith("\"")) {
            return symbol.substring(1, symbol.length() - 1);
        } else {
            return "'" + symbol + "'";
        }
    }

}

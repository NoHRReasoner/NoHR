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
import static pt.unl.fct.di.novalincs.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;
import com.igormaznitsa.prologparser.terms.AbstractPrologTerm;
import com.igormaznitsa.prologparser.terms.PrologAtom;
import com.igormaznitsa.prologparser.terms.PrologFloatNumber;
import com.igormaznitsa.prologparser.terms.PrologIntegerNumber;
import com.igormaznitsa.prologparser.terms.PrologStructure;
import com.igormaznitsa.prologparser.terms.PrologTermType;
import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;

/**
 * Reads and writes {@link Program programs} with the Prolog syntax. Uses
 * {@link <a href="https://github.com/raydac/java-prolog-parser">java-prolog-parser</a>}
 * to parse the programs.
 *
 * @author Nuno Costa
 */
public class ProgramPersistenceManager {

    /**
     * Writes a given program to a given file.
     *
     * @param program the program the write.
     * @param file the file to where the program will be written.
     * @throws java.io.IOException
     */
    public static void write(Program program, File file) throws IOException {
        //final FormatVisitor format = new PrologFormatVisitor();
        final FormatVisitor format = new NoHRFormatVisitor();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (final Rule rule : program) {
                writer.write(rule.accept(format));
                writer.write(".");
                writer.newLine();
            }
        }
    }

    /**
     * The prolog parser used to read programs.
     */
//    private final PrologParser parser;
    private final NoHRParser parser;
    /**
     * The vocabulary used to recognize the predicates and constants of the
     * readed program.
     */
    private Vocabulary v;

    /**
     * Constructs a new {@link ProgramPersistenceManager} with a given
     * {@link Vocabulary vocabulary}.
     *
     * @param vocabulary the vocabulary used to recognize the predicates and
     * constants of the readed program.
     */
    public ProgramPersistenceManager(Vocabulary vocabulary) {
        v = vocabulary;
        //parser = new PrologParser(null);
        parser = new NoHRRecursiveDescentParser(v);
    }

    private Atom atom(final AbstractPrologTerm term) {
        if (term.getType() == PrologTermType.STRUCT) {
            final PrologStructure struct = (PrologStructure) term;
            final String pred = unquote(struct.getFunctor().getText());
            final List<Term> args = new ArrayList<Term>(struct.getArity());
            for (int i = 0; i < struct.getArity(); i++) {
                final AbstractPrologTerm prologArg = struct.getElement(i);
                switch (prologArg.getType()) {
                    case ATOM:
                        if (prologArg instanceof PrologIntegerNumber) {
                            args.add(v.cons(((PrologIntegerNumber) prologArg).getValue()));
                        } else if (prologArg instanceof PrologFloatNumber) {
                            args.add(v.cons(((PrologFloatNumber) prologArg).getValue()));
                        } else {
                            args.add(v.cons(unquote(prologArg.getText())));
                        }
                        break;
                    case VAR:
                        args.add(var(prologArg.getText()));
                        break;
                    default:
                        break;
                }
            }
            return Model.atom(v, pred, args);
        } else if (term.getType() == PrologTermType.ATOM) {
            final PrologAtom atom = (PrologAtom) term;
            return Model.atom(v, unquote(atom.getText()));
        } else {
            throw new IllegalArgumentException("isn't an atom");
        }
    }

    public Vocabulary getVoculary() {
        return v;
    }

    private Literal literal(AbstractPrologTerm term) {
        if (term.getType() == PrologTermType.STRUCT) {
            final String pred = ((PrologStructure) term).getFunctor().getText();
            if (pred.equals("tnot")) {
                return negLiteral(atom(((PrologStructure) term).getElement(0)));
            }
        }
        return atom(term);
    }

    private void literalsList(PrologStructure struct, List<Literal> literals)
            throws IOException, PrologParserException {
        final String functor = struct.getFunctor().getText();
        // If the functor is not a "," then struct is an atom of arity >0 or appears under tnot
        if (!functor.equals(",")) {
            literals.add(literal(struct));
        } //Otherwise, there are at least two body elements
        else {
            // Handle the first element in literal()
            literals.add(literal(struct.getElement(0)));
            // Handle the remaining atoms recursively
            if (struct.getElement(1).getType() == PrologTermType.STRUCT) {
                literalsList((PrologStructure) struct.getElement(1), literals);
            } // Unless it is a single atom of arity 0, taken care of next
            else if (struct.getElement(1).getType() == PrologTermType.ATOM) {
                literals.add(atom(struct.getElement(1)));
            } else {
                throw new IllegalArgumentException("This is not a body element in the correct format.");
            }
        }
    }

    /**
     * Loads the rules of a given file to a given program.
     *
     * @param file the file from where to read the rules.
     * @param program the program where the rules will be loaded.
     * @throws IOException
     * @throws PrologParserException if the file has some syntax error.
     */
    public void load(File file, Program program) throws IOException, PrologParserException, ParseException {
//        final PrologCharDataSource src = new PrologCharDataSource(new BufferedReader(new FileReader(file)));
//        Rule currentRule = nextRule(src);
//        while (currentRule != null) {
//            program.add(currentRule);
//            currentRule = nextRule(src);
//        }
        parser.parseProgram(file, program);
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
    public Program read(File file) throws IOException, PrologParserException, ParseException {
        final Program program = Model.program();
        load(file, program);
        return program;
    }

    private Rule rule(AbstractPrologTerm term) throws IOException, PrologParserException {
        // Main case for any rule but a fact of arity 0
        if (term.getType() == PrologTermType.STRUCT) {
            final PrologStructure structure = (PrologStructure) term;
            // Case for a fact of arity > 0
            if (!structure.getFunctor().getText().equals(":-")) {
                return Model.rule((Atom) literal(structure));
            }
            // Otherwise there exist distinct head and body elements
            final Atom head = (Atom) literal(structure.getElement(0));
            final List<Literal> body = new LinkedList<Literal>();
            final AbstractPrologTerm bodyTerm = structure.getElement(1);
            if (bodyTerm != null) // Main case for any body but a single atom of arity 0
            {
                if (bodyTerm.getType() == PrologTermType.STRUCT) {
                    literalsList((PrologStructure) bodyTerm, body);
                } // Alternative case for a body containing only a single atom of arity 0
                else if (bodyTerm.getType() == PrologTermType.ATOM) {
                    body.add(atom(bodyTerm));
                } else {
                    throw new IllegalArgumentException("This is not a rule body in the correct format.");
                }
            }

            return Model.rule(head, body);
            // Alternative case for a fact of arity 0
        } else if (term.getType() == PrologTermType.ATOM) {
            return Model.rule(atom(term));
        } else {
            throw new IllegalArgumentException("This is not a rule in the correct format.");
        }
    }

    public void setVocabulary(Vocabulary vocabulary) {
        v = vocabulary;
    }

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

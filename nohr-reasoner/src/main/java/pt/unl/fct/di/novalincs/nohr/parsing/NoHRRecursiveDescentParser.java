/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.parsing;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import static pt.unl.fct.di.novalincs.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.COMMA;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.ID;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.IF;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.L_PAREN;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.NOT;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.QUESTION_MARK;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.R_PAREN;
import static pt.unl.fct.di.novalincs.nohr.parsing.TokenType.SYMBOL;
import pt.unl.fct.di.novalincs.nohr.utils.PrologSyntax;

/**
 * A {@link <a href="https://en.wikipedia.org/wiki/Recursive_descent_parser">
 * recursive descent parser</a>} that implements {@link NoHRParser}.
 *
 * @author Nuno Costa
 */
public class NoHRRecursiveDescentParser implements NoHRParser {

    /**
     * The {@link NoHRScanner} that recoginzes and consumes the tokens.
     */
    private NoHRScanner scanner;

    /**
     * The {@link Vocabulary} used to recognize
     * {@link OWLClass concepts}, {@link OWLProperty role} and
     * {@link OWLIndividual individual} symbols.
     */
    private Vocabulary v;

    /**
     * Constructs a {@link NoHRRecursiveDescentParser}.
     */
    public NoHRRecursiveDescentParser() {
        this(null);
    }

    /**
     * Constructs a {@link NoHRRecursiveDescentParser}, that recognizes the
     * {@link OWLClass concept}, {@link OWLProperty role}, and
     * {@link OWLIndividual individual} symbols mapped in a given
     * {@link Vocabulary}.
     *
     * @param vocabularyMapping the {@link Vocabulary} used to recognize
     * {@link OWLClass concepts}, {@link OWLProperty roles} and
     * {@link OWLIndividual individuals}.
     */
    public NoHRRecursiveDescentParser(Vocabulary vocabularyMapping) {
        v = vocabularyMapping;
    }

    /**
     * Tries to apply: <br>
     * <code> atom = symbol ["(" term {"," term} ")"].</code><br>
     * <br>
     * In case of success constructs the recognized {@link Atom} and returns it.
     *
     * @return the recognized {@link Atom}.
     * @throws ParseException if the parser can't recognize an {@link Atom} at
     * current position.
     */
    private Atom atom() throws ParseException {
        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.PROLOG_PREFIX);
        }

        if (!scanner.next(SYMBOL)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), SYMBOL);
        }

        final String predicateSymbol = scanner.value();

        if (!scanner.next(L_PAREN)) {
            return Model.atom(v, predicateSymbol);
        } else {
            final List<Term> args = new LinkedList<>();

            do {
                args.add(term());
            } while (scanner.next(COMMA));

            if (!scanner.next(R_PAREN)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), COMMA, R_PAREN);
            }

            return Model.atom(v, predicateSymbol, args);
        }
    }

    private Atom atomExtended() throws ParseException {
        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            return prologAtom();
        } else {
            return atom();
        }
    }

    @Override
    public Vocabulary getVocabulary() {
        return v;
    }

    /**
     * Tries to apply: <br>
     * <code> literal = atom | "not " atom. </code><br>
     * <br>
     * In case of success constructs the recognized {@link Literal} and returns
     * it.
     *
     * @return the recognized literal.
     * @throws ParseException the parser can't recognize a {@link Literal} at
     * current position.
     */
    private Literal literal() throws ParseException {
        if (scanner.next(NOT)) {
            final Atom atom = atom();
            return negLiteral(atom);
        } else {
            return atomExtended();
        }
    }

    /**
     * Tries to apply:<br>
     * <code> literals = literal {"," literal}.</code><br>
     * <br>
     * In case of success constructs the recognized {@link Literal} {@link List}
     * and returns it.
     *
     * @return the recognized list of literals.
     * @throws ParseException if no {@link Literal} was recognized or there was
     * trailing chars.
     */
    private List<Literal> literals() throws ParseException {
        final List<Literal> result = new LinkedList<Literal>();
        do {
            result.add(literal());
        } while (scanner.next(COMMA));
        return result;
    }

    @Override
    public Program parseProgram(File file) throws ParseException, FileNotFoundException {
        scanner = new NoHRScanner(file);
        return program();
    }

    @Override
    public Query parseQuery(String str) throws ParseException {
        scanner = new NoHRScanner(str);
        final Query query = query();
        if (scanner.hasNext()) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length());
        }
        return query;
    }

    @Override
    public Rule parseRule(String str) throws ParseException {
        scanner = new NoHRScanner(str);
        final Rule rule = rule();
        if (scanner.hasNext()) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length());
        }
        return rule;
    }

    /**
     * Tries to apply:<br>
     * <code> program = rule "." { rule "." }.</code><br>
     * <br>
     * In case of success constructs the recognized {@link Program} and returns
     * it.
     *
     * @return the recognized program.
     * @throws ParseException if no {@link Program program} was recognized or
     * there was trailing chars.
     */
    private Program program() throws ParseException {
        final Program program = Model.program();
        int l = 0;
        do {
            program.add(rule());
            l++;
            if (!scanner.next(TokenType.DOT)) {
                throw new ParseException(l, scanner.position(), scanner.position(), TokenType.DOT);
            }
        } while (scanner.hasNext());
        return program;
    }

    private Atom prologAtom() throws ParseException {
        if (!scanner.next(TokenType.PROLOG_PREDICATE_SYMBOL)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
        }

        final String predicateSymbol = scanner.value();
        final int startPos = scanner.position();

        if (!scanner.next(L_PAREN)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.L_PAREN);
        }

        final List<Term> args = new LinkedList<>();

        do {
            args.add(termExtended());
        } while (scanner.next(COMMA));

        if (!scanner.next(R_PAREN)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.position(), COMMA, R_PAREN);
        }

        if (!PrologSyntax.validPredicate(predicateSymbol, args.size())) {
            throw new ParseException(scanner.line(), startPos, scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
        }

        return Model.prologAtom(v, predicateSymbol, args);
    }

    /**
     * Tries to apply: <br>
     * <code>query = literals.}</code> <br>
     * <br>
     * In case of success constructs the recognized {@link Query} and returns
     * it.
     *
     * @return the recognized query.
     * @throws ParseException if the parser can't recognize any {@link Query}.
     */
    private Query query() throws ParseException {
        final List<Literal> literals = literals();
        return Model.query(literals);
    }

    /**
     * Tries to apply:<br>
     * <code> rule = atom [":-" literals ]. </code> <br>
     * <br>
     * In case of success contructs the recognized {@link Rule} and returns it.
     *
     * @return the recognized rule.
     * @throws ParseException if the parser can't recognize any {@link Rule}.
     */
    private Rule rule() throws ParseException {
        final Atom head = atom();

        if (!scanner.next(IF)) {
            return Model.rule(head);
        } else {
            final List<Literal> body = literals();

            return Model.rule(head, body);
        }
    }

    @Override
    public void setVocabulary(Vocabulary vocabulary) {
        v = vocabulary;
    }

    /**
     * Tries to apply: <br>
     * <code> term = variable | constant | list. </code> <br>
     * <br>
     * In cases of success constructs the recognized {@link Term} and returns
     * if.
     *
     * @throws ParseException the parser can't recognize any {@link Term} at the
     * current position.
     * @return the recognized term.
     */
    private Term term() throws ParseException {
        final Variable variable = variable();

        if (variable != null) {
            return variable;
        }

        if (scanner.next(SYMBOL)) {
            return v.cons(scanner.value());
        }

        throw new ParseException(scanner.line(), scanner.position(), scanner.length(), SYMBOL, QUESTION_MARK);
    }

    private Term termExtended() throws ParseException {
        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            return Model.atomTerm(prologAtom());
        }

        final Variable var = variable();

        if (var != null) {
            return var;
        }

        if (scanner.next(TokenType.SYMBOL)) {
            final String predicateSymbol = scanner.value();

            if (!scanner.next(L_PAREN)) {
                return v.cons(predicateSymbol);
            } else {
                final List<Term> args = new LinkedList<>();

                do {
                    args.add(termExtended());
                } while (scanner.next(COMMA));

                if (!scanner.next(R_PAREN)) {
                    throw new ParseException(scanner.line(), scanner.position(), scanner.position(), COMMA, R_PAREN);
                }

                return Model.atomTerm(Model.atom(v, predicateSymbol, args));
            }
        }

        throw new ParseException(scanner.line(), scanner.position(), scanner.length(), SYMBOL, QUESTION_MARK);
    }

    /**
     * Tries to apply: <br>
     * <code> variable = "?" id.</code><br>
     * <br>
     * In case of success constructs the recognized {@link Variable} and returns
     * it.
     *
     * @return the recognized variable, or {@code null} if none is recognized.
     * @throws ParseException if the parser can recognize an
     * {@link TokenType#QUESTION_MARK} but not an {@link TokenType#ID}.
     */
    private Variable variable() throws ParseException {
        if (!scanner.next(QUESTION_MARK)) {
            return null;
        }

        if (!scanner.next(ID)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), ID);
        }

        return var(scanner.value());
    }

}

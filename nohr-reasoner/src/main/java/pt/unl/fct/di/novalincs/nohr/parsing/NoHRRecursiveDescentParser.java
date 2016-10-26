package pt.unl.fct.di.novalincs.nohr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.AtomTerm;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.utils.PrologSyntax;

public class NoHRRecursiveDescentParser implements NoHRParser {

    private NoHRScanner scanner;
    private Vocabulary vocabulary;

    public NoHRRecursiveDescentParser() {
        this(null);
    }

    public NoHRRecursiveDescentParser(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    private Atom atom() throws ParseException {
        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            final Atom atom = pAtom();

            return atom;
        }

        final Term left = pTerm();

        if (scanner.next(TokenType.PROLOG_BINARY_OPERATOR)) {
            final String op = scanner.value();

            final Term right = pTerm();

            return Model.atomOperator(vocabulary.prologOpPred(op, 2), left, right);
        }

        if (!(left instanceof AtomTerm)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL, TokenType.SYMBOL);
        }

        return ((AtomTerm) left).getAtom();
    }

    private Atom head() throws ParseException {
        if (!scanner.next(TokenType.SYMBOL)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.SYMBOL);
        }

        final String predicate = scanner.value();

        if (scanner.next(TokenType.L_PAREN)) {
            if (scanner.next(TokenType.R_PAREN)) {
                return Model.atom(vocabulary, predicate);
            }

            final List<Term> terms = new LinkedList<>();

            do {
                terms.add(kbTerm());
            } while (scanner.next(TokenType.COMMA));

            if (!scanner.next(TokenType.R_PAREN)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_PAREN);
            }

            return Model.atom(vocabulary, predicate, terms);
        } else {
            return Model.atom(vocabulary, predicate);
        }
    }

    private Atom kbAtom() throws ParseException {
        if (!scanner.next(TokenType.SYMBOL)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.SYMBOL);
        }

        final String predicate = scanner.value();

        if (!scanner.next(TokenType.L_PAREN)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.L_PAREN);
        }

        if (scanner.next(TokenType.R_PAREN)) {
            return Model.atom(vocabulary, predicate);
        }

        final List<Term> terms = new LinkedList<>();

        do {
            terms.add(kbTerm());
        } while (scanner.next(TokenType.COMMA));

        if (!scanner.next(TokenType.R_PAREN)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_PAREN);
        }

        return Model.atom(vocabulary, predicate, terms);
    }

    private Term kbTerm() throws ParseException {
        if (scanner.next(TokenType.QUESTION_MARK)) {
            return variableExpression();
        }

        if (scanner.next(TokenType.L_BRACK)) {
            if (scanner.next(TokenType.R_BRACK)) {
                return Model.list(null, null);
            }
            
            final Term list = listExpression();

            if (!scanner.next(TokenType.R_BRACK)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_BRACK);
            }

            return list;
        }

        if (scanner.next(TokenType.SYMBOL)) {
            return vocabulary.cons(scanner.value());
        }

        throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.QUESTION_MARK, TokenType.SYMBOL);
    }

    private Term listExpression() throws ParseException {
        final List<Term> head = new LinkedList<>();

        do {
            final Term pTerm = pTerm();

            head.add(pTerm);
        } while (scanner.next(TokenType.COMMA));

        if (scanner.next(TokenType.PIPE)) {
            final Term tail = pTerm();

            return Model.list(head, tail);
        }

        return Model.list(head, null);
    }

    private Literal literal() throws ParseException {
        if (scanner.next(TokenType.NOT)) {
            final Atom kbAtom = kbAtom();

            return Model.negLiteral(kbAtom);
        } else {
            final Atom atom = atom();

            return atom;
        }
    }

    private List<Literal> literals() throws ParseException {
        final List<Literal> literals = new LinkedList<>();

        do {
            literals.add(literal());
        } while (scanner.next(TokenType.COMMA));

        return literals;
    }

    private Atom pAtom() throws ParseException {
        if (!scanner.next(TokenType.PROLOG_PREDICATE_SYMBOL)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
        }

        final String predicateSymbol = scanner.value();
        final int startPos = scanner.position();

        if (!scanner.next(TokenType.L_PAREN)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.L_PAREN);
        }

        final List<Term> args = pTerms();

        if (!PrologSyntax.validPredicate(predicateSymbol, args.size())) {
            throw new ParseException(scanner.line(), startPos, scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
        }

        return Model.prologAtom(vocabulary, predicateSymbol, args);
    }

    private Term pTerm() throws ParseException {
        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            return Model.atomTerm(pAtom());
        }

        if (scanner.next(TokenType.L_BRACK)) {
            if (scanner.next(TokenType.R_BRACK)) {
                return Model.list(null, null);
            }

            final Term list = listExpression();

            if (!scanner.next(TokenType.R_BRACK)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_BRACK);
            }

            return list;
        }

        if (scanner.next(TokenType.QUESTION_MARK)) {
            return variableExpression();
        }

        if (scanner.next(TokenType.SYMBOL)) {
            final String predicateSymbol = scanner.value();

            if (scanner.next(TokenType.L_PAREN)) {
                if (scanner.next(TokenType.R_PAREN)) {
                    return Model.atomTerm(Model.atom(vocabulary, predicateSymbol));
                }

                final List<Term> terms = pTerms();

                if (!scanner.next(TokenType.R_PAREN)) {
                    throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_PAREN);
                }

                return Model.atomTerm(Model.atom(vocabulary, predicateSymbol, terms));
            } else {
                return vocabulary.cons(predicateSymbol);
            }
        }

        throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.SYMBOL, TokenType.QUESTION_MARK);
    }

    private List<Term> pTerms() throws ParseException {
        final List<Term> pTerms = new LinkedList<>();

        do {
            pTerms.add(pTerm());
        } while (scanner.next(TokenType.COMMA));

        return pTerms;
    }

    @Override
    public Program parseProgram(File file) throws ParseException, FileNotFoundException {
        this.scanner = new NoHRScanner(file);

        return program();
    }

    @Override
    public Query parseQuery(String str) throws ParseException {
        this.scanner = new NoHRScanner(str);

        final Query query = query();

        if (scanner.hasNext()) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length());
        }

        return query;
    }

    @Override
    public Rule parseRule(String str) throws ParseException {
        this.scanner = new NoHRScanner(str);

        final Rule rule = rule();

        if (scanner.hasNext()) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length());
        }

        return rule;
    }

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

    private Query query() throws ParseException {
        final List<Literal> literals = literals();

        return Model.query(literals);
    }

    private Rule rule() throws ParseException {
        final Atom head = head();

        if (scanner.next(TokenType.IF)) {
            final List<Literal> body = literals();

            return Model.rule(head, body);
        } else {
            return Model.rule(head);
        }
    }

    @Override
    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    private Term variableExpression() throws ParseException {
        if (!scanner.next(TokenType.ID)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.ID);
        }

        return Model.var(scanner.value());
    }

}

package pt.unl.fct.di.novalincs.nohr.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.ODBCDriver;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.LiteralTerm;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.utils.PrologSyntax;

public class NoHRRecursiveDescentParser implements NoHRParser {

    private NoHRScanner scanner;
    private Vocabulary vocabulary;

    public NoHRRecursiveDescentParser(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    private Literal atom() throws ParseException {
        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            final Atom atom = prologAtom();

            return atom;
        }

        Term term = operatorTerm(true);

        if (!(term instanceof LiteralTerm)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length());
        }

        return ((LiteralTerm) term).getLiteral();

    }

    private Atom kbAtom() throws ParseException {
        if (scanner.next(TokenType.FUNCTOR)) {

            final String functor = scanner.value();

            if (scanner.next(TokenType.L_PAREN)) {
                if (scanner.next(TokenType.R_PAREN)) {
                    return Model.atom(vocabulary, functor);
                }

                final List<Term> args = kbTerms();

                if (!scanner.next(TokenType.R_PAREN)) {
                    throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_PAREN);
                }

                return Model.atom(vocabulary, functor, args);
            }

            return Model.atom(vocabulary, functor);
        }

        if (scanner.next(TokenType.CONSTANT)) {
            return Model.atom(vocabulary, scanner.value());
        }

        throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.FUNCTOR);
    }

    private Term kbTerm() throws ParseException {
        if (scanner.next(TokenType.QUESTION_MARK)) {
            return variableTerm();
        }

        if (scanner.next(TokenType.L_BRACK)) {
            if (scanner.next(TokenType.R_BRACK)) {
                return Model.list();
            }

            final Term list = listTerm();

            if (!scanner.next(TokenType.R_BRACK)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_BRACK);
            }

            return list;
        }

        if (scanner.next(TokenType.CONSTANT)) {
            return vocabulary.cons(scanner.value());
        }

        return null;
    }

    private List<Term> kbTerms() throws ParseException {
        final List<Term> kbTerms = new LinkedList<>();

        do {
            final Term kbTerm = kbTerm();

            if (kbTerm == null) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.QUESTION_MARK, TokenType.L_BRACK, TokenType.CONSTANT);
            }

            kbTerms.add(kbTerm);
        } while (scanner.next(TokenType.COMMA));

        return kbTerms;
    }

    private Term listTerm() throws ParseException {
        final List<Term> head = new LinkedList<>();

        do {
            final Term pTerm = kbTerm();

            head.add(pTerm);
        } while (scanner.next(TokenType.COMMA));

        if (scanner.next(TokenType.PIPE)) {
            final Term tail = kbTerm();

            return Model.list(head, tail);
        }

        return Model.list(head);
    }

    private Literal literal() throws ParseException {
        if (scanner.next(TokenType.NOT)) {
            final Atom kbAtom = kbAtom();

            return Model.negLiteral(kbAtom);
        } else {
            return atom();
        }
    }

    private List<Literal> literals() throws ParseException {
        final List<Literal> literals = new LinkedList<>();

        do {
            literals.add(literal());
        } while (scanner.next(TokenType.COMMA));

        return literals;
    }

    private Term operatorTerm(boolean literal) throws ParseException {
        Term left = prologTerm(literal);

        while (scanner.next(TokenType.PROLOG_BINARY_OPERATOR)) {
            final String op = scanner.value();

            if (!PrologSyntax.validOperator(op)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
            }

            final Term right = operatorTerm(false);

            left = Model.atomOperatorTerm(Model.atomOperator(vocabulary.prologOpPred(op), left, right));
        }

        return left;
    }

    private Atom prologAtom() throws ParseException {
        if (!scanner.next(TokenType.PROLOG_PREDICATE_SYMBOL)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
        }

        final String functor = scanner.value();
        final int startPos = scanner.position();

        if (scanner.next(TokenType.L_PAREN)) {
            if (scanner.next(TokenType.R_PAREN)) {
                if (!PrologSyntax.validPredicate(functor, 0)) {
                    throw new ParseException(scanner.line(), startPos, scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
                }

                return Model.prologAtom(vocabulary, functor, Collections.<Term>emptyList());
            }

            final List<Term> args = prologTerms();

            if (!PrologSyntax.validPredicate(functor, args.size())) {
                throw new ParseException(scanner.line(), startPos, scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
            }

            if (!scanner.next(TokenType.R_PAREN)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.L_PAREN);
            }

            return Model.prologAtom(vocabulary, functor, args);
        }

        if (!PrologSyntax.validPredicate(functor, 0)) {
            throw new ParseException(scanner.line(), startPos, scanner.length(), TokenType.PROLOG_PREDICATE_SYMBOL);
        }

        return Model.prologAtom(vocabulary, functor, Collections.<Term>emptyList());
    }

    private Term prologTerm(boolean literal) throws ParseException {
        if (scanner.next(TokenType.L_PAREN)) {
            final Term term = operatorTerm(false);

            if (!scanner.next(TokenType.R_PAREN)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_PAREN);
            }

            return Model.parenthesis(term);
        }

        if (scanner.next(TokenType.PROLOG_PREFIX)) {
            return Model.atomTerm(prologAtom());
        }

        if (scanner.next(TokenType.QUESTION_MARK)) {
            return variableTerm();
        }

        if (scanner.next(TokenType.L_BRACK)) {
            if (scanner.next(TokenType.R_BRACK)) {
                return Model.list();
            }

            final Term list = listTerm();

            if (!scanner.next(TokenType.R_BRACK)) {
                throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_BRACK);
            }

            return list;
        }

        if (scanner.next(TokenType.FUNCTOR)) {
            final String functor = scanner.value();

            if (scanner.next(TokenType.L_PAREN)) {
                if (scanner.next(TokenType.R_PAREN)) {
                    return Model.atomTerm(Model.atom(vocabulary, functor));
                }

                final List<Term> terms = kbTerms();

                if (!scanner.next(TokenType.R_PAREN)) {
                    throw new ParseException(scanner.line(), scanner.position(), scanner.position(), TokenType.R_PAREN);
                }

                return Model.atomTerm(Model.atom(vocabulary, functor, terms));
            }
        }

        if (scanner.next(TokenType.NUMERIC_CONSTANT)) {
            return vocabulary.cons(scanner.value());
        }

        if (scanner.next(TokenType.CONSTANT)) {
            if (literal) {
                return Model.atomTerm(Model.atom(vocabulary, scanner.value()));
            } else {
                return vocabulary.cons(scanner.value());
            }
        }

        throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.CONSTANT, TokenType.FUNCTOR, TokenType.PROLOG_PREFIX, TokenType.QUESTION_MARK, TokenType.R_BRACK);
    }

    private List<Term> prologTerms() throws ParseException {
        final List<Term> pTerms = new LinkedList<>();

        do {
            pTerms.add(operatorTerm(false));
        } while (scanner.next(TokenType.COMMA));

        return pTerms;
    }

    @Override
    public Program parseProgram(File file) throws ParseException, FileNotFoundException {
        this.scanner = new NoHRScanner(file);

        return program();
    }

    @Override
    public Program parseProgram(File file, Program program) throws FileNotFoundException, ParseException {
        this.scanner = new NoHRScanner(file);

        return this.program(program);
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

        return this.program(program);
    }

    private Program program(Program program) throws ParseException {
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
        final Atom head = kbAtom();

        if (scanner.next(TokenType.IF)) {
            final List<Literal> body = literals();

            return Model.rule(head, body);
        } else {
            return Model.rule(head);
        }
    }

	@Override
	public DBMapping parseDBMapping(String str) throws ParseException {
		// TODO HAVE TO BE DEFINED
		return null;
	}
	
	@Override
	public void parseDBMappingSet(File file, DBMappingSet dbMappingSet, List<ODBCDriver> odbcDriversList) throws IOException {

		FileReader in = new FileReader(file);
        BufferedReader input = new BufferedReader(in);
        String mapping;
        int line = 1;
        while ((mapping = input.readLine()) != null) {
        	DBMapping tmpMapping = new DBMappingImpl(mapping, odbcDriversList, line, vocabulary);
        	dbMappingSet.add(tmpMapping);
        	line++;
        }
        
	}

    
    
    @Override
    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    private Term variableTerm() throws ParseException {
        if (!scanner.next(TokenType.VARIABLE)) {
            throw new ParseException(scanner.line(), scanner.position(), scanner.length(), TokenType.VARIABLE);
        }

        return Model.var(scanner.value());
    }



}

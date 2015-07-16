/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.query;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;

import com.igormaznitsa.prologparser.PrologCharDataSource;
import com.igormaznitsa.prologparser.PrologParser;
import com.igormaznitsa.prologparser.exceptions.PrologParserException;
import com.igormaznitsa.prologparser.terms.AbstractPrologTerm;
import com.igormaznitsa.prologparser.terms.PrologFloatNumber;
import com.igormaznitsa.prologparser.terms.PrologIntegerNumber;
import com.igormaznitsa.prologparser.terms.PrologStructure;

/**
 * @author nunocosta
 *
 */
public class Parser {

    private static final PrologParser parser = new PrologParser(null);

    private static Atom parseAtom(final PrologStructure struct) {
	final String pred = struct.getFunctor().getText();
	final List<Term> args = new LinkedList<Term>();
	for (int i = 0; i < struct.getArity(); i++) {
	    final AbstractPrologTerm prologArg = struct.getElement(i);
	    switch (prologArg.getType()) {
	    case ATOM:
		if (prologArg instanceof PrologIntegerNumber)
		    args.add(cons(((PrologIntegerNumber) prologArg).getValue()));
		else if (prologArg instanceof PrologFloatNumber)
		    args.add(cons(((PrologFloatNumber) prologArg).getValue()));
		else {
		    String symbol = prologArg.getText();
		    if (symbol.startsWith("'") && symbol.endsWith("'"))
			symbol = symbol.substring(1, symbol.length() - 1);
		    args.add(cons(symbol));
		}
		break;
	    case VAR:
		args.add(var(prologArg.getText()));
		break;
	    default:
		break;
	    }
	}
	return atom(pred, args);
    }

    private static Literal parseLiteral(PrologStructure struct) {
	final String pred = struct.getFunctor().getText();
	if (pred.equals("tnot"))
	    return negLiteral(parseAtom((PrologStructure) struct.getElement(0)));
	return parseAtom(struct);
    }

    private static void parseLiteralsList(PrologStructure struct,
	    List<Literal> literals) throws IOException, PrologParserException {
	final String functor = struct.getFunctor().getText();
	if (!functor.equals(","))
	    literals.add(parseLiteral(struct));
	else {
	    literals.add(parseLiteral((PrologStructure) struct.getElement(0)));
	    parseLiteralsList((PrologStructure) struct.getElement(1), literals);
	}
    }

    public static Query parseQuery(String string) throws IOException,
    PrologParserException {
	final PrologStructure rootStructure = (PrologStructure) parser
		.nextSentence(string);
	final List<Literal> literals = new LinkedList<Literal>();
	parseLiteralsList(rootStructure, literals);
	return query(literals);
    }

    private static Rule parseRule(PrologStructure structure)
	    throws IOException, PrologParserException {
	if (structure.getArity() < 1 || structure.getArity() > 1
		&& !structure.getFunctor().getText().equals(":-"))
	    throw new IllegalArgumentException("isn't a rule");
	final Atom head;
	if (structure.getArity() == 1)
	    head = (Atom) parseLiteral(structure);
	else
	    head = (Atom) parseLiteral((PrologStructure) structure
		    .getElement(0));
	final List<Literal> body = new LinkedList<Literal>();
	if (structure.getArity() > 1) {
	    final AbstractPrologTerm bodyTerm = structure.getElement(1);
	    if (bodyTerm != null)
		parseLiteralsList((PrologStructure) bodyTerm, body);
	}
	return rule(head, body);
    }

    public static Rule parseRule(String rule) throws IOException,
    PrologParserException {
	return parseRule((PrologStructure) parser.nextSentence(rule));
    }

    private final PrologCharDataSource src;

    public Parser(Reader reader) {
	src = new PrologCharDataSource(reader);
    }

    public Rule nextRule() throws IOException, PrologParserException {
	final PrologStructure struct = (PrologStructure) parser
		.nextSentence(src);
	if (struct == null)
	    return null;
	return parseRule(struct);
    }

}

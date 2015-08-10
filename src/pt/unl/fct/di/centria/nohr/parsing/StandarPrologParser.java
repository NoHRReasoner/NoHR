/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import static pt.unl.fct.di.centria.nohr.model.Model.*;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.reasoner.OntologyIndex;

import com.igormaznitsa.prologparser.PrologCharDataSource;
import com.igormaznitsa.prologparser.PrologParser;
import com.igormaznitsa.prologparser.exceptions.PrologParserException;
import com.igormaznitsa.prologparser.terms.AbstractPrologTerm;
import com.igormaznitsa.prologparser.terms.PrologFloatNumber;
import com.igormaznitsa.prologparser.terms.PrologIntegerNumber;
import com.igormaznitsa.prologparser.terms.PrologStructure;
import com.igormaznitsa.prologparser.terms.PrologTermType;

/**
 * @author nunocosta
 *
 */
public class StandarPrologParser {

    private final PrologParser parser = new PrologParser(null);

    private final PrologCharDataSource src;

    private final OntologyIndex ontologyIndex;

    public StandarPrologParser() {
	this(null, null);
    }

    public StandarPrologParser(OntologyIndex ontologyIndex) {
	this(null, ontologyIndex);
    }

    public StandarPrologParser(Reader reader, OntologyIndex ontologyIndex) {
	if (reader != null)
	    src = new PrologCharDataSource(reader);
	else
	    src = null;
	this.ontologyIndex = ontologyIndex;
    }

    public Rule nextRule() throws IOException, PrologParserException {
	if (src == null)
	    return null;
	final PrologStructure struct = (PrologStructure) parser.nextSentence(src);
	if (struct == null)
	    return null;
	return parseRule(struct);
    }

    private Atom parseAtom(final PrologStructure struct) {
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
	if (ontologyIndex != null)
	    return atom(pred, args, ontologyIndex);
	else
	    return atom(pred, args);
    }

    private Literal parseLiteral(PrologStructure struct) {
	final String pred = struct.getFunctor().getText();
	if (pred.equals("tnot"))
	    return negLiteral(parseAtom((PrologStructure) struct.getElement(0)));
	return parseAtom(struct);
    }

    private void parseLiteralsList(PrologStructure struct, List<Literal> literals)
	    throws IOException, PrologParserException {
	final String functor = struct.getFunctor().getText();
	if (!functor.equals(","))
	    literals.add(parseLiteral(struct));
	else {
	    literals.add(parseLiteral((PrologStructure) struct.getElement(0)));
	    parseLiteralsList((PrologStructure) struct.getElement(1), literals);
	}
    }

    public Query parseQuery(String string) throws IOException, PrologParserException {
	final AbstractPrologTerm rootTerm = parser.nextSentence(string);
	if (rootTerm == null)
	    throw new PrologParserException("", 0, 0);
	if (rootTerm.getType() != PrologTermType.STRUCT)
	    throw new PrologParserException(null, 0, 0);
	final PrologStructure rootStructure = (PrologStructure) rootTerm;
	final List<Literal> literals = new LinkedList<Literal>();
	parseLiteralsList(rootStructure, literals);
	return query(literals);
    }

    private Rule parseRule(PrologStructure structure) throws IOException, PrologParserException {
	final Atom head;
	if (!structure.getFunctor().getText().equals(":-")) {
	    head = (Atom) parseLiteral(structure);
	    return rule(head);
	}
	head = (Atom) parseLiteral((PrologStructure) structure.getElement(0));
	final List<Literal> body = new LinkedList<Literal>();
	if (structure.getArity() > 1) {
	    final AbstractPrologTerm bodyTerm = structure.getElement(1);
	    if (bodyTerm != null)
		parseLiteralsList((PrologStructure) bodyTerm, body);
	}
	return rule(head, body);
    }

    public Rule parseRule(String rule) throws IOException, PrologParserException {
	return parseRule((PrologStructure) parser.nextSentence(rule));
    }

}

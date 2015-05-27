/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.posLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.query;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;

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

    private final PrologParser parser = new PrologParser(null);

    private Literal parseLiteral(AbstractPrologTerm prologTerm) {
	PrologStructure struct = (PrologStructure) prologTerm;
	String pred = struct.getFunctor().getText();
	List<Term> args = new LinkedList<Term>();
	for (int i = 0; i < struct.getArity(); i++) {
	    AbstractPrologTerm prologArg = struct.getElement(i);
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
	return posLiteral(pred, args);
    }

    private void parseLiteralsList(AbstractPrologTerm prologTerm,
	    List<Literal> literals) throws IOException, PrologParserException {
	PrologStructure struct = (PrologStructure) prologTerm;
	String functor = struct.getFunctor().getText();
	if (!functor.equals(","))
	    literals.add(parseLiteral(prologTerm));
	else {
	    literals.add(parseLiteral(struct.getElement(0)));
	    parseLiteralsList(struct.getElement(1), literals);
	}
    }

    public Query parseQuery(String string) throws IOException,
	    PrologParserException {
	PrologStructure rootStructure = (PrologStructure) parser
		.nextSentence(string);
	List<Literal> literals = new LinkedList<Literal>();
	parseLiteralsList(rootStructure, literals);
	return query(literals);
    }
}

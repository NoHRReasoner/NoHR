/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * @author nunocosta
 *
 */
public class NoHRParser implements Parser {

    private static final String IF = ":-";
    private static final String NEG = "not";

    /**
     * @param str
     * @param i
     * @return
     */
    private Predicate createPredicate(String str, int arity) {
	return pred(getSymbol(str), arity);
    }

    /**
     * @param substring
     * @return
     */
    private String getId(String str) {
	return str;
    }

    /**
     * @param str
     * @return
     */
    private String getSymbol(String str) {
	return str;
    }

    /**
     * @param string
     * @return
     * @throws ParseException
     * @throws OWLExpressionParserException
     */
    private Atom parseAtom(String str) throws ParseException {
	final String[] atomSp = str.split("\\(", -1);
	if (atomSp.length > 2)
	    throw new ParseException("unexpected '('", 0, 1);
	if (atomSp.length == 1) {
	    final Predicate predicate = createPredicate(str.trim(), 0);
	    return atom(predicate);
	}
	if (atomSp[1].trim().isEmpty())
	    throw new ParseException("term expected", 0, 1);
	if (!atomSp[1].contains(")"))
	    throw new ParseException("unmatched left parenthises", 0, 1);
	if (!atomSp[1].endsWith(")"))
	    throw new ParseException("invalid trailing chars", 0, 1);
	String argsStr = atomSp[1];
	argsStr = argsStr.substring(0, argsStr.length() - 1);
	final String[] argsSp = argsStr.split(",");
	final Predicate predicate = createPredicate(atomSp[0].trim(), argsSp.length);
	final List<Term> arguments = new ArrayList<Term>(argsSp.length);
	for (final String argStr : argsSp) {
	    if (argStr.trim().isEmpty())
		throw new ParseException("term expected", 0, 1);
	    arguments.add(parseTerm(argStr.trim()));
	}
	return atom(predicate, arguments);
    }

    /**
     * @param str
     * @return
     */
    // private Term parseListTerm(String str) {
    // final String[] listSp = str.split(",");
    // final List<Term> list = new ArrayList<Term>(listSp.length);
    // for (final String e : listSp)
    // list.add(parseTerm(e.trim()));
    // return list(list);
    // }

    /**
     * @param str
     * @return
     */
    private Literal parseLiteral(String str) throws ParseException {
	if (!str.startsWith(NEG))
	    return parseAtom(str);
	else {
	    final String atomStr = str.substring(NEG.length()).trim();
	    return negLiteral(parseAtom(atomStr));
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.parsing.Parser#parseQuery(java.lang.String)
     */
    @Override
    public Query parseQuery(String str) {
	// TODO
	return null;
    }

    @Override
    public Rule parseRule(String str) throws ParseException {
	if (str.trim().isEmpty())
	    throw new ParseException("empty", 0, 0);
	if (!str.contains(IF)) {
	    final Atom head = parseAtom(str);
	    return rule(head);
	} else {
	    final String[] ruleSp = str.split(IF, -1);
	    if (ruleSp.length > 2)
		throw new ParseException("\":-\" must occur only once", str.lastIndexOf(IF), str.length());
	    if (ruleSp[1].trim().isEmpty())
		throw new ParseException("body can't be empty", ruleSp[0].length(), 1);
	    final Atom head = parseAtom(ruleSp[0].trim());
	    final String[] bodySp = ruleSp[1].split(",");
	    final List<Literal> body = new ArrayList<Literal>(bodySp.length);
	    for (final String l : bodySp)
		body.add(parseLiteral(l.trim()));
	    return rule(head, body);
	}
    }

    /**
     * @param str
     * @return
     */
    private Term parseTerm(String str) {
	// TODO list
	// if (str.startsWith("[")) {
	// if (!str.endsWith("]"))
	// throw new ParseException("unmatched left brackets", 0, 0);
	// return createListTerm(str.substring(1, str.length() - 1).trim());
	// }
	if (str.startsWith("?"))
	    return var(getId(str.substring(1)));
	try {
	    final Double num = Double.valueOf(str);
	    return cons(num);
	} catch (final NumberFormatException e) {
	    return cons(getSymbol(str));
	}
    }

}

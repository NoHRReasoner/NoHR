/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.list;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * @author nunocosta
 *
 */
public class RuleExpressionChecker implements OWLExpressionChecker<Rule> {

    private static final String IF = ":-";
    private static final String NEG = "not";

    /**
     *
     */
    public RuleExpressionChecker() {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#
     * createObject(java.lang.String)
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#check
     * (java.lang.String)
     */
    @Override
    public void check(String str) throws OWLExpressionParserException {
	createObject(str);
    }

    /**
     * @param string
     * @return
     * @throws OWLExpressionParserException
     */
    private Atom createAtom(String str) throws OWLExpressionParserException {
	final String[] atomSp = str.split("\\(", -1);
	if (atomSp.length > 2)
	    throw createPaserException("unexpected '('", 0, 1);
	if (atomSp.length == 1) {
	    final Predicate predicate = createPredicate(str.trim(), 0);
	    return atom(predicate);
	}
	if (atomSp[1].trim().isEmpty())
	    throw createPaserException("term expected", 0, 1);
	if (!atomSp[1].contains(")"))
	    throw createPaserException("unmatched left parenthises", 0, 1);
	if (!atomSp[1].endsWith(")"))
	    throw createPaserException("invalid trailing chars", 0, 1);
	String argsStr = atomSp[1];
	argsStr = argsStr.substring(0, argsStr.length() - 1);
	final String[] argsSp = argsStr.split(",");
	final Predicate predicate = createPredicate(atomSp[0].trim(), argsSp.length);
	final List<Term> arguments = new ArrayList<Term>(argsSp.length);
	for (final String argStr : argsSp) {
	    if (argStr.trim().isEmpty())
		throw createPaserException("term expected", 0, 1);
	    arguments.add(createTerm(argStr.trim()));
	}
	return atom(predicate, arguments);
    }

    /**
     * @param str
     * @return
     * @throws OWLExpressionParserException
     */
    private Term createListTerm(String str) throws OWLExpressionParserException {
	final String[] listSp = str.split(",");
	final List<Term> list = new ArrayList<Term>(listSp.length);
	for (final String e : listSp)
	    list.add(createTerm(e.trim()));
	return list(list);
    }

    /**
     * @param str
     * @return
     * @throws OWLExpressionParserException
     */
    private Literal createLiteral(String str) throws OWLExpressionParserException {
	if (!str.startsWith(NEG))
	    return createAtom(str);
	else {
	    final String atomStr = str.substring(NEG.length()).trim();
	    return negLiteral(createAtom(atomStr));
	}
    }

    @Override
    public Rule createObject(String str) throws OWLExpressionParserException {
	if (str.trim().isEmpty())
	    throw createPaserException("empty", 0, 0);
	if (!str.contains(IF)) {
	    final Atom head = createAtom(str);
	    return rule(head);
	} else {
	    final String[] ruleSp = str.split(IF, -1);
	    if (ruleSp.length > 2)
		throw createPaserException("\":-\" must occur only once", str.lastIndexOf(IF), str.length());
	    if (ruleSp[1].trim().isEmpty())
		throw createPaserException("body can't be empty", ruleSp[0].length(), 1);
	    final Atom head = createAtom(ruleSp[0].trim());
	    final String[] bodySp = ruleSp[1].split(",");
	    final List<Literal> body = new ArrayList<Literal>(bodySp.length);
	    for (final String l : bodySp)
		body.add(createLiteral(l.trim()));
	    return rule(head, body);
	}
    }

    private OWLExpressionParserException createPaserException(String msg, int start, int end) {
	return new OWLExpressionParserException(msg, start, end, false, false, false, false, false, false, null);
    }

    /**
     * @param str
     * @param i
     * @return
     */
    private Predicate createPredicate(String str, int arity) {
	return pred(getSymbol(str), arity);
    }

    /**
     * @param str
     * @return
     * @throws OWLExpressionParserException
     */
    private Term createTerm(String str) throws OWLExpressionParserException {
	// TODO list
	// if (str.startsWith("[")) {
	// if (!str.endsWith("]"))
	// throw createPaserException("unmatched left brackets", 0, 0);
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
}

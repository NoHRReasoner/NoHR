/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.VocabularyMapping;

/**
 * @author nunocosta
 *
 *
 *         rule = atom [":-" literals ]. <br>
 *         query = literals. <br>
 *         literals = literal {"," literal}. <br>
 *         literal = atom | "not" atom. <br>
 *         atom = symbol ["(" term {"," term} ")"]. <br>
 *         term = variable | symbol | list. <br>
 *         variable = "?" id. <br>
 *         list = "[" [term] {"," term} "]" . <br>
 */

public class NoHRParser implements Parser {

    private NoHRScanner scanner;

    private final VocabularyMapping vocabularyMapping;

    public NoHRParser() {
	this(null);
    }

    public NoHRParser(VocabularyMapping vocabularyMapping) {
	this.vocabularyMapping = vocabularyMapping;
    }

    private boolean acept(TokenType type) {
	return scanner.next(type);
    }

    /*
     * atom = symbol ["(" term {"," term} ")"].
     */
    private Atom atom() {
	if (!hasNext(TokenType.SYMBOL))
	    return null;
	final String predicateSymbol = next();
	if (!acept(TokenType.L_PAREN))
	    return Model.atom(predicateSymbol, vocabularyMapping);
	else {
	    final List<Term> args = new LinkedList<>();
	    boolean comma = false;
	    while (true) {
		if (comma && !acept(TokenType.COMMA))
		    break;
		final Term term = term();
		if (term == null)
		    break;
		args.add(term);
		comma = true;
	    }
	    if (args.isEmpty())
		return null;
	    if (!acept(TokenType.R_PAREN))
		return null;
	    return Model.atom(predicateSymbol, vocabularyMapping, args);
	}
    }

    private boolean hasNext(TokenType type) {
	return scanner.next(type);
    }

    private Literal literal() {
	if (acept(TokenType.NOT)) {
	    final Atom atom = atom();
	    if (atom == null)
		return null;
	    return negLiteral(atom);
	} else
	    return atom();
    }

    /*
     * literals = literal {"," literal}.
     */
    private List<Literal> literals() {
	final List<Literal> result = new LinkedList<>();
	boolean comma = false;
	while (true) {
	    if (comma && !acept(TokenType.COMMA))
		break;
	    final Literal literal = literal();
	    if (literal == null)
		break;
	    result.add(literal);
	    comma = true;
	}
	if (result.isEmpty())
	    return null;
	return result;
    }

    private String next() {
	return scanner.getToken();
    }

    @Override
    public Query parseQuery(String str) throws ParseException {
	scanner = new NoHRScanner(str);
	final Query query = query();
	if (query == null)
	    throw new ParseException("", 0, 0);
	return query;
    }

    @Override
    public Rule parseRule(String str) throws ParseException {
	scanner = new NoHRScanner(str);
	final Rule rule = rule();
	if (rule == null)
	    throw new ParseException("", 0, 0);
	return rule;
    }

    /*
     * query = literals.
     */
    private Query query() {
	final List<Literal> literals = literals();
	if (literals == null)
	    return null;
	return Model.query(literals);
    }

    private Rule rule() {
	final Atom head = atom();
	if (head == null)
	    return null;
	if (!acept(TokenType.IF))
	    return Model.rule(head);
	else {
	    final List<Literal> body = literals();
	    if (body == null)
		return null;
	    return Model.rule(head, body);
	}
    }

    /*
     * term = variable | constant | list.
     */
    private Term term() {
	final Variable variable = variable();
	if (variable != null)
	    return variable;
	if (hasNext(TokenType.SYMBOL))
	    return cons(next(), vocabularyMapping);
	return null;
    }

    /*
     * variable = "?" id.
     */

    private Variable variable() {
	if (!acept(TokenType.QUESTION_MARK))
	    return null;
	if (!hasNext(TokenType.ID))
	    return null;
	return var(next());
    }

}

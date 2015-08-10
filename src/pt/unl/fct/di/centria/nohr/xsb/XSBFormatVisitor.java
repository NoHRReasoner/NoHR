/**
 *
 */
package pt.unl.fct.di.centria.nohr.xsb;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ListTerm;
import pt.unl.fct.di.centria.nohr.model.ListTermImpl;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.NegativeLiteral;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * @author nunocosta
 *
 */
public class XSBFormatVisitor implements FormatVisitor {

    private String quoted(String str) {
	return "'" + str + "'";
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.Answer)
     */
    @Override
    public String visit(Answer answer) {
	return Model.concat(",", answer.apply(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.Atom)
     */
    @Override
    public String visit(Atom atom) {
	final String pred = atom.getFunctor().accept(this);
	final String args = Model.concat(",", atom.getArguments(), this);
	return pred + "(" + args + ")";
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.Constant)
     */
    @Override
    public String visit(Constant constant) {
	if (constant.isNumber())
	    return constant.asNumber().toString();
	return quoted(constant.asRuleConstant());
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.ListTermImpl)
     */
    @Override
    public String visit(ListTerm listTerm) {
	return "[" + Model.concat(",", listTerm.asList(), this) + "]";
    }

    @Override
    public String visit(MetaPredicate metaPredicate) {
	return quoted(metaPredicate.getPrefix() + metaPredicate.getPredicate().getSymbol());
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.NegativeLiteral)
     */
    @Override
    public String visit(NegativeLiteral literal) {
	final String format = literal.isExistentiallyNegative() ? "not_exists(%s)" : "tnot(%s)";
	return String.format(format, literal.getAtom().accept(this));
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.predicates.Predicate)
     */
    @Override
    public String visit(Predicate predicate) {
	return quoted(predicate.getSymbol());
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.Query)
     */
    @Override
    public String visit(Query query) {
	return Model.concat(",", query.getLiterals(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.Rule)
     */
    @Override
    public String visit(Rule rule) {
	final String head = rule.getHead().accept(this);
	final String body = Model.concat(",", rule.getBody(), this);
	if (rule.isFact())
	    return head + ".";
	else
	    return head + ":-" + body + ".";
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.FormatVisitor#visit(pt.unl.fct.di.
     * centria .nohr.model.Variable)
     */
    @Override
    public String visit(Variable variable) {
	return variable.getSymbol();
    }

}

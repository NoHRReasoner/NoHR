package pt.unl.fct.di.novalincs.nohr.deductivedb;

import java.util.List;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.DefaultFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.ListTerm;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.NegativeLiteral;
import pt.unl.fct.di.novalincs.nohr.model.ParenthesisTerm;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Symbol;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.AtomOperator;
import pt.unl.fct.di.novalincs.nohr.model.AtomOperatorTerm;
import pt.unl.fct.di.novalincs.nohr.model.AtomTerm;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.HybridConstant;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PrologPredicate;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

public class NoHRFormatVisitor extends DefaultFormatVisitor {

    private final boolean displayIri;

    public NoHRFormatVisitor() {
        displayIri = false;
    }

    public NoHRFormatVisitor(boolean displayIri) {
        this.displayIri = displayIri;
    }

    @Override
    public String visit(Answer answer) {
        return Model.concat(answer.apply(), this, ",");
    }

    @Override
    public String visit(Atom atom) {
        final String pred = atom.getFunctor().accept(this);
        final String args = Model.concat(atom.getArguments(), this, ",");

        if (atom.getArity() == 0) {
            return pred;
        }

        return pred + "(" + args + ")";
    }

    @Override
    public String visit(AtomOperator atomOp) {
        final String pred = atomOp.getFunctor().asString();
        final String arg1 = atomOp.getLeft().accept(this);
        final String arg2 = atomOp.getRight().accept(this);

        return arg1 + " " + pred + " " + arg2;
    }

    @Override
    public String visit(AtomOperatorTerm term) {
        return term.getAtomOperator().accept(this);
    }

    @Override
    public String visit(AtomTerm atomTerm) {
        final Atom atom = atomTerm.getAtom();

        if (atom.getArity() == 0) {
            return atom.accept(this) + "()";
        } else {
            return atom.accept(this);
        }
    }

    @Override
    public String visit(HybridConstant constant) {
        if (constant.isNumber()) {
            return constant.asString();
        }

        if (displayIri) {
            return StringUtils.escapeSymbol(constant.asString());
        } else {
            return StringUtils.escapeSymbol(constant.toString());
        }
    }

    @Override
    public String visit(ListTerm list) {
        List<Term> head = list.getHead();

        if (head == null) {
            return "[]";
        }

        final String h = Model.concat(list.getHead(), this, ",");

        final Term tail = list.getTail();

        if (tail == null) {
            return "[" + h + "]";
        }

        final String t = tail.accept(this);

        return "[" + h + "|" + t + "]";
    }

    @Override
    public String visit(NegativeLiteral negativeLiteral) {
        final String literal = negativeLiteral.getAtom().accept(this);

        return "not " + literal;
    }

    @Override
    public String visit(ParenthesisTerm paren) {
        final String parentheticalContent = paren.getTerm().accept(this);
        return "(" + parentheticalContent + ")";
    }

    @Override
    public String visit(PrologPredicate predicate) {
        return "#" + predicate.asString();
    }

    public String visit(Program program) {
        StringBuffer visit = new StringBuffer();

        for (Rule r : program) {
            visit.append(r.accept(this)).append(".\n");
        }

        return new String(visit);
    }

    @Override
    public String visit(Query query) {
        return Model.concat(query.getLiterals(), this, ",");
    }

    @Override
    public String visit(Rule rule) {
        final String head = rule.getHead().accept(this);
        final String body = Model.concat(rule.getBody(), this, ", ");

        if (rule.isFact()) {
            return head;
        } else {
            return head + " :- " + body;
        }

    }

    @Override
    public String visit(Symbol symbolic) {
        if (displayIri) {
            return StringUtils.escapeSymbol(symbolic.asString());
        } else {
            return StringUtils.escapeSymbol(symbolic.toString());
        }
    }

    @Override
    public String visit(Variable variable) {
        return "?" + variable.asString();
    }

}

package pt.unl.fct.di.centria.nohr.model.predicates;

import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.*;
import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.ListTermImpl;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.NegativeLiteral;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.Visitor;

public class PredicateTypesVisitor implements Visitor {

    private final PredicateType predicateType;

    public PredicateTypesVisitor(PredicateType predicateType) {
	this.predicateType = predicateType;
    }

    @Override
    public Atom visit(Atom atom) {
	return atom.acept(this);
    }

    @Override
    public Constant visit(Constant constant) {
	if (constant.isNumber())
	    return cons(constant.asNumber().toString());
	else
	    return cons(constant.asString());
    }

    @Override
    public Term visit(ListTermImpl list) {
	return list;
    }

    @Override
    public Literal visit(Literal literal) {
	return literal.acept(this);
    }

    @Override
    public NegativeLiteral visit(NegativeLiteral literal) {
	return literal.acept(this);
    }

    @Override
    public Predicate visit(Predicate pred) {
	if (pred.isConcept())
	    pred(pred.asConcept(), predicateType);
	else if (pred.isRole())
	    pred(pred.asRole(), predicateType);
	return pred(pred.asRulePredicate(), pred.getArity(), predicateType);
    }

    @Override
    public Query visit(Query query) {
	return query.acept(this);
    }

    @Override
    public Rule visit(Rule rule) {
	return rule.acept(this);
    }

    @Override
    public Term visit(Term term) {
	return term.acept(this);
    }

    @Override
    public Variable visit(Variable variable) {
	return variable;
    }

}

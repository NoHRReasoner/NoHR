package pt.unl.fct.di.centria.nohr.reasoner.translation;

import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.ListTermImpl;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.NegativeLiteral;
import pt.unl.fct.di.centria.nohr.model.PositiveLiteral;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.Visitor;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateImpl;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabel;

public class UnescapeVisitor implements Visitor {

    public UnescapeVisitor() {
    }

    @Override
    public Atom visit(Atom atom) {
	return atom.acept(this);
    }

    @Override
    public Constant visit(Constant constant) {
	if (constant.isNumber())
	    return cons(OntologyLabel.unescapeAtom(constant.toString()));
	else
	    return cons(OntologyLabel.unescapeAtom(constant.asString()));
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
    public PositiveLiteral visit(PositiveLiteral literal) {
	return literal.acept(this);
    }

    @Override
    public Predicate visit(Predicate pred) {
	return new PredicateImpl(OntologyLabel.unescapeAtom(pred.getSymbol()),
		pred.getArity());
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

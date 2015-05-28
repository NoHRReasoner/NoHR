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
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypes;

public class EncodeVisitor implements Visitor {

    private static char prefix(PredicateTypes predicateType) {
	switch (predicateType) {
	case ORIGINAL:
	    return ORIGINAL_PREFIX;
	case DOUBLED:
	    return DOUBLED_PREFIX;
	case NEGATION:
	    return CLASSICAL_NEGATION_PREFIX;
	case ORIGINAL_DOMAIN:
	    return ORIGINAL_DOM_PREFIX;
	case ORIGINAL_RANGE:
	    return ORIGINAL_RAN_PREFIX;
	case DOUBLED_DOMAIN:
	    return DOUBLED_DOM_PREFIX;
	case DOUBLED_RANGE:
	    return DOUBLED_RAN_PREFIX;
	}
	return '0';
    }

    public static final char ORIGINAL_PREFIX = 'a';
    public static final char DOUBLED_PREFIX = 'd';
    public static final char ORIGINAL_DOM_PREFIX = 'e';
    public static final char ORIGINAL_RAN_PREFIX = 'f';
    public static final char DOUBLED_DOM_PREFIX = 'g';
    public static final char DOUBLED_RAN_PREFIX = 'h';
    public static final char CLASSICAL_NEGATION_PREFIX = 'n';

    // private static char prefix(PredicateTypes predicateType) {
    // return (char) (((int) 'A') + predicateType.ordinal());
    // }

    public static final char CONSTANT_PREFIX = 'c';

    private char prefix;

    public EncodeVisitor(PredicateTypes predicateType) {
	prefix = prefix(predicateType);
    }

    @Override
    public Atom visit(Atom atom) {
	return atom.acept(this);
    }

    @Override
    public Constant visit(Constant constant) {
	if (constant.isNumber())
	    return cons(CONSTANT_PREFIX + constant.asNumber().toString());
	else
	    return cons(CONSTANT_PREFIX + constant.asString());
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
	return new PredicateImpl(prefix + pred.getSymbol(), pred.getArity());
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
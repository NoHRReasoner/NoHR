package nohr.reasoner.translation;

import nohr.model.Atom;
import nohr.model.Constant;
import nohr.model.ConstantImpl;
import nohr.model.ListTermImpl;
import nohr.model.Literal;
import nohr.model.ModelException;
import nohr.model.NegativeLiteral;
import nohr.model.PositiveLiteral;
import nohr.model.Query;
import nohr.model.Rule;
import nohr.model.Term;
import nohr.model.Variable;
import nohr.model.Visitor;
import nohr.model.predicates.Predicate;
import nohr.model.predicates.PredicateImpl;
import nohr.model.predicates.PredicateTypes;

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
	try {
	    if (constant.isNumber())
		return new ConstantImpl(CONSTANT_PREFIX
			+ constant.asNumber().toString());
	    else
		return new ConstantImpl(CONSTANT_PREFIX + constant.asString());
	} catch (ModelException e) {
	    e.printStackTrace();
	    return null;
	}
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

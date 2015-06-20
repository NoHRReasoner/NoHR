package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface Visitor {

    public Atom visit(Atom atom);

    public Constant visit(Constant constant);

    public Term visit(ListTermImpl visitor);

    public Literal visit(Literal literal);

    public NegativeLiteral visit(NegativeLiteral literal);

    public Predicate visit(Predicate pred);

    public Query visit(Query query);

    public Rule visit(Rule rule);

    public Term visit(Term term);

    public Variable visit(Variable variable);

}

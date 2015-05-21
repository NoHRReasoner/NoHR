package nohr.model;

import nohr.model.predicates.Predicate;

public interface Visitor {

	public Predicate visit(Predicate pred);
	
	public Variable visit(Variable variable);
	
	public Constant visit(Constant constant);	
	
	public Term visit(ListTermImpl visitor);
	
	public Term visit(Term term);
	
	public Atom visit(Atom atom);
	
	public PositiveLiteral visit(PositiveLiteral literal);
	
	public NegativeLiteral visit(NegativeLiteral literal);
	
	public Rule visit(Rule rule);
	
	public Query visit(Query query);

	public Literal visit(Literal literal);

}

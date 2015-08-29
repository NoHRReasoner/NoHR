package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.ConceptPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RolePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RulePredicate;

/**
 * A model visitor (see {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>} ) to support different model operations.
 * The {@code visit} methods are intended to construct, from the visited elements, and according to some operation, new elements of the same type, and
 * return that new elements. Implement this interface if you want to support a new model operation, returning the result of the application of that
 * operation to each model element, in the corresponding {@code visit} method.
 *
 * @author Nuno Costa
 */
public interface ModelVisitor {

	public Answer visit(Answer visit);

	public Atom visit(Atom atom);

	public Predicate visit(ConceptPredicate pred);

	public Constant visit(Constant constant);

	public Constant visit(IndividualConstant constant);

	public Literal visit(Literal visit);

	public Constant visit(LiteralConstant constant);

	public Predicate visit(MetaPredicate predicate);

	public NegativeLiteral visit(NegativeLiteral literal);

	public Constant visit(NumericConstant constant);

	public Predicate visit(Predicate pred);

	public Query visit(Query query);

	public Predicate visit(RolePredicate pred);

	public Rule visit(Rule rule);

	public Constant visit(RuleConstant constant);

	public Predicate visit(RulePredicate pred);

	public Term visit(Term term);

	public Variable visit(Variable variable);

}
